package com.frontier.api.annotationprocessor.provider.service;

import com.frontier.api.annotationprocessor.provider.rest.FrontierResponseMessage;
import java.util.Optional;
import org.springframework.http.HttpStatus;

public class FrontierResourceErrorHandling {

  public static final String NO_FRONTIER_USAGE_STRING = "No Frontier annotation found. Check the documentation for usage.";
  public static final String FRONTIER_PROCESSOR_ERROR_STRING = "Something went wrong in Frontier API while ingesting the payload";

  public static final FrontierResponseMessage NO_FRONTIER_USAGE = FrontierResponseMessage
      .builder()
      .verboseErrorMessage(
          Optional.of(NO_FRONTIER_USAGE_STRING))
      .status(HttpStatus.METHOD_NOT_ALLOWED)
      .build();

  public static final FrontierResponseMessage FRONTIER_PROCESSOR_ERROR = FrontierResponseMessage
      .builder()
      .verboseErrorMessage(
          Optional.of(FRONTIER_PROCESSOR_ERROR_STRING))
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .build();

}
