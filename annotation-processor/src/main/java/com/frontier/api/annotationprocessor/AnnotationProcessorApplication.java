package com.frontier.api.annotationprocessor;

import com.frontier.api.annotationprocessor.domain.FrontierRepositoryWrapper;
import com.frontier.api.annotationprocessor.test.TestFrontierRepository;
import com.frontier.api.annotationprocessor.test.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.context.support.GenericWebApplicationContext;

@SpringBootApplication
public class AnnotationProcessorApplication implements CommandLineRunner {

  private TestFrontierRepository testRepository;

  private GenericWebApplicationContext context;

  public AnnotationProcessorApplication(TestFrontierRepository testRepository,
      GenericWebApplicationContext context) {
    this.testRepository = testRepository;
    this.context = context;
  }

  public static void main(String[] args) {
    SpringApplication.run(AnnotationProcessorApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    Iterable<User> all = testRepository.findAllByEmail("email@email.pt");
    User user = all.iterator().next();
    System.out.println(user.getEmail() + " DONE");
    FrontierRepositoryWrapper wrapper = (FrontierRepositoryWrapper) context
        .getBean("FrontierRepository");

    CrudRepository bean1 = (CrudRepository) context.getBean(wrapper.getName());
    bean1.findAll();

  }
}
