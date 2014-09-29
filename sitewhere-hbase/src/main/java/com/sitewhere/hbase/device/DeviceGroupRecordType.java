/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.device;

/**
 * Binary type indicator flag for device group records.
 * 
 * @author Derek
 */
public enum DeviceGroupRecordType {

	/** Device group record */
	DeviceGroup((byte) 0x00),

	/** Device group element record */
	DeviceGroupElement((byte) 0x01);

	/** Type indicator */
	private byte type;

	/**
	 * Create a unique id type with the given byte value.
	 * 
	 * @param value
	 */
	private DeviceGroupRecordType(byte type) {
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