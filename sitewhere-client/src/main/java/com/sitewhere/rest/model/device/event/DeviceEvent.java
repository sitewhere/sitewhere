/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.sitewhere.rest.model.device.event;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.datatype.JsonDateSerializer;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceEvent;

/**
 * Model object for an event originating from a remote device.
 * 
 * @author dadams
 */
@JsonInclude(Include.NON_NULL)
public abstract class DeviceEvent extends MetadataProvider implements IDeviceEvent, Comparable<IDeviceEvent>,
		Serializable {

	/** For Java serialization */
	private static final long serialVersionUID = 6581066174724675701L;

	/** Unqiue id for event */
	private String id;

	/** Event type indicator */
	private DeviceEventType eventType;

	/** Site token */
	private String siteToken;

	/** Device assignment token */
	private String deviceAssignmentToken;

	/** Device assignment type */
	private DeviceAssignmentType assignmentType;

	/** Asset module id */
	private String assetModuleId;

	/** Associated asset id */
	private String assetId;

	/** Date event occurred */
	private Date eventDate;

	/** Date event was received */
	private Date receivedDate;

	public DeviceEvent(DeviceEventType type) {
		this.eventType = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceEvent#getId()
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceEvent#getEventType()
	 */
	public DeviceEventType getEventType() {
		return eventType;
	}

	public void setEventType(DeviceEventType eventType) {
		this.eventType = eventType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceEvent#getSiteToken()
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
	 * @see com.sitewhere.spi.device.IDeviceEvent#getDeviceAssignmentToken()
	 */
	public String getDeviceAssignmentToken() {
		return deviceAssignmentToken;
	}

	public void setDeviceAssignmentToken(String deviceAssignmentToken) {
		this.deviceAssignmentToken = deviceAssignmentToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceEvent#getAssignmentType()
	 */
	public DeviceAssignmentType getAssignmentType() {
		return assignmentType;
	}

	public void setAssignmentType(DeviceAssignmentType assignmentType) {
		this.assignmentType = assignmentType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceEvent#getAssetModuleId()
	 */
	public String getAssetModuleId() {
		return assetModuleId;
	}

	public void setAssetModuleId(String assetModuleId) {
		this.assetModuleId = assetModuleId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceEvent#getAssetId()
	 */
	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceEvent#getEventDate()
	 */
	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceEvent#getReceivedDate()
	 */
	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(IDeviceEvent other) {
		if ((getEventDate() != null) && (other.getEventDate() != null)) {
			return getEventDate().compareTo(other.getEventDate());
		}
		return 0;
	}

	/**
	 * Create a copy of an SPI object. Used by web services for marshaling.
	 * 
	 * @param source
	 * @param target
	 */
	public static void copy(IDeviceEvent source, DeviceEvent target) {
		target.setId(source.getId());
		target.setSiteToken(source.getSiteToken());
		target.setDeviceAssignmentToken(source.getDeviceAssignmentToken());
		target.setAssignmentType(source.getAssignmentType());
		target.setAssetModuleId(source.getAssetModuleId());
		target.setAssetId(source.getAssetId());
		target.setReceivedDate(source.getReceivedDate());
		target.setEventDate(source.getEventDate());
		MetadataProvider.copy(source, target);
	}
}