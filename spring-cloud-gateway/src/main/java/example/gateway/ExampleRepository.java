package example.gateway;

import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ExampleRepository {

  private static final Logger log = LoggerFactory.getLogger(ExampleRepository.class);

  private final AtomicBoolean saved = new AtomicBoolean(false);

  public Mono<String> save(final Model model) {
    log.info("save called");
    return Mono
        .just(saved.getAndSet(true))
        .then(Mono.just(model.getId()))
        ;
  }

  public Flux<Model> findAll() {
    if (!saved.get()) {
      log.info("Found 2");
      return Flux.just(
          Model.builder().id("1").name("SpongeBob").build(),
          Model.builder().id("3").name("Mr. Krabs").build()
      );
    }

    // TODO is this correct?
    log.info("Found 3");
    saved.getAndSet(false);
    return
        Flux.just(
            Model.builder().id("1").name("SpongeBob").build(),
            Model.builder().id("2").name("Patrick").build(),
            Model.builder().id("3").name("Mr. Krabs").build()
        );
  }

}
