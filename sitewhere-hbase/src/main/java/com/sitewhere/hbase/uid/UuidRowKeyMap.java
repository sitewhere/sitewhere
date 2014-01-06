/*
 * UuidRowKeyMap.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.uid;

import java.util.UUID;

import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.spi.SiteWhereException;

/**
 * Maps UUIDs to row keys.
 * 
 * @author Derek
 */
public class UuidRowKeyMap extends UniqueIdMap<String, byte[]> {

	public UuidRowKeyMap(ISiteWhereHBaseClient hbase, UniqueIdType keyIndicator, UniqueIdType valueIndicator) {
		super(hbase, keyIndicator, valueIndicator);
	}

	/**
	 * Create a UUID and associate it with the given row key.
	 * 
	 * @param rowkey
	 * @return
	 * @throws SiteWhereException
	 */
	public String createUniqueId(byte[] rowkey) throws SiteWhereException {
		String uuid = UUID.randomUUID().toString();
		create(uuid, rowkey);
		return uuid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.hbase.uid.UniqueIdMap#convertName(byte[])
	 */
	public String convertName(byte[] bytes) {
		return new String(bytes);
	}

	@Override
	public byte[] convertName(String name) {
		return name.getBytes();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.hbase.uid.UniqueIdMap#convertValue(byte[])
	 */
	public byte[] convertValue(byte[] bytes) {
		return bytes;
	}
}