package com.frontier.api.annotationprocessor.provider.amqp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontier.api.annotationprocessor.domain.FrontierRequestBody;
import com.frontier.api.annotationprocessor.domain.FrontierResponseBody;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FrontierProviderAMQPProducer {

  private final RabbitTemplate rabbitTemplate;

  private final String frontierQueueName;

  public FrontierProviderAMQPProducer(RabbitTemplate rabbitTemplate,
      @Value("${queue-name}") String frontierQueueName) {
    this.rabbitTemplate = rabbitTemplate;
    this.frontierQueueName = frontierQueueName;
  }

  public void produceResponse(FrontierResponseBody responseBody) {
    try {
      String json = new ObjectMapper().writeValueAsString(responseBody);
      rabbitTemplate.convertAndSend(frontierQueueName, "", json);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void produceRequest(FrontierRequestBody requestBody) {
    try {
      String json = new ObjectMapper().writeValueAsString(requestBody);
      rabbitTemplate.convertAndSend(frontierQueueName, "", json);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}
