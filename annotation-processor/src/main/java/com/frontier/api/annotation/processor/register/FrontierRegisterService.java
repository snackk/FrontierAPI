package com.frontier.api.annotation.processor.register;

import com.frontier.api.annotation.processor.annotation.immutables.FrontierRepositoryIdentity;
import com.frontier.api.annotation.processor.annotation.immutables.Guarantee;
import com.frontier.api.annotation.processor.annotation.service.FrontierRepositoryCacheService;
import com.frontier.api.annotation.processor.register.immutables.FrontierDestination;
import com.frontier.api.annotation.processor.register.immutables.FrontierRegisterNode;
import com.frontier.api.annotation.processor.register.immutables.FrontierRegisterRequestMessage;
import com.frontier.api.annotation.processor.register.immutables.FrontierRegisterResponseMessage;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
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
          .flatMap(f -> f.getProperties().stream().map(p -> FrontierRegisterNode.builder()
              .beanName(f.getBeanName())
              .methodName(p.getMethodName())
              .guarantee(p.getGuarantee().getName())
              .build()))
          .collect(ImmutableList.toImmutableList());

  private Map<FrontierDestination, List<FrontierRegisterNode>> cachedFrontierIdentitiesByServiceName = new HashMap<>();

  public FrontierRegisterService(
      FrontierRepositoryCacheService frontierRepositoryCacheService) {
    this.frontierRepositoryCacheService = frontierRepositoryCacheService;
  }

  private void registerRequest() {
    cachedFrontierIdentitiesByServiceName = ImmutableMap.of(
        FrontierDestination.builder()
            .name("http://localhost:8080")
            .build(), ImmutableList.of(FrontierRegisterNode.builder()
            .beanName("VisitRepository")
            .methodName("findByPetId")
            .guarantee("synchronous")
            .build()),
        FrontierDestination.builder()
            .name("petclinic")
            .build(),
        ImmutableList.of(FrontierRegisterNode.builder()
            .beanName("VisitRepository")
            .methodName("updateVisit")
            .guarantee("asynchronous")
            .build()));

    RestTemplate restTemplate = new RestTemplate();

    List<FrontierRegisterNode> frontierRegisterNodes = FRONTIER_REGISTER_ADAPTER_FUNC
        .apply(frontierRepositoryCacheService.finalizeFrontierRepositoryBuild());

    FrontierRegisterRequestMessage registerRequestMessage = FrontierRegisterRequestMessage
        .builder()
        .serviceName(serviceName)
        .frontierRegisterNodeBatch(frontierRegisterNodes)
        .build();

    ResponseEntity<FrontierRegisterResponseMessage> response = restTemplate
        .postForEntity(FRONTIER_API_ENDPOINT + "/register",
            registerRequestMessage,
            FrontierRegisterResponseMessage.class);

    cachedFrontierIdentitiesByServiceName = Objects.requireNonNull(response.getBody())
        .getFrontierIdentitiesByServiceName();
  }

  public Optional<Pair<FrontierDestination, FrontierRegisterNode>> resolveFrontierDestination(
      String beanName, String methodName, Guarantee guarantee) {

    //TODO Register should be the end of spring bean life cycle
    registerRequest();

    return cachedFrontierIdentitiesByServiceName.keySet().stream()
        .flatMap(a ->
            cachedFrontierIdentitiesByServiceName.get(a).stream()
                .filter(
                    c -> StringUtils.containsIgnoreCase(beanName, c.getBeanName())
                        && StringUtils.containsIgnoreCase(methodName, c.getMethodName())
                        && StringUtils.containsIgnoreCase(guarantee.getName(), c.getGuarantee()))
                .map(d -> Stream.of(Pair.of(a, d)))
                .findFirst()
                .orElseGet(Stream::empty))
        .findFirst();
  }

  @Scheduled(fixedDelay = 1000, initialDelay = 1000)
  public void cacheRefresh() {
    RestTemplate restTemplate = new RestTemplate();

    ResponseEntity<FrontierRegisterResponseMessage> response = restTemplate
        .getForEntity(FRONTIER_API_ENDPOINT + "/pull", FrontierRegisterResponseMessage.class);

    cachedFrontierIdentitiesByServiceName = Objects.requireNonNull(response.getBody())
        .getFrontierIdentitiesByServiceName();
  }
}
