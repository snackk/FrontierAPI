package com.frontier.api.coordinator.rest.imutables;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class FrontierRegisterRequestMessage {

  private List<FrontierRegisterNode> frontierRegisterNodeBatch;

  private String serviceName;
}


