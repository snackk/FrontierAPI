package com.frontier.api.annotation.processor.test;

import com.frontier.api.annotation.processor.annotation.provider.properties.FrontierProperties;
import com.frontier.api.annotation.processor.annotation.provider.repository.FrontierProviderRepository;
import java.util.List;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.repository.CrudRepository;

@FrontierProviderRepository
@TestConfiguration
public interface TestFrontierRepository extends CrudRepository<User, Long> {

  @FrontierProperties(guarantee = "synchronous")
  List<User> findAllByEmail(String email);

}
