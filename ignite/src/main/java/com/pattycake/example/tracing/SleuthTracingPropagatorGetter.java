package com.pattycake.example.tracing;

import java.util.Map;
import org.springframework.cloud.sleuth.propagation.Propagator;

public class SleuthTracingPropagatorGetter implements Propagator.Getter<Map<String, String>> {

  @Override
  public String get(final Map<String, String> carrier, final String key) {
    if (carrier != null) {
      return carrier.get(key);
    }
    return null;
  }
}
