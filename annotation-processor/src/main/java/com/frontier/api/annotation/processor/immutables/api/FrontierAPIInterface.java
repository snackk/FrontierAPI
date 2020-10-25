package com.frontier.api.annotation.processor.immutables.api;

import java.util.Set;

public interface FrontierAPIInterface<T> {

  T produceMessage(FrontierApiIdentity frontierApiIdentity, Set<Object> methodParams);

}

