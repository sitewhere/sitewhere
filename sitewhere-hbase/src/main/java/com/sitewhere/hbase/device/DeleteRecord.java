/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.device;

/**
 * Data structure for storing which records will be deleted.
 * 
 * @author Derek
 */
public class DeleteRecord {

	private byte[] rowkey;
	private byte[] json;

	public DeleteRecord(byte[] rowkey, byte[] json) {
		this.rowkey = rowkey;
		this.json = json;
	}

	public byte[] getRowkey() {
		return rowkey;
	}

	public byte[] getJson() {
		return json;
	}
}