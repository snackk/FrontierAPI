package com.frontier.api.annotationprocessor.domain;

public class FrontierRepositoryWrapper {

  private String name;

  private Class<?> domainType;

  private Class<?> idType;

  public FrontierRepositoryWrapper(String name,
      Class<?> domainType,
      Class<?> idType) {
    this.name = name;
    this.domainType = domainType;
    this.idType = idType;
  }

  public String getName() {
    return name;
  }

  public Class<?> getDomainType() {
    return domainType;
  }

  public Class<?> getIdType() {
    return idType;
  }
}
