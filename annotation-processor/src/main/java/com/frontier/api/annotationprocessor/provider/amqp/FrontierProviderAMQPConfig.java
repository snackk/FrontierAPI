package com.frontier.api.annotationprocessor.provider.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FrontierProviderAMQPConfig {

  public static String QUEUE;

  public static String EXCHANGE_NAME;

  public static String ROUTING_KEY = "";

  @Autowired
  public FrontierProviderAMQPConfig(@Value("${queue-name}") String frontierQueueName) {
    this.QUEUE = frontierQueueName;
    this.EXCHANGE_NAME = frontierQueueName;
  }

  @Bean
  public Exchange declareExchange() {
    return ExchangeBuilder
        .directExchange(EXCHANGE_NAME)
        .durable(true)
        .build();
  }

  @Bean
  public Queue declareQueue() {
    return QueueBuilder
        .durable(QUEUE)
        .build();
  }

  @Bean
  public Binding declareBinding(Exchange exchange, Queue queue) {
    return BindingBuilder
        .bind(queue)
        .to(exchange)
        .with(ROUTING_KEY)
        .noargs();
  }
}
