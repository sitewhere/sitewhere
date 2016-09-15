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

    /** Specification to match in filter */
    private String specificationToken;

    /** Site to match in filter */
    private String siteToken;

    /** Indicates if assigned devices should be excluded */
    private boolean excludeAssigned = true;

    public DeviceSearchCriteria(int pageNumber, int pageSize, Date startDate, Date endDate) {
	super(pageNumber, pageSize, startDate, endDate);
    }

    public DeviceSearchCriteria(String specToken, String siteToken, boolean excludeAssigned, int pageNumber,
	    int pageSize, Date startDate, Date endDate) {
	super(pageNumber, pageSize, startDate, endDate);
	this.specificationToken = specToken;
	this.siteToken = siteToken;
	this.excludeAssigned = excludeAssigned;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.search.device.IDeviceSearchCriteria#
     * getSpecificationToken()
     */
    public String getSpecificationToken() {
	return specificationToken;
    }

    public void setSpecificationToken(String specificationToken) {
	this.specificationToken = specificationToken;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.search.device.IDeviceSearchCriteria#getSiteToken()
     */
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
    public boolean isExcludeAssigned() {
	return excludeAssigned;
    }

    public void setExcludeAssigned(boolean excludeAssigned) {
	this.excludeAssigned = excludeAssigned;
    }
}