package com.pattycake.example.tracing;

import org.springframework.cloud.sleuth.docs.DocumentedSpan;

public enum SleuthIgniteSpan implements DocumentedSpan {
  IGNITE_CONSUMER_SPAN {
    @Override
    public String getName() {
      return "ignite.consume";
    }

  },
  IGNITE_PRODUCER_SPAN {
    @Override
    public String getName() {
      return "ignite.produce";
    }
  }

}
