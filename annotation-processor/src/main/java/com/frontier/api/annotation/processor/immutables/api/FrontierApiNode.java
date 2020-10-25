package com.frontier.api.annotation.processor.immutables.api;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class FrontierApiNode {

  private String serviceName;

  private String beanName;

  private String methodName;
}
