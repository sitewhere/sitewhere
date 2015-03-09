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
import org.apache.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.Tracer;
import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.device.marshaling.DeviceMarshalHelper;
import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.common.Pager;
import com.sitewhere.hbase.encoder.PayloadMarshalerResolver;
import com.sitewhere.hbase.uid.IdManager;
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
import com.sitewhere.spi.search.device.DeviceSearchType;
import com.sitewhere.spi.search.device.IDeviceBySpecificationParameters;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;
import com.sitewhere.spi.server.debug.TracerCategory;

/**
 * HBase specifics for dealing with SiteWhere devices.
 * 
 * @author Derek
 */
public class HBaseDevice {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(HBaseDevice.class);

	/** Length of device identifier (subset of 8 byte long) */
	public static final int DEVICE_IDENTIFIER_LENGTH = 4;

	/** Byte that indicates an assignment history entry qualifier */
	public static final byte ASSIGNMENT_HISTORY_INDICATOR = (byte) 0x01;

	/** Column qualifier for current site */
	public static final byte[] CURRENT_SITE = "site".getBytes();

	/** Column qualifier for current device assignment */
	public static final byte[] CURRENT_ASSIGNMENT = "assn".getBytes();

	/** Used for cloning device results */
	private static DeviceMarshalHelper DEVICE_HELPER =
			new DeviceMarshalHelper().setIncludeAsset(false).setIncludeAssignment(false).setIncludeSpecification(
					false);

	/**
	 * Create a new device.
	 * 
	 * @param context
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDevice createDevice(IHBaseContext context, IDeviceCreateRequest request)
			throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "createDevice (HBase)", LOGGER);
		try {
			Long existing = IdManager.getInstance().getDeviceKeys().getValue(request.getHardwareId());
			if (existing != null) {
				throw new SiteWhereSystemException(ErrorCode.DuplicateHardwareId, ErrorLevel.ERROR,
						HttpServletResponse.SC_CONFLICT);
			}
			Long value = IdManager.getInstance().getDeviceKeys().getNextCounterValue();
			Long inverse = Long.MAX_VALUE - value;
			IdManager.getInstance().getDeviceKeys().create(request.getHardwareId(), inverse);

			Device device = SiteWherePersistence.deviceCreateLogic(request);
			return putDevicePayload(context, device);
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * Update an existing device.
	 * 
	 * @param context
	 * @param hardwareId
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDevice updateDevice(IHBaseContext context, String hardwareId, IDeviceCreateRequest request)
			throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "updateDevice (HBase) " + hardwareId, LOGGER);
		try {
			Device updated = getDeviceByHardwareId(context, hardwareId);
			if (updated == null) {
				throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.ERROR);
			}
			SiteWherePersistence.deviceUpdateLogic(request, updated);
			return putDevicePayload(context, updated);
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * List devices that meet the given criteria.
	 * 
	 * @param context
	 * @param includeDeleted
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDevice> listDevices(IHBaseContext context, boolean includeDeleted,
			IDeviceSearchCriteria criteria) throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "listDevices (HBase)", LOGGER);
		try {
			Pager<IDevice> matches = getFilteredDevices(context, includeDeleted, criteria);
			return new SearchResults<IDevice>(matches.getResults(), matches.getTotal());
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * Get a list of devices filtered with certain criteria.
	 * 
	 * @param context
	 * @param includeDeleted
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	protected static Pager<IDevice> getFilteredDevices(IHBaseContext context, boolean includeDeleted,
			IDeviceSearchCriteria criteria) throws SiteWhereException {
		HTableInterface devices = null;
		ResultScanner scanner = null;

		String specificationToken = null;
		if (criteria.getSearchType() == DeviceSearchType.UsesSpecification) {
			IDeviceBySpecificationParameters params = criteria.getDeviceBySpecificationParameters();
			if (params == null) {
				throw new SiteWhereException(
						"Querying devices by specification token, but parameters were not passed.");
			}
			specificationToken = params.getSpecificationToken();
			if (specificationToken == null) {
				throw new SiteWhereException("No specification token passed for device query.");
			}
		}

		try {
			devices = context.getClient().getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
			Scan scan = new Scan();
			scan.setStartRow(new byte[] { DeviceRecordType.Device.getType() });
			scan.setStopRow(new byte[] { DeviceRecordType.DeviceSpecification.getType() });
			scanner = devices.getScanner(scan);

			Pager<IDevice> pager = new Pager<IDevice>(criteria);
			for (Result result : scanner) {
				boolean shouldAdd = true;
				byte[] payload = null;
				for (KeyValue column : result.raw()) {
					byte[] qualifier = column.getQualifier();
					if ((Bytes.equals(CURRENT_ASSIGNMENT, qualifier)) && (criteria.isExcludeAssigned())) {
						shouldAdd = false;
					}
					if ((Bytes.equals(ISiteWhereHBase.DELETED, qualifier)) && (!includeDeleted)) {
						shouldAdd = false;
					}
					if (Bytes.equals(ISiteWhereHBase.PAYLOAD, qualifier)) {
						payload = column.getValue();
					}
				}
				if ((shouldAdd) && (payload != null)) {
					Device device = context.getPayloadMarshaler().decodeDevice(payload);
					switch (criteria.getSearchType()) {
					case All: {
						break;
					}
					case UsesSpecification: {
						if (!specificationToken.equals(device.getSpecificationToken())) {
							continue;
						}
					}
					}
					pager.process(device);
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
	 * Save the payload for a device.
	 * 
	 * @param context
	 * @param device
	 * @return
	 * @throws SiteWhereException
	 */
	public static Device putDevicePayload(IHBaseContext context, Device device) throws SiteWhereException {
		Long value = IdManager.getInstance().getDeviceKeys().getValue(device.getHardwareId());
		if (value == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.ERROR);
		}
		byte[] primary = getDeviceRowKey(value);
		byte[] payload = context.getPayloadMarshaler().encodeDevice(device);

