package com.pattycake.example.event;

import com.pattycake.example.cache.loader.CacheLoader;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteEvents;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.events.CacheEvent;
import org.apache.ignite.events.DiscoveryEvent;
import org.apache.ignite.events.Event;
import org.apache.ignite.events.EventType;
import org.apache.ignite.internal.events.DiscoveryCustomEvent;
import org.apache.ignite.internal.processors.cache.CacheAffinityChangeMessage;
import org.apache.ignite.lang.IgnitePredicate;
import org.springframework.beans.factory.InitializingBean;

//@Component
@Slf4j
public class IgniteEventHandler implements InitializingBean {

    private final Ignite ignite;

    public IgniteEventHandler(final Ignite ignite) {
        this.ignite = ignite;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final IgniteEvents events = ignite.events();
        events.localListen(localListener(), EventType.EVTS_ALL);
    }

    private IgnitePredicate<Event> localListener() {
        return event -> {
            if (event.type() != EventType.EVT_NODE_METRICS_UPDATED) {
                log.info("Event[{}]", event);
            }
            if (event instanceof CacheEvent) {
                handleEvent((CacheEvent) event);
            } else if (event instanceof DiscoveryCustomEvent) {
                handleEvent((DiscoveryCustomEvent) event);
            } else if (event instanceof DiscoveryEvent) {
                handleEvent((DiscoveryEvent) event);
            }
            return true;
        };
    }

    private void handleEvent(final DiscoveryEvent event) {
        if (event.type() == EventType.EVT_NODE_LEFT) {
          final IgniteCache<Object, Object> cache = ignite.cache(CacheLoader.CACHE_NAME);
          final Collection<Integer> lostPartitions = cache.lostPartitions();

          log.error("Lost partitions: {} Local partitions: {}", lostPartitions, partitions());
        }
    }

    private void handleEvent(final DiscoveryCustomEvent event) {
        if (event.customMessage() instanceof CacheAffinityChangeMessage) {
            log.info("Yo, the affinity changed!  Local partitions: {}", partitions());
        }
    }

    private Set<Integer> partitions() {
      final ClusterNode clusterNode = ignite.cluster().localNode();
      final Affinity<Object> affinity = ignite.affinity(CacheLoader.CACHE_NAME);
      final int[] partitions = affinity.primaryPartitions(clusterNode);
      return IntStream.of(partitions).mapToObj(Integer::valueOf).collect(Collectors.toSet());
    }


    private void handleEvent(final CacheEvent cacheEvent) {
        if (cacheEvent.type() == EventType.EVT_CACHE_STARTED) {
            try {
                log.info("Waiting a few moments before loading cache");
                Thread.sleep(10_000L);
//                loadCache(cacheEvent.cacheName());
            } catch (final InterruptedException e) {
                throw new RuntimeException("Error when loading cache", e);
            }
        }
    }

}
