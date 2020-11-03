package com.frontier.api.annotation.processor.annotation.provider.properties;

import static com.frontier.api.annotation.processor.annotation.provider.repository.FrontierProviderRepositoryAnnotationProcessor.BEAN_SUFFIX_NAME;

import com.frontier.api.annotation.processor.immutables.domain.FrontierRepositoryIdentity;
import com.frontier.api.annotation.processor.immutables.domain.FrontierRepositoryProperty;
import com.frontier.api.annotation.processor.immutables.domain.Guarantee;
import com.frontier.api.annotation.processor.service.FrontierApiRegisterService;
import com.frontier.api.annotation.processor.service.FrontierRepositoryWrapperService;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.stereotype.Component;

//@Component
public class FrontierPropertiesAnnotationProcessor implements BeanPostProcessor, Ordered {

  private final FrontierRepositoryWrapperService frontierRepositoryWrapperService;
  private final FrontierApiRegisterService frontierAPIRegisterService;

  private final static String WRONG_GUARANTEE = "@Properties(guarantee = \"\"\n"
      + "Valid guarantees are: synchronous, asynchronous and best-effort.";

  private final static Logger LOG = LoggerFactory
      .getLogger(FrontierPropertiesAnnotationProcessor.class);

  public FrontierPropertiesAnnotationProcessor(
      FrontierRepositoryWrapperService frontierRepositoryWrapperService,
      FrontierApiRegisterService frontierAPIRegisterService) {
    this.frontierRepositoryWrapperService = frontierRepositoryWrapperService;
    this.frontierAPIRegisterService = frontierAPIRegisterService;
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

      repositoryInformation.getQueryMethods()
          .stream()
          .filter(m -> m.isAnnotationPresent(FrontierProperties.class))
          .forEach(m -> registerRepositoryProperties(m, repositoryInformation, beanName));
    }
  }

  private void registerRepositoryProperties(Method method,
      RepositoryInformation repositoryInformation, String beanName) {

    Guarantee guarantee = Guarantee.getMethodGuarantee(
        method.getAnnotation(FrontierProperties.class).guarantee())
        .orElseThrow(() -> new IllegalArgumentException(WRONG_GUARANTEE));

    String classPath = repositoryInformation.getRepositoryInterface().getName();
    String methodName = method.getName();

    FrontierRepositoryProperty frontierRepositoryProperty = FrontierRepositoryProperty.builder()
        .guarantee(guarantee)
        .methodName(methodName)
        .build();

    FrontierRepositoryIdentity frontierRepositoryIdentity = FrontierRepositoryIdentity.builder()
        .classpath(classPath)
        .beanName(beanName)
        .build();

    LOG.info("Registering Frontier properties {} for {}", frontierRepositoryProperty,
        frontierRepositoryIdentity);

    frontierRepositoryWrapperService
        .addFrontierRepositoryProperty(frontierRepositoryIdentity, frontierRepositoryProperty);

    this.frontierAPIRegisterService
        .cacheFrontierApiToRegister(beanName, methodName, guarantee);
  }

  @Override
  public int getOrder() {
    return 0;
  }
}
