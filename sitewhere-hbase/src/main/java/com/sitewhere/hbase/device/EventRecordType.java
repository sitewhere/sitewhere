/*
 * DeviceAssignmentRecordType.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.device;

/**
 * Indicates type of event record.
 * 
 * @author Derek
 */
public enum EventRecordType {

	/** Device measurement record */
	Measurement((byte) 0x01),

	/** Device location record */
	Location((byte) 0x02),

	/** Device alert record */
	Alert((byte) 0x03),

	/** Device command invocation record */
	CommandInvocation((byte) 0x0a),

	/** Device command response record */
	CommandResponse((byte) 0x0b),

	/** Device invocation command response entry */
	CommandResponseEntry((byte) 0x0c),

	/** Device invocation command response counter */
	CommandResponseCounter((byte) 0x0d),

	/** Device state change record */
	StateChange((byte) 0x10);

	/** Type indicator */
	private byte type;

	/**
	 * Create a unique id type with the given byte value.
	 * 
	 * @param value
	 */
	private EventRecordType(byte type) {
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

	/**
	 * Decode a byte value to a record type.
	 * 
	 * @param type
	 * @return
	 */
	public static EventRecordType decode(byte type) {
		for (EventRecordType current : EventRecordType.values()) {
			if (current.getType() == type) {
				return current;
			}
		}
		return null;
	}
}