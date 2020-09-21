package com.frontier.api.annotationprocessor;

import static com.frontier.api.annotationprocessor.provider.service.FrontierResourceErrorHandling.NO_FRONTIER_USAGE_STRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontier.api.annotationprocessor.domain.FrontierRequestBody;
import com.frontier.api.annotationprocessor.domain.FrontierResponseBody;
import com.frontier.api.annotationprocessor.provider.amqp.FrontierProviderAMQPProducer;
import com.frontier.api.annotationprocessor.test.TestFrontierRepository;
import com.frontier.api.annotationprocessor.test.User;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MySQLContainer;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestFrontierRepository.class)
public class AnnotationProcessorApplicationTests {

  @ClassRule
  public static MySQLContainer mySQLContainer = new MySQLContainer()
      .withDatabaseName("frontier-test-db")
      .withUsername("sa")
      .withPassword("sa");

  static class Initializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues.of(
          "spring.datasource.url=" + mySQLContainer.getJdbcUrl(),
          "spring.datasource.username=" + mySQLContainer.getUsername(),
          "spring.datasource.password=" + mySQLContainer.getPassword()
      ).applyTo(configurableApplicationContext.getEnvironment());
    }
  }

  private static final String testQueueName = "annotation-processor";

  @Autowired
  private TestFrontierRepository repository;

  @Autowired
  private MockMvc mockMvc;

  ObjectMapper objectMapper = new ObjectMapper();

  private FrontierProviderAMQPProducer producer;

  private RabbitTemplate rabbitTemplateMock;

  @Before
  public void setUp() {
    this.rabbitTemplateMock = Mockito.mock(RabbitTemplate.class);
    this.producer = new FrontierProviderAMQPProducer(this.rabbitTemplateMock, testQueueName);
  }

  @Test
  @Ignore
  public void shouldThrowAFrontierNotPresentAnnotation() throws Exception {

    repository.findAllByEmail("");

    FrontierRequestBody requestBody = FrontierRequestBody
        .builder()
        .methodName("findAllByEmail")
        .methodParams(ImmutableSet.of("email@email.pt"))
        .build();

    MvcResult mvcResult = mockMvc.perform(post("/api/frontier/testFrontierRepository")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestBody))
        .characterEncoding("utf-8"))
        .andExpect(status().isMethodNotAllowed())
        .andReturn();

    assertThat(mvcResult.getResponse().getContentAsString())
        .contains(NO_FRONTIER_USAGE_STRING);
  }

  @Test
  public void shouldReturnAValidResponse() throws Exception {

    FrontierRequestBody requestBody = FrontierRequestBody
        .builder()
        .methodName("findAllByEmail")
        .methodParams(ImmutableSet.of("email@email.pt"))
        .build();

    MvcResult mvcResult = mockMvc.perform(post("/api/frontier/testFrontierRepository")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestBody))
        .characterEncoding("utf-8"))
        .andExpect(status().isOk())
        .andReturn();

    List<User> expectedEmail = repository.findAllByEmail("email@email.pt");
    FrontierResponseBody response = objectMapper.readValue(
        mvcResult.getResponse().getContentAsString(),
        new TypeReference<FrontierResponseBody<List<User>>>() {
        });

    assertThat(response.getResponse()).isEqualTo(expectedEmail);
    assertThat(response.getStatus().value()).isEqualTo(200);
    //assertThat(response.getVerboseErrorMessage().isPresent()).isEqualTo(false);
  }

  @Test
  public void shouldProduceRabbitMessageSuccessfully() throws JsonProcessingException {
    FrontierRequestBody testMessage = FrontierRequestBody.builder()
        .methodName("findAllByEmail")
        .methodParams(
            ImmutableSet.of("email@email.pt"))
        .build();
    assertThatCode(() -> this.producer.produceRequest(testMessage)).doesNotThrowAnyException();
    String json = new ObjectMapper().writeValueAsString(testMessage);
    Mockito.verify(this.rabbitTemplateMock)
        .convertAndSend(eq(testQueueName), eq(""), eq(json));
  }

}
