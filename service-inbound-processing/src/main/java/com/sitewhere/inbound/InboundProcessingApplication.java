/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.sitewhere.inbound.spi.microservice.IInboundProcessingMicroservice;
import com.sitewhere.microservice.MicroserviceApplication;

/**
 * Main application which runs the inbound processing microservice.
 */
@ApplicationScoped
public class InboundProcessingApplication extends MicroserviceApplication<IInboundProcessingMicroservice> {

    @Inject
    private IInboundProcessingMicroservice microservice;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public IInboundProcessingMicroservice getMicroservice() {
	return microservice;
    }
}