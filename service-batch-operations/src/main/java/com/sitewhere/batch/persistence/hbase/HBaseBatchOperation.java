/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.persistence.hbase;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.sitewhere.batch.persistence.BatchManagementPersistence;
import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.uid.UniqueIdCounterMap;
import com.sitewhere.hbase.uid.UniqueIdCounterMapRowKeyBuilder;
import com.sitewhere.rest.model.device.batch.BatchElement;
import com.sitewhere.rest.model.device.batch.BatchOperation;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.batch.BatchOperationStatus;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.common.IFilter;
import com.sitewhere.spi.device.request.IBatchOperationCreateRequest;
import com.sitewhere.spi.device.request.IBatchOperationUpdateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchCriteria;

/**
 * HBase specifics for dealing with SiteWhere batch operations.
 * 
 * @author Derek
 */
public class HBaseBatchOperation {

    /** Length of group identifier (subset of 8 byte long) */
    public static final int IDENTIFIER_LENGTH = 4;

    /** Column qualifier for batch operation processing status */
    public static final byte[] PROCESSING_STATUS = Bytes.toBytes("s");

    /** Used to look up row keys from tokens */
    public static UniqueIdCounterMapRowKeyBuilder KEY_BUILDER = new UniqueIdCounterMapRowKeyBuilder() {

	@Override
	public UniqueIdCounterMap getMap(IHBaseContext context) {
	    return context.getDeviceIdManager().getBatchOperationKeys();
	}

	@Override
	public byte getTypeIdentifier() {
	    return BatchRecordType.BatchOperation.getType();
	}

	@Override
	public byte getPrimaryIdentifier() {
	    return BatchOperationRecordType.BatchOperation.getType();
	}

	@Override
	public int getKeyIdLength() {
	    return 4;
	}

	@Override
	public void throwInvalidKey() throws SiteWhereException {
	    throw new SiteWhereSystemException(ErrorCode.InvalidBatchOperationToken, ErrorLevel.ERROR);
	}
    };

