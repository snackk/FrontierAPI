package com.frontier.api.annotation.processor.controller.rest;

import com.frontier.api.annotation.processor.immutables.api.FrontierAPIInterface;
import com.frontier.api.annotation.processor.immutables.api.FrontierApiIdentity;
import com.frontier.api.annotation.processor.immutables.api.FrontierApiRequestMessage;
import com.frontier.api.annotation.processor.immutables.api.FrontierApiResponseMessage;
import com.frontier.api.annotation.processor.immutables.domain.FrontierRepositoryProperty;
import com.frontier.api.annotation.processor.immutables.domain.Guarantee;
import com.frontier.api.annotation.processor.service.FrontierApiRegisterService;
import com.frontier.api.annotation.processor.service.FrontierRepositoryWrapperService;
import com.frontier.api.annotation.processor.service.FrontierRequestService;
import com.frontier.api.annotation.processor.service.FrontierResourceErrorHandling;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.support.GenericWebApplicationContext;

@RestController
public class FrontierAPIController implements Ordered,
    FrontierAPIInterface<FrontierApiResponseMessage> {

  private final static String FRONTIER_ENDPOINT = "/api/frontier/";
  private final static Function<Pair<String, FrontierApiIdentity>, String> FRONTIER_API_BUILDER = (a) ->
      a.getLeft()
          + FRONTIER_ENDPOINT + a.getRight().getBeanName();

  private final Map<String, FrontierRequestService> controllersEndpoint = new HashMap<>();
  private final GenericWebApplicationContext context;
  private final FrontierApiRegisterService frontierAPIRegisterService;

  @Autowired
  public FrontierAPIController(GenericWebApplicationContext context,
      FrontierApiRegisterService frontierAPIRegisterService) {
    this.context = context;
    this.frontierAPIRegisterService = frontierAPIRegisterService;
  }

  public FrontierApiResponseMessage produceMessage(
      FrontierApiIdentity frontierApiIdentity, Set<Object> methodParams) {

    //TODO Fix get here...handle cache miss
    Pair<String, FrontierApiIdentity> foundFrontierServiceIdentity = this.frontierAPIRegisterService
        .resolveServiceName(frontierApiIdentity.getBeanName(), frontierApiIdentity.getMethodName(),
            Guarantee.SYNCHRONOUS)
        .get();

    RestTemplate restTemplate = new RestTemplate();

    FrontierApiRequestMessage frontierApiRequestMessage = FrontierApiRequestMessage.builder()
        .beanName(foundFrontierServiceIdentity.getRight().getBeanName())
        .methodName(foundFrontierServiceIdentity.getRight().getMethodName())
        .methodParams(methodParams)
        .build();

    ResponseEntity<FrontierApiResponseMessage> response = restTemplate
        .postForEntity(FRONTIER_API_BUILDER.apply(foundFrontierServiceIdentity),
            frontierApiRequestMessage, FrontierApiResponseMessage.class);

    return response.getBody();
  }

  @RequestMapping(FRONTIER_ENDPOINT + "/**")
  public ResponseEntity<FrontierApiResponseMessage> handleRequests(HttpServletRequest request,
      HttpServletResponse response,
      @RequestBody FrontierApiRequestMessage body) {

    Optional<FrontierRepositoryWrapperService> frontierRepositoryWrapperOpt = Optional.empty();
    try {
      frontierRepositoryWrapperOpt = Optional.of(context
          .getBean(FrontierRepositoryWrapperService.class));
    } catch (NoSuchBeanDefinitionException e) {
    }

    if (!frontierRepositoryWrapperOpt.isPresent()) {
      return buildFrontierResponseEntity(FrontierResourceErrorHandling.NO_FRONTIER_USAGE);
    }

    frontierRepositoryWrapperOpt.ifPresent(frw -> frw.getFrontierRepositoryProperties()
        .forEach((key, value) -> {
          if (value.stream().map(FrontierRepositoryProperty::getGuarantee)
              .anyMatch(p -> p.equals(Guarantee.SYNCHRONOUS))) {
            CrudRepository crudRepository = (CrudRepository) context
                .getBean(key.getBeanName());
            controllersEndpoint.put(FRONTIER_ENDPOINT + key.getBeanName(),
                new FrontierRequestService(crudRepository));
          }
        }));

    String requestURI = request.getRequestURI();

    FrontierApiResponseMessage frontierApiResponseMessage =
        controllersEndpoint
            .entrySet()
            .stream()
            .filter(e -> requestURI.contains(e.getKey()))
            .map(e -> e.getValue().doFrontierApiRequest(body))
            .findFirst()
            .orElse(FrontierResourceErrorHandling.FRONTIER_PROCESSOR_ERROR);

    return buildFrontierResponseEntity(frontierApiResponseMessage);
  }

  private ResponseEntity<FrontierApiResponseMessage> buildFrontierResponseEntity(
      FrontierApiResponseMessage frontierApiResponseMessage) {
    return new ResponseEntity<>(
        frontierApiResponseMessage,
        frontierApiResponseMessage.getStatus()
    );
  }

  @Override
  public int getOrder() {
    return LOWEST_PRECEDENCE;
  }
}
