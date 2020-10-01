package com.frontier.api.annotationprocessor.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FrontierApiRegisterClient {

  private static final String FRONTIER_API_SERVICE_NAME_URI = "http://FRONTIER-API";

  private final String springApplicationName;

  private Map<String, List<FrontierApiIdentity>> cachedFrontierIdentitiesByServiceName = new HashMap<>();

  public FrontierApiRegisterClient(
      @Value("${spring-application-name}") String springApplicationName) {
    this.springApplicationName = springApplicationName;
  }

  public void register(String beanName, String methodName) {

/*    FrontierApiRegisterRequestMessage frontierAPIRegisterRequestMessage = FrontierApiRegisterRequestMessage
        .builder()
        .serviceName(springApplicationName)
        .beanName(beanName)
        .methodName(methodName)
        .build();

    RestTemplate restTemplate = new RestTemplate();

    ResponseEntity<FrontierApiRegisterResponseMessage> response = restTemplate
        .postForEntity(FRONTIER_API_SERVICE_NAME_URI + "/register", frontierAPIRegisterRequestMessage,
            FrontierApiRegisterResponseMessage.class);

    cachedFrontierIdentitiesByServiceName = response.getBody().getFrontierIdentitiesByServiceName();*/
  }

  public void pullForCache() {
    RestTemplate restTemplate = new RestTemplate();

    ResponseEntity<FrontierApiRegisterResponseMessage> response = restTemplate
        .getForEntity(FRONTIER_API_SERVICE_NAME_URI + "/pull",
            FrontierApiRegisterResponseMessage.class);

    cachedFrontierIdentitiesByServiceName = response.getBody().getFrontierIdentitiesByServiceName();
  }

  public Optional<String> resolveServiceName(String beanName, String methodName) {
    FrontierApiIdentity frontierApiIdentity = FrontierApiIdentity.builder()
        .beanName(beanName)
        .methodName(methodName)
        .build();

    return cachedFrontierIdentitiesByServiceName.keySet().stream()
        .filter(a -> cachedFrontierIdentitiesByServiceName.get(a).equals(frontierApiIdentity))
        .findFirst();
  }
}
