package com.frontier.api.annotationprocessor.provider.amqp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontier.api.annotationprocessor.provider.rest.FrontierRequestMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class FrontierProviderAMQPProducer {

  private final RabbitTemplate rabbitTemplate;

  public FrontierProviderAMQPProducer(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  public void produceMessage(FrontierRequestMessage requestBody,
      String frontierQueueName) {
    try {
      String json = new ObjectMapper().writeValueAsString(requestBody);
      rabbitTemplate.convertAndSend(frontierQueueName, "", json);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}
