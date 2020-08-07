/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.coap;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.config.NetworkConfig;

import com.sitewhere.sources.InboundEventReceiver;
import com.sitewhere.sources.configuration.eventsource.coap.CoapServerConfiguration;
import com.sitewhere.sources.spi.IInboundEventReceiver;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Implementation of {@link IInboundEventReceiver} that starts a CoAP server
 * using the Eclipse Californium implementation.
 */
public class CoapServerEventReceiver extends InboundEventReceiver<byte[]> {

    /** Configuration */
    private CoapServerConfiguration configuration;

    /** Customized SiteWhere CoAP server */
    private CoapServer server;

    /** CoAP message deliverer */
    private CoapMessageDeliverer messageDeliverer;

    public CoapServerEventReceiver(CoapServerConfiguration configuration) {
	this.configuration = configuration;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.messageDeliverer = new CoapMessageDeliverer(this);
	this.server = new CoapServer(NetworkConfig.createStandardWithoutFile(), getConfiguration().getPort());
	server.setMessageDeliverer(getMessageDeliverer());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getServer().start();
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
	getServer().stop();
    }

    protected CoapServerConfiguration getConfiguration() {
	return configuration;
    }

    protected CoapServer getServer() {
	return server;
    }

    protected void setServer(CoapServer server) {
	this.server = server;
    }

    protected CoapMessageDeliverer getMessageDeliverer() {
	return messageDeliverer;
    }

    protected void setMessageDeliverer(CoapMessageDeliverer messageDeliverer) {
	this.messageDeliverer = messageDeliverer;
    }
}