package com.frontier.api.annotation.processor.annotation.service;

import com.frontier.api.annotation.processor.annotation.immutables.FrontierRepositoryIdentity;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class FrontierRepositoryCacheService {

  private final ImmutableSet.Builder<FrontierRepositoryIdentity> frontierRepositoryPropertiesBuilder = ImmutableSet
      .builder();

  public void addFrontierRepositoryIdentity(FrontierRepositoryIdentity frontierRepositoryIdentity) {
    frontierRepositoryPropertiesBuilder.add(frontierRepositoryIdentity);
  }

  public Set<FrontierRepositoryIdentity> finalizeFrontierRepositoryBuild() {
    return this.frontierRepositoryPropertiesBuilder.build();
  }

}
