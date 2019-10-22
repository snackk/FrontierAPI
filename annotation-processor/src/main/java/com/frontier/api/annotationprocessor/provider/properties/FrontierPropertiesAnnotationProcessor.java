package com.frontier.api.annotationprocessor.provider.properties;

import com.frontier.api.annotationprocessor.domain.FrontierRepositoryProperty;
import com.frontier.api.annotationprocessor.domain.FrontierRepositoryWrapper;
import com.frontier.api.annotationprocessor.domain.Guarantee;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.GenericWebApplicationContext;

@Component
public class FrontierPropertiesAnnotationProcessor implements BeanPostProcessor, Ordered {

  @Autowired
  private GenericWebApplicationContext context;

  private final static String WRONG_GUARANTEE = "@Properties(guarantee = \"\"\n"
      + "Valid guarantees are: synchronous, asynchronous and bes-effort.";

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
      RepositoryInformation repositoryInformation = ((JpaRepositoryFactoryBean) bean)
          .getRepositoryInformation();
      repositoryInformation.getQueryMethods().stream()
          .filter(a -> a.isAnnotationPresent(FrontierProperties.class))
          .forEach(a -> doSomething(a, repositoryInformation));
    }
  }

  private void doSomething(Method method, RepositoryInformation repositoryInformation) {
    Guarantee guarantee = guaranteeIfValid(
        method.getAnnotation(FrontierProperties.class).guarantee())
        .orElseThrow(() -> new IllegalArgumentException(WRONG_GUARANTEE));
    String classPath = repositoryInformation.getRepositoryInterface().getName();
    String methodName = method.getName();

    FrontierRepositoryProperty frontierRepositoryProperty = new FrontierRepositoryProperty(
        guarantee, methodName);

    FrontierRepositoryWrapper frontierRepositoryWrapper = context
        .getBean(FrontierRepositoryWrapper.class);
    frontierRepositoryWrapper.addFrontierRepositoryProperty(classPath, frontierRepositoryProperty);
  }

  private Optional<Guarantee> guaranteeIfValid(String guarantee) {
    return Arrays.stream(Guarantee.values())
        .filter(g -> g.getName().equals(guarantee))
        .findFirst();
  }

  @Override
  public int getOrder() {
    return 1;
  }
}
