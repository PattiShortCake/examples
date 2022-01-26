package com.pattycake.example.cache.store;

import com.pattycake.example.config.ApacheIgniteCacheConfiguration;
import com.pattycake.example.model.InputModel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.cache.integration.CacheLoaderException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.cache.store.CacheLoadOnlyStoreAdapter;
import org.apache.ignite.cache.store.CacheStoreSession;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.lang.IgniteBiTuple;
import org.apache.ignite.resources.CacheStoreSessionResource;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.jetbrains.annotations.Nullable;

@Slf4j
public class RandomNumberCacheLoadOnlyStoreAdapter extends CacheLoadOnlyStoreAdapter<String, InputModel, InputModel> {

    private final AtomicInteger counter = new AtomicInteger();
    /**
     * Store session.
     */
    @CacheStoreSessionResource
    private CacheStoreSession ses;
    @IgniteInstanceResource
    private Ignite ignite;

    public RandomNumberCacheLoadOnlyStoreAdapter() {
        setBatchSize(100);
    }

    @Override
    protected Iterator<InputModel> inputIterator(@Nullable final Object... args) throws CacheLoaderException {
        log.info("Getting InputModel Iterator");

        final Set<Integer> partitions = partitions();
        log.info("Local node is assigned partitions: {}", partitions);

        return generateInput().stream()
//                .filter(model -> partitions.contains(model.getValue() % ApacheIgniteCacheConfiguration.CACHE_PARTITIONS))
            .collect(Collectors.toList())
            .iterator();
    }

    private Set<Integer> partitions() {
        final ClusterNode clusterNode = ignite.cluster().localNode();
        final Affinity<Object> affinity = ignite.affinity(ApacheIgniteCacheConfiguration.CACHE_NAME);
        final int[] partitions = affinity.primaryPartitions(clusterNode);
        return IntStream.of(partitions).mapToObj(Integer::valueOf).collect(Collectors.toSet());
    }

    @Override
    protected @Nullable IgniteBiTuple<String, InputModel> parse(final InputModel rec, @Nullable final Object... args) {
//        log.info("Adding InputModel[{}]", rec);
        final int count = counter.incrementAndGet();
        if (count % 5_000 == 0) {
            log.info("Loaded {} records", count);
        }
        return new IgniteBiTuple<>(rec.getKey(), rec);
    }

    private List<InputModel> generateInput() {
        return IntStream.range(0, 100_000).mapToObj(i -> InputModel.builder()
                .key(Integer.toString(i))
                .value(i)
                .build()
            )
            .collect(Collectors.toList());
    }


}
