package com.frontier.api.coordinator.rest.imutables;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class FrontierDestination {

  private String name;
}

