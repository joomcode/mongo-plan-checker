package com.github.joomcode.mongoplanchecker.core;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.bson.Document;
import org.bson.conversions.Bson;

public final class PlanChecker {

  public static Bson explainModifier() {
    return new Document("$explain", true);
  }

  private final AtomicInteger broadcastsToIgnore = new AtomicInteger();
  private final AtomicInteger collscansToIgnore = new AtomicInteger();
  private final AtomicInteger excessReadsToIgnore = new AtomicInteger();
  private final AtomicInteger sortsToIgnore = new AtomicInteger();

  public Violations getViolations(Document plan) {
    return getViolations(plan, 0);
  }

  public void ignoreBroadcast() {
    broadcastsToIgnore.incrementAndGet();
  }

  public void ignoreCollscan() {
    collscansToIgnore.incrementAndGet();
  }

  public void ignoreExcessRead() {
    excessReadsToIgnore.incrementAndGet();
  }

  public void ignoreSort() {
    sortsToIgnore.incrementAndGet();
  }

  public boolean anyIgnores() {
    return broadcastsToIgnore.get() > 0
        || collscansToIgnore.get() > 0
        || excessReadsToIgnore.get() > 0
        || sortsToIgnore.get() > 0;
  }

  public Violations getViolations(Document plan, int skip) {
    Document queryPlanner = (Document) plan.get("queryPlanner");
    if (queryPlanner == null) {
      throw new NotExplainException(plan);
    }
    Violations.Builder resultBuilder = new Violations.Builder();
    checkExcessRead((Document) plan.get("executionStats"), skip, resultBuilder);
    traverseStage((Document) queryPlanner.get("winningPlan"), resultBuilder);

    if (resultBuilder.broadcast && broadcastsToIgnore.get() > 0) {
      if (broadcastsToIgnore.decrementAndGet() >= 0) {
        resultBuilder.broadcast = false;
      } else {
        broadcastsToIgnore.incrementAndGet();
      }
    }

    if (resultBuilder.excessRead && excessReadsToIgnore.get() > 0) {
      if (excessReadsToIgnore.decrementAndGet() >= 0) {
        resultBuilder.excessRead = false;
      } else {
        excessReadsToIgnore.incrementAndGet();
      }
    }

    if (resultBuilder.collscans > 0 && collscansToIgnore.get() >= resultBuilder.collscans) {
      if (collscansToIgnore.addAndGet(-resultBuilder.collscans) >= 0) {
        resultBuilder.collscans = 0;
      } else {
        collscansToIgnore.addAndGet(resultBuilder.collscans);
      }
    }

    if (resultBuilder.sorts > 0 && sortsToIgnore.get() >= resultBuilder.sorts) {
      if (sortsToIgnore.addAndGet(-resultBuilder.sorts) >= 0) {
        resultBuilder.sorts = 0;
      } else {
        sortsToIgnore.addAndGet(resultBuilder.sorts);
      }
    }

    return resultBuilder.build();
  }

  private static void checkExcessRead(
      Document executionStats, int skip, Violations.Builder violationsBuilder) {
    Integer nReturned = (Integer) executionStats.get("nReturned");
    Integer totalDocsExamined = (Integer) executionStats.get("totalDocsExamined");
    Integer totalKeysExamined = (Integer) executionStats.get("totalKeysExamined");
    int needExamine = nReturned + skip + 1; // to work around zero returning
    if (needExamine < totalDocsExamined / 4 || needExamine < totalKeysExamined / 8) {
      violationsBuilder.excessRead = true;
    }
  }

  private static void traverseStage(Document inputStage, Violations.Builder violationsBuilder) {
    List<Document> shards = (List<Document>) inputStage.get("shards");
    if (shards != null) {
      if (shards.size() > 1) {
        violationsBuilder.broadcast = true;
      }
      traverseStage((Document) shards.get(0).get("winningPlan"), violationsBuilder);
      return;
    }

    if ("COLLSCAN".equals(inputStage.get("stage"))) {
      violationsBuilder.collscans++;
    }
    if ("SORT".equals(inputStage.get("stage"))) {
      violationsBuilder.sorts++;
    }

    inputStage = (Document) inputStage.get("inputStage");
    if (inputStage != null) {
      traverseStage(inputStage, violationsBuilder);
    }
  }
}
