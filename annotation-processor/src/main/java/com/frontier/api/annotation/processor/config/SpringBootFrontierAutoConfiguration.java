package com.frontier.api.annotation.processor.config;

import com.frontier.api.annotation.processor.annotation.consumer.FrontierConsumerRepositoryAnnotationAspect;
import com.frontier.api.annotation.processor.annotation.provider.properties.FrontierPropertiesAnnotationProcessor;
import com.frontier.api.annotation.processor.annotation.provider.repository.FrontierProviderRepositoryAnnotationProcessor;
import com.frontier.api.annotation.processor.controller.amqp.FrontierAPIAMQPProducer;
import com.frontier.api.annotation.processor.controller.amqp.FrontierProviderAMQPConsumer;
import com.frontier.api.annotation.processor.controller.rest.FrontierAPIController;
import com.frontier.api.annotation.processor.service.FrontierApiRegisterService;
import com.frontier.api.annotation.processor.service.FrontierRepositoryWrapperService;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.support.GenericWebApplicationContext;

@Configuration
public class SpringBootFrontierAutoConfiguration {
  //@Scope(value = "prototype")

  @Value("${frontier-rabbitmq-queue-name}")
  private String QUEUE;

  @Value("${frontier-rabbitmq-queue-name}")
  private String EXCHANGE_NAME;

  private static String ROUTING_KEY = "";

  @Autowired
  private ApplicationContext applicationContext;

  @Bean
  @ConditionalOnMissingBean(RestTemplate.class)
  RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  ServletWebServerFactory servletWebServerFactory(){
    return new TomcatServletWebServerFactory();
  }

  @Bean
  public ConnectionFactory connectionFactory() {
    CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
    connectionFactory.setUsername("guest");
    connectionFactory.setPassword("guest");
    return connectionFactory;
  }

  @Bean
  public AmqpAdmin amqpAdmin() {
    return new RabbitAdmin(connectionFactory());
  }

  @Bean
  @ConditionalOnMissingBean(RabbitTemplate.class)
  public RabbitTemplate rabbitTemplate() {
    RabbitTemplate template = new RabbitTemplate(connectionFactory());
    //The routing key is set to the name of the queue by the broker for the default exchange.
    //template.setRoutingKey(QUEUE);
    //Where we will synchronously receive messages from
    //template.setDefaultReceiveQueue(QUEUE);
    return template;
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

  @Bean
  @ConditionalOnBean({RabbitTemplate.class, FrontierApiRegisterService.class})
  FrontierAPIAMQPProducer frontierAPIAMQPProducer() {
    return new FrontierAPIAMQPProducer(rabbitTemplate(), frontierApiRegisterService());
  }

  @Bean
  @ConditionalOnBean({GenericWebApplicationContext.class, FrontierRepositoryWrapperService.class})
  FrontierProviderRepositoryAnnotationProcessor frontierProviderRepositoryAnnotationProcessor() {
    return new FrontierProviderRepositoryAnnotationProcessor(frontierRepositoryWrapperService());
  }

  @Bean
  @ConditionalOnBean({GenericWebApplicationContext.class, FrontierRepositoryWrapperService.class})
  FrontierProviderAMQPConsumer frontierAPIAMQPConsumer() {
    return new FrontierProviderAMQPConsumer(applicationContext,
        frontierRepositoryWrapperService());
  }

  @Bean
  @ConditionalOnBean({FrontierRepositoryWrapperService.class, FrontierApiRegisterService.class})
  FrontierAPIController frontierAPIController() {
    return new FrontierAPIController(applicationContext, frontierRepositoryWrapperService(),
        frontierApiRegisterService());
  }

  @Bean
  @ConditionalOnBean({FrontierRepositoryWrapperService.class, FrontierApiRegisterService.class})
  FrontierPropertiesAnnotationProcessor frontierPropertiesAnnotationProcessor() {
    return new FrontierPropertiesAnnotationProcessor(frontierRepositoryWrapperService(),
        frontierApiRegisterService());
  }

  @Bean
  @ConditionalOnBean({FrontierAPIAMQPProducer.class, FrontierAPIController.class})
  FrontierConsumerRepositoryAnnotationAspect frontierConsumerRepositoryAnnotationAspect() {
    return new FrontierConsumerRepositoryAnnotationAspect(frontierAPIAMQPProducer(),
        frontierAPIController());
  }

  @Bean
  FrontierApiRegisterService frontierApiRegisterService() {
    return new FrontierApiRegisterService();
  }

  @Bean
  FrontierRepositoryWrapperService frontierRepositoryWrapperService() {
    return new FrontierRepositoryWrapperService();
  }

}
