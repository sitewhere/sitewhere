/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.search.device;

import java.util.Date;

import com.sitewhere.rest.model.search.DateRangeSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;

/**
 * Adds options specific to device searches.
 * 
 * @author Derek
 */
public class DeviceSearchCriteria extends DateRangeSearchCriteria implements IDeviceSearchCriteria {

    /** Search criteria for getting all records */
    public static DeviceSearchCriteria ALL = new DeviceSearchCriteria(null, null, false, 1, 0, null, null);

    /** Device type to match in filter */
    private String deviceTypeToken;

    /** Site to match in filter */
    private String siteToken;

    /** Indicates if assigned devices should be excluded */
    private boolean excludeAssigned = true;

    public DeviceSearchCriteria(int pageNumber, int pageSize, Date startDate, Date endDate) {
	super(pageNumber, pageSize, startDate, endDate);
    }

    public DeviceSearchCriteria(String deviceTypeToken, String siteToken, boolean excludeAssigned, int pageNumber,
	    int pageSize, Date startDate, Date endDate) {
	super(pageNumber, pageSize, startDate, endDate);
	this.deviceTypeToken = deviceTypeToken;
	this.siteToken = siteToken;
	this.excludeAssigned = excludeAssigned;
    }

    /*
     * @see
     * com.sitewhere.spi.search.device.IDeviceSearchCriteria#getDeviceTypeToken()
     */
    @Override
    public String getDeviceTypeToken() {
	return deviceTypeToken;
    }

    public void setDeviceTypeToken(String deviceTypeToken) {
	this.deviceTypeToken = deviceTypeToken;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.search.device.IDeviceSearchCriteria#getSiteToken()
     */
    @Override
    public String getSiteToken() {
	return siteToken;
    }

    public void setSiteToken(String siteToken) {
	this.siteToken = siteToken;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.search.device.IDeviceSearchCriteria#isExcludeAssigned()
     */
    @Override
    public boolean isExcludeAssigned() {
	return excludeAssigned;
    }

    public void setExcludeAssigned(boolean excludeAssigned) {
	this.excludeAssigned = excludeAssigned;
    }
}