/*
 * UniqueIdType.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.uid;

/**
 * Row type indicators for the UID table.
 * 
 * @author Derek
 */
public enum UniqueIdType {

	/** First byte for rows that act as counters */
	CounterPlaceholder((byte) 0x00),

	/** Key and value for site UUIDs */
	SiteKey((byte) 0x01), SiteValue((byte) 0x02),

	/** Key and value for device UUIDs */
	DeviceKey((byte) 0x03), DeviceValue((byte) 0x04),

	/** Key and value for zone UUIDs */
	ZoneKey((byte) 0x05), ZoneValue((byte) 0x06),

	/** Key and value for assignment UUIDs */
	DeviceAssignmentKey((byte) 0x07), DeviceAssignmentValue((byte) 0x08);

	/** Type indicator */
	private byte indicator;

	/**
	 * Create a unique id type with the given byte value.
	 * 
	 * @param value
	 */
	private UniqueIdType(byte indicator) {
		this.indicator = indicator;
	}

	/**
	 * Get the UID type indicator.
	 * 
	 * @return
	 */
	public byte getIndicator() {
		return indicator;
	}
}