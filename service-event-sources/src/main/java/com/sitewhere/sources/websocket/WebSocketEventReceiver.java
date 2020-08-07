/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.websocket;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.sitewhere.sources.InboundEventReceiver;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Event receiver that pulls data from a web socket.
 *
 * @param <T>
 */
public abstract class WebSocketEventReceiver<T> extends InboundEventReceiver<T> {

    /** User property that references the event reciever */
    public static final String PROP_EVENT_RECEIVER = "sw.event.receiver";

    /** Web socket session */
    private Session session;

    /** Web socket URL to connect to */
    private String webSocketUrl;

    /** Headers passed in web socket configuration */
    private Map<String, String> headers = new HashMap<String, String>();

    /**
     * Get concrete {@link Endpoint} implementation class.
     * 
     * @return
     */
    public abstract Class<? extends Endpoint> getWebSocketClientClass();

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	WebSocketContainer container = ContainerProvider.getWebSocketContainer();
	ClientEndpointConfig config = ClientEndpointConfig.Builder.create().configurator(new WebSocketConfigurator())
		.build();
	config.getUserProperties().put(PROP_EVENT_RECEIVER, this);
	try {
	    session = container.connectToServer(getWebSocketClientClass(), config, URI.create(getWebSocketUrl()));
	} catch (DeploymentException e) {
	    throw new SiteWhereException("Unable to connect to web socket.", e);
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to connect to web socket.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	try {
	    session.close();
	} catch (IOException e) {
	    getLogger().error("IOException closing web socket: ", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#
     * getDisplayName()
     */
    @Override
    public String getDisplayName() {
	return "WebSocket()";
    }

    /**
     * Configurator that passes in configured headers.
     */
    private class WebSocketConfigurator extends ClientEndpointConfig.Configurator {
	@Override
	public void beforeRequest(Map<String, List<String>> headers) {
	    Map<String, String> extHeaders = WebSocketEventReceiver.this.headers;
	    for (String key : extHeaders.keySet()) {
		headers.put(key, Arrays.asList(extHeaders.get(key)));
	    }
	    super.beforeRequest(headers);
	}
    }

    public String getWebSocketUrl() {
	return webSocketUrl;
    }

    public void setWebSocketUrl(String webSocketUrl) {
	this.webSocketUrl = webSocketUrl;
    }

    public Map<String, String> getHeaders() {
	return headers;
    }

    public void setHeaders(Map<String, String> headers) {
	this.headers = headers;
    }
}