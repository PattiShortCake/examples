package com.pattycake.example.config;

import com.pattycake.example.cache.store.RandomNumberCacheLoadOnlyStoreAdapter;
import com.pattycake.example.cache.store.RandomNumberCacheStoreSessionListener;
import com.pattycake.example.node.lifecycle.IgniteLifecycleBean;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.cache.eviction.fifo.FifoEvictionPolicy;
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
    public static final String MANAGEMENT_CACHE_NAME = "management";


    @Bean
    public IgniteConfigurer igniteConfigurer() {
        return cfg -> {
            cfg.setGridLogger(new Slf4jLogger())
            .setCacheConfiguration(cacheConfiguration(), managementCacheConfiguration())
            .setDataStorageConfiguration(dataStorageConfiguration())
            .setIncludeEventTypes(EventType.EVTS_ALL)
            .setLifecycleBeans(new IgniteLifecycleBean())
            ;
        };
    }

    private CacheConfiguration<String,Integer> cacheConfiguration() {
        return new CacheConfiguration<String,Integer>()
                .setCacheStoreFactory(FactoryBuilder.factoryOf(RandomNumberCacheLoadOnlyStoreAdapter.class))
                .setCacheStoreSessionListenerFactories(() -> new RandomNumberCacheStoreSessionListener())
//                .setReadThrough(true)
//                .setWriteThrough(false)
                .setEvictionPolicyFactory(() -> new FifoEvictionPolicy<>(Integer.MAX_VALUE))
                .setOnheapCacheEnabled(true)
                .setRebalanceMode(CacheRebalanceMode.NONE)
                .setName(CACHE_NAME)
                ;
    }

    private CacheConfiguration<String,Integer> managementCacheConfiguration() {
        return new CacheConfiguration<String,Integer>()
//                .setCacheStoreFactory(FactoryBuilder.factoryOf(RandomNumberCacheLoadOnlyStoreAdapter.class))
//                .setCacheStoreSessionListenerFactories(() -> new RandomNumberCacheStoreSessionListener())
//                .setReadThrough(true)
//                .setWriteThrough(false)
                .setEvictionPolicyFactory(() -> new FifoEvictionPolicy<>(Integer.MAX_VALUE))
                .setOnheapCacheEnabled(true)
                .setRebalanceMode(CacheRebalanceMode.SYNC)
                .setName(MANAGEMENT_CACHE_NAME)
                ;
    }

    private DataStorageConfiguration dataStorageConfiguration() {
        DataStorageConfiguration config = new DataStorageConfiguration()
                .setDefaultWarmUpConfiguration(new LoadAllWarmUpConfiguration())
                .setPageSize(DataStorageConfiguration.MAX_PAGE_SIZE)
                ;

//        config.getDefaultDataRegionConfiguration().setPersistenceEnabled(true)
                ;

        return config;
    }

    @Bean
    public TcpDiscoverySpi tcpDiscoverySpi() {
        return new TcpDiscoverySpi();
    }
}
