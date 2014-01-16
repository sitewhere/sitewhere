/*
 * DeviceSpecificationRecordType.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.device;

/**
 * Used to differentiate records stored under a device specification row.
 * 
 * @author Derek
 */
public enum DeviceSpecificationRecordType {

	/** Device command record */
	DeviceSpecification((byte) 0x00),

	/** Device command record */
	DeviceCommand((byte) 0x01),

	/** End marker for scan ranges */
	End((byte) 0x02);

	/** Type indicator */
	private byte type;

	/**
	 * Create a unique id type with the given byte value.
	 * 
	 * @param value
	 */
	private DeviceSpecificationRecordType(byte type) {
		this.type = type;
	}

	/**
	 * Get the record type indicator.
	 * 
	 * @return
	 */
	public byte getType() {
		return type;
	}
}