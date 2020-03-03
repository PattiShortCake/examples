package example.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomGlobalFilter.class);

  private Flux<String> fluxString = Flux.fromStream();

  @Override
  public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
    LOGGER.info("before inside of global filter for {}", exchange.getRequest().getPath());

//    ServerWebExchangeUtils.setAlreadyRouted(exchange);

    LOGGER.info("after inside of global filter for {}", exchange.getRequest().getPath());

    if (ServerWebExchangeUtils.isAlreadyRouted(exchange)) {
      return chain.filter(exchange);
    }

    ServerWebExchangeUtils.setAlreadyRouted(exchange);

    return fluxString
        .filter(s -> s.startsWith(exchange.getRequest().getId()))
        .last()
        .then(chain.filter(exchange));


  }

  @Override
  public int getOrder() {
    return -1;
  }
}
