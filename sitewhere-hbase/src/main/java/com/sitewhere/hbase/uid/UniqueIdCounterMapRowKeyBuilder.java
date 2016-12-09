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

import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.common.IRowKeyBuilder;
import com.sitewhere.spi.SiteWhereException;

/**
 * Implementation of {@link IRowKeyBuilder} that uses a
 * {@link UniqueIdCounterMap} to look up an identifier for the associated token.
 * 
 * @author Derek
 */
public abstract class UniqueIdCounterMapRowKeyBuilder implements IRowKeyBuilder {

    /**
     * Get map used for lookups.
     * 
     * @return
     */
    public abstract UniqueIdCounterMap getMap(IHBaseContext context);

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.hbase.common.IRowKeyBuilder#buildPrimaryKey(com.sitewhere.
     * hbase. IHBaseContext, java.lang.String)
     */
    @Override
    public byte[] buildPrimaryKey(IHBaseContext context, String token) throws SiteWhereException {
	Long entityId = getMap(context).getValue(token);
	if (entityId == null) {
	    throwInvalidKey();
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
     * @see
     * com.sitewhere.hbase.common.IRowKeyBuilder#buildSubkey(com.sitewhere.hbase
     * .IHBaseContext , java.lang.String, byte)
     */
    @Override
    public byte[] buildSubkey(IHBaseContext context, String token, byte type) throws SiteWhereException {
	Long entityId = getMap(context).getValue(token);
	if (entityId == null) {
	    throwInvalidKey();
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
     * @see
     * com.sitewhere.hbase.common.IRowKeyBuilder#deleteReference(com.sitewhere.
     * hbase. IHBaseContext, java.lang.String)
     */
    @Override
    public void deleteReference(IHBaseContext context, String token) throws SiteWhereException {
	getMap(context).delete(token);
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