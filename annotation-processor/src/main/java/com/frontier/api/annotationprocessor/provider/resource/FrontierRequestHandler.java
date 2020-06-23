package com.frontier.api.annotationprocessor.provider.resource;

import com.frontier.api.annotationprocessor.domain.FrontierRequestBody;
import java.util.function.IntFunction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class FrontierRequestHandler {

  private final CrudRepository crudRepository;

  public FrontierRequestHandler(CrudRepository crudRepository) {
    this.crudRepository = crudRepository;
  }

  protected ResponseEntity<Object> handleRequest(FrontierRequestBody body) {
    try {
      Class<?>[] paramTypes = body
          .getMethodParams()
          .stream()
          .map(Object::getClass)
          .toArray((IntFunction<Class<?>[]>) Class[]::new);

      Object[] paramValues = body.getMethodParams().toArray();

      return ResponseEntity.ok()
          .body(crudRepository.getClass().getMethod(body.getMethodName(), paramTypes)
              .invoke(crudRepository, paramValues));
    } catch (Exception e) {
      return new ResponseEntity<>(
          "Something went wrong in Frontier API while ingesting the payload: " + body,
          HttpStatus.METHOD_NOT_ALLOWED);
    }
  }
}
