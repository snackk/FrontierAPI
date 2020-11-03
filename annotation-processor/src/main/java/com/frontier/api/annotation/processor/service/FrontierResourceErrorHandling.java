package com.frontier.api.annotation.processor.service;

import com.frontier.api.annotation.processor.immutables.api.FrontierApiResponseMessage;
import java.util.Optional;
import org.springframework.http.HttpStatus;

public class FrontierResourceErrorHandling {

  private static final String FRONTIER_PROCESSOR_ERROR_STRING = "Something went wrong in Frontier API while ingesting the payload";

  public static final FrontierApiResponseMessage FRONTIER_PROCESSOR_ERROR = FrontierApiResponseMessage
      .builder()
      .verboseErrorMessage(
          Optional.of(FRONTIER_PROCESSOR_ERROR_STRING))
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .build();
}
