package com.frontier.api.annotationprocessor.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrontierRepositoryWrapper {

  private final Map<FrontierRepositoryIdentity, List<FrontierRepositoryProperty>> frontierRepositoryProperties =
      new HashMap<>();

  public FrontierRepositoryWrapper(FrontierRepositoryIdentity frontierRepositoryIdentity) {
    ArrayList<FrontierRepositoryProperty> frontierRepositoryProperties = new ArrayList<>();
    this.frontierRepositoryProperties.put(frontierRepositoryIdentity, frontierRepositoryProperties);
  }

  public void addFrontierRepositoryProperty(FrontierRepositoryIdentity frontierRepositoryIdentity,
      FrontierRepositoryProperty frontierRepositoryProperty) {
    frontierRepositoryProperties.get(frontierRepositoryIdentity).add(frontierRepositoryProperty);
  }

  public Map<FrontierRepositoryIdentity, List<FrontierRepositoryProperty>> getFrontierRepositoryProperties() {
    return this.frontierRepositoryProperties;
  }

}
