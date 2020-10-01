package com.frontier.api.annotationprocessor.provider.service;

import static com.frontier.api.annotationprocessor.provider.service.FrontierResourceErrorHandling.FRONTIER_PROCESSOR_ERROR;

import com.frontier.api.annotationprocessor.provider.rest.FrontierRequestMessage;
import com.frontier.api.annotationprocessor.provider.rest.FrontierResponseMessage;
import java.util.Optional;
import java.util.function.IntFunction;
import org.springframework.data.repository.CrudRepository;

public class FrontierRequestHandler {

  private final CrudRepository crudRepository;

  public FrontierRequestHandler(CrudRepository crudRepository) {
    this.crudRepository = crudRepository;
  }

  public FrontierResponseMessage doFrontierApiRequest(FrontierRequestMessage body) {
    try {
      Class<?>[] paramTypes = body
          .getMethodParams()
          .stream()
          .map(Object::getClass)
          .toArray((IntFunction<Class<?>[]>) Class[]::new);

      Object[] paramValues = body.getMethodParams().toArray();

      Optional<Object> response = Optional
          .of(crudRepository.getClass().getMethod(body.getMethodName(), paramTypes)
              .invoke(crudRepository, paramValues));

      return FrontierResponseMessage
          .builder()
          .response(response)
          .build();

    } catch (Exception e) {
      return FRONTIER_PROCESSOR_ERROR;
    }
  }
}
