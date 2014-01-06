/*
* $Id$
* --------------------------------------------------------------------------------------
* Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
*
* The software in this package is published under the terms of the CPAL v1.0
* license, a copy of which has been included with this distribution in the
* LICENSE.txt file.
*/

package com.sitewhere.spi.device;

/**
 * Enumerated list of event types.
 * 
 * @author dadams
 */
public enum DeviceEventType {

	/** Arbitrary measurements */
	Measurements("measurements"),

	/** Location information */
	Location("location"),

	/** Device alert condition */
	Alert("alert");

	/** Code that gets persisted */
	private String code;

	private DeviceEventType(String code) {
		this.code = code;
	}

	/**
	 * Get the code.
	 * 
	 * @return
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Get DeviceEventType from code.
	 * 
	 * @param type
	 * @return
	 */
	public static DeviceEventType fromCode(String code) {
		for (DeviceEventType current : DeviceEventType.values()) {
			if (current.getCode() == code) {
				return current;
			}
		}
		throw new RuntimeException("Unknown DeviceEventType code: " + code);
	}
}