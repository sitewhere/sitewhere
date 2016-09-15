/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.core.test.websocket;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;

/**
 * Configuration for endpoint that serves Strings.
 * 
 * @author Derek
 */
public class WebSocketConfiguration implements ServerApplicationConfig {

    /*
     * (non-Javadoc)
     * 
     * @see javax.websocket.server.ServerApplicationConfig#
     * getAnnotatedEndpointClasses( java.util.Set)
     */
    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> annotated) {
	return Collections.emptySet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.websocket.server.ServerApplicationConfig#getEndpointConfigs(java.
     * util .Set)
     */
    @Override
    public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpoints) {
	Set<ServerEndpointConfig> configs = new HashSet<ServerEndpointConfig>();
	configs.add(ServerEndpointConfig.Builder.create(StringSender.class, "/stringsender").build());
	configs.add(ServerEndpointConfig.Builder.create(BinarySender.class, "/binarysender").build());
	return configs;
    }
}
