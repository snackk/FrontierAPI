package com.frontier.api.annotation.processor.api.immutables;

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
public class FrontierApiResponseMessage<U> {

  private U response;

  @Builder.Default
  private HttpStatus status = HttpStatus.OK;

  @Builder.Default
  private Optional<String> verboseErrorMessage = Optional.empty();
}
