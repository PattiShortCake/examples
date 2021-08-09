package com.pattycake.example.cache.loader;

import com.pattycake.example.config.ApacheIgniteCacheConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.cluster.ClusterNode;
import org.springframework.stereotype.Component;

import javax.cache.CacheException;

@Slf4j
@Component
public class CacheLoader {

    private Ignite ignite;

    public CacheLoader(Ignite ignite) {
        this.ignite = ignite;
    }
    public void loadCache(String name) throws InterruptedException {
        do {
            try {
                IgniteCache<Object, Object> cache = ignite.cache(name);
                loadCache(cache);
                break;
            } catch(CacheException | IgniteException e) {
                log.warn("Error getting cache, will try again.  Reason: {}", e.getMessage());
                Thread.sleep(1_000L);
            }
        } while(true);
    }

    public <K,V> void loadCache(IgniteCache<K, V> cache) {
        log.info("Attempting to load cache");

        int beforeLoadSize = cache.size(CachePeekMode.ALL);

        cache.loadCache((a, b) -> true);
        int afterLoadSize = cache.size(CachePeekMode.ALL);
        log.info("Cache is toasty size {} -> {}", beforeLoadSize, afterLoadSize);
        log.info("Cache Ready");
    }

    public <K,V> void loadCacheLocally(IgniteCache<K, V> cache) {
        log.info("Attempting to load cache locally");

        int beforeLoadSize = cache.size(CachePeekMode.ALL);

        cache.localLoadCache((a, b) -> true);
        int afterLoadSize = cache.size(CachePeekMode.ALL);
        log.info("Cache is toasty size {} -> {}", beforeLoadSize, afterLoadSize);
        log.info("Cache Ready");
    }

}
