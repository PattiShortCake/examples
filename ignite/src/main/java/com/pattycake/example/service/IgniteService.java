package com.pattycake.example.service;

import com.pattycake.example.config.ApacheIgniteConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.springframework.stereotype.Service;

import javax.cache.Cache;
import java.util.Collection;

@Service
@Slf4j
public class IgniteService {

    private Ignite ignite;

    public IgniteService(Ignite ignite) {
        this.ignite = ignite;
    }

    public void printEven() {
        log.info("Getting management cache");
        IgniteCache<Object, Object> managementCache = ignite.getOrCreateCache(ApacheIgniteConfiguration.MANAGEMENT_CACHE_NAME);

        log.info("Getting data cache");
        IgniteCache<String, Integer> cache = ignite.cache(ApacheIgniteConfiguration.CACHE_NAME);

        // Better if this is centralized
        log.info("Checking whether data cache is ready");
        while(! (boolean) managementCache.get("this-node-ready")) {
            try {
                log.info("Cache not warm yet");
                Thread.sleep(1_000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        try (QueryCursor<Cache.Entry<String, Integer>> cursor = cache.query(new ScanQuery<>())) {
            cursor.forEach(entry -> log.info("key[{}] value[{}]", entry.getKey(), entry.getValue()));
        }
    }
}
