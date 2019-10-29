package com.frontier.api.annotationprocessor.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FrontierRepositoryProperty {

  private Guarantee guarantee;

  private String methodName;

  //TODO will this be needed?
  //private Class<?> domainType;
  //private Class<?> idType;
}