		HTableInterface devices = null;
		try {
			devices = context.getClient().getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
			Put put = new Put(primary);
			HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
			put.add(ISiteWhereHBase.FAMILY_ID, CURRENT_SITE, Bytes.toBytes(device.getSiteToken()));
			devices.put(put);
			if (context.getCacheProvider() != null) {
				context.getCacheProvider().getDeviceCache().put(device.getHardwareId(), device);
			}
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
	 * @param context
	 * @param hardwareId
	 * @return
	 * @throws SiteWhereException
	 */
	public static Device getDeviceByHardwareId(IHBaseContext context, String hardwareId)
			throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "getDeviceByHardwareId (HBase) " + hardwareId,
				LOGGER);
		try {
			if (context.getCacheProvider() != null) {
				IDevice result = context.getCacheProvider().getDeviceCache().get(hardwareId);
				if (result != null) {
					Tracer.info("Returning cached device.", LOGGER);
					return DEVICE_HELPER.convert(result, SiteWhere.getServer().getAssetModuleManager());
				}
			}
			Long deviceId = IdManager.getInstance().getDeviceKeys().getValue(hardwareId);
			if (deviceId == null) {
				Tracer.info("Device not found for hardware id.", LOGGER);
				return null;
			}

			// Find row key based on value associated with hardware id.
			byte[] primary = getDeviceRowKey(deviceId);

			HTableInterface devices = null;
			try {
				devices = context.getClient().getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
				Get get = new Get(primary);
				HBaseUtils.addPayloadFields(get);
				Result result = devices.get(get);

				byte[] type = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
				byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
				if ((type == null) || (payload == null)) {
					return null;
				}

				Device found =
						PayloadMarshalerResolver.getInstance().getMarshaler(type).decodeDevice(payload);
				if ((context.getCacheProvider() != null) && (found != null)) {
					context.getCacheProvider().getDeviceCache().put(hardwareId, found);
				}
				return found;
			} catch (IOException e) {
				throw new SiteWhereException("Unable to load device by hardware id.", e);
			} finally {
				HBaseUtils.closeCleanly(devices);
			}
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * Delete a device based on hardware id. Depending on 'force' the record will be
	 * physically deleted or a marker qualifier will be added to mark it as deleted. Note:
	 * Physically deleting a device can leave orphaned references and should not be done
	 * in a production system!
	 * 
	 * @param context
	 * @param hardwareId
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDevice deleteDevice(IHBaseContext context, String hardwareId, boolean force)
			throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "deleteDevice (HBase) " + hardwareId, LOGGER);
		try {
			Long deviceId = IdManager.getInstance().getDeviceKeys().getValue(hardwareId);
			if (deviceId == null) {
				Tracer.warn("Unable to find device to delete by hardware id.", null, LOGGER);
				throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.ERROR);
			}

