package com.frontier.api.annotationprocessor.provider.repository;

import com.frontier.api.annotationprocessor.domain.FrontierRepositoryWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public class FrontierProviderRepositoryAnnotationProcessor implements BeanPostProcessor, Ordered {

  private ConfigurableListableBeanFactory configurableBeanFactory;

  @Autowired
  public FrontierProviderRepositoryAnnotationProcessor(
      ConfigurableListableBeanFactory beanFactory) {
    this.configurableBeanFactory = beanFactory;
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName)
      throws BeansException {
    if (beanName.contains("FrontierRepository")) {
      this.registerRepositoryWrapper(bean);
    }
    return bean;
  }

  private void registerRepositoryWrapper(Object bean) {
    if (bean instanceof JpaRepositoryFactoryBean) {
      Class<?> repositoryBeanClass = ((JpaRepositoryFactoryBean) bean).getRepositoryInformation()
          .getRepositoryInterface();
      if (repositoryBeanClass.isAnnotationPresent(FrontierProviderRepository.class)) {
        CrudRepository repository = (CrudRepository) bean;
        GenericApplicationContext gac = new GenericApplicationContext();
        //TODO register a single bean here?
        gac.registerBean("FrontierRepository", FrontierRepositoryWrapper.class,
            () -> new FrontierRepositoryWrapper(repository));
      }
    }
  }

  @Override
  public int getOrder() {
    return 0;
  }
}
