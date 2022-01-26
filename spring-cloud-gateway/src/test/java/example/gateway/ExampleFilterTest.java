package example.sleuth;

import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class ExampleFilterTest {

  private final ExampleFilter sut = new ExampleFilter();
  ServerWebExchange exchange;

  @Mock
  private GatewayFilterChain chain;

  @BeforeEach
  void beforeEach() {
    Hooks.onOperatorDebug();
    exchange = MockServerWebExchange
        .from(MockServerHttpRequest.get("localendpoint").build());

  }

  @Test
  void filter() throws InterruptedException {
    // Setup
    BDDMockito.given(chain.filter(exchange)).willReturn(Mono.empty());
    Thread.sleep(5_000L);

    // When
    sut.filter(exchange, chain)
        .block(Duration.ofSeconds(30L));

    // Then
    Mockito.verify(chain).filter(exchange);
    Mockito.verifyNoMoreInteractions(chain);
  }
}
