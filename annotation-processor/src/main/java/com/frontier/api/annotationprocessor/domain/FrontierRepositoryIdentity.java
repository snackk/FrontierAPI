package com.frontier.api.annotationprocessor.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class FrontierRepositoryIdentity {

  private String classpath;

  private String beanName;
}
