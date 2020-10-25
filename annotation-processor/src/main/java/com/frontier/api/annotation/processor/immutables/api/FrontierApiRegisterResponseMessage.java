package com.frontier.api.annotation.processor.immutables.api;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class FrontierApiRegisterResponseMessage {

  private Map<String, List<FrontierApiIdentity>> frontierIdentitiesByServiceName;

}

