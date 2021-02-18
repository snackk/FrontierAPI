package com.frontier.api.annotation.processor.register;

import com.frontier.api.annotation.processor.annotation.immutables.FrontierRepositoryIdentity;
import com.frontier.api.annotation.processor.annotation.service.FrontierRepositoryCacheService;
import com.frontier.api.annotation.processor.api.immutables.FrontierApiRequestMessage;
import com.frontier.api.annotation.processor.api.immutables.FrontierApiResponseMessage;
import com.frontier.api.annotation.processor.register.immutables.FrontierRegisterNode;
import com.frontier.api.annotation.processor.register.immutables.FrontierRegisterRequestMessage;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FrontierRegisterService {

  @Value("${frontier-api:http://FRONTIER-API:8090/frontier/api/}")
  private String FRONTIER_API_ENDPOINT;

  @Value("${spring.application.name}")
  private String serviceName;

  private final FrontierRepositoryCacheService frontierRepositoryCacheService;

  private final static Function<Set<FrontierRepositoryIdentity>, List<FrontierRegisterNode>> FRONTIER_REGISTER_ADAPTER_FUNC = (a) ->
      a.stream()
          .flatMap(f -> f.getProperties().stream()
              .map(p -> FrontierRegisterNode.builder()
                  .beanName(f.getBeanName())
                  .methodName(p.getMethodName())
                  .guarantee(p.getGuarantee().getName())
                  .build()))
          .collect(ImmutableList.toImmutableList());

  public FrontierRegisterService(
      FrontierRepositoryCacheService frontierRepositoryCacheService) {
    this.frontierRepositoryCacheService = frontierRepositoryCacheService;
  }

  private void registerRequest() {
    RestTemplate restTemplate = new RestTemplate();

    List<FrontierRegisterNode> frontierRegisterNodes = FRONTIER_REGISTER_ADAPTER_FUNC
        .apply(frontierRepositoryCacheService.finalizeFrontierRepositoryBuild());

    FrontierRegisterRequestMessage registerRequestMessage = FrontierRegisterRequestMessage
        .builder()
        .serviceName(serviceName)
        .frontierRegisterNodeBatch(frontierRegisterNodes)
        .build();

    restTemplate
        .postForLocation(FRONTIER_API_ENDPOINT + "/register",
            registerRequestMessage);
  }

  public ResponseEntity<FrontierApiResponseMessage> proxyFrontierRequest(
      FrontierApiRequestMessage frontierApiRequestMessage) {
    //TODO minor: Register should be the end of spring bean life cycle,
    // this way avoiding to be a time consuming task on the 1st request
    registerRequest();

    RestTemplate restTemplate = new RestTemplate();

    return restTemplate
        .postForEntity(FRONTIER_API_ENDPOINT + "/proxy",
            frontierApiRequestMessage, FrontierApiResponseMessage.class);
  }
}
