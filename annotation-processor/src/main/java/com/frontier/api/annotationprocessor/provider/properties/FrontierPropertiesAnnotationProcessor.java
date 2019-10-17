package com.frontier.api.annotationprocessor.provider.properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

@Component
public class FrontierPropertiesAnnotationProcessor implements BeanPostProcessor, Ordered {

  private ConfigurableListableBeanFactory configurableBeanFactory;

  @Autowired
  public FrontierPropertiesAnnotationProcessor(ConfigurableListableBeanFactory beanFactory) {
    this.configurableBeanFactory = beanFactory;
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    this.scanDataAccessAnnotation(bean, beanName);
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName)
      throws BeansException {
    return bean;
  }

  protected void scanDataAccessAnnotation(Object bean, String beanName) {
    this.configureFieldInjection(bean);
  }

  private void configureFieldInjection(Object bean) {
    Class<?> managedBeanClass = bean.getClass();
    FieldCallback fieldCallback =
        new FrontierPropertiesFieldCallback(configurableBeanFactory, bean);
    ReflectionUtils.doWithFields(managedBeanClass, fieldCallback);
  }

  @Override
  public int getOrder() {
    return 1;
  }
}
