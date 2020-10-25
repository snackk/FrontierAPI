package com.frontier.api.annotation.processor.immutables.domain;

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
