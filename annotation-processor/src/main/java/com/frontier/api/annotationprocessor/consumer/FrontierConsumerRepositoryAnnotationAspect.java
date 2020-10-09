package com.frontier.api.annotationprocessor.consumer;

import com.frontier.api.annotationprocessor.api.FrontierApiIdentity;
import com.frontier.api.annotationprocessor.provider.amqp.FrontierProviderAMQPProducer;
import com.frontier.api.annotationprocessor.provider.rest.FrontierProviderController;
import com.google.common.collect.ImmutableSet;
import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FrontierConsumerRepositoryAnnotationAspect {

  private final FrontierProviderAMQPProducer frontierProviderAMQPProducer;
  private final FrontierProviderController frontierProviderController;
  private final ApplicationContext applicationContext;

  public FrontierConsumerRepositoryAnnotationAspect(
      FrontierProviderAMQPProducer frontierProviderAMQPProducer,
      FrontierProviderController frontierProviderController,
      ApplicationContext context) {
    this.frontierProviderAMQPProducer = frontierProviderAMQPProducer;
    this.frontierProviderController = frontierProviderController;
    this.applicationContext = context;
  }

  @Around("@annotation(FrontierConsumerRepository)")
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) {

    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    applicationContext.getBeanNamesForType(method.getDeclaringClass());

    FrontierApiIdentity frontierApiIdentity = FrontierApiIdentity.builder()
        .beanName(method.getDeclaringClass().getName())
        .methodName(method.getName())
        .build();

    return frontierProviderController
        .doFrontierRemoteRequest(frontierApiIdentity, ImmutableSet.copyOf(joinPoint.getArgs()))
        .getResponse();
  }

}