    /**
     * Create a batch operation.
     * 
     * @param context
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static IBatchOperation createBatchOperation(IHBaseContext context, IBatchOperationCreateRequest request)
	    throws SiteWhereException {
	String uuid = null;
	if (request.getToken() != null) {
	    uuid = KEY_BUILDER.getMap(context).useExistingId(request.getToken());
	} else {
	    uuid = KEY_BUILDER.getMap(context).createUniqueId();
	}

	// Use common logic so all backend implementations work the same.
	BatchOperation batch = BatchManagementPersistence.batchOperationCreateLogic(request, uuid);

	Map<byte[], byte[]> qualifiers = new HashMap<byte[], byte[]>();
	qualifiers.put(PROCESSING_STATUS, Bytes.toBytes(String.valueOf(BatchOperationStatus.Unprocessed.getCode())));
	BatchOperation operation = HBaseUtils.createOrUpdate(context, context.getPayloadMarshaler(),
		ISiteWhereHBase.DEVICES_TABLE_NAME, batch, uuid, KEY_BUILDER, qualifiers);

	// Create elements for each device in the operation.
	long index = 0;
	Table devices = null;
	try {
	    devices = getDeviceTableInterface(context);
	    for (String hardwareId : request.getHardwareIds()) {
		BatchElement element = BatchManagementPersistence.batchElementCreateLogic(batch.getToken(), hardwareId,
			++index);
		HBaseBatchElement.createBatchElement(context, devices, element);
	    }
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to create device group element.", e);
	} finally {
	    HBaseUtils.closeCleanly(devices);
	}

	return operation;
    }

    /**
     * Update an existing batch operation.
     * 
     * @param context
     * @param token
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static IBatchOperation updateBatchOperation(IHBaseContext context, String token,
	    IBatchOperationUpdateRequest request) throws SiteWhereException {
	BatchOperation updated = assertBatchOperation(context, token);
	BatchOperationStatus oldProcessingStatus = updated.getProcessingStatus();
	BatchManagementPersistence.batchOperationUpdateLogic(request, updated);

	Map<byte[], byte[]> qualifiers = new HashMap<byte[], byte[]>();
	if (updated.getProcessingStatus() != oldProcessingStatus) {
	    qualifiers.put(PROCESSING_STATUS, Bytes.toBytes(String.valueOf(updated.getProcessingStatus().getCode())));
	}
	return HBaseUtils.createOrUpdate(context, context.getPayloadMarshaler(), ISiteWhereHBase.DEVICES_TABLE_NAME,
		updated, token, KEY_BUILDER, qualifiers);
    }

    /**
     * Get a {@link BatchOperation} by unique token.
     * 
     * @param context
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public static BatchOperation getBatchOperationByToken(IHBaseContext context, String token)
	    throws SiteWhereException {
	return HBaseUtils.get(context, ISiteWhereHBase.DEVICES_TABLE_NAME, token, KEY_BUILDER, BatchOperation.class);
    }

    /**
     * Get paged {@link IBatchOperation} results based on the given search
     * criteria.
     * 
     * @param context
     * @param includeDeleted
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IBatchOperation> listBatchOperations(IHBaseContext context, boolean includeDeleted,
	    ISearchCriteria criteria) throws SiteWhereException {
	Comparator<BatchOperation> comparator = new Comparator<BatchOperation>() {

	    public int compare(BatchOperation a, BatchOperation b) {
		return -1 * (a.getCreatedDate().compareTo(b.getCreatedDate()));
	    }

	};
	IFilter<BatchOperation> filter = new IFilter<BatchOperation>() {

	    public boolean isExcluded(BatchOperation item) {
		return false;
	    }
	};
	return HBaseUtils.getFilteredList(context, ISiteWhereHBase.DEVICES_TABLE_NAME, KEY_BUILDER, includeDeleted,
		IBatchOperation.class, BatchOperation.class, filter, criteria, comparator);
    }

    /**
     * Delete an existing batch operation.
     * 
     * @param context
     * @param token
     * @param force
     * @return
     * @throws SiteWhereException
     */
    public static IBatchOperation deleteBatchOperation(IHBaseContext context, String token, boolean force)
	    throws SiteWhereException {
	// If actually deleting batch operation, delete all elements.
	if (force) {
	    HBaseBatchElement.deleteBatchElements(context, token);
	}
	return HBaseUtils.delete(context, context.getPayloadMarshaler(), ISiteWhereHBase.DEVICES_TABLE_NAME, token,
		force, KEY_BUILDER, BatchOperation.class);
    }

    /**
     * Get a {@link BatchOperation} by token or throw an exception if token is
     * not valid.
     * 
     * @param context
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public static BatchOperation assertBatchOperation(IHBaseContext context, String token) throws SiteWhereException {
	BatchOperation existing = getBatchOperationByToken(context, token);
	if (existing == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidBatchOperationToken, ErrorLevel.ERROR);
	}
	return existing;
    }

    /**
     * Get the unique device identifier based on the long value associated with
     * the batch operation UUID. This will be a subset of the full 8-bit long
     * value.
     * 
     * @param value
     * @return
     */
    public static byte[] getTruncatedIdentifier(Long value) {
	byte[] bytes = Bytes.toBytes(value);
	byte[] result = new byte[IDENTIFIER_LENGTH];
	System.arraycopy(bytes, bytes.length - IDENTIFIER_LENGTH, result, 0, IDENTIFIER_LENGTH);
	return result;
    }

    /**
     * Get row key for a batch operation with the given internal id.
     * 
     * @param groupId
     * @return
     */
    public static byte[] getPrimaryRowKey(Long groupId) {
	ByteBuffer buffer = ByteBuffer.allocate(IDENTIFIER_LENGTH + 2);
	buffer.put(BatchRecordType.BatchOperation.getType());
	buffer.put(getTruncatedIdentifier(groupId));
	buffer.put(BatchOperationRecordType.BatchOperation.getType());
	return buffer.array();
    }

    /**
     * Get device table based on context.
     * 
     * @param context
     * @return
     * @throws SiteWhereException
     */
    protected static Table getDeviceTableInterface(IHBaseContext context) throws SiteWhereException {
	return context.getClient().getTableInterface(context.getTenant(), ISiteWhereHBase.DEVICES_TABLE_NAME);
    }
}