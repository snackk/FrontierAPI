package com.frontier.api.annotationprocessor.provider.rest;

import java.util.Set;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class FrontierRequestMessage {

  private String beanName;

  private String methodName;

  private Set<Object> methodParams;
}
