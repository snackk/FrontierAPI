package com.frontier.api.annotation.processor.api.rest;

import static com.frontier.api.annotation.processor.annotation.immutables.Guarantee.SYNCHRONOUS;

import com.frontier.api.annotation.processor.annotation.service.FrontierRepositoryCacheService;
import com.frontier.api.annotation.processor.api.FrontierAPIInterface;
import com.frontier.api.annotation.processor.api.immutables.FrontierApiIdentity;
import com.frontier.api.annotation.processor.api.immutables.FrontierApiRequestMessage;
import com.frontier.api.annotation.processor.api.immutables.FrontierApiResponseMessage;
import com.frontier.api.annotation.processor.exception.FrontierRecoverableException;
import com.frontier.api.annotation.processor.exception.FrontierUnrecoverableException;
import com.frontier.api.annotation.processor.executor.FrontierExecutor;
import com.frontier.api.annotation.processor.register.FrontierRegisterService;
import java.util.Set;
import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.Repository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class FrontierRestController implements
    FrontierAPIInterface<FrontierApiResponseMessage, FrontierApiIdentity, Set<Object>> {

  private final static String FRONTIER_ENDPOINT = "/api/frontier/";
  private final static Function<Pair<String, FrontierApiIdentity>, String> FRONTIER_API_BUILDER =
      (a) -> a.getLeft() + FRONTIER_ENDPOINT + a.getRight().getBeanName();

  private final FrontierRepositoryCacheService frontierRepositoryCacheService;
  private final FrontierRegisterService frontierRegisterService;
  private final ApplicationContext applicationContext;

  public FrontierRestController(
      ApplicationContext applicationContext,
      FrontierRepositoryCacheService frontierRepositoryCacheService,
      FrontierRegisterService frontierRegisterService) {
    this.applicationContext = applicationContext;
    this.frontierRepositoryCacheService = frontierRepositoryCacheService;
    this.frontierRegisterService = frontierRegisterService;
  }

  public FrontierApiResponseMessage produceMessage(
      FrontierApiIdentity identity, Set<Object> params) {

    Pair<String, FrontierApiIdentity> foundFrontierServiceIdentity =
        this.frontierRegisterService
            .resolveServiceName(identity.getBeanName(),
                identity.getMethodName(),
                SYNCHRONOUS)
            .orElseThrow(() -> new FrontierUnrecoverableException("Cache Miss."));

    RestTemplate restTemplate = new RestTemplate();

    FrontierApiRequestMessage frontierApiRequestMessage = FrontierApiRequestMessage.builder()
        .beanName(foundFrontierServiceIdentity.getRight().getBeanName())
        .methodName(foundFrontierServiceIdentity.getRight().getMethodName())
        .methodParams(params)
        .build();

    ResponseEntity<FrontierApiResponseMessage> response = restTemplate
        .postForEntity(FRONTIER_API_BUILDER.apply(foundFrontierServiceIdentity),
            frontierApiRequestMessage, FrontierApiResponseMessage.class);

    return response.getBody();
  }

  @RequestMapping(FRONTIER_ENDPOINT + "/**")
  public ResponseEntity<FrontierApiResponseMessage> frontierHandler(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestBody FrontierApiRequestMessage body) throws FrontierRecoverableException {
    String requestURI = request.getRequestURI();

    return new ResponseEntity<>(
        frontierRepositoryCacheService.finalizeFrontierRepositoryBuild().stream()
            .filter(i -> StringUtils.containsIgnoreCase(requestURI, i.getBeanName()))
            .map(i -> {
              Repository repository = (Repository) applicationContext.getBean(i.getBeanName());
              return FrontierExecutor.execute(repository, body);
            })
            .findFirst()
            .orElseThrow(() -> new FrontierRecoverableException("No beans matching request.")),
        HttpStatus.OK);
  }
}
