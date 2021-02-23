package com.frontier.api.coordinator.rest;

import com.frontier.api.coordinator.rest.imutables.FrontierRegisterRequestMessage;
import com.frontier.api.coordinator.rest.imutables.FrontierRegisterResponseMessage;
import com.frontier.api.coordinator.service.FrontierNodeCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/frontier/api")
public class FrontierRegisterController {

  private final Logger LOGGER = LoggerFactory.getLogger(FrontierRegisterController.class);
  private final FrontierNodeCacheService frontierNodeCacheService;

  public FrontierRegisterController(FrontierNodeCacheService frontierNodeCacheService) {
    this.frontierNodeCacheService = frontierNodeCacheService;
  }

  @PostMapping("/register")
  public FrontierRegisterResponseMessage registerFrontierNode(
      FrontierRegisterRequestMessage frontierRegisterRequestMessage) {
    frontierNodeCacheService.registerNode(frontierRegisterRequestMessage.getServiceName(),
        frontierRegisterRequestMessage.getFrontierRegisterNodeBatch());
    return FrontierRegisterResponseMessage.builder()
        .frontierIdentitiesByServiceName(frontierNodeCacheService.buildSnapshotFrontierNodes())
        .build();
  }

  @GetMapping("/pull")
  public FrontierRegisterResponseMessage pullFrontierNodes() {
    return FrontierRegisterResponseMessage.builder()
        .frontierIdentitiesByServiceName(frontierNodeCacheService.buildSnapshotFrontierNodes())
        .build();
  }

}

