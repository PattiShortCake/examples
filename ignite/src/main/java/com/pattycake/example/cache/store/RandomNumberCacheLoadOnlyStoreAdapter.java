package com.pattycake.example.cache.store;

import com.pattycake.example.model.InputModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.cache.store.CacheLoadOnlyStoreAdapter;
import org.apache.ignite.cache.store.CacheStoreSession;
import org.apache.ignite.lang.IgniteBiTuple;
import org.apache.ignite.resources.CacheStoreSessionResource;
import org.jetbrains.annotations.Nullable;

import javax.cache.integration.CacheLoaderException;
import java.util.Iterator;
import java.util.List;
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

    @Override
    protected Iterator<InputModel> inputIterator(@Nullable Object... args) throws CacheLoaderException {
        log.info("Getting InputModel Iterator");

        if (args.length == 1 && args[0] instanceof Integer) {
            int divisor = (int) args[0];
            return generateInput().stream()
                    .filter(model -> model.getValue() % divisor == 0)
                    .collect(Collectors.toList())
                    .iterator();
        }
        return generateInput().iterator();
    }

    @Override
    protected @Nullable IgniteBiTuple<String, Integer> parse(InputModel rec, @Nullable Object... args) {
        log.info("Adding InputModel[{}]", rec);
        return new IgniteBiTuple<>(rec.getKey(), rec.getValue());
    }

    private List<InputModel> generateInput() {
        return IntStream.range(1, 10).mapToObj( i -> InputModel.builder()
        .key(Integer.toString(i))
                .value(i)
                .build()
        )
                .collect(Collectors.toList());
    }
}
