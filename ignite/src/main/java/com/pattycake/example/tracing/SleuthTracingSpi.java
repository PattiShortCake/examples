package com.pattycake.example.tracing;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.spi.IgniteSpiAdapter;
import org.apache.ignite.spi.IgniteSpiConsistencyChecked;
import org.apache.ignite.spi.IgniteSpiException;
import org.apache.ignite.spi.IgniteSpiMultipleInstancesSupport;
import org.apache.ignite.spi.tracing.TracingSpi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.docs.AssertingSpanBuilder;
import org.springframework.cloud.sleuth.propagation.Propagator;

@IgniteSpiMultipleInstancesSupport(value = true)
@IgniteSpiConsistencyChecked(optional = true)
@Slf4j
public class SleuthTracingSpi extends IgniteSpiAdapter implements TracingSpi<SleuthSpanAdapter> {

  private final byte SPAN_TYPE = -1;
  private final Tracer tracer;
  private final Propagator propagator;
  private final Propagator.Setter<Map<String, String>> injector;
  private final Propagator.Getter<Map<String, String>> extractor;
  private final Kryo kryo;

  public SleuthTracingSpi(final Tracer tracer, final Propagator propagator) {
    this.tracer = tracer;
    this.propagator = propagator;
    this.injector = new SleuthTracingPropagatorSetter();
    this.extractor = new SleuthTracingPropagatorGetter();
    this.kryo= new Kryo();
    kryo.register(HashMap.class);
  }

  @Override
  public SleuthSpanAdapter create(@NotNull final String name, @Nullable final byte[] serializedSpan) throws Exception {
    try(final ByteArrayInputStream stream = new ByteArrayInputStream(serializedSpan)) {
      try (        final Input input = new Input(stream)) {

      log.info("Creating span from serialized byte-array");
      final Map<String, String> map = kryo.readObjectOrNull(input, HashMap.class);
      log.info("Deserialized map[{}]", map);

      final Span.Builder spanBuilder = AssertingSpanBuilder.of(SleuthIgniteSpan.IGNITE_CONSUMER_SPAN, propagator.extract(map, extractor)
              .kind(Span.Kind.CONSUMER))
          .name(name);

      final Span span = spanBuilder.start();

      return new SleuthSpanAdapter(span);
      }
    } catch (final Exception e) {
      log.error("Error creating span", e);
      throw e;
    }
  }

  @Override
  public @NotNull SleuthSpanAdapter create(@NotNull final String name, @Nullable final SleuthSpanAdapter parentSpan) {
    if (parentSpan != null) {
      try (final Tracer.SpanInScope ws = this.tracer.withSpan(parentSpan.getSpan())) {
        final Span newSpan = this.tracer.nextSpan().name(name);
        log.info("Creating span from parent");

        return new SleuthSpanAdapter(newSpan);
      }
    }
    log.info("Creating new span");
    final Span nextSpan = this.tracer.nextSpan();
    final Span newSpan = nextSpan.name(name);
    return new SleuthSpanAdapter(newSpan);
  }

  @Override
  public byte[] serialize(@NotNull final SleuthSpanAdapter span) {
    log.info("Serializing span");
    final Map<String,String> map = new HashMap<>();
    propagator.inject(span.getSpan().context(), map, injector);
    log.info("Serialized map[{}]", map);

    try(final ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
      try (final Output output = new Output(stream)) {
        kryo.writeObject(output, map);
      }
      return stream.toByteArray();
    } catch (final IOException e) {
      throw new UncheckedIOException("Error serializing map["+ map +"] using kyro", e);
    }
  }

  @Override
  public byte type() {
    return SPAN_TYPE;
  }

  @Override
  public void spiStart(@Nullable final String igniteInstanceName) throws IgniteSpiException {
    log.info("Starting {}", SleuthTracingSpi.class);
  }

  @Override
  public void spiStop() throws IgniteSpiException {
    log.info("Stopping {}", SleuthTracingSpi.class);
  }
}
