/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.search.device;

import java.util.UUID;

import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.search.device.IDeviceStatusSearchCriteria;

/**
 * Provides extra information for searching device statuses.
 * 
 * @author Derek
 */
public class DeviceStatusSearchCriteria extends SearchCriteria implements IDeviceStatusSearchCriteria {

    /** Filter by device type */
    private UUID deviceTypeId;

    /** Filter by code */
    private String code;

    public DeviceStatusSearchCriteria(int pageNumber, int pageSize) {
	super(pageNumber, pageSize);
    }

    /*
     * @see
     * com.sitewhere.spi.search.device.IDeviceStatusSearchCriteria#getDeviceTypeId()
     */
    @Override
    public UUID getDeviceTypeId() {
	return deviceTypeId;
    }

    public void setDeviceTypeId(UUID deviceTypeId) {
	this.deviceTypeId = deviceTypeId;
    }

    /*
     * @see com.sitewhere.spi.search.device.IDeviceStatusSearchCriteria#getCode()
     */
    @Override
    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }
}