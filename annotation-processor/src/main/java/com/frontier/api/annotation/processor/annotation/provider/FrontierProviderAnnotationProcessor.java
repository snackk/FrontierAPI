package com.frontier.api.annotation.processor.annotation.provider;

import com.frontier.api.annotation.processor.annotation.immutables.FrontierRepositoryIdentity;
import com.frontier.api.annotation.processor.annotation.immutables.FrontierRepositoryProperty;
import com.frontier.api.annotation.processor.annotation.immutables.Guarantee;
import com.frontier.api.annotation.processor.annotation.service.FrontierRepositoryCacheService;
import com.frontier.api.annotation.processor.exception.FrontierUnrecoverableException;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class FrontierProviderAnnotationProcessor implements BeanPostProcessor, Ordered {

  private final static Logger LOG = LoggerFactory
      .getLogger(FrontierProviderAnnotationProcessor.class);

  private final FrontierRepositoryCacheService frontierRepositoryCacheService;
  private final ApplicationContext applicationContext;

  private final static String WRONG_GUARANTEE = "@Properties(guarantee = \"\"\n"
      + "Valid guarantees are: synchronous, asynchronous and best-effort.";

  public FrontierProviderAnnotationProcessor(
      FrontierRepositoryCacheService frontierRepositoryCacheService,
      ApplicationContext applicationContext) {
    this.frontierRepositoryCacheService = frontierRepositoryCacheService;
    this.applicationContext = applicationContext;
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName)
      throws BeansException {

    Optional<FrontierProviderRepository> repositoryAnnotationOpt = Optional.empty();
    try {
      repositoryAnnotationOpt = Optional.ofNullable(applicationContext
          .findAnnotationOnBean(beanName, FrontierProviderRepository.class));
    } catch (NoSuchBeanDefinitionException e) {
    }

    if (repositoryAnnotationOpt.isPresent()) {
      registerRepositoryWrapper(bean, beanName);
    }
    return bean;
  }

  private void registerRepositoryWrapper(Object bean, String beanName) {

    Class<?> springClass = Arrays.stream(((Advised) bean).getProxiedInterfaces())
        .filter(m -> m.isAnnotationPresent(FrontierProviderRepository.class))
        .findFirst()
        .get();

    List<FrontierRepositoryProperty> properties = Arrays.stream(springClass.getDeclaredMethods())
        .filter(m -> m.isAnnotationPresent(FrontierProperties.class))
        .map(method -> {
          Guarantee guarantee = Guarantee.getMethodGuarantee(
              method.getAnnotation(FrontierProperties.class).guarantee())
              .orElseThrow(() -> new FrontierUnrecoverableException(WRONG_GUARANTEE));

          return FrontierRepositoryProperty.builder()
              .guarantee(guarantee)
              .methodName(method.getName())
              .build();
        })
        .collect(ImmutableList.toImmutableList());

    FrontierRepositoryIdentity frontierRepositoryIdentity = FrontierRepositoryIdentity
        .builder()
        .packageName(springClass.getPackage().getName())
        .beanName(beanName)
        .properties(properties)
        .build();

    LOG.info("Registering Frontier Provider Repository {}", frontierRepositoryIdentity);

    frontierRepositoryCacheService.addFrontierRepositoryIdentity(frontierRepositoryIdentity);
  }


  @Override
  public int getOrder() {
    return HIGHEST_PRECEDENCE;
  }
}
