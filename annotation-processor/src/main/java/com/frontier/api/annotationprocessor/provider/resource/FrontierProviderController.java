package com.frontier.api.annotationprocessor.provider.resource;

import com.frontier.api.annotationprocessor.domain.FrontierRepositoryWrapper;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.GenericWebApplicationContext;

@RestController
public class FrontierProviderController {

  private final FrontierRepositoryWrapper frontierRepositoryWrapper;

  private final Map<String, FrontierRequestHandler> controllersEndpoint = new HashMap<>();

  private final static String FRONTIER_ENDPOINT = "api/frontier/";

  public FrontierProviderController(GenericWebApplicationContext context) {
    this.frontierRepositoryWrapper = context
        .getBean(FrontierRepositoryWrapper.class);
    frontierRepositoryWrapper.getFrontierRepositoryProperties().entrySet().stream()
        .forEach(beans -> {
          CrudRepository crudRepository = (CrudRepository) context
              .getBean(beans.getKey().getBeanName());
          controllersEndpoint.put(FRONTIER_ENDPOINT + beans.getKey().getBeanName(),
              new FrontierRequestHandler(crudRepository));
        });
  }


  @RequestMapping(FRONTIER_ENDPOINT + "/**")
  public ResponseEntity<Object> index(HttpServletRequest request, HttpServletResponse response,
      @RequestBody Object body) {
    String requestURI = request.getRequestURI();
    String method = request.getMethod();

    return controllersEndpoint.entrySet().stream()
        .filter(e -> requestURI.contains(e.getKey()))
        .map(e -> e.getValue().handleRequest(method, request, response, body))
        .findFirst()
        .orElse(new ResponseEntity<>(
            "No bean registered under Frontier API for the request: " + requestURI,
            HttpStatus.METHOD_NOT_ALLOWED));
  }
}
