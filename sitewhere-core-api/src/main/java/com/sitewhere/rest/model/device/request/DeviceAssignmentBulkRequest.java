/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request;

import java.util.List;

import com.sitewhere.spi.device.request.IDeviceAssignmentBulkRequest;

/**
 * Contains information about a request that applies to multiple device
 * assignments.
 * 
 * @author Derek
 */
public class DeviceAssignmentBulkRequest implements IDeviceAssignmentBulkRequest {

    /** Device assignment tokens */
    private List<String> deviceAssignmentTokens;

    /*
     * @see com.sitewhere.spi.device.request.IDeviceAssignmentBulkRequest#
     * getDeviceAssignmentTokens()
     */
    @Override
    public List<String> getDeviceAssignmentTokens() {
	return deviceAssignmentTokens;
    }

    public void setDeviceAssignmentTokens(List<String> deviceAssignmentTokens) {
	this.deviceAssignmentTokens = deviceAssignmentTokens;
    }
}
