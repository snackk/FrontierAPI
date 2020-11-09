package com.frontier.api.annotation.processor;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontier.api.annotation.processor.annotation.consumer.TestFrontierRepository;
import com.frontier.api.annotation.processor.api.amqp.FrontierAMQPProducer;
import com.frontier.api.annotation.processor.api.immutables.FrontierApiIdentity;
import com.frontier.api.annotation.processor.api.immutables.FrontierApiRequestMessage;
import com.frontier.api.annotation.processor.api.immutables.FrontierApiResponseMessage;
import com.frontier.api.annotation.processor.api.rest.FrontierRestController;
import com.frontier.api.annotation.processor.config.FrontierConfiguration;
import com.frontier.api.annotation.processor.register.FrontierRegisterService;
import com.frontier.api.annotation.processor.test.User;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MySQLContainer;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@Import({TestFrontierRepository.class, TestFrontierRepository.class})
@ContextConfiguration(classes = FrontierConfiguration.class)
public class AnnotationProcessorApplicationTests {

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

  @ClassRule
  public static MySQLContainer mySQLContainer = new MySQLContainer()
      .withDatabaseName("frontier-test-db")
      .withUsername("sa")
      .withPassword("sa");

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(8090);

  @Autowired
  private com.frontier.api.annotation.processor.test.TestFrontierRepository repository;

  @Autowired
  private TestFrontierRepository testFrontierConsumerRepository;

  @Autowired
  private FrontierRestController frontierProviderController;

  @Autowired
  private FrontierRegisterService frontierApiRegisterService;

  @Autowired
  private MockMvc mockMvc;

  @LocalServerPort
  private int port;

  private static final String testQueueName = "annotation-processor";

  private ObjectMapper objectMapper = new ObjectMapper();

  private FrontierAMQPProducer producer;

  private RabbitTemplate rabbitTemplateMock;

  @Before
  public void setUp() {
    this.rabbitTemplateMock = Mockito.mock(RabbitTemplate.class);
    this.producer = new FrontierAMQPProducer(this.rabbitTemplateMock,
        frontierApiRegisterService);
  }

  @Test
  @Ignore
  public void shouldThrowAFrontierNotPresentAnnotation() throws Exception {

    repository.findAllByEmail("");

    FrontierApiRequestMessage requestBody = FrontierApiRequestMessage.builder()
        .beanName("testFrontierRepository")
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
        .contains("No Frontier annotation found. Check the documentation for usage.");
  }

