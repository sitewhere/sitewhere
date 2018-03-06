/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.hbase;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.sitewhere.device.persistence.DeviceManagementPersistence;
import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.DeleteRecord;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.encoder.PayloadMarshalerResolver;
import com.sitewhere.rest.model.device.group.DeviceGroupElement;
import com.sitewhere.rest.model.search.Pager;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;
import com.sitewhere.spi.search.ISearchCriteria;

/**
 * HBase specifics for dealing with SiteWhere device group elements.
 * 
 * @author Derek
 */
public class HBaseDeviceGroupElement {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(HBaseDeviceGroupElement.class);

    /** Length of element index info (subset of 8 byte long) */
    public static final int INDEX_LENGTH = 4;

    /** Column qualifier for element identifier (type+id) */
    public static final byte[] ELEMENT_IDENTIFIER = Bytes.toBytes("i");

    /**
     * Create a group of group elements.
     * 
     * @param context
     * @param group
     * @param requests
     * @param ignoreDuplicates
     * @return
     * @throws SiteWhereException
     */
    public static List<IDeviceGroupElement> createDeviceGroupElements(IHBaseContext context, IDeviceGroup group,
	    List<IDeviceGroupElementCreateRequest> requests, boolean ignoreDuplicates) throws SiteWhereException {
	byte[] groupKey = HBaseDeviceGroup.KEY_BUILDER.buildPrimaryKey(context, group.getToken());
	List<IDeviceGroupElement> results = new ArrayList<IDeviceGroupElement>();
	for (IDeviceGroupElementCreateRequest request : requests) {
	    Long eid = HBaseDeviceGroup.allocateNextElementId(context, groupKey);
	    results.add(HBaseDeviceGroupElement.createDeviceGroupElement(context, group, eid, request));
	}
	return results;
    }

    /**
     * Create a new device group element.
     * 
     * @param context
     * @param groupToken
     * @param index
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceGroupElement createDeviceGroupElement(IHBaseContext context, IDeviceGroup group, Long index,
	    IDeviceGroupElementCreateRequest request) throws SiteWhereException {
	byte[] elementKey = getElementRowKey(context, group.getToken(), index);

	// Use common processing logic so all backend implementations work the
	// same.
	DeviceGroupElement element = DeviceManagementPersistence.deviceGroupElementCreateLogic(request, group, null,
		null);

	byte[] payload = context.getPayloadMarshaler().encodeDeviceGroupElement(element);

	Table devices = null;
	try {
	    devices = getDeviceTableInterface(context);
	    Put put = new Put(elementKey);
	    HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
	    put.addColumn(ISiteWhereHBase.FAMILY_ID, ELEMENT_IDENTIFIER, getCombinedIdentifier(request));
	    devices.put(put);
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to create device group element.", e);
	} finally {
	    HBaseUtils.closeCleanly(devices);
	}

	return element;
    }

    /**
     * Remove the given device group elements.
     * 
     * @param context
     * @param group
     * @param elements
     * @return
     * @throws SiteWhereException
     */
    public static List<IDeviceGroupElement> removeDeviceGroupElements(IHBaseContext context, IDeviceGroup group,
	    List<IDeviceGroupElementCreateRequest> elements) throws SiteWhereException {
	List<byte[]> combinedIds = new ArrayList<byte[]>();
	for (IDeviceGroupElementCreateRequest request : elements) {
	    combinedIds.add(getCombinedIdentifier(request));
	}
	return deleteElements(context, group.getToken(), combinedIds);
    }

