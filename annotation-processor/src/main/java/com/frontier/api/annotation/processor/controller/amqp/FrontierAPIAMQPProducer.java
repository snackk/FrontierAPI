package com.frontier.api.annotation.processor.controller.amqp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontier.api.annotation.processor.immutables.api.FrontierAPIInterface;
import com.frontier.api.annotation.processor.immutables.api.FrontierApiIdentity;
import com.frontier.api.annotation.processor.immutables.api.FrontierApiRequestMessage;
import com.frontier.api.annotation.processor.immutables.domain.Guarantee;
import com.frontier.api.annotation.processor.service.FrontierApiRegisterService;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class FrontierAPIAMQPProducer implements FrontierAPIInterface<Void> {

  private final RabbitTemplate rabbitTemplate;

  private final FrontierApiRegisterService frontierAPIRegisterService;

  public FrontierAPIAMQPProducer(RabbitTemplate rabbitTemplate,
      FrontierApiRegisterService frontierAPIRegisterService) {
    this.rabbitTemplate = rabbitTemplate;
    this.frontierAPIRegisterService = frontierAPIRegisterService;
  }

  public void produceMessage(FrontierApiRequestMessage requestBody,
      String frontierQueueName) {
    try {
      String json = new ObjectMapper().writeValueAsString(requestBody);
      rabbitTemplate.convertAndSend(frontierQueueName, "", json);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Void produceMessage(FrontierApiIdentity frontierApiIdentity,
      Set<Object> methodParams) {

    //TODO Fix get here...handle cache miss
    Pair<String, FrontierApiIdentity> foundFrontierServiceIdentity = this.frontierAPIRegisterService
        .resolveServiceName(frontierApiIdentity.getBeanName(), frontierApiIdentity.getMethodName(),
            Guarantee.ASYNCHRONOUS)
        .get();

    FrontierApiRequestMessage frontierApiRequestMessage = FrontierApiRequestMessage.builder()
        .beanName(foundFrontierServiceIdentity.getRight().getBeanName())
        .methodName(foundFrontierServiceIdentity.getRight().getMethodName())
        .methodParams(methodParams)
        .build();

    try {
      String messagePayload = new ObjectMapper().writeValueAsString(frontierApiRequestMessage);
      rabbitTemplate.convertAndSend(foundFrontierServiceIdentity.getLeft(), "", messagePayload);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }
}
