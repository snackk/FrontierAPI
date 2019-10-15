package com.frontier.api.annotationprocessor;

public enum Guarantee {
  SYNCHRONOUS("synchronous"),
  ASYNCHRONOUS("asynchronous"),
  BEST_EFFORT("best-effort");

  private String name;

  Guarantee(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }
}
