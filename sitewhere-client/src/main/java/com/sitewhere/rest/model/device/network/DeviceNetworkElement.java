/*
 * DeviceNetworkElement.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.network;

import com.sitewhere.spi.device.network.IDeviceNetwork;
import com.sitewhere.spi.device.network.IDeviceNetworkElement;
import com.sitewhere.spi.device.network.NetworkElementType;

/**
 * Model object for an element in an {@link IDeviceNetwork}.
 * 
 * @author Derek
 */
public class DeviceNetworkElement implements IDeviceNetworkElement {

	/** Parent network token */
	private String networkToken;

	/** Element index */
	private long index;

	/** Element type */
	private NetworkElementType type;

	/** Element type */
	private String elementId;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.network.IDeviceNetworkElement#getNetworkToken()
	 */
	public String getNetworkToken() {
		return networkToken;
	}

	public void setNetworkToken(String networkToken) {
		this.networkToken = networkToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.network.IDeviceNetworkElement#getIndex()
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
	 * @see com.sitewhere.spi.device.network.IDeviceNetworkElement#getType()
	 */
	public NetworkElementType getType() {
		return type;
	}

	public void setType(NetworkElementType type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.network.IDeviceNetworkElement#getElementId()
	 */
	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}
}