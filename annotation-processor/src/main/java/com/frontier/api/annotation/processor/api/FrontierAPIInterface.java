package com.frontier.api.annotation.processor.api;

import com.frontier.api.annotation.processor.exception.FrontierRecoverableException;

public interface FrontierAPIInterface<R, I, P> {

  R produceMessage(I identity, P params) throws FrontierRecoverableException;

}

