/*
 * HBaseDeviceNetworkElement.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
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
import com.sitewhere.rest.model.device.network.DeviceNetworkElement;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.network.IDeviceNetworkElement;
import com.sitewhere.spi.device.request.IDeviceNetworkElementCreateRequest;
import com.sitewhere.spi.search.ISearchCriteria;

/**
 * HBase specifics for dealing with SiteWhere device network elements.
 * 
 * @author Derek
 */
public class HBaseDeviceNetworkElement {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(HBaseDeviceNetworkElement.class);

	/** Length of element index info (subset of 8 byte long) */
	public static final int INDEX_LENGTH = 4;

	/** Column qualifier for element identifier (type+id) */
	public static final byte[] ELEMENT_IDENTIFIER = Bytes.toBytes("ident");

	/**
	 * Create a group of network elements.
	 * 
	 * @param hbase
	 * @param networkToken
	 * @param requests
	 * @return
	 * @throws SiteWhereException
	 */
	public static List<IDeviceNetworkElement> createDeviceNetworkElements(ISiteWhereHBaseClient hbase,
			String networkToken, List<IDeviceNetworkElementCreateRequest> requests) throws SiteWhereException {
		byte[] networkKey = HBaseDeviceNetwork.KEY_BUILDER.buildPrimaryKey(networkToken);
		List<IDeviceNetworkElement> results = new ArrayList<IDeviceNetworkElement>();
		for (IDeviceNetworkElementCreateRequest request : requests) {
			Long eid = HBaseDeviceNetwork.allocateNextElementId(hbase, networkKey);
			results.add(HBaseDeviceNetworkElement.createDeviceNetworkElement(hbase, networkToken, eid,
					request));
		}
		return results;
	}

	/**
	 * Create a new device network element.
	 * 
	 * @param hbase
	 * @param networkToken
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceNetworkElement createDeviceNetworkElement(ISiteWhereHBaseClient hbase,
			String networkToken, Long index, IDeviceNetworkElementCreateRequest request)
			throws SiteWhereException {
		byte[] elementKey = getElementRowKey(networkToken, index);

		// Use common processing logic so all backend implementations work the same.
		DeviceNetworkElement element =
				SiteWherePersistence.deviceNetworkElementCreateLogic(request, networkToken, index);

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
			throw new SiteWhereException("Unable to create zone.", e);
		} finally {
			HBaseUtils.closeCleanly(devices);
		}

		return element;
	}

	/**
	 * Remove the given device network elements.
	 * 
	 * @param hbase
	 * @param networkToken
	 * @param elements
	 * @return
	 * @throws SiteWhereException
	 */
	public static List<IDeviceNetworkElement> removeDeviceNetworkElements(ISiteWhereHBaseClient hbase,
			String networkToken, List<IDeviceNetworkElementCreateRequest> elements) throws SiteWhereException {
		List<byte[]> combinedIds = new ArrayList<byte[]>();
		for (IDeviceNetworkElementCreateRequest request : elements) {
			combinedIds.add(getCombinedIdentifier(request));
		}
		return deleteElements(hbase, networkToken, combinedIds);
	}

	/**
	 * Handles logic for finding and deleting device network elements.
	 * 
	 * @param hbase
	 * @param networkToken
	 * @param combinedIds
	 * @return
	 * @throws SiteWhereException
	 */
	protected static List<IDeviceNetworkElement> deleteElements(ISiteWhereHBaseClient hbase,
			String networkToken, List<byte[]> combinedIds) throws SiteWhereException {
		HTableInterface table = null;
		ResultScanner scanner = null;
		try {
			table = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
			byte[] primary =
					HBaseDeviceNetwork.KEY_BUILDER.buildSubkey(networkToken,
							DeviceNetworkRecordType.DeviceNetworkElement.getType());
			byte[] after =
					HBaseDeviceNetwork.KEY_BUILDER.buildSubkey(networkToken,
							(byte) (DeviceNetworkRecordType.DeviceNetworkElement.getType() + 1));
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
			List<IDeviceNetworkElement> results = new ArrayList<IDeviceNetworkElement>();
			for (DeleteRecord dr : matches) {
				try {
					Delete delete = new Delete(dr.getRowkey());
					table.delete(delete);
					results.add(MarshalUtils.unmarshalJson(dr.getJson(), DeviceNetworkElement.class));
				} catch (IOException e) {
					LOGGER.warn("Network element delete failed for key: " + dr.getRowkey());
				}
			}
			return results;
		} catch (IOException e) {
			throw new SiteWhereException("Error scanning device network rows.", e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
			HBaseUtils.closeCleanly(table);
		}
	}

	/**
	 * Data structure that holds information about a record to be deleted.
	 * 
	 * @author Derek
	 */
	public static class DeleteRecord {

		private byte[] rowkey;
		private byte[] json;

		public DeleteRecord(byte[] rowkey, byte[] json) {
			this.rowkey = rowkey;
			this.json = json;
		}

		public byte[] getRowkey() {
			return rowkey;
		}

		public byte[] getJson() {
			return json;
		}
	}

	/**
	 * Get paged results for listing device network elements. TODO: This is not optimized!
	 * Getting the correct record count requires a full scan of all elements in the
	 * network.
	 * 
	 * @param hbase
	 * @param networkToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceNetworkElement> listDeviceNetworkElements(ISiteWhereHBaseClient hbase,
			String networkToken, ISearchCriteria criteria) throws SiteWhereException {
		HTableInterface table = null;
		ResultScanner scanner = null;
		try {
			table = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
			byte[] primary =
					HBaseDeviceNetwork.KEY_BUILDER.buildSubkey(networkToken,
							DeviceNetworkRecordType.DeviceNetworkElement.getType());
			byte[] after =
					HBaseDeviceNetwork.KEY_BUILDER.buildSubkey(networkToken,
							(byte) (DeviceNetworkRecordType.DeviceNetworkElement.getType() + 1));
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
			List<IDeviceNetworkElement> results = new ArrayList<IDeviceNetworkElement>();
			for (byte[] json : pager.getResults()) {
				results.add(MarshalUtils.unmarshalJson(json, DeviceNetworkElement.class));
			}
			return new SearchResults<IDeviceNetworkElement>(results);
		} catch (IOException e) {
			throw new SiteWhereException("Error scanning device network rows.", e);
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
	 * @param networkToken
	 * @param elementId
	 * @return
	 * @throws SiteWhereException
	 */
	public static byte[] getElementRowKey(String networkToken, Long elementId) throws SiteWhereException {
		byte[] baserow =
				HBaseDeviceNetwork.KEY_BUILDER.buildSubkey(networkToken,
						DeviceNetworkRecordType.DeviceNetworkElement.getType());
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
	public static byte[] getCombinedIdentifier(IDeviceNetworkElementCreateRequest request) {
		byte[] id = Bytes.toBytes(request.getElementId());
		ByteBuffer buffer = ByteBuffer.allocate(1 + id.length);
		switch (request.getType()) {
		case Device: {
			buffer.put((byte) 0x00);
			break;
		}
		case Network: {
			buffer.put((byte) 0x01);
			break;
		}
		default: {
			throw new RuntimeException("Unknown device network element type: " + request.getType().name());
		}
		}
		buffer.put(id);
		return buffer.array();
	}
}