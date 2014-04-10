/*
 * DeviceGroupElement.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.group;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.spi.device.group.GroupElementType;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;

/**
 * Model object for an element in an {@link IDeviceGroup}.
 * 
 * @author Derek
 */
public class DeviceGroupElement implements IDeviceGroupElement {

	/** Parent group token */
	private String groupToken;

	/** Element index */
	private long index;

	/** Element type */
	private GroupElementType type;

	/** Element type */
	private String elementId;

	/** List of roles for the element */
	private List<String> roles = new ArrayList<String>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.group.IDeviceGroupElement#getGroupToken()
	 */
	public String getGroupToken() {
		return groupToken;
	}

	public void setGroupToken(String groupToken) {
		this.groupToken = groupToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.group.IDeviceGroupElement#getIndex()
	 */
	public long getIndex() {
		return index;
	}

	public void setIndex(long index) {
		this.index = index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.group.IDeviceGroupElement#getType()
	 */
	public GroupElementType getType() {
		return type;
	}

	public void setType(GroupElementType type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.group.IDeviceGroupElement#getElementId()
	 */
	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.group.IDeviceGroupElement#getRoles()
	 */
	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public static DeviceGroupElement copy(IDeviceGroupElement input) {
		DeviceGroupElement result = new DeviceGroupElement();
		result.setGroupToken(input.getGroupToken());
		result.setIndex(input.getIndex());
		result.setType(input.getType());
		result.setElementId(input.getElementId());
		result.getRoles().addAll(input.getRoles());
		return result;
	}
}