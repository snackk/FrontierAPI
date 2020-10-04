package com.frontier.api.annotationprocessor.api;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class FrontierApiRegisterRequestMessage {

  private List<SingleFrontierApi> frontierApiBatchMessages;
}

@Builder
@Getter
@EqualsAndHashCode
class SingleFrontierApi {

  private String serviceName;

  private String beanName;

  private String methodName;
}
