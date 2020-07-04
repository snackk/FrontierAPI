package com.frontier.api.annotationprocessor.provider.rest;

import static com.frontier.api.annotationprocessor.provider.service.FrontierResourceErrorHandling.NO_FRONTIER_USAGE;

import com.frontier.api.annotationprocessor.domain.FrontierRepositoryWrapper;
import com.frontier.api.annotationprocessor.domain.FrontierRequestBody;
import com.frontier.api.annotationprocessor.domain.FrontierResponseBody;
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
import org.springframework.web.context.support.GenericWebApplicationContext;

@RestController
public class FrontierProviderController implements Ordered {

  private final Map<String, FrontierRequestHandler> controllersEndpoint = new HashMap<>();

  private final static String FRONTIER_ENDPOINT = "/api/frontier/";

  private final GenericWebApplicationContext context;

  @Autowired
  public FrontierProviderController(GenericWebApplicationContext context) {
    this.context = context;
  }

  @RequestMapping(FRONTIER_ENDPOINT + "/**")
  public ResponseEntity<FrontierResponseBody> index(HttpServletRequest request,
      HttpServletResponse response,
      @RequestBody FrontierRequestBody body) {

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
          //TODO Create REST OR SQS based on guarantee level
          //TODO Disable access to ALL methods, allow for those who have the annotation only
          CrudRepository crudRepository = (CrudRepository) context
              .getBean(key.getBeanName());
          controllersEndpoint.put(FRONTIER_ENDPOINT + key.getBeanName(),
              new FrontierRequestHandler(crudRepository));
        }));

    String requestURI = request.getRequestURI();

    FrontierResponseBody frontierResponseBody = FrontierResponseBody
        .builder()
        .response(
            Optional.of(
                controllersEndpoint
                    .entrySet()
                    .stream()
                    .filter(e -> requestURI.contains(e.getKey()))
                    .map(e -> e.getValue().doFrontierApiRequest(body))
                    .findFirst())
        )
        .build();

    return buildFrontierResponseEntity(frontierResponseBody);
  }

  private ResponseEntity<FrontierResponseBody> buildFrontierResponseEntity(
      FrontierResponseBody frontierResponseBody) {
    return new ResponseEntity<>(
        frontierResponseBody,
        frontierResponseBody.getStatus()
    );
  }

  @Override
  public int getOrder() {
    return LOWEST_PRECEDENCE;
  }
}
