package com.frontier.api.annotationprocessor.provider.rest;

import static com.frontier.api.annotationprocessor.provider.service.FrontierResourceErrorHandling.FRONTIER_PROCESSOR_ERROR;
import static com.frontier.api.annotationprocessor.provider.service.FrontierResourceErrorHandling.NO_FRONTIER_USAGE;

import com.frontier.api.annotationprocessor.api.FrontierApiRegisterClient;
import com.frontier.api.annotationprocessor.domain.FrontierRepositoryProperty;
import com.frontier.api.annotationprocessor.domain.FrontierRepositoryWrapper;
import com.frontier.api.annotationprocessor.domain.Guarantee;
import com.frontier.api.annotationprocessor.provider.service.FrontierRequestHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class FrontierProviderController implements Ordered {

  private final Map<String, FrontierRequestHandler> controllersEndpoint = new HashMap<>();

  private final static String FRONTIER_ENDPOINT = "/api/frontier/";

  private final GenericWebApplicationContext context;

  private final FrontierApiRegisterClient frontierAPIRegisterClient;

  @Autowired
  public FrontierProviderController(GenericWebApplicationContext context,
      FrontierApiRegisterClient frontierAPIRegisterClient) {
    this.context = context;
    this.frontierAPIRegisterClient = frontierAPIRegisterClient;
  }

  public FrontierResponseMessage doFrontierRemoteRequest(
      int port, FrontierRequestMessage frontierRequestMessage) {
    //TODO remove port, and use serviceName
    Optional<String> serviceName = this.frontierAPIRegisterClient
        .resolveServiceName(frontierRequestMessage.getBeanName(),
            frontierRequestMessage.getMethodName());
    final String url =
        "http://localhost:" + port + FRONTIER_ENDPOINT + frontierRequestMessage.getBeanName();
    RestTemplate restTemplate = new RestTemplate();

    ResponseEntity<FrontierResponseMessage> response = restTemplate
        .postForEntity(url, frontierRequestMessage, FrontierResponseMessage.class);

    return response.getBody();
  }

  @RequestMapping(FRONTIER_ENDPOINT + "/**")
  public ResponseEntity<FrontierResponseMessage> handleRequests(HttpServletRequest request,
      HttpServletResponse response,
      @RequestBody FrontierRequestMessage body) {

    Optional<FrontierRepositoryWrapper> frontierRepositoryWrapperOpt = Optional.empty();
    try {
      frontierRepositoryWrapperOpt = Optional.of(context
          .getBean(FrontierRepositoryWrapper.class));
    } catch (NoSuchBeanDefinitionException e) {
    }

    if (!frontierRepositoryWrapperOpt.isPresent()) {
      return buildFrontierResponseEntity(NO_FRONTIER_USAGE);
    }

    frontierRepositoryWrapperOpt.ifPresent(frw -> frw.getFrontierRepositoryProperties()
        .forEach((key, value) -> {
          if (value.stream().map(FrontierRepositoryProperty::getGuarantee)
              .anyMatch(p -> p.equals(Guarantee.SYNCHRONOUS))) {
            CrudRepository crudRepository = (CrudRepository) context
                .getBean(key.getBeanName());
            controllersEndpoint.put(FRONTIER_ENDPOINT + key.getBeanName(),
                new FrontierRequestHandler(crudRepository));
          }
        }));

    String requestURI = request.getRequestURI();

    FrontierResponseMessage frontierResponseMessage =
        controllersEndpoint
            .entrySet()
            .stream()
            .filter(e -> requestURI.contains(e.getKey()))
            .map(e -> e.getValue().doFrontierApiRequest(body))
            .findFirst()
            .orElse(FRONTIER_PROCESSOR_ERROR);

    return buildFrontierResponseEntity(frontierResponseMessage);
  }

  private ResponseEntity<FrontierResponseMessage> buildFrontierResponseEntity(
      FrontierResponseMessage frontierResponseMessage) {
    return new ResponseEntity<>(
        frontierResponseMessage,
        frontierResponseMessage.getStatus()
    );
  }

  @Override
  public int getOrder() {
    return LOWEST_PRECEDENCE;
  }
}
