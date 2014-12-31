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
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.common.MarshalUtils;
import com.sitewhere.hbase.common.Pager;
import com.sitewhere.rest.model.device.group.DeviceGroupElement;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
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
	private static Logger LOGGER = Logger.getLogger(HBaseDeviceGroupElement.class);

	/** Length of element index info (subset of 8 byte long) */
	public static final int INDEX_LENGTH = 4;

	/** Column qualifier for element identifier (type+id) */
	public static final byte[] ELEMENT_IDENTIFIER = Bytes.toBytes("ident");

	/**
	 * Create a group of group elements.
	 * 
	 * @param hbase
	 * @param groupToken
	 * @param requests
	 * @return
	 * @throws SiteWhereException
	 */
	public static List<IDeviceGroupElement> createDeviceGroupElements(ISiteWhereHBaseClient hbase,
			String groupToken, List<IDeviceGroupElementCreateRequest> requests) throws SiteWhereException {
		byte[] groupKey = HBaseDeviceGroup.KEY_BUILDER.buildPrimaryKey(groupToken);
		List<IDeviceGroupElement> results = new ArrayList<IDeviceGroupElement>();
		for (IDeviceGroupElementCreateRequest request : requests) {
			Long eid = HBaseDeviceGroup.allocateNextElementId(hbase, groupKey);
			results.add(HBaseDeviceGroupElement.createDeviceGroupElement(hbase, groupToken, eid, request));
		}
		return results;
	}

	/**
	 * Create a new device group element.
	 * 
	 * @param hbase
	 * @param groupToken
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceGroupElement createDeviceGroupElement(ISiteWhereHBaseClient hbase,
			String groupToken, Long index, IDeviceGroupElementCreateRequest request)
			throws SiteWhereException {
		byte[] elementKey = getElementRowKey(groupToken, index);

		// Use common processing logic so all backend implementations work the same.
		DeviceGroupElement element =
				SiteWherePersistence.deviceGroupElementCreateLogic(request, groupToken, index);

		// Serialize as JSON.
		byte[] json = MarshalUtils.marshalJson(element);

		HTableInterface devices = null;
		try {
			devices = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
			Put put = new Put(elementKey);
			put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, json);
			put.add(ISiteWhereHBase.FAMILY_ID, ELEMENT_IDENTIFIER, getCombinedIdentifier(request));
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
	 * @param hbase
	 * @param groupToken
	 * @param elements
	 * @return
	 * @throws SiteWhereException
	 */
	public static List<IDeviceGroupElement> removeDeviceGroupElements(ISiteWhereHBaseClient hbase,
			String groupToken, List<IDeviceGroupElementCreateRequest> elements) throws SiteWhereException {
		List<byte[]> combinedIds = new ArrayList<byte[]>();
		for (IDeviceGroupElementCreateRequest request : elements) {
			combinedIds.add(getCombinedIdentifier(request));
		}
		return deleteElements(hbase, groupToken, combinedIds);
	}

	/**
	 * Handles logic for finding and deleting device group elements.
	 * 
	 * @param hbase
	 * @param groupToken
	 * @param combinedIds
	 * @param deleteAll
	 * @return
	 * @throws SiteWhereException
	 */
	protected static List<IDeviceGroupElement> deleteElements(ISiteWhereHBaseClient hbase, String groupToken,
			List<byte[]> combinedIds) throws SiteWhereException {
		HTableInterface table = null;
		ResultScanner scanner = null;
		try {
			table = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
			byte[] primary =
					HBaseDeviceGroup.KEY_BUILDER.buildSubkey(groupToken,
							DeviceGroupRecordType.DeviceGroupElement.getType());
			byte[] after =
					HBaseDeviceGroup.KEY_BUILDER.buildSubkey(groupToken,
							(byte) (DeviceGroupRecordType.DeviceGroupElement.getType() + 1));
			Scan scan = new Scan();
			scan.setStartRow(primary);
			scan.setStopRow(after);
			scanner = table.getScanner(scan);

			List<DeleteRecord> matches = new ArrayList<DeleteRecord>();
			for (Result result : scanner) {
				byte[] row = result.getRow();

				boolean shouldAdd = false;
				byte[] json = null;
				for (KeyValue column : result.raw()) {
					byte[] qualifier = column.getQualifier();
					if (Bytes.equals(ELEMENT_IDENTIFIER, qualifier)) {
						for (byte[] toDelete : combinedIds) {
							if (Bytes.equals(toDelete, column.getValue())) {
								shouldAdd = true;
								break;
							}
						}
					}
					if (Bytes.equals(ISiteWhereHBase.JSON_CONTENT, qualifier)) {
						json = column.getValue();
					}
				}
				if ((shouldAdd) && (json != null)) {
					matches.add(new DeleteRecord(row, json));
				}
			}
			List<IDeviceGroupElement> results = new ArrayList<IDeviceGroupElement>();
			for (DeleteRecord dr : matches) {
				try {
					Delete delete = new Delete(dr.getRowkey());
					table.delete(delete);
					results.add(MarshalUtils.unmarshalJson(dr.getJson(), DeviceGroupElement.class));
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
	 * @param hbase
	 * @param groupToken
	 * @throws SiteWhereException
	 */
	public static void deleteElements(ISiteWhereHBaseClient hbase, String groupToken)
			throws SiteWhereException {
		HTableInterface table = null;
		ResultScanner scanner = null;
		try {
			table = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
			byte[] primary =
					HBaseDeviceGroup.KEY_BUILDER.buildSubkey(groupToken,
							DeviceGroupRecordType.DeviceGroupElement.getType());
			byte[] after =
					HBaseDeviceGroup.KEY_BUILDER.buildSubkey(groupToken,
							(byte) (DeviceGroupRecordType.DeviceGroupElement.getType() + 1));
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
	 * Get paged results for listing device group elements. TODO: This is not optimized!
	 * Getting the correct record count requires a full scan of all elements in the group.
	 * 
	 * @param hbase
	 * @param groupToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceGroupElement> listDeviceGroupElements(ISiteWhereHBaseClient hbase,
			String groupToken, ISearchCriteria criteria) throws SiteWhereException {
		HTableInterface table = null;
		ResultScanner scanner = null;
		try {
			table = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
			byte[] primary =
					HBaseDeviceGroup.KEY_BUILDER.buildSubkey(groupToken,
							DeviceGroupRecordType.DeviceGroupElement.getType());
			byte[] after =
					HBaseDeviceGroup.KEY_BUILDER.buildSubkey(groupToken,
							(byte) (DeviceGroupRecordType.DeviceGroupElement.getType() + 1));
			Scan scan = new Scan();
			scan.setStartRow(primary);
			scan.setStopRow(after);
			scanner = table.getScanner(scan);

			Pager<byte[]> pager = new Pager<byte[]>(criteria);
			for (Result result : scanner) {
				for (KeyValue column : result.raw()) {
					byte[] qualifier = column.getQualifier();
					if (Bytes.equals(ISiteWhereHBase.JSON_CONTENT, qualifier)) {
						pager.process(column.getValue());
					}
				}
			}
			List<IDeviceGroupElement> results = new ArrayList<IDeviceGroupElement>();
			for (byte[] json : pager.getResults()) {
				results.add(MarshalUtils.unmarshalJson(json, DeviceGroupElement.class));
			}
			return new SearchResults<IDeviceGroupElement>(results);
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
	 * Get key for a network element.
	 * 
	 * @param groupToken
	 * @param elementId
	 * @return
	 * @throws SiteWhereException
	 */
	public static byte[] getElementRowKey(String groupToken, Long elementId) throws SiteWhereException {
		byte[] baserow =
				HBaseDeviceGroup.KEY_BUILDER.buildSubkey(groupToken,
						DeviceGroupRecordType.DeviceGroupElement.getType());
		byte[] eidBytes = getTruncatedIdentifier(elementId);
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

	/**
	 * Create an identifier based on element type and id.
	 * 
	 * @param request
	 * @return
	 */
	public static byte[] getCombinedIdentifier(IDeviceGroupElementCreateRequest request) {
		byte[] id = Bytes.toBytes(request.getElementId());
		ByteBuffer buffer = ByteBuffer.allocate(1 + id.length);
		switch (request.getType()) {
		case Device: {
			buffer.put((byte) 0x00);
			break;
		}
		case Group: {
			buffer.put((byte) 0x01);
			break;
		}
		default: {
			throw new RuntimeException("Unknown device group element type: " + request.getType().name());
		}
		}
		buffer.put(id);
		return buffer.array();
	}
}