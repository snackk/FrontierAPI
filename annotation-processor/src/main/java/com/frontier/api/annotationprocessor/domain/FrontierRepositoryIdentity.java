package com.frontier.api.annotationprocessor.domain;

public class FrontierRepositoryIdentity {

  private final String classpath;

  private final String beanName;

  public FrontierRepositoryIdentity(String classpath,
      String beanName) {
    this.beanName = beanName;
    this.classpath = classpath;
  }

  public String getClasspath() {
    return classpath;
  }

  public String getBeanName() {
    return beanName;
  }
}
