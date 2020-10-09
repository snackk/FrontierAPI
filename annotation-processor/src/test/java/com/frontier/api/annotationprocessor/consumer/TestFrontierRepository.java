package com.frontier.api.annotationprocessor.consumer;

import com.frontier.api.annotationprocessor.test.User;
import java.util.List;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.stereotype.Service;

@Service
@TestConfiguration
public class TestFrontierRepository {

  @FrontierConsumerRepository
  public List<User> findAllByEmail(String email) {
    return null;
  }

}
