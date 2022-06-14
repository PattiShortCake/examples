package com.pattycake.example.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.IgniteSpring;
import org.apache.ignite.cluster.ClusterState;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.ExecutorConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.configuration.LoadAllWarmUpConfiguration;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.collision.priorityqueue.PriorityQueueCollisionSpi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.propagation.Propagator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

@Configuration
@Slf4j
public class ApacheIgniteConfiguration {

  public static final String CUSTOM_DATA_REGION = "CUSTOM-DATA_REGION";
  public static final String BLUE = "blue";

  @Bean(destroyMethod = "close")
  public Ignite ignite(final IgniteConfiguration configuration, final ApplicationContext context)
      throws IgniteCheckedException {
    final Ignite ignite = IgniteSpring.start(configuration, context);
//        for (final Scope scope : Scope.values()) {
//            ignite.tracingConfiguration().set(
//                new TracingConfigurationCoordinates.Builder(scope).build(),
//                new TracingConfigurationParameters.Builder().withSamplingRate(1).build());
//
//        }

    ignite.cluster().state(ClusterState.ACTIVE);
    return ignite;
  }

  @Bean
  public IgniteConfiguration igniteConfiguration(final Tracer tracer, final Propagator propagator,
      final Environment environment,
      @Value("${server.port}") final int serverPort
  ) {
    final IgniteConfiguration cfg = new IgniteConfiguration()
        .setExecutorConfiguration(new ExecutorConfiguration(BLUE).setSize(16))
        .setGridLogger(new Slf4jLogger())
        .setConsistentId("server-" + serverPort)
        .setCollisionSpi(priorityQueueCollisionSpi())
//            .setDataStorageConfiguration(dataStorageConfiguration())
//                .setIncludeEventTypes(EventType.EVTS_ALL)
//            .setLifecycleBeans(new IgniteLifecycleBean())
//            .setDiscoverySpi(tcpDiscoverySpi())
//            .setTracingSpi(new SleuthTracingSpi(tracer, propagator))
        ;

    if (environment.acceptsProfiles(Profiles.of("red"))) {
      log.info("Using custom data storage configuration");
      cfg.setDataStorageConfiguration(dataStorageConfiguration());
    } else {
      log.info("Using default data storage configuration");
    }

    System.setProperty("Cat", "true");

    return cfg;
  }

  private PriorityQueueCollisionSpi priorityQueueCollisionSpi() {
    final PriorityQueueCollisionSpi spi = new PriorityQueueCollisionSpi();
    spi.setParallelJobsNumber(3);
    return spi;
  }

  private DataStorageConfiguration dataStorageConfiguration() {
    final DataStorageConfiguration config = new DataStorageConfiguration();

    config.setDefaultWarmUpConfiguration(new LoadAllWarmUpConfiguration());
    config.setDataRegionConfigurations(dataRegionConfiguration());

    return config;
  }

  private DataRegionConfiguration dataRegionConfiguration() {
    final DataRegionConfiguration config = new DataRegionConfiguration();
    config.setPersistenceEnabled(true);
    config.setName(CUSTOM_DATA_REGION);
//        config.set
//        config.setWarmUpConfiguration(new LoadAllWarmUpConfiguration());
    return config;
  }

}
