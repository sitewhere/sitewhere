/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication.coap;

import java.util.Map;

import org.apache.log4j.Logger;

import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IInboundEventReceiver;
import com.sitewhere.spi.device.communication.IInboundEventSource;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IInboundEventReceiver} that starts a CoAP server using the
 * Eclipse Californium implementation.
 * 
 * @author Derek
 */
public class CoapServerEventReceiver extends LifecycleComponent implements IInboundEventReceiver<byte[]> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(CoapServerEventReceiver.class);

	/** Default hostname */
	private static final String DEFAULT_HOSTNAME = "localhost";

	/** Parent event source */
	private IInboundEventSource<byte[]> eventSource;

	/** Customized SiteWhere CoAP server */
	private SiteWhereCoapServer server;

	/** Hostname for binding socket */
	private String hostname = DEFAULT_HOSTNAME;

	public CoapServerEventReceiver() {
		super(LifecycleComponentType.InboundEventReceiver);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		if (server == null) {
			server = new SiteWhereCoapServer(this);
			server.listenOn(getHostname());
		}
		server.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		server.stop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "coap:" + getHostname();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IInboundEventReceiver#onEventPayloadReceived
	 * (java.lang.Object, java.util.Map)
	 */
	@Override
	public void onEventPayloadReceived(byte[] payload, Map<String, String> metadata) {
		getEventSource().onEncodedEventReceived(CoapServerEventReceiver.this, payload, metadata);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IInboundEventReceiver#setEventSource(com.
	 * sitewhere.spi.device.communication.IInboundEventSource)
	 */
	@Override
	public void setEventSource(IInboundEventSource<byte[]> source) {
		this.eventSource = source;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#getEventSource()
	 */
	@Override
	public IInboundEventSource<byte[]> getEventSource() {
		return eventSource;
	}

	public SiteWhereCoapServer getServer() {
		return server;
	}

	public void setServer(SiteWhereCoapServer server) {
		this.server = server;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
}