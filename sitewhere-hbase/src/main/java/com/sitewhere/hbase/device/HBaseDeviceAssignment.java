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
import java.util.Date;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.Tracer;
import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.device.marshaling.DeviceAssignmentMarshalHelper;
import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.encoder.PayloadMarshalerResolver;
import com.sitewhere.hbase.uid.IdManager;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceAssignmentState;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.common.IMetadataProvider;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceAssignmentState;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.server.debug.TracerCategory;

/**
 * HBase specifics for dealing with SiteWhere device assignments.
 * 
 * @author Derek
 */
public class HBaseDeviceAssignment {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(HBaseDeviceAssignment.class);

	/** Length of device identifier (subset of 8 byte long) */
	public static final int ASSIGNMENT_IDENTIFIER_LENGTH = 4;

	/** Qualifier for assignment status */
	public static final byte[] ASSIGNMENT_STATUS = Bytes.toBytes("status");

	/** Qualifier for assignment state */
	public static final byte[] ASSIGNMENT_STATE = Bytes.toBytes("state");

	/** Used for cloning device assignment results */
	private static DeviceAssignmentMarshalHelper ASSIGNMENT_HELPER =
			new DeviceAssignmentMarshalHelper().setIncludeAsset(false).setIncludeDevice(false).setIncludeSite(
					false);

