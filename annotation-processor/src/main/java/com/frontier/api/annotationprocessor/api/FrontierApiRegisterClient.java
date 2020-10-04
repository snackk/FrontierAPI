package com.frontier.api.annotationprocessor.api;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FrontierApiRegisterClient {

  private final String FRONTIER_API_SERVICE_NAME_URI;

  private final String springApplicationName;

  private Map<String, List<FrontierApiIdentity>> cachedFrontierIdentitiesByServiceName = new HashMap<>();

  private List<SingleFrontierApi> frontierApiLeftToRegister = new ArrayList<>();

  public FrontierApiRegisterClient(
      @Value("${spring-application-name}") String springApplicationName,
      @Value("${frontier-api:http://FRONTIER-API:8090}") String frontierApiServiceName) {
    this.springApplicationName = springApplicationName;
    this.FRONTIER_API_SERVICE_NAME_URI = frontierApiServiceName;
  }

  public void cacheFrontierApiToRegister(String beanName, String methodName) {
    SingleFrontierApi singleFrontierApi = SingleFrontierApi
        .builder()
        .serviceName(springApplicationName)
        .beanName(beanName)
        .methodName(methodName)
        .build();

    frontierApiLeftToRegister.add(singleFrontierApi);
  }

  private void register() {
    if (frontierApiLeftToRegister.size() > 0) {
      RestTemplate restTemplate = new RestTemplate();

      FrontierApiRegisterRequestMessage registerRequestMessage = FrontierApiRegisterRequestMessage
          .builder()
          .frontierApiBatchMessages(frontierApiLeftToRegister)
          .build();

      ResponseEntity<FrontierApiRegisterResponseMessage> response = restTemplate
          .postForEntity(FRONTIER_API_SERVICE_NAME_URI + "/register",
              registerRequestMessage,
              FrontierApiRegisterResponseMessage.class);

      frontierApiLeftToRegister = ImmutableList.of();

      cachedFrontierIdentitiesByServiceName = Objects.requireNonNull(response.getBody())
          .getFrontierIdentitiesByServiceName();
    }
  }

  public Optional<String> resolveServiceName(String beanName, String methodName) {
    //TODO Register should be the end of spring bean start life cycle
    register();

    FrontierApiIdentity frontierApiIdentity = FrontierApiIdentity.builder()
        .beanName(beanName)
        .methodName(methodName)
        .build();

    return cachedFrontierIdentitiesByServiceName.keySet().stream()
        .filter(a -> cachedFrontierIdentitiesByServiceName.get(a).contains(frontierApiIdentity))
        .findFirst();
  }

  public void pullForCache() {
    RestTemplate restTemplate = new RestTemplate();

    ResponseEntity<FrontierApiRegisterResponseMessage> response = restTemplate
        .getForEntity(FRONTIER_API_SERVICE_NAME_URI + "/pull",
            FrontierApiRegisterResponseMessage.class);

    cachedFrontierIdentitiesByServiceName = Objects.requireNonNull(response.getBody())
        .getFrontierIdentitiesByServiceName();
  }
}
