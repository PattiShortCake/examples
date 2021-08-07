package com.pattycake.example.cache.store;

import com.pattycake.example.config.ApacheIgniteConfiguration;
import com.pattycake.example.model.InputModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.cache.store.CacheLoadOnlyStoreAdapter;
import org.apache.ignite.cache.store.CacheStoreSession;
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

        if (args[0] instanceof int[]) {
            Set<Integer> partitions = IntStream.of((int[]) args[0]).mapToObj(Integer::valueOf).collect(Collectors.toSet());

            return generateInput().stream()
                    .filter(model -> partitions.contains(model.getValue() % ApacheIgniteConfiguration.CACHE_PARTITIONS))
                    .collect(Collectors.toList())
                    .iterator();
        }

        return Collections.emptyIterator();
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
