package com.frontier.api.annotationprocessor.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Guarantee {
  SYNCHRONOUS("synchronous"),
  ASYNCHRONOUS("asynchronous"),
  BEST_EFFORT("best-effort");

  private String name;
}
