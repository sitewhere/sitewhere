/*
 * HBaseDeviceAssignment.java 
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
import java.util.Date;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.common.MarshalUtils;
import com.sitewhere.hbase.uid.IdManager;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceAssignmentState;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.common.IMetadataProvider;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceEventBatch;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;

/**
 * HBase specifics for dealing with SiteWhere device assignments.
 * 
 * @author Derek
 */
public class HBaseDeviceAssignment {

	/** Length of device identifier (subset of 8 byte long) */
	public static final int ASSIGNMENT_IDENTIFIER_LENGTH = 4;

	/** Qualifier for assignment status */
	public static final byte[] ASSIGNMENT_STATUS = Bytes.toBytes("status");

	/** Qualifier for assignment state */
	public static final byte[] ASSIGNMENT_STATE = Bytes.toBytes("state");

	/**
	 * Create a new device assignment.
	 * 
	 * @param hbase
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceAssignment createDeviceAssignment(ISiteWhereHBaseClient hbase,
			IDeviceAssignmentCreateRequest request) throws SiteWhereException {
		Long siteId = IdManager.getInstance().getSiteKeys().getValue(request.getSiteToken());
		if (siteId == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
		}
		String existing = HBaseDevice.getCurrentAssignmentId(hbase, request.getDeviceHardwareId());
		if (existing != null) {
			throw new SiteWhereSystemException(ErrorCode.DeviceAlreadyAssigned, ErrorLevel.ERROR);
		}
		byte[] baserow = HBaseSite.getAssignmentRowKey(siteId);
		Long assnId = HBaseSite.allocateNextAssignmentId(hbase, siteId);
		byte[] assnIdBytes = getAssignmentIdentifier(assnId);
		ByteBuffer buffer = ByteBuffer.allocate(baserow.length + assnIdBytes.length);
		buffer.put(baserow);
		buffer.put(assnIdBytes);
		byte[] rowkey = buffer.array();

		// Associate new UUID with assignment row key.
		String uuid = IdManager.getInstance().getAssignmentKeys().createUniqueId(rowkey);

		// Create device assignment for JSON.
		DeviceAssignment newAssignment =
				SiteWherePersistence.deviceAssignmentCreateLogic(request, request.getSiteToken(), uuid);
		byte[] json = MarshalUtils.marshalJson(newAssignment);

		HTableInterface sites = null;
		try {
			sites = hbase.getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
			Put put = new Put(rowkey);
			put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, json);
			put.add(ISiteWhereHBase.FAMILY_ID, ASSIGNMENT_STATUS,
					DeviceAssignmentStatus.Active.name().getBytes());
			sites.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to create device assignment.", e);
		} finally {
			HBaseUtils.closeCleanly(sites);
		}

		// Set the back reference from the device that indicates it is currently assigned.
		HBaseDevice.setDeviceAssignment(hbase, request.getDeviceHardwareId(), uuid);

		return newAssignment;
	}

	/**
	 * Get a device assignment based on its unique token.
	 * 
	 * @param hbase
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceAssignment getDeviceAssignment(ISiteWhereHBaseClient hbase, String token)
			throws SiteWhereException {
		byte[] rowkey = IdManager.getInstance().getAssignmentKeys().getValue(token);
		if (rowkey == null) {
			return null;
		}

		HTableInterface sites = null;
		try {
			sites = hbase.getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
			Get get = new Get(rowkey);
			get.addColumn(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT);
			Result result = sites.get(get);
			if (result.size() != 1) {
				throw new SiteWhereException("Expected one JSON entry for device assignment and found: "
						+ result.size());
			}
			return MarshalUtils.unmarshalJson(result.value(), DeviceAssignment.class);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to load device assignment by token.", e);
		} finally {
			HBaseUtils.closeCleanly(sites);
		}
	}

	/**
	 * Update metadata associated with a device assignment.
	 * 
	 * @param hbase
	 * @param token
	 * @param metadata
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceAssignment updateDeviceAssignmentMetadata(ISiteWhereHBaseClient hbase, String token,
			IMetadataProvider metadata) throws SiteWhereException {
		DeviceAssignment updated = getDeviceAssignment(hbase, token);
		updated.clearMetadata();
		MetadataProvider.copy(metadata, updated);
		SiteWherePersistence.setUpdatedEntityMetadata(updated);

		byte[] rowkey = IdManager.getInstance().getAssignmentKeys().getValue(token);
		byte[] json = MarshalUtils.marshalJson(updated);

		HTableInterface sites = null;
		try {
			sites = hbase.getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
			Put put = new Put(rowkey);
			put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, json);
			sites.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to update device assignment metadata.", e);
		} finally {
			HBaseUtils.closeCleanly(sites);
		}
		return updated;
	}

	/**
	 * Update state associated with device assignment.
	 * 
	 * @param hbase
	 * @param token
	 * @param batch
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceAssignment updateDeviceAssignmentState(ISiteWhereHBaseClient hbase, String token,
			IDeviceEventBatch batch) throws SiteWhereException {
		DeviceAssignment updated = getDeviceAssignment(hbase, token);
		DeviceAssignmentState state = SiteWherePersistence.assignmentStateUpdateLogic(updated, batch);
		updated.setState(state);

		byte[] rowkey = IdManager.getInstance().getAssignmentKeys().getValue(token);
		byte[] json = MarshalUtils.marshalJson(updated);
		byte[] stateJson = MarshalUtils.marshalJson(state);

		HTableInterface sites = null;
		try {
			sites = hbase.getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
			Put put = new Put(rowkey);
			put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, json);
			put.add(ISiteWhereHBase.FAMILY_ID, ASSIGNMENT_STATE, stateJson);
			sites.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to update device assignment state.", e);
		} finally {
			HBaseUtils.closeCleanly(sites);
		}
		return updated;
	}

	/**
	 * Update status for a given device assignment.
	 * 
	 * @param hbase
	 * @param token
	 * @param status
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceAssignment updateDeviceAssignmentStatus(ISiteWhereHBaseClient hbase, String token,
			DeviceAssignmentStatus status) throws SiteWhereException {
		DeviceAssignment updated = getDeviceAssignment(hbase, token);
		updated.setStatus(status);
		SiteWherePersistence.setUpdatedEntityMetadata(updated);

		byte[] rowkey = IdManager.getInstance().getAssignmentKeys().getValue(token);
		byte[] json = MarshalUtils.marshalJson(updated);

		HTableInterface sites = null;
		try {
			sites = hbase.getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
			Put put = new Put(rowkey);
			put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, json);
			put.add(ISiteWhereHBase.FAMILY_ID, ASSIGNMENT_STATUS, status.name().getBytes());
			sites.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to update device assignment status.", e);
		} finally {
			HBaseUtils.closeCleanly(sites);
		}
		return updated;
	}

	/**
	 * End a device assignment.
	 * 
	 * @param hbase
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceAssignment endDeviceAssignment(ISiteWhereHBaseClient hbase, String token)
			throws SiteWhereException {
		DeviceAssignment updated = getDeviceAssignment(hbase, token);
		updated.setStatus(DeviceAssignmentStatus.Released);
		updated.setReleasedDate(new Date());
		SiteWherePersistence.setUpdatedEntityMetadata(updated);

		// Remove assignment reference from device.
		HBaseDevice.removeDeviceAssignment(hbase, updated.getDeviceHardwareId());

		// Update json and status qualifier.
		byte[] rowkey = IdManager.getInstance().getAssignmentKeys().getValue(token);
		byte[] json = MarshalUtils.marshalJson(updated);

		HTableInterface sites = null;
		try {
			sites = hbase.getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
			Put put = new Put(rowkey);
			put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, json);
			put.add(ISiteWhereHBase.FAMILY_ID, ASSIGNMENT_STATUS,
					DeviceAssignmentStatus.Released.name().getBytes());
			sites.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to update device assignment status.", e);
		} finally {
			HBaseUtils.closeCleanly(sites);
		}
		return updated;
	}

	/**
	 * Delete a device assignmant based on token. Depending on 'force' the record will be
	 * physically deleted or a marker qualifier will be added to mark it as deleted. Note:
	 * Physically deleting an assignment can leave orphaned references and should not be
	 * done in a production system!
	 * 
	 * @param hbase
	 * @param token
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceAssignment deleteDeviceAssignment(ISiteWhereHBaseClient hbase, String token,
			boolean force) throws SiteWhereException {
		byte[] assnId = IdManager.getInstance().getAssignmentKeys().getValue(token);
		if (assnId == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
		}
		DeviceAssignment existing = getDeviceAssignment(hbase, token);
		existing.setDeleted(true);
		HBaseDevice.removeDeviceAssignment(hbase, existing.getDeviceHardwareId());
		if (force) {
			IdManager.getInstance().getAssignmentKeys().delete(token);
			HTableInterface sites = null;
			try {
				Delete delete = new Delete(assnId);
				sites = hbase.getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
				sites.delete(delete);
			} catch (IOException e) {
				throw new SiteWhereException("Unable to delete device.", e);
			} finally {
				HBaseUtils.closeCleanly(sites);
			}
		} else {
			byte[] marker = { (byte) 0x01 };
			SiteWherePersistence.setUpdatedEntityMetadata(existing);
			byte[] updated = MarshalUtils.marshalJson(existing);
			HTableInterface sites = null;
			try {
				sites = hbase.getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
				Put put = new Put(assnId);
				put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, updated);
				put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.DELETED, marker);
				sites.put(put);
			} catch (IOException e) {
				throw new SiteWhereException("Unable to set deleted flag for device assignment.", e);
			} finally {
				HBaseUtils.closeCleanly(sites);
			}
		}
		return existing;
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