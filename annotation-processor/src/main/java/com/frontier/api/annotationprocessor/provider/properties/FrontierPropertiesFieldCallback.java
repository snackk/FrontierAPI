package com.frontier.api.annotationprocessor.provider.properties;

import com.frontier.api.annotationprocessor.Guarantee;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

public class FrontierPropertiesFieldCallback implements FieldCallback {

  //private static Logger logger = LoggerFactory.getLogger(FrontierPropertiesFieldCallback.class);

  private static int AUTOWIRE_MODE = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;

  private static String ERROR_WRONG_GUARANTEE_STRING = "@FrontierProperties(guarantee = \"\") "
      + "valid strings are: synchronous, asynchronous and best-effort";

  private ConfigurableListableBeanFactory configurableBeanFactory;
  private Object bean;

  public FrontierPropertiesFieldCallback(ConfigurableListableBeanFactory bf, Object bean) {
    configurableBeanFactory = bf;
    this.bean = bean;
  }

  @Override
  public void doWith(Field field)
      throws IllegalArgumentException, IllegalAccessException {
    if (!field.isAnnotationPresent(FrontierProperties.class)) {
      return;
    }
    ReflectionUtils.makeAccessible(field);
    Type fieldGenericType = field.getGenericType();

    Class<?> generic = field.getType();
    String guarantee = field.getDeclaredAnnotation(FrontierProperties.class).guarantee();

    if (isGuaranteeValid(guarantee)) {
/*      String beanName = generic.getSimpleName();
      Object beanInstance = getBeanInstance(beanName, generic, classValue);
      field.set(bean, beanInstance);*/
    } else {
      throw new IllegalArgumentException(ERROR_WRONG_GUARANTEE_STRING);
    }
  }

  public boolean isGuaranteeValid(String guarantee) {
    return Arrays.stream(Guarantee.values())
        .map(g -> g.getName())
        .anyMatch(g -> g.equals(guarantee));
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
