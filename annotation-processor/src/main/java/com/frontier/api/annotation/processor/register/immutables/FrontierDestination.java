package com.frontier.api.annotation.processor.register.immutables;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class FrontierDestination {

  private String name;
}
