package com.frontier.api.annotation.processor.executor;

import com.frontier.api.annotation.processor.annotation.provider.FrontierProviderRepository;
import com.frontier.api.annotation.processor.api.immutables.FrontierApiRequestMessage;
import com.frontier.api.annotation.processor.api.immutables.FrontierApiResponseMessage;
import com.frontier.api.annotation.processor.exception.FrontierUnrecoverableException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.IntFunction;
import org.springframework.aop.framework.Advised;
import org.springframework.data.repository.Repository;

public class FrontierExecutor {

  public static FrontierApiResponseMessage execute(
      Repository repository,
      FrontierApiRequestMessage body) {
    try {
      Class<?>[] paramTypes = body
          .getMethodParams()
          .stream()
          .map(Object::getClass)
          .toArray((IntFunction<Class<?>[]>) Class[]::new);

      Object[] paramValues = body.getMethodParams().toArray();

      Class<?> actualClass = Arrays.stream(((Advised) repository).getProxiedInterfaces())
          .filter(m -> m.isAnnotationPresent(FrontierProviderRepository.class))
          .findFirst()
          .get();

      Optional<Object> response = Optional
          .of(actualClass.getMethod(body.getMethodName(), paramTypes)
              .invoke(repository, paramValues));

      return FrontierApiResponseMessage
          .builder()
          .response(response)
          .build();

    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new FrontierUnrecoverableException("Reflection issue while invoking method.");
    }
  }
}
