package com.frontier.api.annotationprocessor.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class FrontierRepositoryWrapper {

  private final Map<FrontierRepositoryIdentity, Set<FrontierRepositoryProperty>> frontierRepositoryProperties =
      new HashMap<>();

  private static FrontierRepositoryWrapper frontierRepositoryWrapperInstance;

  public static FrontierRepositoryWrapper getInstance() {
    if (frontierRepositoryWrapperInstance == null) {
      frontierRepositoryWrapperInstance = new FrontierRepositoryWrapper();
    }
    return frontierRepositoryWrapperInstance;
  }

  private FrontierRepositoryWrapper() {
  }

  public void addFrontierRepositoryProperty(FrontierRepositoryIdentity frontierRepositoryIdentity,
      FrontierRepositoryProperty frontierRepositoryProperty) {
    Set<FrontierRepositoryProperty> updatedFrontierProperties = Optional
        .ofNullable(this.frontierRepositoryProperties.get(frontierRepositoryIdentity))
        .map(f -> {
          f.add(frontierRepositoryProperty);
          return f;
        })
        .orElseGet(() -> {
          HashSet<FrontierRepositoryProperty> frontierProperties = new HashSet<>();
          frontierProperties.add(frontierRepositoryProperty);
          return frontierProperties;
        });
    this.frontierRepositoryProperties.put(frontierRepositoryIdentity, updatedFrontierProperties);
  }

  public void addFrontierRepositoryIdentity(FrontierRepositoryIdentity frontierRepositoryIdentity) {
    Set<FrontierRepositoryProperty> emptyFrontierProperties = Optional
        .ofNullable(this.frontierRepositoryProperties.get(frontierRepositoryIdentity))
        .orElseGet(HashSet::new);
    this.frontierRepositoryProperties.put(frontierRepositoryIdentity, emptyFrontierProperties);
  }

  public Map<FrontierRepositoryIdentity, Set<FrontierRepositoryProperty>> getFrontierRepositoryProperties() {
    return this.frontierRepositoryProperties;
  }

}
