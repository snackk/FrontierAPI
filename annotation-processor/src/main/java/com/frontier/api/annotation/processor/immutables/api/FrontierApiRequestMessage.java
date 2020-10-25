package com.frontier.api.annotation.processor.immutables.api;

import java.util.Set;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class FrontierApiRequestMessage {

  private String beanName;

  private String methodName;

  private Set<Object> methodParams;
}
