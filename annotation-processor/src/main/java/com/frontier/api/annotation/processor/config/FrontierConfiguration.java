package com.frontier.api.annotation.processor.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.frontier.api.annotation.processor")
public class FrontierConfiguration {

  public static final String SERVLET_BEAN_NAME = "frontierDispatcherServlet";
  public static final String SERVLET_BEAN_NAME_OBJ = "frontierDispatcherServletObj";
/*
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
  @ConditionalOnMissingBean(ServletWebServerFactory.class)
  ServletWebServerFactory servletWebServerFactory(){
    return new TomcatServletWebServerFactory(8090);
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
  }*/
/*
  @Autowired
  private ApplicationContext applicationContext;

  @Bean
  FrontierApiRegisterService frontierApiRegisterService() {
    return new FrontierApiRegisterService(frontierRepositoryCacheService());
  }

  @Bean
  FrontierRepositoryCacheService frontierRepositoryCacheService() {
    return new FrontierRepositoryCacheService();
  }

  @Bean
  FrontierProviderAnnotationProcessor frontierProviderRepositoryAnnotationProcessor() {
    return new FrontierProviderAnnotationProcessor(frontierRepositoryCacheService(),
        applicationContext);
  }


  //REST
  @Bean(name = SERVLET_BEAN_NAME)
  @Order
  public DispatcherServlet dispatcherServlet() {
    AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
    webContext.setParent(applicationContext);
    return new DispatcherServlet(webContext);
  }

  @Bean(name = SERVLET_BEAN_NAME_OBJ)
  @Order
  public ServletRegistrationBean dispatcherServletRegistration(
      DispatcherServlet dispatcherServlet) {

    ServletRegistrationBean registration = new ServletRegistrationBean(
        dispatcherServlet, "/test/*");

    registration
        .setName(SERVLET_BEAN_NAME_OBJ);

    return registration;
  }

  @Bean
  public FrontierAPIController coiso() {
    return new FrontierAPIController(applicationContext, frontierRepositoryCacheService(),
        frontierApiRegisterService());
  }*/

}
