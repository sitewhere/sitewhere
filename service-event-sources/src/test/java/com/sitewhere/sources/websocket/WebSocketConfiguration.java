/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.sources.websocket;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;

/**
 * Configuration for endpoint that serves Strings.
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
     * @see javax.websocket.server.ServerApplicationConfig#getEndpointConfigs(java.
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
