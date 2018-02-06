/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.support;

import com.sitewhere.rest.model.common.Location;

/**
 * Entry used in InterpolatedAssignmentHistory.
 * 
 * @author Derek Adams
 */
public class DeviceAssignmentHistoryEntry {

    /** Time slot (in ms.) */
    private long timeSlot;

    /** Location information */
    private Location location;

    public long getTimeSlot() {
	return timeSlot;
    }

    public void setTimeSlot(long timeSlot) {
	this.timeSlot = timeSlot;
    }

    public Location getLocation() {
	return location;
    }

    public void setLocation(Location location) {
	this.location = location;
    }
}