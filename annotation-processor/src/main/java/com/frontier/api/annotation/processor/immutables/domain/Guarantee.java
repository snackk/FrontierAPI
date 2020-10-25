package com.frontier.api.annotation.processor.immutables.domain;

import java.util.Arrays;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Guarantee {
  SYNCHRONOUS("synchronous"),
  ASYNCHRONOUS("asynchronous"),
  BEST_EFFORT("best-effort");

  private String name;

  public static Optional<Guarantee> getMethodGuarantee(String guarantee) {
    return Arrays.stream(Guarantee.values())
        .filter(g -> g.getName().equals(guarantee))
        .findFirst();
  }
}
