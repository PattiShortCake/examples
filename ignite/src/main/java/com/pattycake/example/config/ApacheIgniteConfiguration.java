package com.pattycake.example.config;

import com.pattycake.example.node.lifecycle.IgniteLifecycleBean;
import com.pattycake.example.tracing.SleuthTracingSpi;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.IgniteSpring;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.configuration.LoadAllWarmUpConfiguration;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.tracing.Scope;
import org.apache.ignite.spi.tracing.TracingConfigurationCoordinates;
import org.apache.ignite.spi.tracing.TracingConfigurationParameters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.propagation.Propagator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApacheIgniteConfiguration {

    public static final String CUSTOM_DATA_REGION = "CUSTOM-DATA_REGION";
    @Value("${server.port}")
    private int serverPort;

    @Bean(destroyMethod = "close")
    public Ignite ignite(final IgniteConfiguration configuration, final ApplicationContext context) throws IgniteCheckedException {
        final Ignite ignite = IgniteSpring.start(configuration, context);
        for (final Scope scope : Scope.values()) {
            ignite.tracingConfiguration().set(
                new TracingConfigurationCoordinates.Builder(scope).build(),
                new TracingConfigurationParameters.Builder().withSamplingRate(1).build());

        }
        return ignite;
    }

    @Bean
    public IgniteConfiguration igniteConfiguration(final Tracer tracer, final Propagator propagator) {
        final IgniteConfiguration cfg = new IgniteConfiguration()
            .setGridLogger(new Slf4jLogger())
            .setConsistentId("server-" + serverPort)
            .setDataStorageConfiguration(dataStorageConfiguration())
//                .setIncludeEventTypes(EventType.EVTS_ALL)
            .setLifecycleBeans(new IgniteLifecycleBean())
//            .setDiscoverySpi(tcpDiscoverySpi())
            .setTracingSpi(new SleuthTracingSpi(tracer, propagator));
        return cfg;
    }

    private DataStorageConfiguration dataStorageConfiguration() {
        final DataStorageConfiguration config = new DataStorageConfiguration()
            .setDefaultWarmUpConfiguration(new LoadAllWarmUpConfiguration());
//        config.setDataRegionConfigurations(dataRegionConfiguration());

        return config;
    }

    private DataRegionConfiguration dataRegionConfiguration() {
        final DataRegionConfiguration config = new DataRegionConfiguration();
        config.setPersistenceEnabled(true);
        config.setName(CUSTOM_DATA_REGION);
        config.setWarmUpConfiguration(new LoadAllWarmUpConfiguration());
        return config;
    }

}
