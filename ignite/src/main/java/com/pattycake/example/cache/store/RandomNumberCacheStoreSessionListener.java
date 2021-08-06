package com.pattycake.example.cache.store;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cache.store.CacheStoreSession;
import org.apache.ignite.cache.store.CacheStoreSessionListener;
import org.apache.ignite.lifecycle.LifecycleAware;

@Slf4j
public class RandomNumberCacheStoreSessionListener implements CacheStoreSessionListener, LifecycleAware {
    @Override
    public void onSessionStart(CacheStoreSession ses) {
        log.info("Session Start");
    }

    @Override
    public void onSessionEnd(CacheStoreSession ses, boolean commit) {
        log.info("Session end");
    }

    @Override
    public void start() throws IgniteException {
        log.info("Start life cycle");
    }

    @Override
    public void stop() throws IgniteException {
        log.info("Stop life cycle");
    }
}
