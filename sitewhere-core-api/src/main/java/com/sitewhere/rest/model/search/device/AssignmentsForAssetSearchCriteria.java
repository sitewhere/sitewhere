/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.search.device;

import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.search.device.IAssignmentsForAssetSearchCriteria;

/**
 * Criteria used to search for assignments associated with a given asset type.
 * 
 * @author Derek
 */
public class AssignmentsForAssetSearchCriteria extends SearchCriteria implements IAssignmentsForAssetSearchCriteria {

    /** Limit by site */
    private String siteToken;

    /** Limit by status */
    private DeviceAssignmentStatus status;

    public AssignmentsForAssetSearchCriteria(int pageNumber, int pageSize) {
	super(pageNumber, pageSize);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.search.device.IAssignmentsForAssetSearchCriteria#
     * getSiteToken()
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
     * @see com.sitewhere.spi.search.device.IAssignmentsForAssetSearchCriteria#
     * getStatus()
     */
    public DeviceAssignmentStatus getStatus() {
	return status;
    }

    public void setStatus(DeviceAssignmentStatus status) {
	this.status = status;
    }
}