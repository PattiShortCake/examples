package com.pattycake.example.config;

import com.pattycake.example.cache.loader.CacheLoader;
import com.pattycake.example.cache.store.RandomNumberCacheLoadOnlyStoreAdapter;
import com.pattycake.example.cache.store.RandomNumberCacheStoreSessionListener;
import com.pattycake.example.cache.validator.ClusterSizeTopologyValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteAtomicLong;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.cache.PartitionLossPolicy;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StopWatch;

import javax.cache.configuration.FactoryBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.IntStream;

@Configuration
@Slf4j
public class ApacheIgniteCacheConfiguration {
    public static final String CACHE_NAME = "even_numbers";
    public static final int CACHE_PARTITIONS = 12;

    private final Ignite ignite;
    private final CacheLoader cacheLoader;

    public ApacheIgniteCacheConfiguration(final Ignite ignite, final CacheLoader cacheLoader) {
        this.ignite = ignite;
        this.cacheLoader = cacheLoader;
    }

    @Scheduled(fixedDelay = Long.MAX_VALUE, initialDelay = 15_000L)
    public void createCache() {
        IgniteCache<String, Integer> cache = ignite.cache(CACHE_NAME);
        if (cache != null) {
            log.info("Successfully got cache[{}], so skipping configuration", cache);
            cacheLoader.loadCacheLocally(cache);
        } else {
            cache = ignite.getOrCreateCache(cacheConfiguration());
            cacheLoader.loadCache(cache);
        }
    }

    private CacheConfiguration<String, Integer> cacheConfiguration() {
        return new CacheConfiguration<String, Integer>()
//                .setAffinity(new RendezvousAffinityFunction(false, CACHE_PARTITIONS))
                .setCacheStoreFactory(FactoryBuilder.factoryOf(RandomNumberCacheLoadOnlyStoreAdapter.class))
                .setCacheStoreSessionListenerFactories(() -> new RandomNumberCacheStoreSessionListener())
//                .setEvictionPolicyFactory(() -> new FifoEvictionPolicy<>(Integer.MAX_VALUE))
//                .setOnheapCacheEnabled(true)
//                .setCacheMode(CacheMode.LOCAL)
                .setPartitionLossPolicy(PartitionLossPolicy.READ_ONLY_SAFE)
//                .setDataRegionName(ApacheIgniteConfiguration.CUSTOM_DATA_REGION)
                .setRebalanceMode(CacheRebalanceMode.NONE)
                .setRebalanceDelay(Long.MAX_VALUE)
                .setName(CACHE_NAME)
                .setTopologyValidator(new ClusterSizeTopologyValidator(3))
                ;
    }

    @Scheduled(fixedDelay = 5_000L, initialDelay = 25_000L)
    public void partitionMetrics() {
        try {
            final IgniteCache<Object, Object> cache = ignite.cache(ApacheIgniteCacheConfiguration.CACHE_NAME);

            final Collection<Integer> lostPartitions = cache.lostPartitions();
            log.info("Lost partitions check: {}", lostPartitions);

            final ClusterNode clusterNode = ignite.cluster().localNode();
            final int[] primaryPartitions = ignite.affinity(ApacheIgniteCacheConfiguration.CACHE_NAME).primaryPartitions(clusterNode);
            final int[] backupPartitions = ignite.affinity(ApacheIgniteCacheConfiguration.CACHE_NAME).backupPartitions(clusterNode);
            log.info("Primary partitions({}): {}", primaryPartitions.length, Arrays.toString(primaryPartitions));
            log.info("Backup partitions({}): {}", backupPartitions.length, Arrays.toString(backupPartitions));

            final Affinity<Object> affinity = ignite.affinity(ApacheIgniteCacheConfiguration.CACHE_NAME);


            final StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            final long partitionsLoaded = IntStream.range(0, affinity.partitions())
                    .mapToObj(partition -> cache.sizeLong(partition, CachePeekMode.ALL))
                    .filter(size -> size > 0)
                    .count();
            stopWatch.stop();
            log.info("Cache partitions loaded[{}]. Elapsed time.", partitionsLoaded, stopWatch);

            final IgniteAtomicLong partitionsLoadedAtomic = ignite.atomicLong("cacheLoaded", partitionsLoaded, true);
            partitionsLoadedAtomic.getAndSet(partitionsLoaded);
        } catch (final Exception e) {
            log.warn("Unable to get cache metrics", e);
        }
    }
}
