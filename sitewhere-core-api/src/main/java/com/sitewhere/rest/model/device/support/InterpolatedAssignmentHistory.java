/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.support;

import java.util.List;

/**
 * Holds interpolated locations for an assignment in one-minute intervals.
 * 
 * @author Derek Adams
 */
public class InterpolatedAssignmentHistory {

    /** Device assignment token */
    private String deviceAssignmentToken;

    /** List of history entries */
    private List<DeviceAssignmentHistoryEntry> slots;

    public String getDeviceAssignmentToken() {
	return deviceAssignmentToken;
    }

    public void setDeviceAssignmentToken(String deviceAssignmentToken) {
	this.deviceAssignmentToken = deviceAssignmentToken;
    }

    public List<DeviceAssignmentHistoryEntry> getSlots() {
	return slots;
    }

    public void setSlots(List<DeviceAssignmentHistoryEntry> slots) {
	this.slots = slots;
    }
}