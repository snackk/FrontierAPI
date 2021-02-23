package com.frontier.api.coordinator.rest.imutables;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class FrontierRegisterNode {

  private String beanName;

  private String methodName;

  private String guarantee;
}

