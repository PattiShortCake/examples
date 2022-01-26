package com.pattycake.example.tracing;

import org.apache.ignite.spi.tracing.SpanStatus;
import org.apache.ignite.spi.tracing.SpiSpecificSpan;
import org.springframework.cloud.sleuth.Span;

public class SleuthSpanAdapter implements SpiSpecificSpan  {

  /** Sleuth span delegate. */
  private final Span span;

  /** Flag indicates that span is ended. */
  private volatile boolean ended;

  public SleuthSpanAdapter(final Span span) {
    this.span = span;
  }

  @Override
  public SpiSpecificSpan addTag(final String tagName, final String tagVal) {
    span.tag(tagName, tagVal != null ? tagVal : "null");
    return this;
  }

  @Override
  public SpiSpecificSpan addLog(final String logDesc) {
    span.event(logDesc);
    return this;
  }

  @Override
  public SpiSpecificSpan setStatus(final SpanStatus spanStatus) {
    return addTag("span-status", spanStatus.name());
  }

  @Override
  public SpiSpecificSpan end() {
    ended = true;
    span.end();
    return this;
  }

  @Override
  public boolean isEnded() {
    return ended;
  }

  public Span getSpan() {
    return span;
  }
}
