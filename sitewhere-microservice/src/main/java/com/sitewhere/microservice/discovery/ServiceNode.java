/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.discovery;

import com.sitewhere.spi.microservice.discovery.IServiceNode;
import com.sitewhere.spi.microservice.discovery.ServiceNodeStatus;

/**
 * Contains node information for service discovery.
 * 
 * @author Derek
 */
public class ServiceNode implements IServiceNode {

    /** Node address */
    private String address;

    /** Service node status */
    private ServiceNodeStatus status;

    public String getAddress() {
	return address;
    }

    public void setAddress(String address) {
	this.address = address;
    }

    public ServiceNodeStatus getStatus() {
	return status;
    }

    public void setStatus(ServiceNodeStatus status) {
	this.status = status;
    }
}