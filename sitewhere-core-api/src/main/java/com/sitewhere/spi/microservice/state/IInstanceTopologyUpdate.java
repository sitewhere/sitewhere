/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.state;

/**
 * Indicates an update to instance topology.
 * 
 * @author Derek
 */
public interface IInstanceTopologyUpdate {

    /**
     * Get microservice identifier.
     * 
     * @return
     */
    public String getMicroserviceIdentifier();

    /**
     * Get hostname for microservice.
     * 
     * @return
     */
    public String getMicroserviceHostname();

    /**
     * Get topology update type.
     * 
     * @return
     */
    public InstanceTopologyUpdateType getType();
}