package com.frontier.api.annotation.processor.annotation.immutables;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class FrontierRepositoryProperty {

  private Guarantee guarantee;

  private String methodName;
}
