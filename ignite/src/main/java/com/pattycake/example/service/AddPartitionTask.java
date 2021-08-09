package com.pattycake.example.service;

import com.pattycake.example.config.ApacheIgniteCacheConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.resources.IgniteInstanceResource;

import javax.cache.Cache;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Slf4j
public class AddPartitionTask implements IgniteCallable<Integer> {

    @IgniteInstanceResource
    private Ignite ignite;

    private int partition;

    public AddPartitionTask(int partition) {
        this.partition = partition;
    }

    @Override
    public Integer call() throws Exception {
        ClusterNode clusterNode = ignite.cluster().localNode();
        Affinity<Object> affinity = ignite.affinity(ApacheIgniteCacheConfiguration.CACHE_NAME);
        int[] localPartitons = affinity.primaryPartitions(clusterNode);

        log.info("Running for partition[{}], All local partitions[{}]", partition, localPartitons);

        IgniteCache<String, Integer> cache = ignite.cache(ApacheIgniteCacheConfiguration.CACHE_NAME);
        try(QueryCursor<Cache.Entry<String, Integer>> cursor = cache.query(new ScanQuery<String, Integer>(partition).setLocal(true))) {
            Optional<Integer> sum = StreamSupport.stream(cursor.spliterator(), false)
                    .peek(entry -> log.info("key[{}] value[{}]", entry.getKey(), entry.getValue()))
                    .map(entry -> entry.getValue())
                    .reduce(Integer::sum);

            return sum.orElse(0);
        }
    }
}
