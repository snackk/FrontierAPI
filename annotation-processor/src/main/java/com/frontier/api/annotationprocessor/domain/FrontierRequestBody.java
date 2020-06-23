package com.frontier.api.annotationprocessor.domain;

import java.util.Set;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class FrontierRequestBody {

  private String methodName;

  private Set<Object> methodParams;
}
