package com.frontier.api.annotation.processor.controller.amqp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontier.api.annotation.processor.immutables.api.FrontierApiRequestMessage;
import com.frontier.api.annotation.processor.immutables.domain.FrontierRepositoryProperty;
import com.frontier.api.annotation.processor.immutables.domain.Guarantee;
import com.frontier.api.annotation.processor.service.FrontierRepositoryWrapperService;
import com.frontier.api.annotation.processor.service.FrontierRequestService;
import java.util.Optional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.GenericWebApplicationContext;

@Service
public class FrontierProviderAMQPConsumer {

  private final GenericWebApplicationContext context;

  public FrontierProviderAMQPConsumer(GenericWebApplicationContext context) {
    this.context = context;
  }

  @RabbitListener(queues = "${frontier-rabbitmq-queue-name}")
  public void consumeMessage(String jsonPayload) {
    Optional<FrontierApiRequestMessage> requestBodyOpt = Optional.empty();
    try {
      requestBodyOpt = Optional
          .of(new ObjectMapper().readValue(jsonPayload, FrontierApiRequestMessage.class));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    if (!requestBodyOpt.isPresent()) {
      return;
    }

    FrontierApiRequestMessage requestBody = requestBodyOpt.get();
    Optional<FrontierRepositoryWrapperService> frontierRepositoryWrapperOpt = Optional.empty();
    try {
      frontierRepositoryWrapperOpt = Optional.of(context
          .getBean(FrontierRepositoryWrapperService.class));
    } catch (NoSuchBeanDefinitionException e) {
    }

    frontierRepositoryWrapperOpt.ifPresent(frw -> frw.getFrontierRepositoryProperties()
        .forEach((key, value) -> {
          if (value.stream().map(FrontierRepositoryProperty::getGuarantee)
              .anyMatch(p -> p.equals(Guarantee.ASYNCHRONOUS) || p.equals(Guarantee.BEST_EFFORT))) {
            CrudRepository crudRepository = (CrudRepository) context
                .getBean(key.getBeanName());
            FrontierRequestService frontierRequestService = new FrontierRequestService(
                crudRepository);
            frontierRequestService.doFrontierApiRequest(requestBody);
          }
        }));
  }
}
