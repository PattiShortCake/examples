package com.pattycake.example.service;

import com.pattycake.example.config.ApacheIgniteCacheConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.lang.IgniteFuture;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class IgniteService {

    private final Ignite ignite;

    public IgniteService(final Ignite ignite) {
        this.ignite = ignite;
    }

    //    @Scheduled(fixedDelay = Long.MAX_VALUE, initialDelay = 60_000L)
    public void printEven() {
        log.info("Getting management cache");

        log.info("Getting data cache");
        final Affinity<Object> cache = ignite.affinity(ApacheIgniteCacheConfiguration.CACHE_NAME);
        final IgniteCompute compute = ignite.compute();

        final List<IgniteFuture<Integer>> igniteFutures = IntStream.range(0, cache.partitions())
                .mapToObj(i -> compute.affinityCallAsync(Arrays.asList(ApacheIgniteCacheConfiguration.CACHE_NAME), i, new AddPartitionTask(i)))
                .collect(Collectors.toList());

        igniteFutures.stream()
                .map(future -> future.get())
                .forEach(sum -> log.info("sum[{}]", sum));
    }
}
