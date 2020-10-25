package com.frontier.api.annotation.processor.service;

import static com.frontier.api.annotation.processor.service.FrontierResourceErrorHandling.FRONTIER_PROCESSOR_ERROR;

import com.frontier.api.annotation.processor.immutables.api.FrontierApiRequestMessage;
import com.frontier.api.annotation.processor.immutables.api.FrontierApiResponseMessage;
import java.util.Optional;
import java.util.function.IntFunction;
import org.springframework.data.repository.CrudRepository;

public class FrontierRequestService {

  private final CrudRepository crudRepository;

  public FrontierRequestService(CrudRepository crudRepository) {
    this.crudRepository = crudRepository;
  }

  public FrontierApiResponseMessage doFrontierApiRequest(FrontierApiRequestMessage body) {
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

      return FrontierApiResponseMessage
          .builder()
          .response(response)
          .build();

    } catch (Exception e) {
      return FRONTIER_PROCESSOR_ERROR;
    }
  }
}
