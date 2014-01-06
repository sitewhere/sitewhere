/*
 * UuidCounterMap.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.uid;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.util.Bytes;

import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.spi.SiteWhereException;

/**
 * Unique id mapper that generates UUIDs as keys and matches them to integer values.
 * 
 * @author Derek
 */
public class UnqiueIdCounterMap extends UniqueIdMap<String, Long> {

	public UnqiueIdCounterMap(ISiteWhereHBaseClient hbase, UniqueIdType keyIndicator, UniqueIdType valueIndicator) {
		super(hbase, keyIndicator, valueIndicator);
	}

	/**
	 * Create a UUID and add it to the UID table with corresponding numeric value.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public String createUniqueId() throws SiteWhereException {
		String uuid = UUID.randomUUID().toString();
		Long value = getNextCounterValue();
		create(uuid, value);
		return uuid;
	}

	/**
	 * Uses a counter row to keep unique values for the given key indicator type.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public Long getNextCounterValue() throws SiteWhereException {
		ByteBuffer counterRow = ByteBuffer.allocate(2);
		counterRow.put(UniqueIdType.CounterPlaceholder.getIndicator());
		counterRow.put(getKeyIndicator().getIndicator());
		byte[] counterKey = counterRow.array();
		HTableInterface uids = null;
		try {
			uids = hbase.getTableInterface(ISiteWhereHBase.UID_TABLE_NAME);
			return uids.incrementColumnValue(counterKey, ISiteWhereHBase.FAMILY_ID, UniqueIdMap.VALUE_QUAL,
					1L);
		} catch (IOException e) {
			throw new SiteWhereException("Error scanning user rows.", e);
		} finally {
			HBaseUtils.closeCleanly(uids);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.hbase.uid.UniqueIdMap#convertName(byte[])
	 */
	public String convertName(byte[] bytes) {
		return new String(bytes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.hbase.uid.UniqueIdMap#convertName(java.lang.Object)
	 */
	public byte[] convertName(String name) {
		return name.getBytes();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.hbase.uid.UniqueIdMap#convertValue(byte[])
	 */
	public Long convertValue(byte[] bytes) {
		return Bytes.toLong(bytes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.hbase.uid.UniqueIdMap#convertValue(java.lang.Object)
	 */
	public byte[] convertValue(Long value) {
		return Bytes.toBytes(value);
	}
}