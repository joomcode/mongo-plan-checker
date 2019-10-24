package com.github.joomcode.mongoplanchecker.core;

import org.bson.Document;

public class NotExplainException extends RuntimeException {
  private final Document doc;

  public NotExplainException(Document doc) {
    this.doc = doc;
  }

  @Override
  public String getMessage() {
    return "Document is not explain plan - " + doc;
  }
}
