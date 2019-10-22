package com.frontier.api.annotationprocessor.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrontierRepositoryWrapper {

  private final Map<String, List<FrontierRepositoryProperty>> frontierRepositoryProperties =
      new HashMap<>();

  public FrontierRepositoryWrapper(String classPath) {
    ArrayList<FrontierRepositoryProperty> frontierRepositoryProperties = new ArrayList<>();
    this.frontierRepositoryProperties.put(classPath, frontierRepositoryProperties);
  }

  public void addFrontierRepositoryProperty(String classPath,
      FrontierRepositoryProperty frontierRepositoryProperty) {
    frontierRepositoryProperties.get(classPath).add(frontierRepositoryProperty);
  }

  public Map<String, List<FrontierRepositoryProperty>> getFrontierRepositoryProperties() {
    return this.frontierRepositoryProperties;
  }

}
