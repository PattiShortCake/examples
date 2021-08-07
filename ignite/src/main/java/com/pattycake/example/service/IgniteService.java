package com.pattycake.example.service;

import com.pattycake.example.config.ApacheIgniteConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.lang.IgniteFuture;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.cache.Cache;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@Slf4j
public class IgniteService {

    private Ignite ignite;

    public IgniteService(Ignite ignite) {
        this.ignite = ignite;
    }

    @Scheduled(fixedDelay = Long.MAX_VALUE, initialDelay = 60_000L)
    public void printEven() {
        log.info("Getting management cache");

        log.info("Getting data cache");
        Affinity<Object> cache = ignite.affinity(ApacheIgniteConfiguration.CACHE_NAME);
        IgniteCompute compute = ignite.compute();

        List<IgniteFuture<Integer>> igniteFutures = IntStream.range(0, cache.partitions())
                .mapToObj(i -> compute.affinityCallAsync(Arrays.asList(ApacheIgniteConfiguration.CACHE_NAME), i, new AddPartitionTask(i)))
                .collect(Collectors.toList());

        igniteFutures.stream()
                .map(future -> future.get())
                .forEach(sum ->log.info("sum[{}]", sum));
    }

}
