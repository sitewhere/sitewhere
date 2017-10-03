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
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.batch.persistence.BatchManagementPersistence;
import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.DeleteRecord;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.encoder.PayloadMarshalerResolver;
import com.sitewhere.rest.model.device.batch.BatchElement;
import com.sitewhere.rest.model.search.Pager;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.device.request.IBatchElementUpdateRequest;
import com.sitewhere.spi.search.device.IBatchElementSearchCriteria;

/**
 * HBase specifics for dealing with SiteWhere batch operation elements.
 * 
 * @author Derek
 */
public class HBaseBatchElement {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Length of element index info (subset of 8 byte long) */
    public static final int INDEX_LENGTH = 4;

    /** Column qualifier for element hardware id */
    public static final byte[] HARDWARE_ID = Bytes.toBytes("i");

    /** Column qualifier for element processing status */
    public static final byte[] PROCESSING_STATUS = Bytes.toBytes("s");

    /**
     * Create a batch element row.
     * 
     * @param context
     * @param devices
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static IBatchElement createBatchElement(IHBaseContext context, Table devices, IBatchElement request)
	    throws SiteWhereException {
	byte[] elementKey = getElementRowKey(context, request.getBatchOperationToken(), request.getIndex());

	// Use common processing logic so all backend implementations work the
	// same.
	BatchElement element = BatchManagementPersistence.batchElementCreateLogic(request.getBatchOperationToken(),
		request.getHardwareId(), request.getIndex());

	// Encode batch element.
	byte[] payload = context.getPayloadMarshaler().encodeBatchElement(element);

	try {
	    Put put = new Put(elementKey);
	    HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
	    put.addColumn(ISiteWhereHBase.FAMILY_ID, HARDWARE_ID, Bytes.toBytes(element.getHardwareId()));
	    put.addColumn(ISiteWhereHBase.FAMILY_ID, PROCESSING_STATUS,
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
     * @param context
     * @param operationToken
     * @param index
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static IBatchElement updateBatchElement(IHBaseContext context, String operationToken, long index,
	    IBatchElementUpdateRequest request) throws SiteWhereException {
	Table devices = null;
	try {
	    devices = getDeviceTableInterface(context);
	    BatchElement element = getBatchElement(context, devices, operationToken, index);
	    byte[] elementKey = getElementRowKey(context, operationToken, index);

	    BatchManagementPersistence.batchElementUpdateLogic(request, element);
	    byte[] payload = context.getPayloadMarshaler().encodeBatchElement(element);

	    Put put = new Put(elementKey);
	    HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
	    put.addColumn(ISiteWhereHBase.FAMILY_ID, HARDWARE_ID, Bytes.toBytes(element.getHardwareId()));
	    put.addColumn(ISiteWhereHBase.FAMILY_ID, PROCESSING_STATUS,
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
     * Gets the batch operation element given the parent operation token and
     * unique index.
     * 
     * @param context
     * @param devices
     * @param operationToken
     * @param index
     * @return
     * @throws SiteWhereException
     */
    public static BatchElement getBatchElement(IHBaseContext context, Table devices, String operationToken, long index)
	    throws SiteWhereException {
	byte[] elementKey = getElementRowKey(context, operationToken, index);
	try {
	    Get get = new Get(elementKey);
	    HBaseUtils.addPayloadFields(get);
	    Result result = devices.get(get);

	    byte[] type = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
	    byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
	    if ((type == null) || (payload == null)) {
		return null;
	    }

	    return PayloadMarshalerResolver.getInstance().getMarshaler(type).decodeBatchElement(payload);
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to create device group element.", e);
	}
    }

    /**
     * List batch elements that meet the given criteria.
     * 
     * @param context
     * @param batchToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IBatchElement> listBatchElements(IHBaseContext context, String batchToken,
	    IBatchElementSearchCriteria criteria) throws SiteWhereException {
	Table table = null;
	ResultScanner scanner = null;
	try {
	    table = getDeviceTableInterface(context);
	    byte[] primary = HBaseBatchOperation.KEY_BUILDER.buildSubkey(context, batchToken,
		    BatchOperationRecordType.BatchElement.getType());
	    byte[] after = HBaseBatchOperation.KEY_BUILDER.buildSubkey(context, batchToken,
		    (byte) (BatchOperationRecordType.BatchElement.getType() + 1));
	    Scan scan = new Scan();
	    scan.setStartRow(primary);
	    scan.setStopRow(after);
	    scanner = table.getScanner(scan);

	    Pager<IBatchElement> pager = new Pager<IBatchElement>(criteria);
	    for (Result result : scanner) {
		byte[] payloadType = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
		byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);

		if ((payload != null) && (payloadType != null)) {
		    BatchElement elm = PayloadMarshalerResolver.getInstance().getMarshaler(payloadType)
			    .decodeBatchElement(payload);
		    if ((criteria.getProcessingStatus() == null)
			    || (criteria.getProcessingStatus() == elm.getProcessingStatus())) {
			pager.process(elm);
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
     * @param context
     * @param batchToken
     * @throws SiteWhereException
     */
    public static void deleteBatchElements(IHBaseContext context, String batchToken) throws SiteWhereException {
	Table table = null;
	ResultScanner scanner = null;
	try {
	    table = getDeviceTableInterface(context);
	    byte[] primary = HBaseBatchOperation.KEY_BUILDER.buildSubkey(context, batchToken,
		    BatchOperationRecordType.BatchElement.getType());
	    byte[] after = HBaseBatchOperation.KEY_BUILDER.buildSubkey(context, batchToken,
		    (byte) (BatchOperationRecordType.BatchElement.getType() + 1));
	    Scan scan = new Scan();
	    scan.setStartRow(primary);
	    scan.setStopRow(after);
	    scanner = table.getScanner(scan);

	    List<DeleteRecord> matches = new ArrayList<DeleteRecord>();
	    for (Result result : scanner) {
		byte[] row = result.getRow();
		byte[] payloadType = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
		byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
		if (payload != null) {
		    matches.add(new DeleteRecord(row, payloadType, payload));
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
     * @param context
     * @param batchToken
     * @param index
     * @return
     * @throws SiteWhereException
     */
    public static byte[] getElementRowKey(IHBaseContext context, String batchToken, Long index)
	    throws SiteWhereException {
	byte[] baserow = HBaseBatchOperation.KEY_BUILDER.buildSubkey(context, batchToken,
		BatchOperationRecordType.BatchElement.getType());
	byte[] eidBytes = getTruncatedIdentifier(index);
	ByteBuffer buffer = ByteBuffer.allocate(baserow.length + eidBytes.length);
	buffer.put(baserow);
	buffer.put(eidBytes);
	return buffer.array();
    }

    /**
     * Truncate element id value to expected length. This will be a subset of
     * the full 8-bit long value.
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