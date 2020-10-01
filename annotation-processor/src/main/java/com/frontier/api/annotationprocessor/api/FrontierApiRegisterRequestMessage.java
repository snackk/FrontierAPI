package com.frontier.api.annotationprocessor.api;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class FrontierApiRegisterRequestMessage {

  private String serviceName;

  private String beanName;

  private String methodName;
}
