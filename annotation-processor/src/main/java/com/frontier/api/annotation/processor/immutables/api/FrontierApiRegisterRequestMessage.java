package com.frontier.api.annotation.processor.immutables.api;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class FrontierApiRegisterRequestMessage {

  private List<FrontierApiNode> frontierApiBatchMessages;
}