    /**
     * Handles logic for finding and deleting device group elements.
     * 
     * @param context
     * @param groupToken
     * @param combinedIds
     * @return
     * @throws SiteWhereException
     */
    protected static List<IDeviceGroupElement> deleteElements(IHBaseContext context, String groupToken,
	    List<byte[]> combinedIds) throws SiteWhereException {
	Table table = null;
	ResultScanner scanner = null;
	try {
	    table = getDeviceTableInterface(context);
	    byte[] primary = HBaseDeviceGroup.KEY_BUILDER.buildSubkey(context, groupToken,
		    DeviceGroupRecordType.DeviceGroupElement.getType());
	    byte[] after = HBaseDeviceGroup.KEY_BUILDER.buildSubkey(context, groupToken,
		    (byte) (DeviceGroupRecordType.DeviceGroupElement.getType() + 1));
	    Scan scan = new Scan();
	    scan.setStartRow(primary);
	    scan.setStopRow(after);
	    scanner = table.getScanner(scan);

	    List<DeleteRecord> matches = new ArrayList<DeleteRecord>();
	    for (Result result : scanner) {
		byte[] row = result.getRow();

		boolean shouldAdd = false;
		byte[] type = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
		byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
		byte[] ident = result.getValue(ISiteWhereHBase.FAMILY_ID, ELEMENT_IDENTIFIER);
		if (ident != null) {
		    for (byte[] toDelete : combinedIds) {
			if (Bytes.equals(toDelete, ident)) {
			    shouldAdd = true;
			    break;
			}
		    }
		}
		if ((shouldAdd) && (type != null) && (payload != null)) {
		    matches.add(new DeleteRecord(row, type, payload));
		}
	    }
	    List<IDeviceGroupElement> results = new ArrayList<IDeviceGroupElement>();
	    for (DeleteRecord dr : matches) {
		try {
		    Delete delete = new Delete(dr.getRowkey());
		    table.delete(delete);
		    results.add(PayloadMarshalerResolver.getInstance().getMarshaler(dr.getPayloadType())
			    .decodeDeviceGroupElement(dr.getPayload()));
		} catch (IOException e) {
		    LOGGER.warn("Group element delete failed for key: " + dr.getRowkey());
		}
	    }
	    return results;
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
     * Deletes all elements for a device group. TODO: There is probably a much more
     * efficient method of deleting the records than calling a delete for each.
     * 
     * @param context
     * @param groupToken
     * @throws SiteWhereException
     */
    public static void deleteElements(IHBaseContext context, String groupToken) throws SiteWhereException {
	Table table = null;
	ResultScanner scanner = null;
	try {
	    table = getDeviceTableInterface(context);
	    byte[] primary = HBaseDeviceGroup.KEY_BUILDER.buildSubkey(context, groupToken,
		    DeviceGroupRecordType.DeviceGroupElement.getType());
	    byte[] after = HBaseDeviceGroup.KEY_BUILDER.buildSubkey(context, groupToken,
		    (byte) (DeviceGroupRecordType.DeviceGroupElement.getType() + 1));
	    Scan scan = new Scan();
	    scan.setStartRow(primary);
	    scan.setStopRow(after);
	    scanner = table.getScanner(scan);

	    List<DeleteRecord> matches = new ArrayList<DeleteRecord>();
	    for (Result result : scanner) {
		byte[] row = result.getRow();
		byte[] type = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
		byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
		if ((type != null) && (payload != null)) {
		    matches.add(new DeleteRecord(row, type, payload));
		}
	    }
	    for (DeleteRecord dr : matches) {
		try {
		    Delete delete = new Delete(dr.getRowkey());
		    table.delete(delete);
		} catch (IOException e) {
		    LOGGER.warn("Group element delete failed for key: " + dr.getRowkey());
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
     * Get paged results for listing device group elements. TODO: This is not
     * optimized! Getting the correct record count requires a full scan of all
     * elements in the group.
     * 
     * @param context
     * @param group
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IDeviceGroupElement> listDeviceGroupElements(IHBaseContext context, IDeviceGroup group,
	    ISearchCriteria criteria) throws SiteWhereException {
	Table table = null;
	ResultScanner scanner = null;
	try {
	    table = getDeviceTableInterface(context);
	    byte[] primary = HBaseDeviceGroup.KEY_BUILDER.buildSubkey(context, group.getToken(),
		    DeviceGroupRecordType.DeviceGroupElement.getType());
	    byte[] after = HBaseDeviceGroup.KEY_BUILDER.buildSubkey(context, group.getToken(),
		    (byte) (DeviceGroupRecordType.DeviceGroupElement.getType() + 1));
	    Scan scan = new Scan();
	    scan.setStartRow(primary);
	    scan.setStopRow(after);
	    scanner = table.getScanner(scan);

	    Pager<IDeviceGroupElement> pager = new Pager<IDeviceGroupElement>(criteria);
	    for (Result result : scanner) {
		byte[] type = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
		byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
		if ((type != null) && (payload != null)) {
		    pager.process(PayloadMarshalerResolver.getInstance().getMarshaler(type)
			    .decodeDeviceGroupElement(payload));
		}
	    }
	    return new SearchResults<IDeviceGroupElement>(pager.getResults());
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
     * Get key for a group element.
     * 
     * @param context
     * @param groupToken
     * @param elementId
     * @return
     * @throws SiteWhereException
     */
    public static byte[] getElementRowKey(IHBaseContext context, String groupToken, Long elementId)
	    throws SiteWhereException {
	byte[] baserow = HBaseDeviceGroup.KEY_BUILDER.buildSubkey(context, groupToken,
		DeviceGroupRecordType.DeviceGroupElement.getType());
	byte[] eidBytes = getTruncatedIdentifier(elementId);
	ByteBuffer buffer = ByteBuffer.allocate(baserow.length + eidBytes.length);
	buffer.put(baserow);
	buffer.put(eidBytes);
	return buffer.array();
    }

    /**
     * Truncate element id value to expected length. This will be a subset of the
     * full 8-bit long value.
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
     * Create an identifier based on element type and id.
     * 
     * @param request
     * @return
     */
    public static byte[] getCombinedIdentifier(IDeviceGroupElementCreateRequest request) {
	ByteBuffer buffer = ByteBuffer.allocate(1);
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