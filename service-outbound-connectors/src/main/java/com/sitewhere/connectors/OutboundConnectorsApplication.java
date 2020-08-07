/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.sitewhere.connectors.spi.microservice.IOutboundConnectorsMicroservice;
import com.sitewhere.microservice.MicroserviceApplication;

/**
 * Main application which runs the outbound connectors microservice.
 */
@ApplicationScoped
public class OutboundConnectorsApplication extends MicroserviceApplication<IOutboundConnectorsMicroservice> {

    @Inject
    private IOutboundConnectorsMicroservice microservice;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public IOutboundConnectorsMicroservice getMicroservice() {
	return microservice;
    }
}