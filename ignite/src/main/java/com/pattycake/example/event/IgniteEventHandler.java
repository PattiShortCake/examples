package com.pattycake.example.event;

import com.pattycake.example.config.ApacheIgniteConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteEvents;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.events.Event;
import org.apache.ignite.events.EventType;
import org.apache.ignite.lang.IgnitePredicate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IgniteEventHandler implements InitializingBean  {

    private final Ignite ignite;

    public IgniteEventHandler(Ignite ignite) {
        this.ignite = ignite;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        IgniteEvents events = ignite.events();
        events.localListen(localListener(), EventType.EVTS_ALL);
    }

    private IgnitePredicate<Event> localListener() {
        return event -> {
            log.info("Event[{}]", event);
            return true;
        };
    }

    private int[] partitions() {
        ClusterNode clusterNode = ignite.cluster().localNode();
        Affinity<Object> affinity = ignite.affinity(ApacheIgniteConfiguration.CACHE_NAME);
        return affinity.primaryPartitions(clusterNode);
    }

    @Scheduled(fixedDelay = Long.MAX_VALUE, initialDelay = 30_000L)
    public void loadCache() throws InterruptedException {
        IgniteCache<Object, Object> cache = ignite.getOrCreateCache(ApacheIgniteConfiguration.CACHE_NAME);
        int beforeLoadSize = cache.size(CachePeekMode.ALL);

        int[] partitions = partitions();
        log.info("Local node is assigned partitions: {}", partitions);

        cache.localLoadCache((a, b) -> true, partitions);
        int afterLoadSize = cache.size(CachePeekMode.ALL);
        log.info("Cache is toasty size {} -> {}", beforeLoadSize, afterLoadSize);
        Thread.sleep(10_000L);
        log.info("Cache Ready");
    }
}
