package com.pattycake.example.service;

import com.pattycake.example.config.ApacheIgniteCacheConfiguration;
import com.pattycake.example.model.InputModel;
import com.pattycake.example.tracing.SleuthTracingPropagatorGetter;
import com.pattycake.example.tracing.SleuthTracingPropagatorSetter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.cache.affinity.Affinity;
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
        this.injector = new SleuthTracingPropagatorSetter();
        this.extractor = new SleuthTracingPropagatorGetter();
    }

    public int printEven() {
        final Span newSpan = this.tracer.nextSpan().name("printEven");
        try (final Tracer.SpanInScope ws = this.tracer.withSpan(newSpan.start())) {

            final Map<String, String> spanMap = new HashMap<>();
            propagator.inject(newSpan.context(), spanMap, injector);

            log.info("Getting management cache");

            log.info("Getting data cache");
            final Affinity<InputModel> affinity = ignite.affinity(ApacheIgniteCacheConfiguration.CACHE_NAME);
            final IgniteCompute compute = ignite.compute();

            final int sum = IntStream.range(0, affinity.partitions())
                .mapToObj(partition -> compute.affinityCallAsync(ApacheIgniteCacheConfiguration.CACHE_NAME, partition, new AddPartitionTask(partition, spanMap)))
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
}
