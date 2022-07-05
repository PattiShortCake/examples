package com.pattycake.example.cache.loader;

import com.pattycake.example.cache.store.RandomNumberCacheLoadOnlyStoreAdapter;
import com.pattycake.example.cache.store.RandomNumberCacheStoreSessionListener;
import com.pattycake.example.config.ApacheIgniteConfiguration;
import com.pattycake.example.model.InputModel;
import javax.cache.configuration.FactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.cache.PartitionLossPolicy;
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CacheLoader {

    public static final String CACHE_NAME = "even_numbers";
    public static final String CACHE_NAME2 = "even_numbers2";
    public static final int CACHE_PARTITIONS = 12;
    private final Environment environment;
    private final Ignite ignite;

    public CacheLoader(final Ignite ignite, final Environment environment) {
        this.ignite = ignite;
        this.environment = environment;
    }

    public void loadCache() {
        if (environment.acceptsProfiles(Profiles.of("red"))) {
            IgniteCache<String, InputModel> cache = ignite.cache(CACHE_NAME);
            if (cache != null) {
                log.info("Successfully got cache[{}], so skipping configuration", cache);
                loadCacheLocally(cache);
            } else {
                cache = ignite.getOrCreateCache(cacheConfiguration());
                cache.loadCache((k, v) -> true);
            }
        } else {
            IgniteCache<String, InputModel> cache = ignite.cache(CACHE_NAME2);
            if (cache != null) {
                log.info("Successfully got cache[{}], so skipping configuration", cache);
                loadCacheLocally(cache);
            } else {
                cache = ignite.getOrCreateCache(cacheConfiguration2());
                cache.loadCache((k, v) -> true);
            }
        }
    }

    private <K, V> void loadCacheLocally(final IgniteCache<K, V> cache) {
        log.info("Attempting to load cache locally");

        final int beforeLoadSize = cache.size(CachePeekMode.ALL);

        cache.localLoadCache((a, b) -> true);
        final int afterLoadSize = cache.size(CachePeekMode.ALL);
        log.info("Cache is toasty size {} -> {}", beforeLoadSize, afterLoadSize);
        log.info("Cache Ready");
    }

    public CacheConfiguration<String, InputModel> cacheConfiguration() {
        final CacheConfiguration<String, InputModel> config = new CacheConfiguration<>();

        config.setAffinity(new RendezvousAffinityFunction(false, CACHE_PARTITIONS));
        config.setCacheStoreFactory(
            FactoryBuilder.factoryOf(RandomNumberCacheLoadOnlyStoreAdapter.class));
        config.setCacheStoreSessionListenerFactories(
            () -> new RandomNumberCacheStoreSessionListener());
        config.setPartitionLossPolicy(PartitionLossPolicy.READ_ONLY_SAFE);
        config.setDataRegionName(ApacheIgniteConfiguration.CUSTOM_DATA_REGION);
        config.setRebalanceMode(CacheRebalanceMode.ASYNC);
        config.setName(CACHE_NAME);
        config.setIndexedTypes(String.class, InputModel.class);
//        config.setAffinity()
//        config.set

        new RendezvousAffinityFunction();

        return config;
    }

    public CacheConfiguration<String, InputModel> cacheConfiguration2() {
        final CacheConfiguration<String, InputModel> config = new CacheConfiguration<>();
        config.setAffinity(new RendezvousAffinityFunction(false, CACHE_PARTITIONS));
        config.setCacheStoreFactory(
            FactoryBuilder.factoryOf(RandomNumberCacheLoadOnlyStoreAdapter.class));
        config.setCacheStoreSessionListenerFactories(
            () -> new RandomNumberCacheStoreSessionListener());
        config.setPartitionLossPolicy(PartitionLossPolicy.READ_ONLY_SAFE);
        config.setName(CACHE_NAME2);
        config.setIndexedTypes(String.class, InputModel.class);

        return config;
    }

}
