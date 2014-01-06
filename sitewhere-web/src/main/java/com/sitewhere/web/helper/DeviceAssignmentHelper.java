/*
* $Id$
* --------------------------------------------------------------------------------------
* Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
*
* The software in this package is published under the terms of the CPAL v1.0
* license, a copy of which has been included with this distribution in the
* LICENSE.txt file.
*/
package com.sitewhere.web.helper;

import java.text.SimpleDateFormat;

import com.sitewhere.spi.device.IDeviceAssignment;

/**
 * Helps with screen presentation of a device asset.
 * 
 * @author dadams
 */
public class DeviceAssignmentHelper {

	/** Wrapped device assignment */
	private IDeviceAssignment assignment;

	/** Formatter used for displaying dates */
	private SimpleDateFormat formatter = new SimpleDateFormat(SiteWhereWebDefaults.DEFAULT_DATE_FORMAT);

	public DeviceAssignmentHelper(IDeviceAssignment assignment) {
		this.assignment = assignment;
	}

	/**
	 * Get the active date as a formatted string.
	 * 
	 * @return
	 */
	public String getActiveDate() {
		if (getAssignment().getActiveDate() == null) {
			return "";
		}
		return formatter.format(getAssignment().getActiveDate().getTime());
	}

	/**
	 * Get the released date as a formatted string.
	 * 
	 * @return
	 */
	public String getReleasedDate() {
		if (getAssignment().getReleasedDate() == null) {
			return "";
		}
		return formatter.format(getAssignment().getReleasedDate().getTime());
	}

	/**
	 * Get a string representation of the assignment status.
	 * 
	 * @return
	 */
	public String getStatus() {
		return getAssignment().getStatus().toString();
	}

	/**
	 * Get a string representation of the device assignment type.
	 * 
	 * @return
	 */
	public String getAssignmentType() {
		return getAssignment().getAssignmentType().toString();
	}

	protected IDeviceAssignment getAssignment() {
		return assignment;
	}

	protected void setAssignment(IDeviceAssignment assignment) {
		this.assignment = assignment;
	}
}