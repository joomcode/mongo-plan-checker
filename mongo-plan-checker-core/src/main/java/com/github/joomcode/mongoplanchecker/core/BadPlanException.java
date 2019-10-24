package com.github.joomcode.mongoplanchecker.core;

import org.bson.Document;

public class BadPlanException extends IllegalStateException {

  private final Document plan;
  private final Violations violations;

  public BadPlanException(Document plan, Violations violations) {
    this.plan = plan;
    this.violations = violations;
  }

  public Document getPlan() {
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
