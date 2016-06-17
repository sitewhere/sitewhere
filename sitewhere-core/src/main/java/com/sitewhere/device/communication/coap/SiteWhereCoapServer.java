/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication.coap;

import java.net.InetSocketAddress;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;

import com.sitewhere.spi.device.communication.IInboundEventReceiver;

/**
 * Extends default CoAP server functionality to support SiteWhere requests.
 * 
 * @author Derek
 */
public class SiteWhereCoapServer extends CoapServer {

	/** Supplies standard CoAP port */
	private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);

	/** Default hostname */
	private static final String DEFAULT_HOSTNAME = "localhost";

	/** Hostname for binding socket */
	private String hostname = DEFAULT_HOSTNAME;

	public SiteWhereCoapServer(IInboundEventReceiver<byte[]> receiver) {
		super();
		setMessageDeliverer(new SiteWhereMessageDeliverer(receiver));
	}

	/**
	 * Instructs server to listen on the given hostname.
	 * 
	 * @param hostname
	 */
	public void listenOn(String hostname) {
		this.hostname = hostname;
		InetSocketAddress bindToAddress = new InetSocketAddress(getHostname(), COAP_PORT);
		addEndpoint(new CoapEndpoint(bindToAddress));
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
}