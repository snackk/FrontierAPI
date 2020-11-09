package com.frontier.api.annotation.processor.annotation.immutables;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class FrontierRepositoryIdentity {

  private String packageName;

  private String beanName;

  private List<FrontierRepositoryProperty> properties;
}
