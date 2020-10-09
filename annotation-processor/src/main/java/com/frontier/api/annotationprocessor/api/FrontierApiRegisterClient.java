package com.frontier.api.annotationprocessor.api;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
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

  public Optional<Pair<String, FrontierApiIdentity>> resolveServiceName(String beanName,
      String methodName) {

    //TODO Register should be the end of spring bean start life cycle
    register();

    return cachedFrontierIdentitiesByServiceName.keySet().stream()
        .flatMap(a ->
            cachedFrontierIdentitiesByServiceName.get(a).stream()
                .filter(
                    c -> StringUtils.containsIgnoreCase(beanName, c.getBeanName()) && StringUtils
                        .containsIgnoreCase(methodName, c.getMethodName()))
                .map(d -> Stream.of(Pair.of(a, d)))
                .findFirst()
                .orElse(Stream.empty()))
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
