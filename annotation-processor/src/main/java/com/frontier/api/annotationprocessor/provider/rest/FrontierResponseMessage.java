package com.frontier.api.annotationprocessor.provider.rest;

import java.util.Optional;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;
import org.springframework.http.HttpStatus;

@Builder
@Getter
@EqualsAndHashCode
@Value
public class FrontierResponseMessage<U> {

  private U response;

  @Builder.Default
  private HttpStatus status = HttpStatus.OK;

  @Builder.Default
  private Optional<String> verboseErrorMessage = Optional.empty();
}
