package example.sleuth;

import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Component;

@Component
public class ExampleService {

  private final Tracer tracer;

  public ExampleService(final Tracer tracer) {
    this.tracer = tracer;
  }

  public String hello() {
    final Span newSpan = this.tracer.nextSpan().name("calculateGreeting");
    try (final Tracer.SpanInScope ws = this.tracer.withSpan(newSpan.start())) {
      return "hello world";
    } finally {
        // Once done remember to end the span. This will allow collecting
        // the span to send it to a distributed tracing system e.g. Zipkin
        newSpan.end();
      }
  }
}
