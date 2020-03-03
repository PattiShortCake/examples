package com.example.dependency;

import com.example.dependency.model.Dependency;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Service
public class DependencyExecutorService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DependencyExecutorService.class);
  private final PomParserService pomParserService;
  private final Set<Dependency> dependenciesProcessed = new CopyOnWriteArraySet<>();

//  private final BlockingDeque<Runnable> blockingDeque = new LinkedBlockingDeque<>();
//  private final Executor executor = new ThreadPoolExecutor(4, 4, 30, TimeUnit.SECONDS, blockingDeque, new ThreadPoolExecutor.CallerRunsPolicy());
//  private final CompletionService<List<Dependency>> completionService = new ExecutorCompletionService<>(executor);
//  private final List<Future<List<Dependency>>> futures = new LinkedList<>();
//  private final List<CompletableFuture<Boolean>> futures = new LinkedList<>();

  public DependencyExecutorService(final PomParserService pomParserService) {
    this.pomParserService = pomParserService;
  }

  public void run() {

    final BlockingDeque<Dependency> dependencyDeque = new LinkedBlockingDeque<>();
    dependencyDeque.add(
        Dependency.builder()
            .groupId("org.springframework.boot")
            .artifactId("spring-boot-dependencies")
            .version("2.2.5.RELEASE")
            .build()
    );

    Flux.create((Consumer<FluxSink<Dependency>>) sink -> {
      Dependency dependency;
      try {
        while ((dependency = dependencyDeque.poll(30, TimeUnit.SECONDS)) != null) {
          sink.next(dependency);
        }
        sink.complete();
      } catch (InterruptedException e) {
        LOGGER.warn("Interrupted when waiting for dependency", e);
        sink.complete();
      }
    })
        .map(dependency -> new DependencyExecutorServiceCallable(dependency, dependenciesProcessed, dependencyDeque.size(), pomParserService))
        .map(DependencyExecutorServiceCallable::call)
        .subscribe(dependencyDeque::addAll)
    ;

  }

}
