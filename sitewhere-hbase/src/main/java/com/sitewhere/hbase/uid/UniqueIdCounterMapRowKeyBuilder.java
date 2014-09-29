/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.uid;

import java.nio.ByteBuffer;

import org.apache.hadoop.hbase.util.Bytes;

import com.sitewhere.hbase.common.IRowKeyBuilder;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorLevel;

/**
 * Implementation of {@link IRowKeyBuilder} that uses a {@link UniqueIdCounterMap} to look
 * up an identifier for the associated token.
 * 
 * @author Derek
 */
public abstract class UniqueIdCounterMapRowKeyBuilder implements IRowKeyBuilder {

	/**
	 * Get map used for lookups.
	 * 
	 * @return
	 */
	public abstract UniqueIdCounterMap getMap();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.hbase.common.IRowKeyBuilder#buildPrimaryKey(java.lang.String)
	 */
	@Override
	public byte[] buildPrimaryKey(String token) throws SiteWhereException {
		Long entityId = getMap().getValue(token);
		if (entityId == null) {
			throw new SiteWhereSystemException(getInvalidKeyErrorCode(), ErrorLevel.ERROR);
		}
		ByteBuffer buffer = ByteBuffer.allocate(getKeyIdLength() + 2);
		buffer.put(getTypeIdentifier());
		buffer.put(getTruncatedIdentifier(entityId));
		buffer.put(getPrimaryIdentifier());
		return buffer.array();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.hbase.common.IRowKeyBuilder#buildSubkey(java.lang.String, byte)
	 */
	@Override
	public byte[] buildSubkey(String token, byte type) throws SiteWhereException {
		Long entityId = getMap().getValue(token);
		if (entityId == null) {
			throw new SiteWhereSystemException(getInvalidKeyErrorCode(), ErrorLevel.ERROR);
		}
		ByteBuffer buffer = ByteBuffer.allocate(getKeyIdLength() + 2);
		buffer.put(getTypeIdentifier());
		buffer.put(getTruncatedIdentifier(entityId));
		buffer.put(type);
		return buffer.array();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.hbase.common.IRowKeyBuilder#deleteReference(java.lang.String)
	 */
	@Override
	public void deleteReference(String token) throws SiteWhereException {
		getMap().delete(token);
	}

	/**
	 * Gets low bytes from the identifier based on key length.
	 * 
	 * @param value
	 * @return
	 */
	protected byte[] getTruncatedIdentifier(Long value) {
		int keyLength = getKeyIdLength();
		byte[] bytes = Bytes.toBytes(value);
		byte[] result = new byte[keyLength];
		System.arraycopy(bytes, bytes.length - keyLength, result, 0, keyLength);
		return result;
	}
}