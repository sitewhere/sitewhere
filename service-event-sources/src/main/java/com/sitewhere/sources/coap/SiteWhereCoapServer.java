/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.coap;

import java.net.InetSocketAddress;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;

import com.sitewhere.spi.device.communication.IInboundEventReceiver;

/**
 * Extends default CoAP server functionality to support SiteWhere requests.
 * 
 * @author Derek
 */
public class SiteWhereCoapServer extends CoapServer {

    public SiteWhereCoapServer(IInboundEventReceiver<byte[]> receiver, String hostname, int port) {
	super();
	setMessageDeliverer(new SiteWhereMessageDeliverer(receiver));
	InetSocketAddress bindToAddress = new InetSocketAddress(hostname, port);
	addEndpoint(new CoapEndpoint(bindToAddress));
    }
}