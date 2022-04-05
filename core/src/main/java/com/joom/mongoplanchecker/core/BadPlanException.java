package com.joom.mongoplanchecker.core;

import org.bson.BsonDocument;
import org.bson.Document;

public class BadPlanException extends IllegalStateException {

  private final String plan;
  private final Violations violations;

  public BadPlanException(Document plan, Violations violations) {
    this.plan = plan.toBsonDocument(Void.class, Util.CODEC_REGISTRY).toJson();
    this.violations = violations;
  }

  public BadPlanException(BsonDocument plan, Violations violations) {
    this.plan = plan.toJson();
    this.violations = violations;
  }

  public String getPlan() {
    return plan;
  }

  public Violations getViolations() {
    return violations;
  }

  @Override
  public String getMessage() {
    return violations + "- in plan " + plan;
  }
}
