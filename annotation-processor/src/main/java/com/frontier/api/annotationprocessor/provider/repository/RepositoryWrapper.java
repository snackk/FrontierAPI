package com.frontier.api.annotationprocessor.provider.repository;

import org.springframework.data.repository.CrudRepository;

public class RepositoryWrapper {

  private final CrudRepository repository;

  public RepositoryWrapper(CrudRepository repository) {
    this.repository = null; //missing set here
  }

  public CrudRepository getRepository() {
    return repository;
  }
}
