/*
 * SiteRecordType.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.device;

/**
 * Indicates site record type.
 * 
 * @author Derek
 */
public enum SiteRecordType {

	/** Primary site record */
	Primary((byte) 0x00),

	/** Zone record */
	Zone((byte) 0x01),

	/** Assignment record */
	Assignment((byte) 0x02),

	/** Assignment record */
	End((byte) 0x03);

	/** Type indicator */
	private byte type;

	/**
	 * Create a unique id type with the given byte value.
	 * 
	 * @param value
	 */
	private SiteRecordType(byte type) {
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