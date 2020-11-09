package com.frontier.api.annotation.processor.test;

import com.frontier.api.annotation.processor.annotation.provider.FrontierProperties;
import com.frontier.api.annotation.processor.annotation.provider.FrontierProviderRepository;
import java.util.List;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

@FrontierProviderRepository
@TestConfiguration
public interface TestFrontierRepository extends CrudRepository<User, Long> {

  @FrontierProperties(guarantee = "synchronous")
  List<User> findAllByEmail(String email);

  @FrontierProperties(guarantee = "asynchronous")
  @Modifying
  @Query(value =
      "insert into User (first_name, last_name, email) values (?1, ?2, ?3)",
      nativeQuery = true)
  void saveUser(String firstName, String lastName, String email);

}
