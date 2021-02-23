package com.frontier.api.coordinator.service;

import com.frontier.api.coordinator.rest.imutables.FrontierDestination;
import com.frontier.api.coordinator.rest.imutables.FrontierRegisterNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FrontierNodeCacheService {

  private final Logger log = LoggerFactory.getLogger(FrontierNodeCacheService.class);

  private final Builder<FrontierDestination, List<FrontierRegisterNode>> frontierNodes = ImmutableMap
      .builder();

  public void registerNode(String destinationName,
      List<FrontierRegisterNode> frontierRegisterNodes) {
    FrontierDestination frontierDestination = FrontierDestination.builder()
        .name(destinationName)
        .build();
    log.info("Registering node {}, with properties {}", frontierDestination, frontierDestination);
    frontierNodes.put(frontierDestination, frontierRegisterNodes);
  }

  public Map<FrontierDestination, List<FrontierRegisterNode>> buildSnapshotFrontierNodes() {
    ImmutableMap<FrontierDestination, List<FrontierRegisterNode>> build = frontierNodes.build();
    log.info("Snapshot of nodes {}", build);
    return build;
  }

}
