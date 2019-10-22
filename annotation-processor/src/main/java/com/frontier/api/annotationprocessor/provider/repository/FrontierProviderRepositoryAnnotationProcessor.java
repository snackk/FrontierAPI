package com.frontier.api.annotationprocessor.provider.repository;

import com.frontier.api.annotationprocessor.domain.FrontierRepositoryWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.GenericWebApplicationContext;

@Component
public class FrontierProviderRepositoryAnnotationProcessor implements BeanPostProcessor, Ordered {

  @Autowired
  private GenericWebApplicationContext context;

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName)
      throws BeansException {
    if (beanName.contains("FrontierRepository")) {
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
        context.registerBean(FrontierRepositoryWrapper.class,
            () -> new FrontierRepositoryWrapper(beanName, repositoryInformation.getDomainType(),
                repositoryInformation.getIdType()));
      }
    }
  }

  @Override
  public int getOrder() {
    return 0;
  }
}
