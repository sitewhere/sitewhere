/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.state.StateChangeCategory;
import com.sitewhere.spi.device.event.state.StateChangeType;

/**
 * Model object for a state change reported by a remote device.
 * 
 * @author Derek
 */
public class DeviceStateChange extends DeviceEvent implements IDeviceStateChange, Serializable {

	/** For Java serialization */
	private static final long serialVersionUID = 7904836116060730355L;

	/** State change category */
	private StateChangeCategory category;

	/** State change type */
	private StateChangeType type;

	/** Previous or expected state */
	private String previousState;

	/** New state */
	private String newState;

	public DeviceStateChange() {
		super(DeviceEventType.StateChange);
	}

	/** Data associated with the state change */
	private Map<String, String> data = new HashMap<String, String>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceStateChange#getCategory()
	 */
	public StateChangeCategory getCategory() {
		return category;
	}

	public void setCategory(StateChangeCategory category) {
		this.category = category;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceStateChange#getType()
	 */
	public StateChangeType getType() {
		return type;
	}

	public void setType(StateChangeType type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceStateChange#getPreviousState()
	 */
	public String getPreviousState() {
		return previousState;
	}

	public void setPreviousState(String previousState) {
		this.previousState = previousState;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceStateChange#getNewState()
	 */
	public String getNewState() {
		return newState;
	}

	public void setNewState(String newState) {
		this.newState = newState;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceStateChange#getData()
	 */
	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	/**
	 * Create a copy of an SPI object. Used by web services for marshaling.
	 * 
	 * @param input
	 * @return
	 */
	public static DeviceStateChange copy(IDeviceStateChange input) throws SiteWhereException {
		DeviceStateChange result = new DeviceStateChange();
		DeviceEvent.copy(input, result);
		result.setCategory(input.getCategory());
		result.setType(input.getType());
		result.setPreviousState(input.getPreviousState());
		result.setNewState(input.getNewState());
		result.getData().putAll(input.getData());
		return result;
	}
}