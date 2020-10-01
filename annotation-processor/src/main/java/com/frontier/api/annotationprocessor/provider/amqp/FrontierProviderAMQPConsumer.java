package com.frontier.api.annotationprocessor.provider.amqp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontier.api.annotationprocessor.domain.FrontierRepositoryProperty;
import com.frontier.api.annotationprocessor.domain.FrontierRepositoryWrapper;
import com.frontier.api.annotationprocessor.domain.Guarantee;
import com.frontier.api.annotationprocessor.provider.rest.FrontierRequestMessage;
import com.frontier.api.annotationprocessor.provider.service.FrontierRequestHandler;
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
    Optional<FrontierRequestMessage> requestBodyOpt = Optional.empty();
    try {
      requestBodyOpt = Optional
          .of(new ObjectMapper().readValue(jsonPayload, FrontierRequestMessage.class));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    if (!requestBodyOpt.isPresent()) {
      return;
    }

    FrontierRequestMessage requestBody = requestBodyOpt.get();
    Optional<FrontierRepositoryWrapper> frontierRepositoryWrapperOpt = Optional.empty();
    try {
      frontierRepositoryWrapperOpt = Optional.of(context
          .getBean(FrontierRepositoryWrapper.class));
    } catch (NoSuchBeanDefinitionException e) {
    }

    frontierRepositoryWrapperOpt.ifPresent(frw -> frw.getFrontierRepositoryProperties()
        .forEach((key, value) -> {
          if (value.stream().map(FrontierRepositoryProperty::getGuarantee)
              .anyMatch(p -> p.equals(Guarantee.ASYNCHRONOUS) || p.equals(Guarantee.BEST_EFFORT))) {
            CrudRepository crudRepository = (CrudRepository) context
                .getBean(key.getBeanName());
            FrontierRequestHandler frontierRequestHandler = new FrontierRequestHandler(
                crudRepository);
            frontierRequestHandler.doFrontierApiRequest(requestBody);
          }
        }));
  }
}
