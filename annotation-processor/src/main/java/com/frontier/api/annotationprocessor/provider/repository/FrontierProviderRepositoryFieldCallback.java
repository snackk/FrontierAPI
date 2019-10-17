package com.frontier.api.annotationprocessor.provider.repository;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

public class FrontierProviderRepositoryFieldCallback implements FieldCallback {

  private static int AUTOWIRE_MODE = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;

  private ConfigurableListableBeanFactory configurableBeanFactory;
  private Object bean;

  public FrontierProviderRepositoryFieldCallback(ConfigurableListableBeanFactory bf, Object bean) {
    configurableBeanFactory = bf;
    this.bean = bean;
  }

  @Override
  public void doWith(Field field)
      throws IllegalArgumentException, IllegalAccessException {

    if (!field.isAnnotationPresent(FrontierProviderRepository.class)) {
      return;
    }

    ReflectionUtils.makeAccessible(field);
/*
    Type fieldGenericType = field.getGenericType();

    Class<?> generic = field.getType();

    if (isGuaranteeValid(guarantee)) {
      String beanName = generic.getSimpleName();
      Object beanInstance = getBeanInstance(beanName, generic, classValue);
      field.set(bean, beanInstance);
    } else {
      throw new IllegalArgumentException(ERROR_WRONG_GUARANTEE_STRING);
    }
*/

    if (bean instanceof CrudRepository) { //or just Repository???
      CrudRepository repository = (CrudRepository) bean;
      GenericApplicationContext gac = new GenericApplicationContext();
      gac.registerBean("asd", RepositoryWrapper.class);
    }
  }

  public Object getBeanInstance(
      String beanName, Class<?> genericClass, Class<?> paramClass) {
    Object daoInstance = null;
    if (!configurableBeanFactory.containsBean(beanName)) {

      Object toRegister = null;
      try {
        Constructor<?> ctr = genericClass.getConstructor(Class.class);
        toRegister = ctr.newInstance(paramClass);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }

      daoInstance = configurableBeanFactory.initializeBean(toRegister, beanName);
      configurableBeanFactory.autowireBeanProperties(daoInstance, AUTOWIRE_MODE, true);
      configurableBeanFactory.registerSingleton(beanName, daoInstance);
    } else {
      daoInstance = configurableBeanFactory.getBean(beanName);
    }
    return daoInstance;
  }
}
