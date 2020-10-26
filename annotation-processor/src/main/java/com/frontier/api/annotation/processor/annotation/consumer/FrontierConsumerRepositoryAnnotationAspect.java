package com.frontier.api.annotation.processor.annotation.consumer;

import com.frontier.api.annotation.processor.controller.amqp.FrontierAPIAMQPProducer;
import com.frontier.api.annotation.processor.controller.rest.FrontierAPIController;
import com.frontier.api.annotation.processor.immutables.api.FrontierApiIdentity;
import com.frontier.api.annotation.processor.immutables.domain.Guarantee;
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

  private final FrontierAPIAMQPProducer frontierProviderAMQPProducer;
  private final FrontierAPIController frontierProviderController;
  private final ApplicationContext applicationContext;

  private final static String WRONG_GUARANTEE = "@Properties(guarantee = \"\"\n"
      + "Valid guarantees are: synchronous, asynchronous and best-effort.";

  public FrontierConsumerRepositoryAnnotationAspect(
      FrontierAPIAMQPProducer frontierProviderAMQPProducer,
      FrontierAPIController frontierProviderController,
      ApplicationContext context) {
    this.frontierProviderAMQPProducer = frontierProviderAMQPProducer;
    this.frontierProviderController = frontierProviderController;
    this.applicationContext = context;
  }

  @Around("@annotation(com.frontier.api.annotation.processor.annotation.consumer.FrontierConsumerRepository)")
  public Object frontierProxyRequest(ProceedingJoinPoint joinPoint) {

    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    applicationContext.getBeanNamesForType(method.getDeclaringClass());

    Guarantee guarantee = Guarantee.getMethodGuarantee(
        method.getAnnotation(FrontierConsumerRepository.class).guarantee())
        .orElseThrow(() -> new IllegalArgumentException(WRONG_GUARANTEE));

    FrontierApiIdentity frontierApiIdentity = FrontierApiIdentity.builder()
        .beanName(method.getDeclaringClass().getName())
        .methodName(method.getName())
        .guarantee(guarantee.toString())
        .build();

    if (guarantee.equals(Guarantee.SYNCHRONOUS)) {
      return frontierProviderController
          .produceMessage(frontierApiIdentity, ImmutableSet.copyOf(joinPoint.getArgs()))
          .getResponse();
    } else {
      return frontierProviderAMQPProducer
          .produceMessage(frontierApiIdentity, ImmutableSet.copyOf(joinPoint.getArgs()));
    }
  }
}
