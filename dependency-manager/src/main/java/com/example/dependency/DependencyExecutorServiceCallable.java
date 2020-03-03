package com.example.dependency;

import com.example.dependency.model.Dependency;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class DependencyExecutorServiceCallable implements Callable<List<Dependency>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DependencyExecutorServiceCallable.class);

  private final Dependency dependency;
  private final Set<Dependency> dependenciesProcessed;
  private final PomParserService pomParserService;
  private final int dependencyQueueSize;

  public DependencyExecutorServiceCallable(final Dependency dependency, final Set<Dependency> dependenciesProcessed, final int dependencyQueueSize, final PomParserService pomParserService) {
    this.dependency = dependency;
    this.dependenciesProcessed = dependenciesProcessed;
    this.dependencyQueueSize = dependencyQueueSize;
    this.pomParserService = pomParserService;
  }

  @Override
  public List<Dependency> call() {
    if (dependenciesProcessed.contains(dependency)) {
      LOGGER.info("Queued[{}] Processed[{}] Already processed dependency {}", dependencyQueueSize, dependenciesProcessed.size(), dependency);
      return Collections.emptyList();
    }

    if (shouldSkip(dependency)) {
      LOGGER.info("Queued[{}] Processed[{}] Skipping dependency {}", dependencyQueueSize, dependenciesProcessed.size(), dependency);
      return Collections.emptyList();
    }

    return processAndGetDependencies();
  }

  private List<Dependency> processAndGetDependencies() {
    final String pomUri = dependency.toUri() + ".pom";
    Optional<String> xmlAsString = null;
    try {
      xmlAsString = PomParserService.exchangeToMono(pomUri)
          .blockOptional()
          .map(clientResponse -> clientResponse.bodyToMono(String.class))
          .map(Mono::block);

      if (xmlAsString.isPresent()) {
        final List<Dependency> dependencies = pomParserService.run(xmlAsString.get());
        dependenciesProcessed.add(dependency);
//        LOGGER.info("Queued[{}] Processed[{}] Found {} dependencies from {}", dependencyQueueSize, dependenciesProcessed.size(), dependencies.size(), pomUri);
        return dependencies;
      }

    } catch (final Exception e) {
      throw new IllegalStateException("Error when processing dependency[" + dependency + "] having uri[" + pomUri + "] xmlAsString[" + xmlAsString + "]", e);
    }

    return Collections.emptyList();
  }

  private boolean shouldSkip(final Dependency dependency) {
    return
        dependency.getVersion().endsWith("-SNAPSHOT")
            || shouldSkip(dependency, "com.sun.mail", "javax.mail")
            || shouldSkip(dependency, "geode-lucene-test", "org.apache.geode")
        ;
  }

  private boolean shouldSkip(final Dependency dependency, final String groupId, final String artifactId) {
    return groupId.equalsIgnoreCase(dependency.getGroupId()) && artifactId.equalsIgnoreCase(dependency.getArtifactId());
  }

  private boolean shouldSkip(final Dependency dependency, final String groupId, final String artifactId, final String version) {
    return groupId.equalsIgnoreCase(dependency.getGroupId()) && artifactId.equalsIgnoreCase(dependency.getArtifactId()) && version.equalsIgnoreCase(dependency.getVersion());
  }
}
