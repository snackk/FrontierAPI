package com.frontier.api.annotation.processor.register;

import com.frontier.api.annotation.processor.annotation.immutables.Guarantee;
import com.frontier.api.annotation.processor.annotation.service.FrontierRepositoryCacheService;
import com.frontier.api.annotation.processor.api.immutables.FrontierApiIdentity;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FrontierRegisterService {

  @Value("${frontier-api:http://FRONTIER-API:8090}")
  private String FRONTIER_API_SERVICE_NAME_URI;

  @Value("${spring.application.name}")
  private String springApplicationName;

  private final FrontierRepositoryCacheService frontierRepositoryCacheService;
  private Map<String, List<FrontierApiIdentity>> cachedFrontierIdentitiesByServiceName = new HashMap<>();
//FrontierApiNode

  public FrontierRegisterService(
      FrontierRepositoryCacheService frontierRepositoryCacheService) {
    this.frontierRepositoryCacheService = frontierRepositoryCacheService;
  }

  private void register() {

    //TODO When registering:
    // - SEND:
    //    - SERVICE_NAME
    //    - FRONTIER_IDENTITIES
    // - RECEIVE:
    //    - IP_ADDRESS:PORT
    //    - FRONTIER_IDENTITIES
    //    - QUEUE_NAME
    //    - FRONTIER_IDENTITIES
    cachedFrontierIdentitiesByServiceName = ImmutableMap.of(
        "http://localhost:8080", ImmutableList.of(FrontierApiIdentity.builder()
            .beanName("VisitRepository")
            .methodName("findByPetId")
            .guarantee("synchronous")
            .build()),
        "petclinic", ImmutableList.of(FrontierApiIdentity.builder()
            .beanName("VisitRepository")
            .methodName("updateVisit")
            .guarantee("asynchronous")
            .build()));

     /*
      RestTemplate restTemplate = new RestTemplate();

      FrontierApiRegisterRequestMessage registerRequestMessage = FrontierApiRegisterRequestMessage
          .builder()
          .frontierApiBatchMessages(frontierApiLeftToRegister)
          .build();

         FrontierApiNode frontierApiNode = FrontierApiNode
        .builder()
        .serviceName(springApplicationName)
        .beanName(beanName)
        .methodName(methodName)
        .guarantee(guarantee.name())
        .build();
      //TODO service-name is not accurate
      ResponseEntity<FrontierApiRegisterResponseMessage> response = restTemplate
          .postForEntity(FRONTIER_API_SERVICE_NAME_URI + "/register",
              registerRequestMessage,
              FrontierApiRegisterResponseMessage.class);

              cachedFrontierIdentitiesByServiceName = Objects.requireNonNull(response.getBody())
          .getFrontierIdentitiesByServiceName();
              */
  }

  public Optional<Pair<String, FrontierApiIdentity>> resolveServiceName(String beanName,
      String methodName, Guarantee guarantee) {

    //TODO Register should be the end of spring bean life cycle
    register();

    return cachedFrontierIdentitiesByServiceName.keySet().stream()
        .flatMap(a ->
            cachedFrontierIdentitiesByServiceName.get(a).stream()
                .filter(
                    c -> StringUtils.containsIgnoreCase(beanName, c.getBeanName())
                        && StringUtils.containsIgnoreCase(methodName, c.getMethodName())
                        && StringUtils.containsIgnoreCase(guarantee.toString(), c.getGuarantee()))
                .map(d -> Stream.of(Pair.of(a, d)))
                .findFirst()
                .orElse(Stream.empty()))
        .findFirst();
  }
/*
  public void pullForCache() {
    RestTemplate restTemplate = new RestTemplate();

    ResponseEntity<FrontierApiRegisterResponseMessage> response = restTemplate
        .getForEntity(FRONTIER_API_SERVICE_NAME_URI + "/pull",
            FrontierApiRegisterResponseMessage.class);

    cachedFrontierIdentitiesByServiceName = Objects.requireNonNull(response.getBody())
        .getFrontierIdentitiesByServiceName();
  }*/
}
