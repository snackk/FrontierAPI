package com.frontier.api.annotation.processor.api.amqp;

import static com.frontier.api.annotation.processor.annotation.immutables.Guarantee.ASYNCHRONOUS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontier.api.annotation.processor.api.FrontierAPIInterface;
import com.frontier.api.annotation.processor.api.immutables.FrontierApiIdentity;
import com.frontier.api.annotation.processor.api.immutables.FrontierApiRequestMessage;
import com.frontier.api.annotation.processor.exception.FrontierUnrecoverableException;
import com.frontier.api.annotation.processor.register.FrontierRegisterService;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class FrontierAMQPProducer implements
    FrontierAPIInterface<Void, FrontierApiIdentity, Set<Object>> {

  private final RabbitTemplate rabbitTemplate;

  private final FrontierRegisterService frontierRegisterService;

  public FrontierAMQPProducer(RabbitTemplate rabbitTemplate,
      FrontierRegisterService frontierRegisterService) {
    this.rabbitTemplate = rabbitTemplate;
    this.frontierRegisterService = frontierRegisterService;
  }

  public void produceMessage(
      FrontierApiRequestMessage requestBody,
      String frontierQueueName) {
    try {
      String json = new ObjectMapper().writeValueAsString(requestBody);
      rabbitTemplate.convertAndSend(frontierQueueName, "", json);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Void produceMessage(FrontierApiIdentity identity,
      Set<Object> params) {

    Pair<String, FrontierApiIdentity> foundFrontierServiceIdentity =
        this.frontierRegisterService
            .resolveServiceName(identity.getBeanName(),
                identity.getMethodName(),
                ASYNCHRONOUS)
            .orElseThrow(() -> new FrontierUnrecoverableException("Cache Miss."));

    FrontierApiRequestMessage frontierApiRequestMessage = FrontierApiRequestMessage.builder()
        .beanName(foundFrontierServiceIdentity.getRight().getBeanName())
        .methodName(foundFrontierServiceIdentity.getRight().getMethodName())
        .methodParams(params)
        .build();

    try {
      String messagePayload = new ObjectMapper().writeValueAsString(frontierApiRequestMessage);
      rabbitTemplate.convertAndSend(foundFrontierServiceIdentity.getLeft(), "", messagePayload);
    } catch (JsonProcessingException e) {
      throw new FrontierUnrecoverableException("Error publishing RabbitMQ message.");
    }
    return null;
  }
}
