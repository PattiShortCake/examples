package example.sleuth;

import java.time.Duration;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

//@Component
public class ExampleFilter implements GlobalFilter, Ordered {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExampleFilter.class);

  private final ConnectableFlux<Model> responses;
  private final ExampleRepository repository = new ExampleRepository();
  private Consumer<Flux<Model>> consumer;

  public ExampleFilter() {

    responses = Flux.create(
        (FluxSink<Model> sink) -> {
          do {
            repository.findAll()
                .subscribe(emitter -> sink.next(emitter));
            try {
              Thread.sleep(1_000);
            } catch (final InterruptedException e) {
              e.printStackTrace();
            }
          } while (true);

        }
    )
        .doOnComplete(() -> LOGGER.info("I iz so done"))
        .doOnCancel(() -> LOGGER.info("I quit this bitch"))
//        .doOnEach(signal -> LOGGER.info("I see each model[{}]", signal.get()))
        .doOnEach(signal -> LOGGER.info("I see next model[{}]", signal.get()))
        .doOnTerminate(() -> LOGGER.info("You've been terminated!"))
        .doOnSubscribe(subscription -> LOGGER.info("Subscribed!"))
        .subscribeOn(Schedulers.newParallel("hi", 3))
        .cache(Duration.ofSeconds(3L))
        .publish();
    responses.connect();
  }

  @Override
  public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
    if (ServerWebExchangeUtils.isAlreadyRouted(exchange)) {
      LOGGER.info("Already routed {}", exchange);
      return chain.filter(exchange);
    }
    LOGGER.info("Routing {}", exchange);
    ServerWebExchangeUtils.setAlreadyRouted(exchange);

    final Model model = Model.builder().id("2").name("Star").build();

    return repository.save(model)
        .doOnNext(s -> LOGGER.info("I saved id-{}", s))
        .thenMany(responses)
        .doOnNext(s -> LOGGER.info("Looking at response[{}]", s))
        .filter(p -> p.getId().equalsIgnoreCase(model.getId()))
        .next()
        .doOnNext(p -> LOGGER.info("Responded with model[{}]", p))
        .doOnSubscribe(c -> LOGGER.info("Subscribed to response!"))
        .doOnCancel(() -> LOGGER.info("cancelled"))
        .doOnTerminate(() -> LOGGER.info("terminated"))
        .cache(Duration.ofSeconds(5L))
        .then(
            chain.filter(exchange)
        );
  }

  @Override
  public int getOrder() {
    return -1;
  }
}
