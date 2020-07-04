package com.frontier.api.annotationprocessor;

import static com.frontier.api.annotationprocessor.provider.service.FrontierResourceErrorHandling.NO_FRONTIER_USAGE_STRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontier.api.annotationprocessor.domain.FrontierRequestBody;
import com.frontier.api.annotationprocessor.provider.test.TestFrontierRepository;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

//@WebMvcTest(FrontierProviderController.class)
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AnnotationProcessorApplicationTests {

  @Autowired
  private TestFrontierRepository repository;

  @Autowired
  private MockMvc mockMvc;

  ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void noFrontierAnnotationFoundError() throws Exception {

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

}
