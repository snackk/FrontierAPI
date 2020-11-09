package com.frontier.api.annotation.processor.annotation.consumer;

import com.frontier.api.annotation.processor.test.User;
import java.util.List;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.stereotype.Service;

@Service
@TestConfiguration
public class TestFrontierRepository {

  @FrontierConsumerRepository(guarantee = "synchronous")
  public List<User> findAllByEmail(String email) {
    return null;
  }

  @FrontierConsumerRepository(guarantee = "asynchronous")
  public Void saveUser(String firstName, String lastName, String email) {
    return null;
  }

}
