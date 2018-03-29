/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.management;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroserviceManagement;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Handles connectivity to GRPC management interfaces for all microservices
 * running for an instance.
 * 
 * @author Derek
 */
public interface IMicroserviceManagementCoordinator extends ILifecycleComponent {

    /**
     * Get management interface for a microservice based on service identifier. Note
     * that if mutiple instances of the microservice are running, mulitiple calls to
     * this method may interact with different services.
     * 
     * @param identifier
     * @return
     * @throws SiteWhereException
     */
    public IMicroserviceManagement getMicroserviceManagement(MicroserviceIdentifier identifier)
	    throws SiteWhereException;
}