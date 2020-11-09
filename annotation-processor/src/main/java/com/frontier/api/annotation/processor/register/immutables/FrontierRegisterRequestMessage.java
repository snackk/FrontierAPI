package com.frontier.api.annotation.processor.register.immutables;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class FrontierRegisterRequestMessage {

  private List<FrontierRegisterNode> frontierApiBatchMessages;
}

