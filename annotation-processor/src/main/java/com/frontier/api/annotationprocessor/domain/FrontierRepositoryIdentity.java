package com.frontier.api.annotationprocessor.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FrontierRepositoryIdentity {

  private String classpath;

  private String beanName;
}
