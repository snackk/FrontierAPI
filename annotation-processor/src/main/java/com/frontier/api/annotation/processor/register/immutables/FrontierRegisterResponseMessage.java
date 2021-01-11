package com.frontier.api.annotation.processor.register.immutables;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class FrontierRegisterResponseMessage {

  private Map<FrontierDestination, List<FrontierRegisterNode>> frontierIdentitiesByServiceName;

}

