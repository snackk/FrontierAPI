package com.frontier.api.annotationprocessor.domain;

public class FrontierRepositoryProperty {

  private final Guarantee guarantee;

  private final String methodName;

  //TODO will this be needed?
  //private Class<?> domainType;
  //private Class<?> idType;

  public FrontierRepositoryProperty(Guarantee guarantee,
      String methodName) {
    this.guarantee = guarantee;
    this.methodName = methodName;
  }

  public Guarantee getGuarantee() {
    return guarantee;
  }

  public String getMethodName() {
    return methodName;
  }
}
