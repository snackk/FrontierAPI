package com.frontier.api.annotationprocessor.domain;

import org.springframework.data.repository.CrudRepository;

public class FrontierRepositoryWrapper {

  private final CrudRepository repository;

  public FrontierRepositoryWrapper(CrudRepository repository) {
    this.repository = null; //missing set here
  }

  public CrudRepository getRepository() {
    return repository;
  }
}
