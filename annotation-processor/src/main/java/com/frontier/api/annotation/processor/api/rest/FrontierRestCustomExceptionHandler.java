package com.frontier.api.annotation.processor.api.rest;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.frontier.api.annotation.processor.api.immutables.FrontierApiResponseMessage;
import com.frontier.api.annotation.processor.exception.FrontierRecoverableException;
import com.frontier.api.annotation.processor.exception.FrontierUnrecoverableException;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(assignableTypes = {FrontierRestController.class})
public class FrontierRestCustomExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler({FrontierUnrecoverableException.class})
  public ResponseEntity<FrontierApiResponseMessage> handleFrontierUnrecoverableException(
      FrontierUnrecoverableException ex,
      WebRequest request) {

    FrontierApiResponseMessage errorMessage = FrontierApiResponseMessage
        .builder()
        .verboseErrorMessage(
            Optional.of(ex.getMessage()))
        .status(INTERNAL_SERVER_ERROR)
        .build();

    return new ResponseEntity<>(errorMessage, new HttpHeaders(), errorMessage.getStatus());
  }

  @ExceptionHandler({FrontierRecoverableException.class})
  public ResponseEntity<FrontierApiResponseMessage> handleFrontierRecoverableException(
      FrontierRecoverableException ex,
      WebRequest request) {

    FrontierApiResponseMessage errorMessage = FrontierApiResponseMessage
        .builder()
        .verboseErrorMessage(
            Optional.of(ex.getMessage()))
        .status(INTERNAL_SERVER_ERROR)
        .build();

    return new ResponseEntity<>(errorMessage, new HttpHeaders(), errorMessage.getStatus());
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<FrontierApiResponseMessage> handleGenericException(
      Exception ex,
      WebRequest request) {

    FrontierApiResponseMessage errorMessage = FrontierApiResponseMessage
        .builder()
        .verboseErrorMessage(
            Optional.of("Something went wrong in Frontier API while ingesting the payload"))
        .status(INTERNAL_SERVER_ERROR)
        .build();

    return new ResponseEntity<>(errorMessage, new HttpHeaders(), errorMessage.getStatus());
  }

}
