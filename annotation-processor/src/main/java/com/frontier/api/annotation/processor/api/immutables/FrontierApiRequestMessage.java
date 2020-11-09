package com.frontier.api.annotation.processor.api.immutables;

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
