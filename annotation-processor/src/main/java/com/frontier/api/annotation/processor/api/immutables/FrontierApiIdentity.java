package com.frontier.api.annotation.processor.api.immutables;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class FrontierApiIdentity {

  private String beanName;

  private String methodName;

  private String guarantee;
}
