package com.pattycake.example.cache.store;

import com.pattycake.example.config.ApacheIgniteCacheConfiguration;
import com.pattycake.example.model.InputModel;
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

import javax.cache.integration.CacheLoaderException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class RandomNumberCacheLoadOnlyStoreAdapter extends CacheLoadOnlyStoreAdapter<String, Integer, InputModel> {

    public RandomNumberCacheLoadOnlyStoreAdapter() {
        setBatchSize(100);
    }

    /** Store session. */
    @CacheStoreSessionResource
    private CacheStoreSession ses;

    @IgniteInstanceResource
    private Ignite ignite;

    @Override
    protected Iterator<InputModel> inputIterator(@Nullable Object... args) throws CacheLoaderException {
        log.info("Getting InputModel Iterator");

        Set<Integer> partitions = partitions();
        log.info("Local node is assigned partitions: {}", partitions);

        return generateInput().stream()
                .filter(model -> partitions.contains(model.getValue() % ApacheIgniteCacheConfiguration.CACHE_PARTITIONS))
                .collect(Collectors.toList())
                .iterator();
    }

    private Set<Integer> partitions() {
        ClusterNode clusterNode = ignite.cluster().localNode();
        Affinity<Object> affinity = ignite.affinity(ApacheIgniteCacheConfiguration.CACHE_NAME);
        int[] partitions = affinity.primaryPartitions(clusterNode);
        return IntStream.of(partitions).mapToObj(Integer::valueOf).collect(Collectors.toSet());
    }

    @Override
    protected @Nullable IgniteBiTuple<String, Integer> parse(InputModel rec, @Nullable Object... args) {
        log.info("Adding InputModel[{}]", rec);
        return new IgniteBiTuple<>(rec.getKey(), rec.getValue());
    }

    private List<InputModel> generateInput() {
        return IntStream.range(1, 50).mapToObj( i -> InputModel.builder()
        .key(Integer.toString(i))
                .value(i)
                .build()
        )
                .collect(Collectors.toList());
    }


}
