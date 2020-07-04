package com.frontier.api.annotationprocessor.provider.properties;

import static com.frontier.api.annotationprocessor.provider.repository.FrontierProviderRepositoryAnnotationProcessor.BEAN_SUFFIX_NAME;

import com.frontier.api.annotationprocessor.domain.FrontierRepositoryIdentity;
import com.frontier.api.annotationprocessor.domain.FrontierRepositoryProperty;
import com.frontier.api.annotationprocessor.domain.FrontierRepositoryWrapper;
import com.frontier.api.annotationprocessor.domain.Guarantee;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.GenericWebApplicationContext;

@Component
public class FrontierPropertiesAnnotationProcessor implements BeanPostProcessor, Ordered {

  private final GenericWebApplicationContext context;

  public FrontierPropertiesAnnotationProcessor(GenericWebApplicationContext context) {
    this.context = context;
  }

  private final static String WRONG_GUARANTEE = "@Properties(guarantee = \"\"\n"
      + "Valid guarantees are: synchronous, asynchronous and best-effort.";

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

      repositoryInformation.getQueryMethods()
          .stream()
          .filter(m -> m.isAnnotationPresent(FrontierProperties.class))
          .forEach(m -> registerRepositoryProperties(m, repositoryInformation, beanName));
    }
  }

  private void registerRepositoryProperties(Method method,
      RepositoryInformation repositoryInformation, String beanName) {

    Guarantee guarantee = getMethodGuarantee(
        method.getAnnotation(FrontierProperties.class).guarantee())
        .orElseThrow(() -> new IllegalArgumentException(WRONG_GUARANTEE));

    String classPath = repositoryInformation.getRepositoryInterface().getName();
    String methodName = method.getName();

    FrontierRepositoryProperty frontierRepositoryProperty = FrontierRepositoryProperty.builder()
        .guarantee(guarantee)
        .methodName(methodName)
        .build();

    FrontierRepositoryWrapper frontierRepositoryWrapper = context
        .getBean(FrontierRepositoryWrapper.class);

    FrontierRepositoryIdentity frontierRepositoryIdentity = FrontierRepositoryIdentity.builder()
        .classpath(classPath)
        .beanName(beanName)
        .build();

    frontierRepositoryWrapper
        .addFrontierRepositoryProperty(frontierRepositoryIdentity, frontierRepositoryProperty);
  }

  private Optional<Guarantee> getMethodGuarantee(String guarantee) {
    return Arrays.stream(Guarantee.values())
        .filter(g -> g.getName().equals(guarantee))
        .findFirst();
  }

  @Override
  public int getOrder() {
    return 0;
  }
}
