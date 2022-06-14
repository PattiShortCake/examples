package com.pattycake.example.service;

import com.pattycake.example.cache.loader.CacheLoader;
import com.pattycake.example.model.InputModel;
import com.pattycake.example.tracing.SleuthIgniteSpan;
import com.pattycake.example.tracing.SleuthTracingPropagatorGetter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.cache.Cache.Entry;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.resources.SpringResource;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.docs.AssertingSpanBuilder;
import org.springframework.cloud.sleuth.propagation.Propagator;

@Slf4j
public class AddPartitionTask implements IgniteCallable<Integer> {

    private final int partition;
    private final Map<String, String> spanMap;
    private final Propagator.Getter<Map<String, String>> extractor;
//    private final Span parentSpan;

    private Tracer tracer;

    private Propagator propagator;

    @IgniteInstanceResource
    private Ignite ignite;

    public AddPartitionTask(final int partition, final Map<String, String> spanMap
                            //, final Span parentSpan
    ) {
        this.partition = partition;
        this.spanMap = spanMap;
//        this.parentSpan = parentSpan;
        extractor = new SleuthTracingPropagatorGetter();
    }

    @SpringResource(resourceClass = Tracer.class)
    public void setTracer(final Tracer tracer) {
        this.tracer = Objects.requireNonNull(tracer);
    }

    @SpringResource(resourceClass = Propagator.class)
    public void setPropagator(final Propagator propagator) {
        this.propagator = Objects.requireNonNull(propagator);
    }

    @Override
    public Integer call() throws Exception {

        Span newSpan = null;
        final Span.Builder spanBuilder = AssertingSpanBuilder.of(SleuthIgniteSpan.IGNITE_CONSUMER_SPAN, propagator.extract(spanMap, extractor)
                .kind(Span.Kind.CONSUMER))
            .name("addPartitionTask");

        final Span initialSpan = spanBuilder.start();
        try (final Tracer.SpanInScope ws = tracer.withSpan(initialSpan)) {
//        try (final Tracer.SpanInScope ws = this.tracer.withSpan(parentSpan)) {
            newSpan = tracer.nextSpan().name("sum-partition-" + partition)
                .tag("partition", Integer.toString(partition)).start();
            return sum();
        } finally {
            // Once done remember to end the span. This will allow collecting
            // the span to send it to e.g. Zipkin. The tags and events set on the
            // newSpan will not be present on the parent
            if (newSpan != null) {
                newSpan.end();
            }
        }
    }

    @NotNull
    private Integer sum() {
        final IgniteCache<String, Integer> cache = ignite.cache(CacheLoader.CACHE_NAME);
        try (final QueryCursor<Entry<String, InputModel>> cursor = cache.query(new ScanQuery<String, InputModel>(partition).setLocal(true))) {
            final Optional<Integer> sum = StreamSupport.stream(cursor.spliterator(), false)
//                .peek(entry -> log.info("key[{}] value[{}]", entry.getKey(), entry.getValue()))
                .map(entry -> entry.getValue().getValue())
                .reduce(Integer::sum);

            log.info("Partition[{}] Sum[{}]", partition, sum);
            return sum.orElse(0);
        }
    }


}
