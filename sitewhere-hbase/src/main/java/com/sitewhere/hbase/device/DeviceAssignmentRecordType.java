/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.device;

/**
 * Record types related to device assignments.
 * 
 * @author Derek
 */
public enum DeviceAssignmentRecordType {

    /** Device assignment record */
    DeviceAssignment((byte) 0x00),

    /** Device stream record */
    DeviceStream((byte) 0x01),

    /** Device stream record */
    EndMarker((byte) 0x02);

    /** Type indicator */
    private byte type;

    /**
     * Create a unique id type with the given byte value.
     * 
     * @param value
     */
    private DeviceAssignmentRecordType(byte type) {
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