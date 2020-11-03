package com.frontier.api.annotation.processor.annotation.provider.repository;

import com.frontier.api.annotation.processor.immutables.domain.FrontierRepositoryIdentity;
import com.frontier.api.annotation.processor.service.FrontierRepositoryWrapperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.stereotype.Component;

//@Component
public class FrontierProviderRepositoryAnnotationProcessor implements BeanPostProcessor, Ordered {

  private final static Logger LOG = LoggerFactory
      .getLogger(FrontierProviderRepositoryAnnotationProcessor.class);

  private final FrontierRepositoryWrapperService frontierRepositoryWrapperService;

  public final static String BEAN_SUFFIX_NAME = "FrontierRepository";

  public FrontierProviderRepositoryAnnotationProcessor(
      FrontierRepositoryWrapperService frontierRepositoryWrapperService) {
    this.frontierRepositoryWrapperService = frontierRepositoryWrapperService;
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName)
      throws BeansException {
    if (beanName.contains(BEAN_SUFFIX_NAME)) {
      this.registerRepositoryWrapper(bean, beanName);
    }
    return bean;
  }

  private void registerRepositoryWrapper(Object bean, String beanName) {
    if (bean instanceof JpaRepositoryFactoryBean) {
      RepositoryInformation repositoryInformation = ((JpaRepositoryFactoryBean) bean)
          .getRepositoryInformation();
      if (repositoryInformation.getRepositoryInterface()
          .isAnnotationPresent(FrontierProviderRepository.class)) {

        FrontierRepositoryIdentity frontierRepositoryIdentity = FrontierRepositoryIdentity
            .builder()
            .classpath(repositoryInformation.getRepositoryInterface().getName())
            .beanName(beanName)
            .build();

        LOG.info("Registering Frontier repository {}", frontierRepositoryIdentity);

        frontierRepositoryWrapperService.addFrontierRepositoryIdentity(frontierRepositoryIdentity);
      }
    }
  }

  @Override
  public int getOrder() {
    return HIGHEST_PRECEDENCE;
  }
}
