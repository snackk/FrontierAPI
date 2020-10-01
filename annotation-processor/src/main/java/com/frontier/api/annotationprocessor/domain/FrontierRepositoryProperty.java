package com.frontier.api.annotationprocessor.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FrontierRepositoryProperty {

  private Guarantee guarantee;

  private String methodName;

}
