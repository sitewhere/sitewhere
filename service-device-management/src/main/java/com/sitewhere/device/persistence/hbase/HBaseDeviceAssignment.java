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
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.sitewhere.device.persistence.DeviceManagementPersistence;
import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.encoder.PayloadMarshalerResolver;
import com.sitewhere.persistence.Persistence;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;
import com.sitewhere.spi.device.state.IDeviceState;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;

/**
 * HBase specifics for dealing with SiteWhere device assignments.
 * 
 * @author Derek
 */
public class HBaseDeviceAssignment {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(HBaseDeviceAssignment.class);

    /** Length of device identifier (subset of 8 byte long) */
    public static final int ASSIGNMENT_IDENTIFIER_LENGTH = 4;

    /** Qualifier for assignment status */
    public static final byte[] ASSIGNMENT_STATUS = Bytes.toBytes("status");

    /** Qualifier for assignment state */
    public static final byte[] ASSIGNMENT_STATE = Bytes.toBytes("state");

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
	Device device = HBaseDevice.getDeviceByToken(context, request.getDeviceToken());
	if (device == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceId, ErrorLevel.ERROR);
	}
	Long areaId = context.getDeviceIdManager().getSiteKeys().getValue(request.getAreaToken());
	if (areaId == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAreaToken, ErrorLevel.ERROR);
	}
	if (device.getDeviceAssignmentId() != null) {
	    throw new SiteWhereSystemException(ErrorCode.DeviceAlreadyAssigned, ErrorLevel.ERROR);
	}
	byte[] baserow = HBaseArea.getAssignmentRowKey(areaId);
	Long assnId = HBaseArea.allocateNextAssignmentId(context, areaId);
	byte[] assnIdBytes = getAssignmentIdentifier(assnId);
	ByteBuffer buffer = ByteBuffer.allocate(baserow.length + assnIdBytes.length);
	buffer.put(baserow);
	buffer.put(assnIdBytes);
	byte[] assnKey = buffer.array();

	// Associate new UUID with assignment row key.
	String uuid;
	if (request.getToken() == null) {
	    uuid = context.getDeviceIdManager().getAssignmentKeys().createUniqueId(assnKey);
	} else {
	    context.getDeviceIdManager().getAssignmentKeys().create(request.getToken(), assnKey);
	    uuid = request.getToken();
	}

	byte[] primary = getPrimaryRowkey(assnKey);

	// Create device assignment for JSON.
	DeviceAssignment newAssignment = new DeviceAssignment();
	newAssignment.setToken(uuid);
	byte[] payload = context.getPayloadMarshaler().encodeDeviceAssignment(newAssignment);

	Table sites = null;
	try {
	    sites = getSitesTableInterface(context);
	    Put put = new Put(primary);
	    HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
	    put.addColumn(ISiteWhereHBase.FAMILY_ID, ASSIGNMENT_STATUS,
		    DeviceAssignmentStatus.Active.name().getBytes());
	    sites.put(put);
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to create device assignment.", e);
	} finally {
	    HBaseUtils.closeCleanly(sites);
	}

	// Set the back reference from the device that indicates it is
	// currently
	// assigned.
	HBaseDevice.setDeviceAssignment(context, device, uuid);

	return newAssignment;
    }

    /**
     * Get a device assignment based on its unique token.
     * 
     * @param context
     * @param assn
     * @return
     * @throws SiteWhereException
     */
    public static DeviceAssignment getDeviceAssignment(IHBaseContext context, IDeviceAssignment assn)
	    throws SiteWhereException {
	byte[] assnKey = context.getDeviceIdManager().getAssignmentKeys().getValue(assn.getToken());
	if (assnKey == null) {
	    return null;
	}
	byte[] primary = getPrimaryRowkey(assnKey);

	Table sites = null;
	try {
	    sites = getSitesTableInterface(context);
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

	    DeviceAssignment found = PayloadMarshalerResolver.getInstance().getMarshaler(type)
		    .decodeDeviceAssignment(payload);
	    if (state != null) {
		// DeviceAssignmentState assnState =
		// PayloadMarshalerResolver.getInstance().getMarshaler(type)
		// .decodeDeviceAssignmentState(state);
		// found.setState(assnState);
	    }
	    return found;
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to load device assignment by token.", e);
	} finally {
	    HBaseUtils.closeCleanly(sites);
	}
    }

    /**
     * Update metadata associated with a device assignment.
     * 
     * @param context
     * @param assn
     * @param metadata
     * @return
     * @throws SiteWhereException
     */
    public static DeviceAssignment updateDeviceAssignmentMetadata(IHBaseContext context, IDeviceAssignment assn,
	    Map<String, String> metadata) throws SiteWhereException {
	DeviceAssignment updated = getDeviceAssignment(context, assn);
	updated.getMetadata().clear();
	MetadataProvider.copy(metadata, updated);
	DeviceManagementPersistence.setUpdatedEntityMetadata(updated);

	byte[] assnKey = context.getDeviceIdManager().getAssignmentKeys().getValue(assn.getToken());
	byte[] payload = context.getPayloadMarshaler().encodeDeviceAssignment(updated);
	byte[] primary = getPrimaryRowkey(assnKey);

	Table sites = null;
	try {
	    sites = getSitesTableInterface(context);
	    Put put = new Put(primary);
	    HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
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
     * @param context
     * @param assn
     * @param state
     * @return
     * @throws SiteWhereException
     */
    public static DeviceAssignment updateDeviceAssignmentState(IHBaseContext context, IDeviceAssignment assn,
	    IDeviceState state) throws SiteWhereException {
	DeviceAssignment updated = getDeviceAssignment(context, assn);
	// updated.setState(DeviceAssignmentState.copy(state));

	byte[] assnKey = context.getDeviceIdManager().getAssignmentKeys().getValue(assn.getToken());
	byte[] updatedState = context.getPayloadMarshaler().encodeDeviceAssignmentState(state);
	byte[] primary = getPrimaryRowkey(assnKey);

	Table sites = null;
	try {
	    sites = getSitesTableInterface(context);
	    Put put = new Put(primary);
	    put.addColumn(ISiteWhereHBase.FAMILY_ID, ASSIGNMENT_STATE, updatedState);
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
     * @param context
     * @param assn
     * @param status
     * @return
     * @throws SiteWhereException
     */
    public static DeviceAssignment updateDeviceAssignmentStatus(IHBaseContext context, IDeviceAssignment assn,
	    DeviceAssignmentStatus status) throws SiteWhereException {
	DeviceAssignment updated = getDeviceAssignment(context, assn);
	updated.setStatus(status);
	Persistence.setUpdatedEntityMetadata(updated);

	byte[] assnKey = context.getDeviceIdManager().getAssignmentKeys().getValue(assn.getToken());
	byte[] payload = context.getPayloadMarshaler().encodeDeviceAssignment(updated);
	byte[] primary = getPrimaryRowkey(assnKey);

	Table sites = null;
	try {
	    sites = getSitesTableInterface(context);
	    Put put = new Put(primary);
	    HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
	    put.addColumn(ISiteWhereHBase.FAMILY_ID, ASSIGNMENT_STATUS, status.name().getBytes());
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
     * @param context
     * @param assn
     * @return
     * @throws SiteWhereException
     */
    public static DeviceAssignment endDeviceAssignment(IHBaseContext context, IDeviceAssignment assn)
	    throws SiteWhereException {
	DeviceAssignment updated = getDeviceAssignment(context, assn);
	updated.setStatus(DeviceAssignmentStatus.Released);
	updated.setReleasedDate(new Date());
	DeviceManagementPersistence.setUpdatedEntityMetadata(updated);

	// Remove assignment reference from device.
	// HBaseDevice.removeDeviceAssignment(context, updated.getDeviceId());

	// Update json and status qualifier.
	byte[] assnKey = context.getDeviceIdManager().getAssignmentKeys().getValue(assn.getToken());
	byte[] payload = context.getPayloadMarshaler().encodeDeviceAssignment(updated);
	byte[] primary = getPrimaryRowkey(assnKey);

	Table sites = null;
	try {
	    sites = getSitesTableInterface(context);
	    Put put = new Put(primary);
	    HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
	    put.addColumn(ISiteWhereHBase.FAMILY_ID, ASSIGNMENT_STATUS,
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
     * Delete a device assignmant based on token. Depending on 'force' the record
     * will be physically deleted or a marker qualifier will be added to mark it as
     * deleted. Note: Physically deleting an assignment can leave orphaned
     * references and should not be done in a production system!
     * 
     * @param context
     * @param assn
     * @param force
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceAssignment deleteDeviceAssignment(IHBaseContext context, IDeviceAssignment assn, boolean force)
	    throws SiteWhereException {
	byte[] assnKey = context.getDeviceIdManager().getAssignmentKeys().getValue(assn.getToken());
	if (assnKey == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
	}
	byte[] primary = getPrimaryRowkey(assnKey);

	DeviceAssignment existing = getDeviceAssignment(context, assn);
	existing.setDeleted(true);
	// try {
	// HBaseDevice.removeDeviceAssignment(context, existing.getDeviceHardwareId());
	// } catch (SiteWhereSystemException e) {
	// }
	if (force) {
	    context.getDeviceIdManager().getAssignmentKeys().delete(assn.getToken());
	    Table sites = null;
	    try {
		Delete delete = new Delete(primary);
		sites = getSitesTableInterface(context);
		sites.delete(delete);
	    } catch (IOException e) {
		throw new SiteWhereException("Unable to delete device.", e);
	    } finally {
		HBaseUtils.closeCleanly(sites);
	    }
	} else {
	    byte[] marker = { (byte) 0x01 };
	    DeviceManagementPersistence.setUpdatedEntityMetadata(existing);
	    byte[] updated = context.getPayloadMarshaler().encodeDeviceAssignment(existing);
	    Table sites = null;
	    try {
		sites = getSitesTableInterface(context);
		Put put = new Put(primary);
		put.addColumn(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE,
			context.getPayloadMarshaler().getEncoding().getIndicator());
		put.addColumn(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD, updated);
		put.addColumn(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.DELETED, marker);
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
     * Get primary row key for a given device assignment.
     * 
     * @param assnKey
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
     * @param assnKey
     * @return
     */
    public static byte[] getStreamRowkey(byte[] assnKey) {
	ByteBuffer rowkey = ByteBuffer.allocate(assnKey.length + 1);
	rowkey.put(assnKey);
	rowkey.put(DeviceAssignmentRecordType.DeviceStream.getType());
	return rowkey.array();
    }

    /**
     * Get base row key for end marker.
     * 
     * @param assnKey
     * @return
     */
    public static byte[] getEndMarkerKey(byte[] assnKey) {
	ByteBuffer rowkey = ByteBuffer.allocate(assnKey.length + 1);
	rowkey.put(assnKey);
	rowkey.put(DeviceAssignmentRecordType.EndMarker.getType());
	return rowkey.array();
    }

    /**
     * Truncate assignment id value to expected length. This will be a subset of the
     * full 8-bit long value.
     * 
     * @param value
     * @return
     */
    public static byte[] getAssignmentIdentifier(Long value) {
	byte[] bytes = Bytes.toBytes(value);
	byte[] result = new byte[ASSIGNMENT_IDENTIFIER_LENGTH];
	System.arraycopy(bytes, bytes.length - ASSIGNMENT_IDENTIFIER_LENGTH, result, 0, ASSIGNMENT_IDENTIFIER_LENGTH);
	return result;
    }

    /**
     * Get assets table based on context.
     * 
     * @param context
     * @return
     * @throws SiteWhereException
     */
    protected static Table getSitesTableInterface(IHBaseContext context) throws SiteWhereException {
	return context.getClient().getTableInterface(context.getTenant(), ISiteWhereHBase.SITES_TABLE_NAME);
    }
}