package com.frontier.api.annotation.processor.api.amqp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontier.api.annotation.processor.annotation.service.FrontierRepositoryCacheService;
import com.frontier.api.annotation.processor.api.immutables.FrontierApiRequestMessage;
import com.frontier.api.annotation.processor.exception.FrontierRecoverableException;
import com.frontier.api.annotation.processor.executor.FrontierExecutor;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Service;

@Service
public class FrontierAMQPConsumer {

  private final FrontierRepositoryCacheService frontierRepositoryCacheService;
  private final ApplicationContext applicationContext;

  public FrontierAMQPConsumer(
      ApplicationContext applicationContext,
      FrontierRepositoryCacheService frontierRepositoryCacheService) {
    this.frontierRepositoryCacheService = frontierRepositoryCacheService;
    this.applicationContext = applicationContext;
  }

  @RabbitListener(queues = "${frontier-rabbitmq-queue-name}")
  public void consumeMessage(String jsonPayload) throws FrontierRecoverableException {
    Optional<FrontierApiRequestMessage> bodyOpt = Optional.empty();
    try {
      bodyOpt = Optional
          .ofNullable(new ObjectMapper().readValue(jsonPayload, FrontierApiRequestMessage.class));
    } catch (JsonProcessingException e) {
      throw new FrontierRecoverableException("Error consuming RabbitMQ message.");
    }

    if (!bodyOpt.isPresent()) {
      return;
    }
    FrontierApiRequestMessage body = bodyOpt.get();
    frontierRepositoryCacheService.finalizeFrontierRepositoryBuild().stream()
        .filter(i -> StringUtils.containsIgnoreCase(body.getBeanName(), i.getBeanName()))
        .forEach(i -> {
          Repository repository = (Repository) applicationContext.getBean(i.getBeanName());
          FrontierExecutor.execute(repository, body);
        });
  }
}
