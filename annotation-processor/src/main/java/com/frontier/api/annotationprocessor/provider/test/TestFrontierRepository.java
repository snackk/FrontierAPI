package com.frontier.api.annotationprocessor.provider.test;

import com.frontier.api.annotationprocessor.provider.properties.FrontierProperties;
import com.frontier.api.annotationprocessor.provider.repository.FrontierProviderRepository;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

@FrontierProviderRepository
public interface TestFrontierRepository extends CrudRepository<User, Long> {

  @FrontierProperties(guarantee = "synchronous")
  List<User> findAllByEmail(String email);

}
