/*
 * HBaseDevice.java 
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
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.common.MarshalUtils;
import com.sitewhere.hbase.common.Pager;
import com.sitewhere.hbase.uid.IdManager;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchCriteria;

/**
 * HBase specifics for dealing with SiteWhere devices.
 * 
 * @author Derek
 */
public class HBaseDevice {

	/** Length of device identifier (subset of 8 byte long) */
	public static final int DEVICE_IDENTIFIER_LENGTH = 4;

	/** Byte that indicates an assignment history entry qualifier */
	public static final byte ASSIGNMENT_HISTORY_INDICATOR = (byte) 0x01;

	/** Column qualifier for current device assignment */
	public static final byte[] CURRENT_ASSIGNMENT = "assignment".getBytes();

	/**
	 * Create a new device.
	 * 
	 * @param hbase
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDevice createDevice(ISiteWhereHBaseClient hbase, IDeviceCreateRequest request)
			throws SiteWhereException {
		Long existing = IdManager.getInstance().getDeviceKeys().getValue(request.getHardwareId());
		if (existing != null) {
			throw new SiteWhereSystemException(ErrorCode.DuplicateHardwareId, ErrorLevel.ERROR,
					HttpServletResponse.SC_CONFLICT);
		}
		Long value = IdManager.getInstance().getDeviceKeys().getNextCounterValue();
		Long inverse = Long.MAX_VALUE - value;
		IdManager.getInstance().getDeviceKeys().create(request.getHardwareId(), inverse);

		Device newDevice = new Device();
		newDevice.setAssetId(request.getAssetId());
		newDevice.setHardwareId(request.getHardwareId());
		newDevice.setComments(request.getComments());

		MetadataProvider.copy(request, newDevice);
		SiteWherePersistence.initializeEntityMetadata(newDevice);

		return putDeviceJson(hbase, newDevice);
	}

	/**
	 * Update an existing device.
	 * 
	 * @param hbase
	 * @param hardwareId
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDevice updateDevice(ISiteWhereHBaseClient hbase, String hardwareId,
			IDeviceCreateRequest request) throws SiteWhereException {

		// Can not update the hardware id on a device.
		if ((request.getHardwareId() != null) && (!request.getHardwareId().equals(hardwareId))) {
			throw new SiteWhereSystemException(ErrorCode.DeviceHardwareIdCanNotBeChanged, ErrorLevel.ERROR,
					HttpServletResponse.SC_BAD_REQUEST);
		}

		// Copy any non-null fields.
		Device updatedDevice = getDeviceByHardwareId(hbase, hardwareId);
		if (request.getAssetId() != null) {
			updatedDevice.setAssetId(request.getAssetId());
		}
		if (request.getComments() != null) {
			updatedDevice.setComments(request.getComments());
		}
		if ((request.getMetadata() != null) && (request.getMetadata().size() > 0)) {
			updatedDevice.getMetadata().clear();
			MetadataProvider.copy(request, updatedDevice);
		}
		SiteWherePersistence.setUpdatedEntityMetadata(updatedDevice);

		return putDeviceJson(hbase, updatedDevice);
	}

	/**
	 * List devices that meet the given criteria.
	 * 
	 * @param hbase
	 * @param includeDeleted
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDevice> listDevices(ISiteWhereHBaseClient hbase, boolean includeDeleted,
			ISearchCriteria criteria) throws SiteWhereException {
		Pager<byte[]> matches = getFilteredDevices(hbase, includeDeleted, false, criteria);
		List<IDevice> response = new ArrayList<IDevice>();
		for (byte[] json : matches.getResults()) {
			response.add(MarshalUtils.unmarshalJson(json, Device.class));
		}
		return new SearchResults<IDevice>(response, matches.getTotal());
	}

	/**
	 * List devices that do not have a current assignment.
	 * 
	 * @param hbase
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDevice> listUnassignedDevices(ISiteWhereHBaseClient hbase,
			ISearchCriteria criteria) throws SiteWhereException {
		Pager<byte[]> matches = getFilteredDevices(hbase, false, true, criteria);
		List<IDevice> response = new ArrayList<IDevice>();
		for (byte[] json : matches.getResults()) {
			response.add(MarshalUtils.unmarshalJson(json, Device.class));
		}
		return new SearchResults<IDevice>(response, matches.getTotal());
	}

	/**
	 * Get a list of devices filtered with certain criteria.
	 * 
	 * @param hbase
	 * @param includeDeleted
	 * @param excludeAssigned
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	protected static Pager<byte[]> getFilteredDevices(ISiteWhereHBaseClient hbase, boolean includeDeleted,
			boolean excludeAssigned, ISearchCriteria criteria) throws SiteWhereException {
		HTableInterface devices = null;
		ResultScanner scanner = null;
		try {
			devices = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
			Scan scan = new Scan();
			scanner = devices.getScanner(scan);

			Pager<byte[]> pager = new Pager<byte[]>(criteria);
			for (Result result : scanner) {
				boolean shouldAdd = true;
				byte[] json = null;
				for (KeyValue column : result.raw()) {
					byte[] qualifier = column.getQualifier();
					if ((Bytes.equals(CURRENT_ASSIGNMENT, qualifier)) && (excludeAssigned)) {
						shouldAdd = false;
					}
					if ((Bytes.equals(ISiteWhereHBase.DELETED, qualifier)) && (!includeDeleted)) {
						shouldAdd = false;
					}
					if (Bytes.equals(ISiteWhereHBase.JSON_CONTENT, qualifier)) {
						json = column.getValue();
					}
				}
				if ((shouldAdd) && (json != null)) {
					pager.process(json);
				}
			}
			return pager;
		} catch (IOException e) {
			throw new SiteWhereException("Error scanning device rows.", e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
			HBaseUtils.closeCleanly(devices);
		}
	}

	/**
	 * Save the JSON representation of a device.
	 * 
	 * @param hbase
	 * @param device
	 * @return
	 * @throws SiteWhereException
	 */
	public static Device putDeviceJson(ISiteWhereHBaseClient hbase, Device device) throws SiteWhereException {
		Long value = IdManager.getInstance().getDeviceKeys().getValue(device.getHardwareId());
		if (value == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.ERROR);
		}
		byte[] primary = getPrimaryRowkey(value);
		byte[] json = MarshalUtils.marshalJson(device);

