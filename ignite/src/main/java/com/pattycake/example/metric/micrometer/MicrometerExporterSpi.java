package com.pattycake.example.metric.micrometer;

import com.google.common.util.concurrent.AtomicDouble;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.ignite.internal.GridKernalContext;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.processors.metric.PushMetricsExporterAdapter;
import org.apache.ignite.spi.IgniteSpiContext;
import org.apache.ignite.spi.IgniteSpiException;
import org.apache.ignite.spi.metric.BooleanMetric;
import org.apache.ignite.spi.metric.DoubleMetric;
import org.apache.ignite.spi.metric.IntMetric;
import org.apache.ignite.spi.metric.LongMetric;
import org.apache.ignite.spi.metric.Metric;
import org.apache.ignite.spi.metric.ObjectMetric;
import org.apache.ignite.spi.metric.ReadOnlyMetricRegistry;
import org.jetbrains.annotations.Nullable;

public class MicrometerExporterSpi extends PushMetricsExporterAdapter {

  private final MeterRegistry meterRegistry;

  /**
   * Ignite instance name.
   */
  private Tag instanceNameTag;

  /**
   * Ignite node id.
   */
  private Tag nodeIdTag;

  /**
   * Ignite node consistent id.
   */
  private Tag consistentIdTag;

  public MicrometerExporterSpi(final MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  @Override
  public void spiStart(@Nullable final String igniteInstanceName) throws IgniteSpiException {
    super.spiStart(igniteInstanceName);

    instanceNameTag = Tag.of("instanceName", igniteInstanceName);

    nodeIdTag = Tag.of("nodeId", igniteContext().localNodeId().toString());

    //Node consistent id will be known in #onContextInitialized0(IgniteSpiContext), after DiscoMgr started.
    consistentIdTag = Tag.of("consistentId", "unknown");
  }

  @Override
  protected void onContextInitialized0(final IgniteSpiContext spiCtx) throws IgniteSpiException {
    super.onContextInitialized0(spiCtx);
    consistentIdTag = Tag.of("consistentId", igniteContext().discovery().localNode().consistentId().toString());
  }

  private GridKernalContext igniteContext() {
    return ((IgniteEx) ignite()).context();
  }

  @Override
  public void export() {
    mreg.forEach(this::onRegistry);
  }

  private void onRegistry(final ReadOnlyMetricRegistry metric) {
    metric.forEach(this::onMetric);
  }

  private void onMetric(final Metric metric) {
    if (metric instanceof LongMetric ||
        metric instanceof IntMetric ||
        metric instanceof BooleanMetric ||
        (metric instanceof ObjectMetric && ((ObjectMetric) metric).type() == Date.class) ||
        (metric instanceof ObjectMetric && ((ObjectMetric) metric).type() == OffsetDateTime.class)) {
      final long val;

      if (metric instanceof LongMetric) {
        val = ((LongMetric) metric).value();
      } else if (metric instanceof IntMetric) {
        val = ((IntMetric) metric).value();
      } else if (metric instanceof BooleanMetric) {
        val = ((BooleanMetric) metric).value() ? 1 : 0;
      } else if (metric instanceof ObjectMetric && ((ObjectMetric) metric).type() == Date.class) {
        val = ((ObjectMetric<Date>) metric).value().getTime();
      } else {
        val = ((ObjectMetric<OffsetDateTime>) metric).value().toInstant().toEpochMilli();
      }

      if (val < 0) {
        if (log.isDebugEnabled()) {
          log.debug("OpenCensus doesn't support negative values. Skip record of " + metric.name());
        }

        return;
      }

      final AtomicLong gauge = buildGauge(metric.name(), new AtomicLong());
      gauge.set(val);

    } else if (metric instanceof DoubleMetric) {
      final double val = ((DoubleMetric) metric).value();

      if (val < 0) {
        if (log.isDebugEnabled()) {
          log.debug("OpenCensus doesn't support negative values. Skip record of " + metric.name());
        }

        return;
      }

      final AtomicDouble gauge = buildGauge(metric.name(), new AtomicDouble());
      gauge.set(val);
//    } else if (metric instanceof HistogramMetric) {
//      final String[] names = histogramBucketNames((HistogramMetric) metric);
//      final long[] vals = ((HistogramMetric) metric).value();
//
//      assert names.length == vals.length;
//
//      for (int i = 0; i < vals.length; i++) {
//        final String name = names[i];
//
//        final MeasureLong msr = (MeasureLong) measures.computeIfAbsent(name,
//            k -> createMeasureLong(name, metric.description()));
//
//        mmap.put(msr, vals[i]);
//      }
    } else if (log.isDebugEnabled()) {
      log.debug(metric.name() +
          "[" + metric.getClass() + "] not supported by Opencensus exporter");
    }
  }


  private <T extends Number> T buildGauge(final String name, final T number) {
    final List<Tag> tags = Arrays.asList(instanceNameTag, consistentIdTag, nodeIdTag);
    return meterRegistry.gauge(name, tags, number);
  }

}
