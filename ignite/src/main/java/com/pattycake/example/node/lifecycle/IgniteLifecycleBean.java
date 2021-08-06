package com.pattycake.example.node.lifecycle;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.IgniteException;
import org.apache.ignite.lifecycle.LifecycleBean;
import org.apache.ignite.lifecycle.LifecycleEventType;

@Slf4j
public class IgniteLifecycleBean implements LifecycleBean {

    @Override
    public void onLifecycleEvent(LifecycleEventType evt) throws IgniteException {
        log.info("Node event[{}]", evt.name());
    }
}
