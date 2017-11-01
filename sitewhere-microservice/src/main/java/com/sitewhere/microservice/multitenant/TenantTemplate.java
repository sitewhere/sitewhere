/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.multitenant;

import java.util.List;

import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;

/**
 * Model object for an {@link ITenantTemplate}.
 * 
 * @author Derek
 */
public class TenantTemplate implements ITenantTemplate {

    /** Template id */
    private String id;

    /** Template name */
    private String name;

    /** Model initializers */
    private Initializers initializers;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.tenant.ITenantTemplate#getId()
     */
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.tenant.ITenantTemplate#getName()
     */
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.tenant.ITenantTemplate#getInitializers()
     */
    public Initializers getInitializers() {
	return initializers;
    }

    public void setInitializers(Initializers initializers) {
	this.initializers = initializers;
    }

    /**
     * Model initializers.
     * 
     * @author Derek
     */
    public static class Initializers implements ITenantTemplate.Initializers {

	/** Device management Groovy script location */
	private List<String> deviceManagement;

	/** Asset management Groovy script location */
	private List<String> assetManagement;

	/** Schedule management Groovy script location */
	private List<String> scheduleManagement;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.tenant.ITenantTemplate.Initializers#
	 * getDeviceManagement()
	 */
	public List<String> getDeviceManagement() {
	    return deviceManagement;
	}

	public void setDeviceManagement(List<String> deviceManagement) {
	    this.deviceManagement = deviceManagement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.tenant.ITenantTemplate.Initializers#
	 * getAssetManagement()
	 */
	public List<String> getAssetManagement() {
	    return assetManagement;
	}

	public void setAssetManagement(List<String> assetManagement) {
	    this.assetManagement = assetManagement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.tenant.ITenantTemplate.Initializers#
	 * getScheduleManagement()
	 */
	public List<String> getScheduleManagement() {
	    return scheduleManagement;
	}

	public void setScheduleManagement(List<String> scheduleManagement) {
	    this.scheduleManagement = scheduleManagement;
	}
    }
}