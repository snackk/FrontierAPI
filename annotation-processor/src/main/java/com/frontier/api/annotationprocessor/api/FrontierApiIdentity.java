package com.frontier.api.annotationprocessor.api;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class FrontierApiIdentity {

  private String beanName;

  private String methodName;
}
