package com.pattycake.example.config;

import com.pattycake.example.node.lifecycle.IgniteLifecycleBean;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.configuration.LoadAllWarmUpConfiguration;
import org.apache.ignite.events.EventType;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApacheIgniteConfiguration {

    public static final String CUSTOM_DATA_REGION = "CUSTOM-DATA_REGION";
    @Value("${server.port}")
    private int serverPort;

    @Bean(destroyMethod = "close")
    public Ignite ignite() {
        return Ignition.start(igniteConfiguration());
    }

    private IgniteConfiguration igniteConfiguration() {
        final IgniteConfiguration cfg = new IgniteConfiguration()
                .setGridLogger(new Slf4jLogger())
                .setConsistentId("server-" + serverPort)
                .setDataStorageConfiguration(dataStorageConfiguration())
                .setIncludeEventTypes(EventType.EVTS_ALL)
                .setLifecycleBeans(new IgniteLifecycleBean());

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