		HTableInterface devices = null;
		try {
			devices = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
			Put put = new Put(primary);
			put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, json);
			devices.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to put device data.", e);
		} finally {
			HBaseUtils.closeCleanly(devices);
		}

		return device;
	}

	/**
	 * Get a device by unique hardware id.
	 * 
	 * @param hbase
	 * @param hardwareId
	 * @return
	 * @throws SiteWhereException
	 */
	public static Device getDeviceByHardwareId(ISiteWhereHBaseClient hbase, String hardwareId)
			throws SiteWhereException {
		Long deviceId = IdManager.getInstance().getDeviceKeys().getValue(hardwareId);
		if (deviceId == null) {
			return null;
		}

		// Find row key based on value associated with hardware id.
		byte[] primary = getPrimaryRowkey(deviceId);

		HTableInterface devices = null;
		try {
			devices = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
			Get get = new Get(primary);
			get.addColumn(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT);
			Result result = devices.get(get);
			if (result.size() != 1) {
				throw new SiteWhereException("Expected one JSON entry for device and found: " + result.size());
			}
			return MarshalUtils.unmarshalJson(result.value(), Device.class);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to load device by hardware id.", e);
		} finally {
			HBaseUtils.closeCleanly(devices);
		}
	}

	/**
	 * Delete a device based on hardware id. Depending on 'force' the record will be
	 * physically deleted or a marker qualifier will be added to mark it as deleted. Note:
	 * Physically deleting a device can leave orphaned references and should not be done
	 * in a production system!
	 * 
	 * @param hbase
	 * @param hardwareId
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDevice deleteDevice(ISiteWhereHBaseClient hbase, String hardwareId, boolean force)
			throws SiteWhereException {
		Long deviceId = IdManager.getInstance().getDeviceKeys().getValue(hardwareId);
		if (deviceId == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.ERROR);
		}
		Device existing = getDeviceByHardwareId(hbase, hardwareId);
		existing.setDeleted(true);
		byte[] primary = getPrimaryRowkey(deviceId);
		if (force) {
			IdManager.getInstance().getDeviceKeys().delete(hardwareId);
			HTableInterface devices = null;
			try {
				Delete delete = new Delete(primary);
				devices = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
				devices.delete(delete);
			} catch (IOException e) {
				throw new SiteWhereException("Unable to delete device.", e);
			} finally {
				HBaseUtils.closeCleanly(devices);
			}
		} else {
			byte[] marker = { (byte) 0x01 };
			SiteWherePersistence.setUpdatedEntityMetadata(existing);
			byte[] updated = MarshalUtils.marshalJson(existing);

			HTableInterface devices = null;
			try {
				devices = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
				Put put = new Put(primary);
				put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, updated);
				put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.DELETED, marker);
				devices.put(put);
			} catch (IOException e) {
				throw new SiteWhereException("Unable to set deleted flag for device.", e);
			} finally {
				HBaseUtils.closeCleanly(devices);
			}
		}
		return existing;
	}

	/**
	 * Get the current device assignment id if assigned or null if not assigned.
	 * 
	 * @param hbase
	 * @param hardwareId
	 * @return
	 * @throws SiteWhereException
	 */
	public static String getCurrentAssignmentId(ISiteWhereHBaseClient hbase, String hardwareId)
			throws SiteWhereException {
		Long deviceId = IdManager.getInstance().getDeviceKeys().getValue(hardwareId);
		if (deviceId == null) {
			return null;
		}
		byte[] primary = getPrimaryRowkey(deviceId);

		HTableInterface devices = null;
		try {
			devices = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
			Get get = new Get(primary);
			get.addColumn(ISiteWhereHBase.FAMILY_ID, CURRENT_ASSIGNMENT);
			Result result = devices.get(get);
			if (result.isEmpty()) {
				return null;
			} else if (result.size() == 1) {
				return new String(result.value());
			} else {
				throw new SiteWhereException("Expected one current assignment entry for device and found: "
						+ result.size());
			}
		} catch (IOException e) {
			throw new SiteWhereException("Unable to load current device assignment value.", e);
		} finally {
			HBaseUtils.closeCleanly(devices);
		}
	}

	/**
	 * Set the current device assignment for a device.
	 * 
	 * @param hbase
	 * @param hardwareId
	 * @param assignmentToken
	 * @throws SiteWhereException
	 */
	public static void setDeviceAssignment(ISiteWhereHBaseClient hbase, String hardwareId,
			String assignmentToken) throws SiteWhereException {
		String existing = getCurrentAssignmentId(hbase, hardwareId);
		if (existing != null) {
			throw new SiteWhereSystemException(ErrorCode.DeviceAlreadyAssigned, ErrorLevel.ERROR);
		}

		// Load object to update assignment token.
		Device updated = getDeviceByHardwareId(hbase, hardwareId);
		updated.setAssignmentToken(assignmentToken);
		byte[] json = MarshalUtils.marshalJson(updated);

		Long deviceId = IdManager.getInstance().getDeviceKeys().getValue(hardwareId);
		if (deviceId == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.ERROR);
		}
		byte[] primary = getPrimaryRowkey(deviceId);
		byte[] assnHistory = getNextDeviceAssignmentHistoryKey();

		HTableInterface devices = null;
		try {
			devices = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
			Put put = new Put(primary);
			put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, json);
			put.add(ISiteWhereHBase.FAMILY_ID, CURRENT_ASSIGNMENT, assignmentToken.getBytes());
			put.add(ISiteWhereHBase.FAMILY_ID, assnHistory, assignmentToken.getBytes());
			devices.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to set device assignment.", e);
		} finally {
			HBaseUtils.closeCleanly(devices);
		}
	}

	/**
	 * Removes the device assignment row if present.
	 * 
	 * @param hbase
	 * @param hardwareId
	 * @throws SiteWhereException
	 */
	public static void removeDeviceAssignment(ISiteWhereHBaseClient hbase, String hardwareId)
			throws SiteWhereException {
		Long deviceId = IdManager.getInstance().getDeviceKeys().getValue(hardwareId);
		if (deviceId == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.ERROR);
		}
		byte[] primary = getPrimaryRowkey(deviceId);

		Device updated = getDeviceByHardwareId(hbase, hardwareId);
		updated.setAssignmentToken(null);
		byte[] json = MarshalUtils.marshalJson(updated);

		HTableInterface devices = null;
		try {
			devices = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
			Put put = new Put(primary);
			put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, json);
			devices.put(put);
			Delete delete = new Delete(primary);
			delete.deleteColumn(ISiteWhereHBase.FAMILY_ID, CURRENT_ASSIGNMENT);
			devices.delete(delete);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to remove device assignment.", e);
		} finally {
			HBaseUtils.closeCleanly(devices);
		}
	}

	/**
	 * Get the assignment history for a device.
	 * 
	 * @param hbase
	 * @param hardwareId
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceAssignment> getDeviceAssignmentHistory(ISiteWhereHBaseClient hbase,
			String hardwareId, ISearchCriteria criteria) throws SiteWhereException {
		Long deviceId = IdManager.getInstance().getDeviceKeys().getValue(hardwareId);
		if (deviceId == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.ERROR);
		}
		byte[] primary = getPrimaryRowkey(deviceId);

		HTableInterface devices = null;
		try {
			devices = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
			Get get = new Get(primary);
			Result result = devices.get(get);

			Map<byte[], byte[]> map = result.getFamilyMap(ISiteWhereHBase.FAMILY_ID);
			Pager<String> pager = new Pager<String>(criteria);
			for (byte[] qualifier : map.keySet()) {
				if (qualifier[0] == ASSIGNMENT_HISTORY_INDICATOR) {
					byte[] value = map.get(qualifier);
					pager.process(new String(value));
				}
			}
			List<IDeviceAssignment> results = new ArrayList<IDeviceAssignment>();
			for (String token : pager.getResults()) {
				DeviceAssignment assn = HBaseDeviceAssignment.getDeviceAssignment(hbase, token);
				results.add(assn);
			}
			return new SearchResults<IDeviceAssignment>(results, pager.getTotal());
		} catch (IOException e) {
			throw new SiteWhereException("Unable to load current device assignment history.", e);
		} finally {
			HBaseUtils.closeCleanly(devices);
		}
	}

	/**
	 * Get the unique device identifier based on the long value associated with the device
	 * UUID. This will be a subset of the full 8-bit long value.
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] getDeviceIdentifier(Long value) {
		byte[] bytes = Bytes.toBytes(value);
		byte[] result = new byte[DEVICE_IDENTIFIER_LENGTH];
		System.arraycopy(bytes, bytes.length - DEVICE_IDENTIFIER_LENGTH, result, 0, DEVICE_IDENTIFIER_LENGTH);
		return result;
	}

	/**
	 * Get primary row key for a given site.
	 * 
	 * @param siteId
	 * @return
	 */
	public static byte[] getPrimaryRowkey(Long deviceId) {
		byte[] did = getDeviceIdentifier(deviceId);
		return did;
	}

	/**
	 * Creates key with an indicator byte followed by the inverted timestamp to order
	 * assignments in most recent to least recent order.
	 * 
	 * @return
	 */
	public static byte[] getNextDeviceAssignmentHistoryKey() {
		long time = System.currentTimeMillis() / 1000;
		byte[] timeBytes = Bytes.toBytes(time);
		ByteBuffer buffer = ByteBuffer.allocate(5);
		buffer.put(ASSIGNMENT_HISTORY_INDICATOR);
		buffer.put((byte) ~timeBytes[4]);
		buffer.put((byte) ~timeBytes[5]);
		buffer.put((byte) ~timeBytes[6]);
		buffer.put((byte) ~timeBytes[7]);
		return buffer.array();
	}
}