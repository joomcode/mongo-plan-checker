package com.joom.mongoplanchecker.core;

import java.util.concurrent.atomic.AtomicInteger;
import org.bson.BsonDocument;
import org.bson.BsonValue;
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

  public Violations getViolations(BsonDocument plan) {
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

  public void resetIgnores() {
    broadcastsToIgnore.set(0);
    collscansToIgnore.set(0);
    excessReadsToIgnore.set(0);
    sortsToIgnore.set(0);
  }

  public Violations getViolations(BsonDocument plan, int skip) {
    BsonValue queryPlanner = plan.get("queryPlanner");
    if (queryPlanner == null) {
      throw new NotExplainException(plan.toJson());
    }
    Violations.Builder resultBuilder = new Violations.Builder();
    checkExcessRead(plan.get("executionStats").asDocument(), skip, resultBuilder);
    traverseStage(queryPlanner.asDocument().get("winningPlan").asDocument(), resultBuilder);

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

  public Violations getViolations(Document plan, int skip) {
    return getViolations(plan.toBsonDocument(Object.class, Util.CODEC_REGISTRY), skip);
  }

  private static void checkExcessRead(
      BsonDocument executionStats, int skip, Violations.Builder violationsBuilder) {
    int nReturned = executionStats.get("nReturned").asNumber().intValue();
    int totalDocsExamined = executionStats.get("totalDocsExamined").asNumber().intValue();
    int totalKeysExamined = executionStats.get("totalKeysExamined").asNumber().intValue();
    int needExamine = nReturned + skip + 1; // to work around zero returning
    if (needExamine < totalDocsExamined / 4 || needExamine < totalKeysExamined / 8) {
      violationsBuilder.excessRead = true;
    }
  }

  private static void traverseStage(BsonDocument inputStage, Violations.Builder violationsBuilder) {
    BsonValue shards = inputStage.get("shards");
    if (shards != null) {
      if (shards.asArray().size() > 1) {
        violationsBuilder.broadcast = true;
      }
      traverseStage(
          shards.asArray().get(0).asDocument().get("winningPlan").asDocument(), violationsBuilder);
      return;
    }

    if ("COLLSCAN".equals(inputStage.get("stage").asString().getValue())) {
      violationsBuilder.collscans++;
    }
    if ("SORT".equals(inputStage.get("stage").asString().getValue())) {
      violationsBuilder.sorts++;
    }

    BsonValue innerInputStage = inputStage.get("inputStage");
    if (innerInputStage != null) {
      traverseStage(innerInputStage.asDocument(), violationsBuilder);
    }
  }
}
