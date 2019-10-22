package com.frontier.api.annotationprocessor.provider.wrapper;

import com.frontier.api.annotationprocessor.domain.FrontierRepositoryWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.web.context.support.GenericWebApplicationContext;

public class FrontierProviderStageWrapper implements BeanPostProcessor, Ordered {

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
    FrontierRepositoryWrapper frontierRepositoryWrapper = context
        .getBean(FrontierRepositoryWrapper.class);
    frontierRepositoryWrapper.getFrontierRepositoryProperties().entrySet().stream()
        .forEach(c -> {
          //TODO
          // For each class we should:
          // - create a new @RestController dynamically
          // - pass the Generic CrudRepository as an argument
          // - define each method, with the resource endpoint. Method should call the same method name defined in CrudRepository
          // - Build a request with method name and such as payload and send it to the API of Frontier
        });
    return bean;
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
