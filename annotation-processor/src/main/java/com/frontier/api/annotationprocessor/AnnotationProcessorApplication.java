package com.frontier.api.annotationprocessor;

import com.frontier.api.annotationprocessor.test.TestFrontierRepository;
import com.frontier.api.annotationprocessor.test.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AnnotationProcessorApplication implements CommandLineRunner {

  private TestFrontierRepository testRepository;

  public AnnotationProcessorApplication(TestFrontierRepository testRepository) {
    this.testRepository = testRepository;
  }

  public static void main(String[] args) {
    SpringApplication.run(AnnotationProcessorApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    Iterable<User> all = testRepository.findAllByEmail("email@email.pt");
    User user = all.iterator().next();
    System.out.println(user.getEmail() + " DONE");
  }
}
