package com.frontier.api.annotation.processor.annotation.immutables;

import java.util.Arrays;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Guarantee {
  SYNCHRONOUS("synchronous") {
    @Override
    public <R, I, P> R accept(FrontierMessageProducerVisitor<R, I, P> visitor, I identity,
        P params) {
      return visitor.produceSynchronous(identity, params);
    }
  },

  ASYNCHRONOUS("asynchronous") {
    @Override
    public <R, I, P> R accept(FrontierMessageProducerVisitor<R, I, P> visitor, I identity,
        P params) {
      return visitor.produceAsynchronous(identity, params);
    }
  },

  BEST_EFFORT("best-effort") {
    @Override
    public <R, I, P> R accept(FrontierMessageProducerVisitor<R, I, P> visitor, I identity,
        P params) {
      return visitor.produceBestEffort(identity, params);
    }
  };

  private String name;

  public static Optional<Guarantee> getMethodGuarantee(String guarantee) {
    return Arrays.stream(Guarantee.values())
        .filter(g -> g.getName().equals(guarantee))
        .findFirst();
  }

  public abstract <R, I, P> R accept(FrontierMessageProducerVisitor<R, I, P> visitor, I identity,
      P params);

  public interface FrontierMessageProducerVisitor<R, I, P> {

    R produceSynchronous(I identity, P params);

    R produceBestEffort(I identity, P params);

    R produceAsynchronous(I identity, P params);
  }
}
