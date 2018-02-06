/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.persistence.hbase;

/**
 * Binary type indicator flag for batch operation records.
 * 
 * @author Derek
 */
public enum BatchOperationRecordType {

    /** Batch operation record */
    BatchOperation((byte) 0x00),

    /** Batch element record */
    BatchElement((byte) 0x01);

    /** Type indicator */
    private byte type;

    /**
     * Create a unique id type with the given byte value.
     * 
     * @param value
     */
    private BatchOperationRecordType(byte type) {
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