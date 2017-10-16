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
import com.sitewhere.spi.search.device.IAssignmentSearchCriteria;

/**
 * Default implementation of {@link IAssignmentSearchCriteria}.
 * 
 * @author Derek
 */
public class AssignmentSearchCriteria extends SearchCriteria implements IAssignmentSearchCriteria {

    /** Only return results with the given status */
    private DeviceAssignmentStatus status;

    public AssignmentSearchCriteria(int pageNumber, int pageSize) {
	super(pageNumber, pageSize);
    }

    public DeviceAssignmentStatus getStatus() {
	return status;
    }

    public void setStatus(DeviceAssignmentStatus status) {
	this.status = status;
    }
}