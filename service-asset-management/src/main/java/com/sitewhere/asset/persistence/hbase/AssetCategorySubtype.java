/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.persistence.hbase;

/**
 * Indicators for asset category record subtype.
 * 
 * @author Derek
 */
public enum AssetCategorySubtype {

    /** Asset category record */
    AssetCategory((byte) 0x00),

    /** Asset record */
    Asset((byte) 0x01);

    /** Type indicator */
    private byte type;

    /**
     * Create a unique id type with the given byte value.
     * 
     * @param value
     */
    private AssetCategorySubtype(byte type) {
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