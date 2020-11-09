package com.frontier.api.annotation.processor.annotation.consumer;

import com.frontier.api.annotation.processor.annotation.immutables.Guarantee;
import com.frontier.api.annotation.processor.annotation.service.FrontierMessageProducerService;
import com.frontier.api.annotation.processor.api.immutables.FrontierApiIdentity;
import com.google.common.collect.ImmutableSet;
import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FrontierConsumerRepositoryAnnotationAspect {

  private final FrontierMessageProducerService frontierMessageProducerService;

  public FrontierConsumerRepositoryAnnotationAspect(
      FrontierMessageProducerService frontierMessageProducerService) {
    this.frontierMessageProducerService = frontierMessageProducerService;
  }

  @Around("@annotation(com.frontier.api.annotation.processor.annotation.consumer.FrontierConsumerRepository)")
  public Object frontierProxyRequest(ProceedingJoinPoint joinPoint) {

    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    //Safe to do a get here, we validate it in compile time
    Guarantee guarantee = Guarantee.getMethodGuarantee(
        method.getAnnotation(FrontierConsumerRepository.class).guarantee())
        .get();

    FrontierApiIdentity frontierApiIdentity = FrontierApiIdentity.builder()
        .beanName(method.getDeclaringClass().getName())
        .methodName(method.getName())
        .guarantee(guarantee.toString())
        .build();

    return frontierMessageProducerService
        .process(guarantee, frontierApiIdentity, ImmutableSet.copyOf(joinPoint.getArgs()));
  }
}