  @Test
  public void shouldPostUsingFrontierAndReturnSuccessResponse() {

    stubFor(WireMock.post(urlEqualTo("/register"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(
                "{\"frontierIdentitiesByServiceName\":{\"http://localhost:" + port
                    + "\":[{\"beanName\":\"testFrontierRepository\",\"methodName\":\"findAllByEmail\",\"guarantee\":\"SYNCHRONOUS\"}]}}")));

    FrontierApiIdentity frontierApiIdentity = FrontierApiIdentity.builder()
        .beanName("testFrontierRepository")
        .methodName("findAllByEmail")
        .build();

    FrontierApiResponseMessage frontierApiResponseMessage = frontierProviderController
        .produceMessage(frontierApiIdentity, ImmutableSet.of("email@email.pt"));

    List<User> expectedEmail = repository.findAllByEmail("email@email.pt");

    assertThat(frontierApiResponseMessage.getResponse()).isEqualTo(expectedEmail);
    assertThat(frontierApiResponseMessage.getStatus().value()).isEqualTo(200);
    //assertThat(response.getVerboseErrorMessage().isPresent()).isEqualTo(false);
  }

  @Test
  @Ignore
  public void shouldRefresherFrontierServicesCache() {

    stubFor(WireMock.get(urlEqualTo("/pull"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(
                "{\"frontierIdentitiesByServiceName\":{\"http://localhost:" + port
                    + "\":[{\"beanName\":\"testFrontierRepository\",\"methodName\":\"findAllByEmail\",\"guarantee\":\"SYNCHRONOUS\"}]}}")));

    stubFor(WireMock.post(urlEqualTo("/register"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(
                "{\"frontierIdentitiesByServiceName\":{\"http://localhost:" + port
                    + "\":[{\"beanName\":\"testFrontierRepository\",\"methodName\":\"findAllByEmail\",\"guarantee\":\"SYNCHRONOUS\"}]}}")));

    //will flush cached values and replace with new ones
    //frontierApiRegisterService.pullForCache();

    FrontierApiIdentity frontierApiIdentity = FrontierApiIdentity.builder()
        .beanName("testFrontierRepository")
        .methodName("findAllByEmail")
        .build();

    FrontierApiResponseMessage frontierApiResponseMessage = frontierProviderController
        .produceMessage(frontierApiIdentity, ImmutableSet.of("email@email.pt"));

    List<User> expectedEmail = repository.findAllByEmail("email@email.pt");

    assertThat(frontierApiResponseMessage.getResponse()).isEqualTo(expectedEmail);
    assertThat(frontierApiResponseMessage.getStatus().value()).isEqualTo(200);
    //assertThat(response.getVerboseErrorMessage().isPresent()).isEqualTo(false);
  }

  @Test
  public void shouldMockPostRequestAndReturnSuccessResponse() throws Exception {

    FrontierApiRequestMessage requestBody = FrontierApiRequestMessage.builder()
        .beanName("testFrontierRepository")
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
    FrontierApiResponseMessage response = objectMapper.readValue(
        mvcResult.getResponse().getContentAsString(),
        new TypeReference<FrontierApiResponseMessage<List<User>>>() {
        });

    assertThat(response.getResponse()).isEqualTo(expectedEmail);
    assertThat(response.getStatus().value()).isEqualTo(200);
    //assertThat(response.getVerboseErrorMessage().isPresent()).isEqualTo(false);
  }

  @Test
  public void shouldProduceRabbitMessageSuccessfully() throws JsonProcessingException {
    FrontierApiRequestMessage testMessage = FrontierApiRequestMessage.builder()
        .beanName("testFrontierRepository")
        .methodName("findAllByEmail")
        .methodParams(
            ImmutableSet.of("email@email.pt"))
        .build();
    assertThatCode(() -> this.producer.produceMessage(testMessage, testQueueName))
        .doesNotThrowAnyException();
    String json = new ObjectMapper().writeValueAsString(testMessage);
    Mockito.verify(this.rabbitTemplateMock)
        .convertAndSend(eq(testQueueName), eq(""), eq(json));
  }

  @Test
  public void syncProviderAnnotationShouldRespondSuccessfully() {
    stubFor(WireMock.post(urlEqualTo("/register"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(
                "{\"frontierIdentitiesByServiceName\":{\"http://localhost:" + port
                    + "\":[{\"beanName\":\"testFrontierRepository\",\"methodName\":\"findAllByEmail\",\"guarantee\":\"SYNCHRONOUS\"}]}}")));

    List<User> expectedEmail = repository.findAllByEmail("email@email.pt");

    List<User> frontierConsumerRequest = testFrontierConsumerRepository
        .findAllByEmail("email@email.pt");

    assertThat(expectedEmail).isEqualTo(frontierConsumerRequest);
  }

  @Test
  @Ignore
  public void asyncProviderAnnotationShouldRespondSuccessfully() throws JsonProcessingException {
    stubFor(WireMock.post(urlEqualTo("/register"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(
                "{\"frontierIdentitiesByServiceName\":{\"annotation-processor\":[{\"beanName\":\"testFrontierRepository\",\"methodName\":\"saveUser\",\"guarantee\":\"ASYNCHRONOUS\"}],"
                    + "\"http://localhost:" + port
                    + "\":[{\"beanName\":\"testFrontierRepository\",\"methodName\":\"findAllByEmail\",\"guarantee\":\"SYNCHRONOUS\"}]}}")));

    User expectedUser = new User("first", "last", "email");
    testFrontierConsumerRepository
        .saveUser(expectedUser.getFirstName(), expectedUser.getLastName(), expectedUser.getEmail());

    User frontierConsumerRequest = repository
        .findAllByEmail("email").get(0);

    assertThat(expectedUser).isEqualTo(frontierConsumerRequest);
  }

}
