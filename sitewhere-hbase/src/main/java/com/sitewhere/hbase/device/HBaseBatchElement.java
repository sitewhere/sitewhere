/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.device;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.common.Pager;
import com.sitewhere.rest.model.device.batch.BatchElement;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.batch.IBatchElement;
import com.sitewhere.spi.device.request.IBatchElementUpdateRequest;
import com.sitewhere.spi.search.device.IBatchElementSearchCriteria;

/**
 * HBase specifics for dealing with SiteWhere batch operation elements.
 * 
 * @author Derek
 */
public class HBaseBatchElement {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(HBaseBatchElement.class);

	/** Length of element index info (subset of 8 byte long) */
	public static final int INDEX_LENGTH = 4;

	/** Column qualifier for element hardware id */
	public static final byte[] HARDWARE_ID = Bytes.toBytes("i");

	/** Column qualifier for element processing status */
	public static final byte[] PROCESSING_STATUS = Bytes.toBytes("s");

	/**
	 * Create a batch element row.
	 * 
	 * @param hbase
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IBatchElement createBatchElement(ISiteWhereHBaseClient hbase, HTableInterface devices,
			IBatchElement request) throws SiteWhereException {
		byte[] elementKey = getElementRowKey(request.getBatchOperationToken(), request.getIndex());

		// Use common processing logic so all backend implementations work the same.
		BatchElement element =
				SiteWherePersistence.batchElementCreateLogic(request.getBatchOperationToken(),
						request.getHardwareId(), request.getIndex());

		// Serialize as JSON.
		byte[] json = MarshalUtils.marshalJson(element);

		try {
			Put put = new Put(elementKey);
			put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, json);
			put.add(ISiteWhereHBase.FAMILY_ID, HARDWARE_ID, Bytes.toBytes(element.getHardwareId()));
			put.add(ISiteWhereHBase.FAMILY_ID, PROCESSING_STATUS,
					Bytes.toBytes(String.valueOf(request.getProcessingStatus().getCode())));
			devices.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to create device group element.", e);
		}

		return element;
	}

	/**
	 * Updates an existing batch operation element.
	 * 
	 * @param hbase
	 * @param operationToken
	 * @param index
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IBatchElement updateBatchElement(ISiteWhereHBaseClient hbase, String operationToken,
			long index, IBatchElementUpdateRequest request) throws SiteWhereException {
		HTableInterface devices = null;
		try {
			devices = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
			BatchElement element = getBatchElement(hbase, devices, operationToken, index);
			byte[] elementKey = getElementRowKey(operationToken, index);

			SiteWherePersistence.batchElementUpdateLogic(request, element);
			byte[] json = MarshalUtils.marshalJson(element);

			Put put = new Put(elementKey);
			put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, json);
			put.add(ISiteWhereHBase.FAMILY_ID, HARDWARE_ID, Bytes.toBytes(element.getHardwareId()));
			put.add(ISiteWhereHBase.FAMILY_ID, PROCESSING_STATUS,
					Bytes.toBytes(String.valueOf(request.getProcessingStatus().getCode())));
			devices.put(put);
			return element;
		} catch (IOException e) {
			throw new SiteWhereException("Unable to update batch element.", e);
		} finally {
			HBaseUtils.closeCleanly(devices);
		}
	}

	/**
	 * Gets the batch operation element given the parent operation token and unique index.
	 * 
	 * @param hbase
	 * @param devices
	 * @param operationToken
	 * @param index
	 * @return
	 * @throws SiteWhereException
	 */
	public static BatchElement getBatchElement(ISiteWhereHBaseClient hbase, HTableInterface devices,
			String operationToken, long index) throws SiteWhereException {
		byte[] elementKey = getElementRowKey(operationToken, index);
		try {
			Get get = new Get(elementKey);
			get.addColumn(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT);
			Result result = devices.get(get);
			if (result.size() != 1) {
				throw new SiteWhereException(
						"Unable to get batch operation element by operation token and index.");
			}
			return MarshalUtils.unmarshalJson(result.value(), BatchElement.class);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to create device group element.", e);
		}
	}

