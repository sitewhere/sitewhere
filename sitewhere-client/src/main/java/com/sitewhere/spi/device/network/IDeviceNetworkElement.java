/*
 * IDeviceNetworkEntry.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.network;

/**
 * Interface for an entry in an {@link IDeviceNetwork}.
 * 
 * @author Derek
 */
public interface IDeviceNetworkElement {

	/**
	 * Get token for parent network.
	 * 
	 * @return
	 */
	public String getNetworkToken();

	/**
	 * Get index that corresponds to this entry.
	 * 
	 * @return
	 */
	public long getIndex();

	/**
	 * Get network element type.
	 * 
	 * @return
	 */
	public NetworkElementType getType();

	/**
	 * Get element id (relative to element type).
	 * 
	 * @return
	 */
	public String getElementId();
}