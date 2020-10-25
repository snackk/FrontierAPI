package com.frontier.api.annotation.processor.annotation.provider.properties;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.DependsOn;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
@DependsOn("frontierRepositoryWrapperService")
public @interface FrontierProperties {

  String guarantee();
}