	/**
	 * List batch elements that meet the given criteria.
	 * 
	 * @param hbase
	 * @param batchToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IBatchElement> listBatchElements(ISiteWhereHBaseClient hbase,
			String batchToken, IBatchElementSearchCriteria criteria) throws SiteWhereException {
		HTableInterface table = null;
		ResultScanner scanner = null;
		try {
			table = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
			byte[] primary =
					HBaseBatchOperation.KEY_BUILDER.buildSubkey(batchToken,
							BatchOperationRecordType.BatchElement.getType());
			byte[] after =
					HBaseBatchOperation.KEY_BUILDER.buildSubkey(batchToken,
							(byte) (BatchOperationRecordType.BatchElement.getType() + 1));
			Scan scan = new Scan();
			scan.setStartRow(primary);
			scan.setStopRow(after);
			scanner = table.getScanner(scan);

			Pager<IBatchElement> pager = new Pager<IBatchElement>(criteria);
			for (Result result : scanner) {
				for (KeyValue column : result.raw()) {
					byte[] qualifier = column.getQualifier();
					if (Bytes.equals(ISiteWhereHBase.JSON_CONTENT, qualifier)) {
						IBatchElement current =
								MarshalUtils.unmarshalJson(column.getValue(), BatchElement.class);
						if ((criteria.getProcessingStatus() == null)
								|| (criteria.getProcessingStatus() == current.getProcessingStatus())) {
							pager.process(current);
						}
					}
				}
			}
			return new SearchResults<IBatchElement>(pager.getResults());
		} catch (IOException e) {
			throw new SiteWhereException("Error scanning batch element rows.", e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
			HBaseUtils.closeCleanly(table);
		}
	}

	/**
	 * Delete all elements for a batch operation.
	 * 
	 * @param hbase
	 * @param batchToken
	 * @throws SiteWhereException
	 */
	public static void deleteBatchElements(ISiteWhereHBaseClient hbase, String batchToken)
			throws SiteWhereException {
		HTableInterface table = null;
		ResultScanner scanner = null;
		try {
			table = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
			byte[] primary =
					HBaseBatchOperation.KEY_BUILDER.buildSubkey(batchToken,
							BatchOperationRecordType.BatchElement.getType());
			byte[] after =
					HBaseDeviceGroup.KEY_BUILDER.buildSubkey(batchToken,
							(byte) (BatchOperationRecordType.BatchElement.getType() + 1));
			Scan scan = new Scan();
			scan.setStartRow(primary);
			scan.setStopRow(after);
			scanner = table.getScanner(scan);

			List<DeleteRecord> matches = new ArrayList<DeleteRecord>();
			for (Result result : scanner) {
				byte[] row = result.getRow();
				byte[] json = null;
				for (KeyValue column : result.raw()) {
					byte[] qualifier = column.getQualifier();
					if (Bytes.equals(ISiteWhereHBase.JSON_CONTENT, qualifier)) {
						json = column.getValue();
					}
				}
				if (json != null) {
					matches.add(new DeleteRecord(row, json));
				}
			}
			for (DeleteRecord dr : matches) {
				try {
					Delete delete = new Delete(dr.getRowkey());
					table.delete(delete);
				} catch (IOException e) {
					LOGGER.warn("Batch element delete failed for key: " + dr.getRowkey());
				}
			}
		} catch (IOException e) {
			throw new SiteWhereException("Error scanning device group element rows.", e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
			HBaseUtils.closeCleanly(table);
		}
	}

	/**
	 * Get key for batch element.
	 * 
	 * @param batchToken
	 * @param index
	 * @return
	 * @throws SiteWhereException
	 */
	public static byte[] getElementRowKey(String batchToken, Long index) throws SiteWhereException {
		byte[] baserow =
				HBaseBatchOperation.KEY_BUILDER.buildSubkey(batchToken,
						BatchOperationRecordType.BatchElement.getType());
		byte[] eidBytes = getTruncatedIdentifier(index);
		ByteBuffer buffer = ByteBuffer.allocate(baserow.length + eidBytes.length);
		buffer.put(baserow);
		buffer.put(eidBytes);
		return buffer.array();
	}

	/**
	 * Truncate element id value to expected length. This will be a subset of the full
	 * 8-bit long value.
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] getTruncatedIdentifier(Long value) {
		byte[] bytes = Bytes.toBytes(value);
		byte[] result = new byte[INDEX_LENGTH];
		System.arraycopy(bytes, bytes.length - INDEX_LENGTH, result, 0, INDEX_LENGTH);
		return result;
	}
}