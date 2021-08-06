package com.pattycake.example.event;

import com.pattycake.example.config.ApacheIgniteConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteEvents;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.events.CacheEvent;
import org.apache.ignite.events.Event;
import org.apache.ignite.events.EventType;
import org.apache.ignite.lang.IgnitePredicate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IgniteEventHandler implements InitializingBean  {

    private Ignite ignite;

    public IgniteEventHandler(Ignite ignite) {
        this.ignite = ignite;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        IgniteEvents events = ignite.events();
        events.localListen(localListener(), EventType.EVTS_ALL);

        IgniteCache<Object, Object> cache = ignite.getOrCreateCache(ApacheIgniteConfiguration.CACHE_NAME);
        IgniteCache<Object, Object> managementCache = ignite.getOrCreateCache(ApacheIgniteConfiguration.MANAGEMENT_CACHE_NAME);

        managementCache.put("this-node-ready", false);
        int beforeLoadSize = cache.size(CachePeekMode.ALL);
        cache.localLoadCache((a, b) -> true, 2);
        int afterLoadSize = cache.size(CachePeekMode.ALL);
        log.info("Cache is toasty size {} -> {}", beforeLoadSize, afterLoadSize);
        Thread.sleep(10_000L);
        managementCache.put("this-node-ready", true);
        log.info("Cache Ready");
    }

    private IgnitePredicate<Event> localListener() {
        return event -> {
            log.info("Event[{}]", event);
            return true;
        };
    }
}
