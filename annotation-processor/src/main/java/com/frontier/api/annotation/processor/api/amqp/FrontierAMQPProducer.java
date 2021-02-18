package com.frontier.api.annotation.processor.api.amqp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontier.api.annotation.processor.api.FrontierAPIInterface;
import com.frontier.api.annotation.processor.api.immutables.FrontierApiIdentity;
import com.frontier.api.annotation.processor.api.immutables.FrontierApiRequestMessage;
import com.frontier.api.annotation.processor.exception.FrontierUnrecoverableException;
import java.util.Set;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FrontierAMQPProducer implements
    FrontierAPIInterface<Void, FrontierApiIdentity, Set<Object>> {

  @Value("${frontier-queue:FRONTIER_QUEUE}")
  private String FRONTIER_QUEUE;

  private final RabbitTemplate rabbitTemplate;

  public FrontierAMQPProducer(
      RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Override
  public Void produceMessage(
      FrontierApiIdentity identity,
      Set<Object> params) {

    FrontierApiRequestMessage frontierApiRequestMessage = FrontierApiRequestMessage.builder()
        .beanName(identity.getBeanName())
        .methodName(identity.getMethodName())
        .methodParams(params)
        .build();

    try {
      String messagePayload = new ObjectMapper().writeValueAsString(frontierApiRequestMessage);
      rabbitTemplate
          .convertAndSend(FRONTIER_QUEUE, "", messagePayload);
    } catch (JsonProcessingException e) {
      throw new FrontierUnrecoverableException("Error publishing RabbitMQ message.");
    }
    return null;
  }
}
