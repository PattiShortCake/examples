package com.pattycake.example.tracing;

import java.util.Map;
import org.springframework.cloud.sleuth.propagation.Propagator;

public class SleuthTracingPropagatorSetter implements Propagator.Setter<Map<String, String>> {

  @Override
  public void set(final Map<String, String> carrier, final String key, final String value) {
    if (carrier != null) {
      carrier.put(key, value);
    }
  }
}