	/**
	 * Create a new device assignment.
	 * 
	 * @param context
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceAssignment createDeviceAssignment(IHBaseContext context,
			IDeviceAssignmentCreateRequest request) throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "createDeviceAssignment (HBase)", LOGGER);
		try {
			Device device = HBaseDevice.getDeviceByHardwareId(context, request.getDeviceHardwareId());
			if (device == null) {
				throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.ERROR);
			}
			Long siteId = IdManager.getInstance().getSiteKeys().getValue(device.getSiteToken());
			if (siteId == null) {
				throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
			}
			if (device.getAssignmentToken() != null) {
				throw new SiteWhereSystemException(ErrorCode.DeviceAlreadyAssigned, ErrorLevel.ERROR);
			}
			byte[] baserow = HBaseSite.getAssignmentRowKey(siteId);
			Long assnId = HBaseSite.allocateNextAssignmentId(context, siteId);
			byte[] assnIdBytes = getAssignmentIdentifier(assnId);
			ByteBuffer buffer = ByteBuffer.allocate(baserow.length + assnIdBytes.length);
			buffer.put(baserow);
			buffer.put(assnIdBytes);
			byte[] assnKey = buffer.array();

			// Associate new UUID with assignment row key.
			String uuid = IdManager.getInstance().getAssignmentKeys().createUniqueId(assnKey);

			byte[] primary = getPrimaryRowkey(assnKey);

			// Create device assignment for JSON.
			DeviceAssignment newAssignment =
					SiteWherePersistence.deviceAssignmentCreateLogic(request, device, uuid);
			byte[] payload = context.getPayloadMarshaler().encodeDeviceAssignment(newAssignment);

			HTableInterface sites = null;
			try {
				sites = context.getClient().getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
				Put put = new Put(primary);
				HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
				put.add(ISiteWhereHBase.FAMILY_ID, ASSIGNMENT_STATUS,
						DeviceAssignmentStatus.Active.name().getBytes());
				sites.put(put);
			} catch (IOException e) {
				throw new SiteWhereException("Unable to create device assignment.", e);
			} finally {
				HBaseUtils.closeCleanly(sites);
			}

			// Set the back reference from the device that indicates it is currently
			// assigned.
			HBaseDevice.setDeviceAssignment(context, request.getDeviceHardwareId(), uuid);

			return newAssignment;
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * Get a device assignment based on its unique token.
	 * 
	 * @param context
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceAssignment getDeviceAssignment(IHBaseContext context, String token)
			throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "getDeviceAssignment (HBase) " + token, LOGGER);
		try {
			if (context.getCacheProvider() != null) {
				IDeviceAssignment result = context.getCacheProvider().getDeviceAssignmentCache().get(token);
				if (result != null) {
					Tracer.info("Returning cached device assignment.", LOGGER);
					return ASSIGNMENT_HELPER.convert(result, SiteWhere.getServer().getAssetModuleManager());
				}
			}
			byte[] assnKey = IdManager.getInstance().getAssignmentKeys().getValue(token);
			if (assnKey == null) {
				return null;
			}
			byte[] primary = getPrimaryRowkey(assnKey);

			HTableInterface sites = null;
			try {
				sites = context.getClient().getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
				Get get = new Get(primary);
				HBaseUtils.addPayloadFields(get);
				get.addColumn(ISiteWhereHBase.FAMILY_ID, ASSIGNMENT_STATE);
				Result result = sites.get(get);

				byte[] type = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
				byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
				byte[] state = result.getValue(ISiteWhereHBase.FAMILY_ID, ASSIGNMENT_STATE);
				if ((type == null) || (payload == null)) {
					return null;
				}

				DeviceAssignment found =
						PayloadMarshalerResolver.getInstance().getMarshaler(type).decodeDeviceAssignment(
								payload);
				if (state != null) {
					DeviceAssignmentState assnState =
							PayloadMarshalerResolver.getInstance().getMarshaler(type).decodeDeviceAssignmentState(
									state);
					found.setState(assnState);
				}
				if ((context.getCacheProvider() != null) && (found != null)) {
					context.getCacheProvider().getDeviceAssignmentCache().put(token, found);
				}
				return found;
			} catch (IOException e) {
				throw new SiteWhereException("Unable to load device assignment by token.", e);
			} finally {
				HBaseUtils.closeCleanly(sites);
			}
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * Update metadata associated with a device assignment.
	 * 
	 * @param context
	 * @param token
	 * @param metadata
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceAssignment updateDeviceAssignmentMetadata(IHBaseContext context, String token,
			IMetadataProvider metadata) throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall,
				"updateDeviceAssignmentMetadata (HBase) " + token, LOGGER);
		try {
			DeviceAssignment updated = getDeviceAssignment(context, token);
			updated.clearMetadata();
			MetadataProvider.copy(metadata, updated);
			SiteWherePersistence.setUpdatedEntityMetadata(updated);

			byte[] assnKey = IdManager.getInstance().getAssignmentKeys().getValue(token);
			byte[] payload = context.getPayloadMarshaler().encodeDeviceAssignment(updated);
			byte[] primary = getPrimaryRowkey(assnKey);

			HTableInterface sites = null;
			try {
				sites = context.getClient().getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
				Put put = new Put(primary);
				HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
				sites.put(put);

				// Make sure that cache is using updated assignment information.
				if (context.getCacheProvider() != null) {
					context.getCacheProvider().getDeviceAssignmentCache().put(updated.getToken(), updated);
				}
			} catch (IOException e) {
				throw new SiteWhereException("Unable to update device assignment metadata.", e);
			} finally {
				HBaseUtils.closeCleanly(sites);
			}
			return updated;
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * Update state associated with device assignment.
	 * 
	 * @param context
	 * @param token
	 * @param state
	 * @param cache
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceAssignment updateDeviceAssignmentState(IHBaseContext context, String token,
			IDeviceAssignmentState state) throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "updateDeviceAssignmentState (HBase) " + token,
				LOGGER);
		try {
			DeviceAssignment updated = getDeviceAssignment(context, token);
			updated.setState(DeviceAssignmentState.copy(state));

			byte[] assnKey = IdManager.getInstance().getAssignmentKeys().getValue(token);
			byte[] updatedState = context.getPayloadMarshaler().encodeDeviceAssignmentState(state);
			byte[] primary = getPrimaryRowkey(assnKey);

			HTableInterface sites = null;
			try {
				sites = context.getClient().getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
				Put put = new Put(primary);
				put.add(ISiteWhereHBase.FAMILY_ID, ASSIGNMENT_STATE, updatedState);
				sites.put(put);

				// Make sure that cache is using updated assignment information.
				if (context.getCacheProvider() != null) {
					context.getCacheProvider().getDeviceAssignmentCache().put(updated.getToken(), updated);
				}
			} catch (IOException e) {
				throw new SiteWhereException("Unable to update device assignment state.", e);
			} finally {
				HBaseUtils.closeCleanly(sites);
			}
			return updated;
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * Update status for a given device assignment.
	 * 
	 * @param context
	 * @param token
	 * @param status
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceAssignment updateDeviceAssignmentStatus(IHBaseContext context, String token,
			DeviceAssignmentStatus status) throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "updateDeviceAssignmentStatus (HBase) " + token,
				LOGGER);
		try {
			DeviceAssignment updated = getDeviceAssignment(context, token);
			updated.setStatus(status);
			SiteWherePersistence.setUpdatedEntityMetadata(updated);

			byte[] assnKey = IdManager.getInstance().getAssignmentKeys().getValue(token);
			byte[] payload = context.getPayloadMarshaler().encodeDeviceAssignment(updated);
			byte[] primary = getPrimaryRowkey(assnKey);

			HTableInterface sites = null;
			try {
				sites = context.getClient().getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
				Put put = new Put(primary);
				HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
				put.add(ISiteWhereHBase.FAMILY_ID, ASSIGNMENT_STATUS, status.name().getBytes());
				sites.put(put);

				// Make sure that cache is using updated assignment information.
				if (context.getCacheProvider() != null) {
					context.getCacheProvider().getDeviceAssignmentCache().put(updated.getToken(), updated);
				}
			} catch (IOException e) {
				throw new SiteWhereException("Unable to update device assignment status.", e);
			} finally {
				HBaseUtils.closeCleanly(sites);
			}
			return updated;
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * End a device assignment.
	 * 
	 * @param context
	 * @param token
	 * @param cache
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceAssignment endDeviceAssignment(IHBaseContext context, String token)
			throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "endDeviceAssignment (HBase) " + token, LOGGER);
		try {
			DeviceAssignment updated = getDeviceAssignment(context, token);
			updated.setStatus(DeviceAssignmentStatus.Released);
			updated.setReleasedDate(new Date());
			SiteWherePersistence.setUpdatedEntityMetadata(updated);

			// Remove assignment reference from device.
			HBaseDevice.removeDeviceAssignment(context, updated.getDeviceHardwareId());

			// Update json and status qualifier.
			byte[] assnKey = IdManager.getInstance().getAssignmentKeys().getValue(token);
			byte[] payload = context.getPayloadMarshaler().encodeDeviceAssignment(updated);
			byte[] primary = getPrimaryRowkey(assnKey);

			HTableInterface sites = null;
			try {
				sites = context.getClient().getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
				Put put = new Put(primary);
				HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
				put.add(ISiteWhereHBase.FAMILY_ID, ASSIGNMENT_STATUS,
						DeviceAssignmentStatus.Released.name().getBytes());
				sites.put(put);

				// Make sure that cache is using updated assignment information.
				if (context.getCacheProvider() != null) {
					context.getCacheProvider().getDeviceAssignmentCache().put(updated.getToken(), updated);
				}
			} catch (IOException e) {
				throw new SiteWhereException("Unable to update device assignment status.", e);
			} finally {
				HBaseUtils.closeCleanly(sites);
			}
			return updated;
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * Delete a device assignmant based on token. Depending on 'force' the record will be
	 * physically deleted or a marker qualifier will be added to mark it as deleted. Note:
	 * Physically deleting an assignment can leave orphaned references and should not be
	 * done in a production system!
	 * 
	 * @param context
	 * @param token
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceAssignment deleteDeviceAssignment(IHBaseContext context, String token, boolean force)
			throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "deleteDeviceAssignment (HBase) " + token, LOGGER);
		try {
			byte[] assnKey = IdManager.getInstance().getAssignmentKeys().getValue(token);
			if (assnKey == null) {
				throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
			}
			byte[] primary = getPrimaryRowkey(assnKey);

			DeviceAssignment existing = getDeviceAssignment(context, token);
			existing.setDeleted(true);
			try {
				HBaseDevice.removeDeviceAssignment(context, existing.getDeviceHardwareId());
			} catch (SiteWhereSystemException e) {
				// Ignore missing reference to handle case where device was deleted
				// underneath
				// assignment.
			}
			if (force) {
				IdManager.getInstance().getAssignmentKeys().delete(token);
				HTableInterface sites = null;
				try {
					Delete delete = new Delete(primary);
					sites = context.getClient().getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
					sites.delete(delete);
				} catch (IOException e) {
					throw new SiteWhereException("Unable to delete device.", e);
				} finally {
					HBaseUtils.closeCleanly(sites);
				}
			} else {
				byte[] marker = { (byte) 0x01 };
				SiteWherePersistence.setUpdatedEntityMetadata(existing);
				byte[] updated = context.getPayloadMarshaler().encodeDeviceAssignment(existing);
				HTableInterface sites = null;
				try {
					sites = context.getClient().getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
					Put put = new Put(primary);
					put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE,
							context.getPayloadMarshaler().getEncoding().getIndicator());
					put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD, updated);
					put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.DELETED, marker);
					sites.put(put);
				} catch (IOException e) {
					throw new SiteWhereException("Unable to set deleted flag for device assignment.", e);
				} finally {
					HBaseUtils.closeCleanly(sites);
				}
			}
			return existing;
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * Get primary row key for a given device assignment.
	 * 
	 * @param siteId
	 * @return
	 */
	public static byte[] getPrimaryRowkey(byte[] assnKey) {
		ByteBuffer rowkey = ByteBuffer.allocate(assnKey.length + 1);
		rowkey.put(assnKey);
		rowkey.put(DeviceAssignmentRecordType.DeviceAssignment.getType());
		return rowkey.array();
	}

	/**
	 * Get base row key for device streams.
	 * 
	 * @param siteId
	 * @return
	 */
	public static byte[] getStreamRowkey(byte[] assnKey) {
		ByteBuffer rowkey = ByteBuffer.allocate(assnKey.length + 1);
		rowkey.put(assnKey);
		rowkey.put(DeviceAssignmentRecordType.DeviceStream.getType());
		return rowkey.array();
	}

	/**
	 * Truncate assignment id value to expected length. This will be a subset of the full
	 * 8-bit long value.
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] getAssignmentIdentifier(Long value) {
		byte[] bytes = Bytes.toBytes(value);
		byte[] result = new byte[ASSIGNMENT_IDENTIFIER_LENGTH];
		System.arraycopy(bytes, bytes.length - ASSIGNMENT_IDENTIFIER_LENGTH, result, 0,
				ASSIGNMENT_IDENTIFIER_LENGTH);
		return result;
	}
}