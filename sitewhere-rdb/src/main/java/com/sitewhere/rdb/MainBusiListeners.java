package com.sitewhere.rdb;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 *  Spring context listener
 *
 *  Simeon Chen
 */
public class MainBusiListeners implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContextUtils.setContext(event.getApplicationContext());
    }
}