/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors;

import com.sitewhere.connectors.spi.microservice.IOutboundConnectorsMicroservice;
import com.sitewhere.microservice.MicroserviceApplication;

/**
 * Spring Boot application for outbound connectors microservice.
 * 
 * @author Derek
 */
public class OutboundConnectorsApplication extends MicroserviceApplication<IOutboundConnectorsMicroservice> {

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