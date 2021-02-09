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