/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.hbase;

/**
 * Data structure for storing which records will be deleted.
 * 
 * @author Derek
 */
public class DeleteRecord {

    private byte[] rowkey;
    private byte[] payloadType;
    private byte[] payload;

    public DeleteRecord(byte[] rowkey, byte[] payloadType, byte[] payload) {
	this.rowkey = rowkey;
	this.payloadType = payloadType;
	this.payload = payload;
    }

    public byte[] getRowkey() {
	return rowkey;
    }

    public byte[] getPayloadType() {
	return payloadType;
    }

    public byte[] getPayload() {
	return payload;
    }
}