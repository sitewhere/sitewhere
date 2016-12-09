/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.encoder;

import org.apache.hadoop.hbase.util.Bytes;

/**
 * Enumerates payload encoding schemes.
 * 
 * @author Derek
 */
public enum PayloadEncoding {

    Json(Bytes.toBytes("j")),

    ProtocolBuffers(Bytes.toBytes("p"));

    /** Type indicator */
    private byte[] indicator;

    /**
     * Create a unique id type with the given byte value.
     * 
     * @param value
     */
    private PayloadEncoding(byte[] indicator) {
	this.indicator = indicator;
    }

    /**
     * Get the UID type indicator.
     * 
     * @return
     */
    public byte[] getIndicator() {
	return indicator;
    }

    /**
     * Get encoding based on indicator value.
     * 
     * @param indicator
     * @return
     */
    public static PayloadEncoding getEncoding(byte[] indicator) {
	for (PayloadEncoding encoding : PayloadEncoding.values()) {
	    if (Bytes.equals(indicator, encoding.getIndicator())) {
		return encoding;
	    }
	}
	return null;
    }
}