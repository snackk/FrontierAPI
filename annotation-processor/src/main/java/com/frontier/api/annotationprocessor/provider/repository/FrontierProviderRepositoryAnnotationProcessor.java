package com.frontier.api.annotationprocessor.provider.repository;

import com.frontier.api.annotationprocessor.domain.FrontierRepositoryIdentity;
import com.frontier.api.annotationprocessor.domain.FrontierRepositoryWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.GenericWebApplicationContext;

@Component
public class FrontierProviderRepositoryAnnotationProcessor implements BeanPostProcessor, Ordered {

  private final GenericWebApplicationContext context;

  private final static String BEAN_SUFFIX_NAME = "FrontierRepository";

  public FrontierProviderRepositoryAnnotationProcessor(GenericWebApplicationContext context) {
    this.context = context;
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

        //TODO Bean should already be present -FrontierRepositoryWrapper-, we should just append new Identities into it
        context.registerBean(FrontierRepositoryWrapper.class,
            () -> new FrontierRepositoryWrapper(frontierRepositoryIdentity));
      }
    }
  }

  @Override
  public int getOrder() {
    return 0;
  }
}
