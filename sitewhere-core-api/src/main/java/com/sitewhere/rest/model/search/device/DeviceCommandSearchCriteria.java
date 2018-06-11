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
import com.sitewhere.spi.search.device.IDeviceCommandSearchCriteria;

/**
 * Provides extra information for searching device commands.
 * 
 * @author Derek
 */
public class DeviceCommandSearchCriteria extends SearchCriteria implements IDeviceCommandSearchCriteria {

    /** Filter by device type */
    private UUID deviceTypeId;

    public DeviceCommandSearchCriteria(int pageNumber, int pageSize) {
	super(pageNumber, pageSize);
    }

    /*
     * @see
     * com.sitewhere.spi.search.device.IDeviceCommandSearchCriteria#getDeviceTypeId(
     * )
     */
    @Override
    public UUID getDeviceTypeId() {
	return deviceTypeId;
    }

    public void setDeviceTypeId(UUID deviceTypeId) {
	this.deviceTypeId = deviceTypeId;
    }
}