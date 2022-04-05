package com.joom.mongoplanchecker.core;

public class NotExplainException extends RuntimeException {
  private final String doc;

  public NotExplainException(String doc) {
    this.doc = doc;
  }

  @Override
  public String getMessage() {
    return "Document is not explain plan - " + doc;
  }
}
