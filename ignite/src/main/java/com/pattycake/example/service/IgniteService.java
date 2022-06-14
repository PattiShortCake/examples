package com.pattycake.example.service;

import com.pattycake.example.cache.loader.CacheLoader;
import com.pattycake.example.config.ApacheIgniteConfiguration;
import com.pattycake.example.model.InputModel;
import com.pattycake.example.tracing.SleuthTracingPropagatorGetter;
import com.pattycake.example.tracing.SleuthTracingPropagatorSetter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.lang.IgniteRunnable;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.propagation.Propagator;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IgniteService {

  private final Ignite ignite;
  private final Tracer tracer;
  private final Propagator propagator;
  private final Propagator.Setter<Map<String, String>> injector;
  private final Propagator.Getter<Map<String, String>> extractor;

  public IgniteService(final Ignite ignite, final Tracer tracer, final Propagator propagator) {
    this.ignite = ignite;
    this.tracer = tracer;
    this.propagator = propagator;
    injector = new SleuthTracingPropagatorSetter();
    extractor = new SleuthTracingPropagatorGetter();
  }

  public int printEven() {
    final Span newSpan = tracer.nextSpan().name("printEven");
    try (final Tracer.SpanInScope ws = tracer.withSpan(newSpan.start())) {

      final Map<String, String> spanMap = new HashMap<>();
      propagator.inject(newSpan.context(), spanMap, injector);

      log.info("Getting management cache");

      log.info("Getting data cache");
      final Affinity<InputModel> affinity = ignite.affinity(CacheLoader.CACHE_NAME);
      final IgniteCompute compute = ignite.compute();

      final int sum = IntStream.range(0, affinity.partitions())
          .mapToObj(partition -> compute.affinityCallAsync(CacheLoader.CACHE_NAME, partition,
              new AddPartitionTask(partition, spanMap)))
          .collect(Collectors.toList())
          .stream()
          .map(f -> f.get())
          .collect(Collectors.summingInt(i -> i));

      return sum;
    } finally {
      // Once done remember to end the span. This will allow collecting
      // the span to send it to a distributed tracing system e.g. Zipkin
      newSpan.end();
    }
  }

  public int printEven2() {
    final Span newSpan = tracer.nextSpan().name("printEven2");
    try (final Tracer.SpanInScope ws = tracer.withSpan(newSpan.start())) {

      final Map<String, String> spanMap = new HashMap<>();
      propagator.inject(newSpan.context(), spanMap, injector);

      log.info("Getting management cache");

      log.info("Getting data cache");
      final Affinity<InputModel> affinity = ignite.affinity(
          CacheLoader.CACHE_NAME2);
      final IgniteCompute compute = ignite.compute();

      final int sum = IntStream.range(0, affinity.partitions())
          .mapToObj(partition -> compute.affinityCallAsync(
              CacheLoader.CACHE_NAME2, partition,
              new AddPartitionTask(partition, spanMap)))
          .collect(Collectors.toList())
          .stream()
          .map(f -> f.get())
          .collect(Collectors.summingInt(i -> i));

      return sum;
    } finally {
      // Once done remember to end the span. This will allow collecting
      // the span to send it to a distributed tracing system e.g. Zipkin
      newSpan.end();
    }
  }

  public void runRedJob() {
    final Span newSpan = tracer.nextSpan().name("printEven");
    try (final Tracer.SpanInScope ws = tracer.withSpan(newSpan.start())) {
      final Map<String, String> spanMap = new HashMap<>();
      propagator.inject(newSpan.context(), spanMap, injector);
      ignite.compute().run(new Job("RED", 30_000L));
    } finally {
      // Once done remember to end the span. This will allow collecting
      // the span to send it to a distributed tracing system e.g. Zipkin
      newSpan.end();
    }
  }

  public void runBlueJob() {
    final Span newSpan = tracer.nextSpan().name("printEven");
    try (final Tracer.SpanInScope ws = tracer.withSpan(newSpan.start())) {
      final Map<String, String> spanMap = new HashMap<>();
      propagator.inject(newSpan.context(), spanMap, injector);
      final ClusterGroup clusterGroup = ignite.cluster().forAttribute("role", "blah");
      ignite.compute().withExecutor(ApacheIgniteConfiguration.BLUE)
          .run(new Job("Blue", 10_000L));
    } finally {
      // Once done remember to end the span. This will allow collecting
      // the span to send it to a distributed tracing system e.g. Zipkin
      newSpan.end();
    }
  }

  public Map<String, Object> printAttributes() {

    log.info("cluster[spring.profiles.active]={}",
        ignite.cluster().forAttribute("spring.profiles.active", "red").hostNames());
    log.info("cluster[spring.profiles.active]={}",
        ignite.cluster().forAttribute("spring.profiles.active", "white").hostNames());
    log.info("cluster[spring.profiles.active]={}",
        ignite.cluster().forAttribute("spring.profiles.active", "blue").hostNames());
    log.info("cluster[spring.profiles.active]={}",
        ignite.cluster().forAttribute("spring.profiles.active", "brown").hostNames());
    log.info("cluster[spring.profiles.active]={}",
        ignite.cluster().forAttribute("spring.profiles.active", Arrays.asList("red"))
            .hostNames());
    log.info("cluster[spring.profiles.active]={}",
        ignite.cluster()
            .forAttribute("spring.profiles.active", Arrays.asList("red", "white", "blue"))
            .hostNames());
    log.info("cluster[Cat]={}", ignite.cluster().forAttribute("Cat", "true").hostNames());
    log.info("cluster[Cat]={}", ignite.cluster().forAttribute("Cat", true).hostNames());
    log.info("cluster[Cat]={}", ignite.cluster().forAttribute("Cat", Boolean.TRUE).hostNames());

    return ignite.cluster().localNode().attributes();
  }

  static class Job implements IgniteRunnable {

    private final String message;
    private final long delay;

    public Job(final String message, final long delay) {
      this.message = message;
      this.delay = delay;
    }

    @Override
    public void run() {
      final long currentTimeMillis = System.currentTimeMillis();

      try {
        log.info("Starting: {} {}", message, currentTimeMillis);
        Thread.sleep(delay);
        log.info("Completing: {} {}", message, currentTimeMillis);
      } catch (final InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
