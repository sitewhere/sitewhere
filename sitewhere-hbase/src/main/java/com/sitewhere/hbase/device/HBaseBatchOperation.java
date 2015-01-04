/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.device;

import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.hbase.util.Bytes;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.uid.IdManager;
import com.sitewhere.hbase.uid.UniqueIdCounterMap;
import com.sitewhere.hbase.uid.UniqueIdCounterMapRowKeyBuilder;
import com.sitewhere.rest.model.device.batch.BatchElement;
import com.sitewhere.rest.model.device.batch.BatchOperation;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.common.IFilter;
import com.sitewhere.spi.device.batch.IBatchOperation;
import com.sitewhere.spi.device.request.IBatchOperationCreateRequest;
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

	/** Used to look up row keys from tokens */
	public static UniqueIdCounterMapRowKeyBuilder KEY_BUILDER = new UniqueIdCounterMapRowKeyBuilder() {

		@Override
		public UniqueIdCounterMap getMap() {
			return IdManager.getInstance().getBatchOperationKeys();
		}

		@Override
		public byte getTypeIdentifier() {
			return DeviceRecordType.BatchOperation.getType();
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
		public ErrorCode getInvalidKeyErrorCode() {
			return ErrorCode.InvalidBatchOperationToken;
		}
	};

	/**
	 * Create a batch operation.
	 * 
	 * @param hbase
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IBatchOperation createBatchOperation(ISiteWhereHBaseClient hbase,
			IBatchOperationCreateRequest request) throws SiteWhereException {
		String uuid = null;
		if (request.getToken() != null) {
			uuid = KEY_BUILDER.getMap().useExistingId(request.getToken());
		} else {
			uuid = KEY_BUILDER.getMap().createUniqueId();
		}

		// Use common logic so all backend implementations work the same.
		BatchOperation batch = SiteWherePersistence.batchOperationCreateLogic(request, uuid);

		Map<byte[], byte[]> qualifiers = new HashMap<byte[], byte[]>();
		BatchOperation operation =
				HBaseUtils.create(hbase, ISiteWhereHBase.DEVICES_TABLE_NAME, batch, uuid, KEY_BUILDER,
						qualifiers);

		// Create elements for each device in the operation.
		long index = 0;
		for (String hardwareId : request.getHardwareIds()) {
			BatchElement element =
					SiteWherePersistence.batchElementCreateLogic(batch.getToken(), hardwareId, ++index);
			HBaseBatchElement.createBatchElement(hbase, element);
		}

		return operation;
	}

	/**
	 * Get a {@link BatchOperation} by unique token.
	 * 
	 * @param hbase
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public static BatchOperation getBatchOperationByToken(ISiteWhereHBaseClient hbase, String token)
			throws SiteWhereException {
		return HBaseUtils.get(hbase, ISiteWhereHBase.DEVICES_TABLE_NAME, token, KEY_BUILDER,
				BatchOperation.class);
	}

	/**
	 * Get paged {@link IBatchOperation} results based on the given search criteria.
	 * 
	 * @param hbase
	 * @param includeDeleted
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IBatchOperation> listBatchOperations(ISiteWhereHBaseClient hbase,
			boolean includeDeleted, ISearchCriteria criteria) throws SiteWhereException {
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
		return HBaseUtils.getFilteredList(hbase, ISiteWhereHBase.DEVICES_TABLE_NAME, KEY_BUILDER,
				includeDeleted, IBatchOperation.class, BatchOperation.class, filter, criteria, comparator);
	}

	/**
	 * Delete an existing batch operation.
	 * 
	 * @param hbase
	 * @param token
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public static IBatchOperation deleteBatchOperation(ISiteWhereHBaseClient hbase, String token,
			boolean force) throws SiteWhereException {
		// If actually deleting batch operation, delete all elements.
		if (force) {
			HBaseBatchElement.deleteBatchElements(hbase, token);
		}
		return HBaseUtils.delete(hbase, ISiteWhereHBase.DEVICES_TABLE_NAME, token, force, KEY_BUILDER,
				BatchOperation.class);
	}

	/**
	 * Get a {@link BatchOperation} by token or throw an exception if token is not valid.
	 * 
	 * @param hbase
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public static BatchOperation assertBatchOperation(ISiteWhereHBaseClient hbase, String token)
			throws SiteWhereException {
		BatchOperation existing = getBatchOperationByToken(hbase, token);
		if (existing == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidBatchOperationToken, ErrorLevel.ERROR);
		}
		return existing;
	}

	/**
	 * Get the unique device identifier based on the long value associated with the batch
	 * operation UUID. This will be a subset of the full 8-bit long value.
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
		buffer.put(DeviceRecordType.BatchOperation.getType());
		buffer.put(getTruncatedIdentifier(groupId));
		buffer.put(BatchOperationRecordType.BatchOperation.getType());
		return buffer.array();
	}
}