			Device existing = getDeviceByHardwareId(context, hardwareId);
			existing.setDeleted(true);
			byte[] primary = getDeviceRowKey(deviceId);
			if (force) {
				IdManager.getInstance().getDeviceKeys().delete(hardwareId);
				HTableInterface devices = null;
				try {
					Delete delete = new Delete(primary);
					devices = context.getClient().getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
					devices.delete(delete);
					if (context.getCacheProvider() != null) {
						context.getCacheProvider().getDeviceCache().remove(hardwareId);
					}
				} catch (IOException e) {
					throw new SiteWhereException("Unable to delete device.", e);
				} finally {
					HBaseUtils.closeCleanly(devices);
				}
			} else {
				byte[] marker = { (byte) 0x01 };
				SiteWherePersistence.setUpdatedEntityMetadata(existing);
				byte[] updated = context.getPayloadMarshaler().encodeDevice(existing);

				HTableInterface devices = null;
				try {
					devices = context.getClient().getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
					Put put = new Put(primary);
					put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE,
							context.getPayloadMarshaler().getEncoding().getIndicator());
					put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD, updated);
					put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.DELETED, marker);
					devices.put(put);
					if (context.getCacheProvider() != null) {
						context.getCacheProvider().getDeviceCache().remove(hardwareId);
					}
				} catch (IOException e) {
					throw new SiteWhereException("Unable to set deleted flag for device.", e);
				} finally {
					HBaseUtils.closeCleanly(devices);
				}
			}
			return existing;
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * Get the current device assignment id if assigned or null if not assigned.
	 * 
	 * @param context
	 * @param hardwareId
	 * @return
	 * @throws SiteWhereException
	 */
	public static String getCurrentAssignmentId(IHBaseContext context, String hardwareId)
			throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "getCurrentAssignmentId (HBase) " + hardwareId,
				LOGGER);
		try {
			if (context.getCacheProvider() != null) {
				IDevice result = context.getCacheProvider().getDeviceCache().get(hardwareId);
				if (result != null) {
					Tracer.info("Returning cached device assignment token.", LOGGER);
					return result.getAssignmentToken();
				}
			}
			Long deviceId = IdManager.getInstance().getDeviceKeys().getValue(hardwareId);
			if (deviceId == null) {
				return null;
			}
			byte[] primary = getDeviceRowKey(deviceId);

			HTableInterface devices = null;
			try {
				devices = context.getClient().getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
				Get get = new Get(primary);
				get.addColumn(ISiteWhereHBase.FAMILY_ID, CURRENT_ASSIGNMENT);
				Result result = devices.get(get);
				if (result.isEmpty()) {
					return null;
				} else if (result.size() == 1) {
					return new String(result.value());
				} else {
					throw new SiteWhereException(
							"Expected one current assignment entry for device and found: " + result.size());
				}
			} catch (IOException e) {
				throw new SiteWhereException("Unable to load current device assignment value.", e);
			} finally {
				HBaseUtils.closeCleanly(devices);
			}
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * Set the current device assignment for a device.
	 * 
	 * @param context
	 * @param hardwareId
	 * @param assignmentToken
	 * @throws SiteWhereException
	 */
	public static void setDeviceAssignment(IHBaseContext context, String hardwareId, String assignmentToken)
			throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "setDeviceAssignment (HBase) " + hardwareId,
				LOGGER);
		try {
			String existing = getCurrentAssignmentId(context, hardwareId);
			if (existing != null) {
				throw new SiteWhereSystemException(ErrorCode.DeviceAlreadyAssigned, ErrorLevel.ERROR);
			}

			// Load object to update assignment token.
			Device updated = getDeviceByHardwareId(context, hardwareId);
			updated.setAssignmentToken(assignmentToken);
			byte[] payload = context.getPayloadMarshaler().encodeDevice(updated);

			Long deviceId = IdManager.getInstance().getDeviceKeys().getValue(hardwareId);
			if (deviceId == null) {
				throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.ERROR);
			}
			byte[] primary = getDeviceRowKey(deviceId);
			byte[] assnHistory = getNextDeviceAssignmentHistoryKey();

			HTableInterface devices = null;
			try {
				devices = context.getClient().getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
				Put put = new Put(primary);
				HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
				put.add(ISiteWhereHBase.FAMILY_ID, CURRENT_ASSIGNMENT, assignmentToken.getBytes());
				put.add(ISiteWhereHBase.FAMILY_ID, assnHistory, assignmentToken.getBytes());
				devices.put(put);

				// Make sure that cache is using updated device information.
				if (context.getCacheProvider() != null) {
					context.getCacheProvider().getDeviceCache().put(updated.getHardwareId(), updated);
				}
			} catch (IOException e) {
				throw new SiteWhereException("Unable to set device assignment.", e);
			} finally {
				HBaseUtils.closeCleanly(devices);
			}
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * Removes the device assignment row if present.
	 * 
	 * @param context
	 * @param hardwareId
	 * @param cache
	 * @throws SiteWhereException
	 */
	public static void removeDeviceAssignment(IHBaseContext context, String hardwareId)
			throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "removeDeviceAssignment (HBase) " + hardwareId,
				LOGGER);
		try {
			Long deviceId = IdManager.getInstance().getDeviceKeys().getValue(hardwareId);
			if (deviceId == null) {
				throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.ERROR);
			}
			byte[] primary = getDeviceRowKey(deviceId);

			Device updated = getDeviceByHardwareId(context, hardwareId);
			updated.setAssignmentToken(null);
			byte[] payload = context.getPayloadMarshaler().encodeDevice(updated);

			HTableInterface devices = null;
			try {
				devices = context.getClient().getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
				Put put = new Put(primary);
				put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE,
						context.getPayloadMarshaler().getEncoding().getIndicator());
				put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD, payload);
				devices.put(put);
				Delete delete = new Delete(primary);
				delete.deleteColumn(ISiteWhereHBase.FAMILY_ID, CURRENT_ASSIGNMENT);
				devices.delete(delete);

				// Make sure that cache is using updated device information.
				if (context.getCacheProvider() != null) {
					context.getCacheProvider().getDeviceCache().put(updated.getHardwareId(), updated);
				}
			} catch (IOException e) {
				throw new SiteWhereException("Unable to remove device assignment.", e);
			} finally {
				HBaseUtils.closeCleanly(devices);
			}
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * Get the assignment history for a device.
	 * 
	 * @param context
	 * @param hardwareId
	 * @param criteria
	 * @param cache
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceAssignment> getDeviceAssignmentHistory(IHBaseContext context,
			String hardwareId, ISearchCriteria criteria) throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "getDeviceAssignmentHistory (HBase) "
				+ hardwareId, LOGGER);
		try {
			Long deviceId = IdManager.getInstance().getDeviceKeys().getValue(hardwareId);
			if (deviceId == null) {
				throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.ERROR);
			}
			byte[] primary = getDeviceRowKey(deviceId);

			HTableInterface devices = null;
			try {
				devices = context.getClient().getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
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
					DeviceAssignment assn = HBaseDeviceAssignment.getDeviceAssignment(context, token);
					results.add(assn);
				}
				return new SearchResults<IDeviceAssignment>(results, pager.getTotal());
			} catch (IOException e) {
				throw new SiteWhereException("Unable to load current device assignment history.", e);
			} finally {
				HBaseUtils.closeCleanly(devices);
			}
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * Get the unique device identifier based on the long value associated with the device
	 * UUID. This will be a subset of the full 8-bit long value.
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] getTruncatedIdentifier(Long value) {
		byte[] bytes = Bytes.toBytes(value);
		byte[] result = new byte[DEVICE_IDENTIFIER_LENGTH];
		System.arraycopy(bytes, bytes.length - DEVICE_IDENTIFIER_LENGTH, result, 0, DEVICE_IDENTIFIER_LENGTH);
		return result;
	}

	/**
	 * Get row key for a device with the given id.
	 * 
	 * @param deviceId
	 * @return
	 */
	public static byte[] getDeviceRowKey(Long deviceId) {
		ByteBuffer buffer = ByteBuffer.allocate(DEVICE_IDENTIFIER_LENGTH + 1);
		buffer.put(DeviceRecordType.Device.getType());
		buffer.put(getTruncatedIdentifier(deviceId));
		return buffer.array();
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