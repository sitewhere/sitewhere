/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.discovery;

/**
 * Remote node that provides a SiteWhere service.
 * 
 * @author Derek
 */
public interface IServiceNode {

    /**
     * Get address used to access node.
     * 
     * @return
     */
    public String getAddress();

    /**
     * Get service node status.
     * 
     * @return
     */
    public ServiceNodeStatus getStatus();
}