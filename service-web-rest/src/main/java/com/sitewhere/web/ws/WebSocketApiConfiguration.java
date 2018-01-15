/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.ws;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.sitewhere.web.ws.components.TopologyBroadcaster;

/**
 * Configures WebSockets used by Web/REST microservice.
 * 
 * @author Derek
 */
@Configuration
@EnableWebSocketMessageBroker
@ComponentScan(basePackageClasses = { TopologyBroadcaster.class })
public class WebSocketApiConfiguration extends AbstractWebSocketMessageBrokerConfigurer {

    /** URL prefix for matching WebSocket calls */
    public static final String WEB_SOCKET_MATCHER = "/ws/*";

    /*
     * @see org.springframework.web.socket.config.annotation.
     * AbstractWebSocketMessageBrokerConfigurer#configureMessageBroker(org.
     * springframework.messaging.simp.config.MessageBrokerRegistry)
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
	config.enableSimpleBroker("/topic");
	config.setApplicationDestinationPrefixes("/app");
    }

    /*
     * @see org.springframework.web.socket.config.annotation.
     * WebSocketMessageBrokerConfigurer#registerStompEndpoints(org.springframework.
     * web.socket.config.annotation.StompEndpointRegistry)
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
	registry.addEndpoint("/admin").setAllowedOrigins("*")
		.setHandshakeHandler(new DefaultHandshakeHandler(new TomcatRequestUpgradeStrategy())).withSockJS();
    }
}