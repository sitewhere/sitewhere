/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.state;

/**
 * Entry for a microservice within the instance topology.
 * 
 * @author Derek
 */
public interface IInstanceTopologyEntry {

    /**
     * Get microservice details.
     * 
     * @return
     */
    public IMicroserviceDetails getMicroserviceDetails();

    /**
     * Get timestamp for last updated.
     * 
     * @return
     */
    public long getLastUpdated();
}
