package com.frontier.api.annotationprocessor.domain;

import java.util.Optional;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
@EqualsAndHashCode
public class FrontierResponseBody {

  private Optional<Object> response;

  @Builder.Default
  private HttpStatus status = HttpStatus.OK;

  private Optional<String> verboseErrorMessage;
}
