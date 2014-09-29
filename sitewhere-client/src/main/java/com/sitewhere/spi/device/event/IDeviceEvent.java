/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event;

import java.util.Date;

import com.sitewhere.spi.common.IMetadataProvider;
import com.sitewhere.spi.device.DeviceAssignmentType;

/**
 * Event that originates from a device.
 * 
 * @author Derek
 */
public interface IDeviceEvent extends IMetadataProvider, Comparable<IDeviceEvent> {

	/**
	 * Get a string that uniquely identifies the event.
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * Get event type indicator.
	 * 
	 * @return
	 */
	public DeviceEventType getEventType();

	/**
	 * Get token for site the event pertains to.
	 * 
	 * @return
	 */
	public String getSiteToken();

	/**
	 * Get the device assignment the event pertains to.
	 * 
	 * @return
	 */
	public String getDeviceAssignmentToken();

	/**
	 * Get assignment type.
	 * 
	 * @return
	 */
	public DeviceAssignmentType getAssignmentType();

	/**
	 * Get id of asset module.
	 * 
	 * @return
	 */
	public String getAssetModuleId();

	/**
	 * Get id of asset if associated.
	 * 
	 * @return
	 */
	public String getAssetId();

	/**
	 * Get the date the event occurred.
	 * 
	 * @return
	 */
	public Date getEventDate();

	/**
	 * Get the date this event was received.
	 * 
	 * @return
	 */
	public Date getReceivedDate();
}