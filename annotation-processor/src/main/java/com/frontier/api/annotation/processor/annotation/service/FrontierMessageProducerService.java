package com.frontier.api.annotation.processor.annotation.service;

import com.frontier.api.annotation.processor.annotation.immutables.Guarantee;
import com.frontier.api.annotation.processor.annotation.immutables.Guarantee.FrontierMessageProducerVisitor;
import com.frontier.api.annotation.processor.api.amqp.FrontierAMQPProducer;
import com.frontier.api.annotation.processor.api.immutables.FrontierApiIdentity;
import com.frontier.api.annotation.processor.api.immutables.FrontierApiResponseMessage;
import com.frontier.api.annotation.processor.api.rest.FrontierRestController;
import com.frontier.api.annotation.processor.exception.FrontierUnrecoverableException;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class FrontierMessageProducerService implements
    FrontierMessageProducerVisitor<Object, FrontierApiIdentity, Set<Object>> {

  private final FrontierRestController frontierProviderController;
  private final FrontierAMQPProducer frontierAMQPProducer;

  public FrontierMessageProducerService(
      FrontierRestController frontierProviderController,
      FrontierAMQPProducer frontierAMQPProducer) {
    this.frontierProviderController = frontierProviderController;
    this.frontierAMQPProducer = frontierAMQPProducer;
  }

  public Object process(final Guarantee guarantee,
      final FrontierApiIdentity identity,
      Set<Object> params) {
    return guarantee.accept(this, identity, params);
  }

  @Override
  public Object produceSynchronous(FrontierApiIdentity identity, Set<Object> params) {
    FrontierApiResponseMessage frontierApiResponseMessage = frontierProviderController
        .produceMessage(identity, params);
    if (frontierApiResponseMessage.getStatus() != HttpStatus.OK) {
      return frontierApiResponseMessage.getResponse();
    } else {
      throw new FrontierUnrecoverableException(
          (String) frontierApiResponseMessage.getVerboseErrorMessage().get());
    }
  }

  @Override
  public Object produceBestEffort(FrontierApiIdentity identity, Set<Object> params) {
    return null;
  }

  @Override
  public Object produceAsynchronous(FrontierApiIdentity identity,
      Set<Object> params) {
    this.frontierAMQPProducer.produceMessage(identity, params);
    return null;
  }
}
