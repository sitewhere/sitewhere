/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.tenant;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.microservice.multitenant.IDatasetTemplate;

/**
 * Model object for an {@link IDatasetTemplate}.
 */
@JsonInclude(Include.NON_NULL)
public class DatasetTemplate implements IDatasetTemplate {

    /** Template id */
    private String id;

    /** Template name */
    private String name;

    /** Model initializers */
    private Initializers initializers;

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IDatasetTemplate#getId()
     */
    @Override
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IDatasetTemplate#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.multitenant.IDatasetTemplate#getInitializers()
     */
    @Override
    public Initializers getInitializers() {
	return initializers;
    }

    public void setInitializers(Initializers initializers) {
	this.initializers = initializers;
    }

    /**
     * Model initializers.
     */
    public static class Initializers implements IDatasetTemplate.Initializers {

	/** Device management Groovy script location */
	private List<String> deviceManagement;

	/** Event management Groovy script location */
	private List<String> eventManagement;

	/** Asset management Groovy script location */
	private List<String> assetManagement;

	/** Schedule management Groovy script location */
	private List<String> scheduleManagement;

	/*
	 * @see
	 * com.sitewhere.spi.microservice.multitenant.IDatasetTemplate.Initializers#
	 * getDeviceManagement()
	 */
	@Override
	public List<String> getDeviceManagement() {
	    return deviceManagement;
	}

	public void setDeviceManagement(List<String> deviceManagement) {
	    this.deviceManagement = deviceManagement;
	}

	/*
	 * @see
	 * com.sitewhere.spi.microservice.multitenant.IDatasetTemplate.Initializers#
	 * getEventManagement()
	 */
	@Override
	public List<String> getEventManagement() {
	    return eventManagement;
	}

	public void setEventManagement(List<String> eventManagement) {
	    this.eventManagement = eventManagement;
	}

	/*
	 * @see
	 * com.sitewhere.spi.microservice.multitenant.IDatasetTemplate.Initializers#
	 * getAssetManagement()
	 */
	@Override
	public List<String> getAssetManagement() {
	    return assetManagement;
	}

	public void setAssetManagement(List<String> assetManagement) {
	    this.assetManagement = assetManagement;
	}

	/*
	 * @see
	 * com.sitewhere.spi.microservice.multitenant.IDatasetTemplate.Initializers#
	 * getScheduleManagement()
	 */
	@Override
	public List<String> getScheduleManagement() {
	    return scheduleManagement;
	}

	public void setScheduleManagement(List<String> scheduleManagement) {
	    this.scheduleManagement = scheduleManagement;
	}
    }
}
