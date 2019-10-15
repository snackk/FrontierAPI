package com.frontier.api.annotationprocessor;

import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import com.frontier.api.annotationprocessor.provider.properties.FrontierProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AnnotationProcessorApplicationTests {

  @FrontierProperties(guarantee = "best-effort")
  private TestDemo getTest() {
    return null;
  }

  @Test
  public void sample() {
    assertThat(getTest(), sameInstance(getTest()));
  }

}

class TestDemo {

  private final String testVar;

  public TestDemo(String testVar) {
    this.testVar = testVar;
  }

}
