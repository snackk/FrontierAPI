package com.frontier.api.annotation.processor.service;

import com.frontier.api.annotation.processor.immutables.api.FrontierApiResponseMessage;
import java.util.Optional;
import org.springframework.http.HttpStatus;

public class FrontierResourceErrorHandling {

  public static final String NO_FRONTIER_USAGE_STRING = "No Frontier annotation found. Check the documentation for usage.";
  public static final String FRONTIER_PROCESSOR_ERROR_STRING = "Something went wrong in Frontier API while ingesting the payload";

  public static final FrontierApiResponseMessage NO_FRONTIER_USAGE = FrontierApiResponseMessage
      .builder()
      .verboseErrorMessage(
          Optional.of(NO_FRONTIER_USAGE_STRING))
      .status(HttpStatus.METHOD_NOT_ALLOWED)
      .build();

  public static final FrontierApiResponseMessage FRONTIER_PROCESSOR_ERROR = FrontierApiResponseMessage
      .builder()
      .verboseErrorMessage(
          Optional.of(FRONTIER_PROCESSOR_ERROR_STRING))
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .build();

}
