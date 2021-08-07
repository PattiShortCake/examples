package com.pattycake.example.config;

import com.pattycake.example.cache.store.RandomNumberCacheLoadOnlyStoreAdapter;
import com.pattycake.example.cache.store.RandomNumberCacheStoreSessionListener;
import com.pattycake.example.node.lifecycle.IgniteLifecycleBean;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction;
import org.apache.ignite.configuration.*;
import org.apache.ignite.events.EventType;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.springframework.boot.autoconfigure.IgniteConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.configuration.FactoryBuilder;

@Configuration
public class ApacheIgniteConfiguration {

    public static final String CACHE_NAME = "even_numbers";
    public static final int CACHE_PARTITIONS = 5;

    @Bean
    public IgniteConfigurer igniteConfigurer() {
        CacheConfiguration<String, Integer> cacheConfiguration = cacheConfiguration();
        return cfg ->
            cfg.setGridLogger(new Slf4jLogger())
            .setCacheConfiguration(cacheConfiguration)
            .setDataStorageConfiguration(dataStorageConfiguration())
            .setIncludeEventTypes(EventType.EVTS_ALL)
            .setLifecycleBeans(new IgniteLifecycleBean())
            ;
    }

    private CacheConfiguration<String,Integer> cacheConfiguration() {
        return new CacheConfiguration<String,Integer>()
                .setAffinity(new RendezvousAffinityFunction(false, CACHE_PARTITIONS))
                .setCacheStoreFactory(FactoryBuilder.factoryOf(RandomNumberCacheLoadOnlyStoreAdapter.class))
                .setCacheStoreSessionListenerFactories(() -> new RandomNumberCacheStoreSessionListener())
//                .setEvictionPolicyFactory(() -> new FifoEvictionPolicy<>(Integer.MAX_VALUE))
                .setOnheapCacheEnabled(true)
                .setRebalanceMode(CacheRebalanceMode.SYNC)
                .setName(CACHE_NAME)
                ;
    }

    private DataStorageConfiguration dataStorageConfiguration() {
        DataStorageConfiguration config = new DataStorageConfiguration()
                .setDefaultWarmUpConfiguration(new LoadAllWarmUpConfiguration())
                ;

        return config;
    }

}
