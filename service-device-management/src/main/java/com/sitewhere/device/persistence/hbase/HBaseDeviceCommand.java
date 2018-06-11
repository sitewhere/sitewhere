/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;

import com.sitewhere.device.persistence.DeviceManagementPersistence;
import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.encoder.PayloadMarshalerResolver;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.request.IDeviceCommandCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;

/**
 * HBase specifics for dealing with SiteWhere device commands.
 * 
 * @author Derek
 */
public class HBaseDeviceCommand {

    /**
     * Create a new device command for an existing device type.
     * 
     * @param context
     * @param deviceType
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceCommand createDeviceCommand(IHBaseContext context, IDeviceType deviceType,
	    IDeviceCommandCreateRequest request) throws SiteWhereException {
	Long deviceTypeId = context.getDeviceIdManager().getSpecificationKeys().getValue(deviceType.getToken());
	if (deviceTypeId == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	}
	String uuid = ((request.getToken() != null) ? request.getToken() : UUID.randomUUID().toString());

	// Use common logic so all backend implementations work the same.
	List<IDeviceCommand> existing = listDeviceCommands(context, deviceType, false);
	DeviceCommand command = DeviceManagementPersistence.deviceCommandCreateLogic(deviceType, request, existing);

	// Create unique row for new device.
	Long nextId = HBaseDeviceType.allocateNextCommandId(context, deviceTypeId);
	byte[] rowkey = HBaseDeviceType.getDeviceCommandRowKey(deviceTypeId, nextId);
	context.getDeviceIdManager().getCommandKeys().create(uuid, rowkey);

	return putDeviceCommandPayload(context, command);
    }

    /**
     * List device commands that match the given criteria.
     * 
     * @param context
     * @param deviceType
     * @param includeDeleted
     * @return
     * @throws SiteWhereException
     */
    public static List<IDeviceCommand> listDeviceCommands(IHBaseContext context, IDeviceType deviceType,
	    boolean includeDeleted) throws SiteWhereException {
	List<IDeviceCommand> matches = getFilteredDeviceCommands(context, deviceType, includeDeleted);
	Collections.sort(matches, new Comparator<IDeviceCommand>() {

	    @Override
	    public int compare(IDeviceCommand a, IDeviceCommand b) {
		return a.getCreatedDate().compareTo(b.getCreatedDate());
	    }
	});
	return matches;
    }

    /**
     * Get device commands that correspond to the given criteria.
     * 
     * @param context
     * @param deviceType
     * @param includeDeleted
     * @return
     * @throws SiteWhereException
     */
    protected static List<IDeviceCommand> getFilteredDeviceCommands(IHBaseContext context, IDeviceType deviceType,
	    boolean includeDeleted) throws SiteWhereException {
	Long specId = context.getDeviceIdManager().getSpecificationKeys().getValue(deviceType.getToken());
	if (specId == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	}

	Table devices = null;
	ResultScanner scanner = null;

	try {
	    devices = getDeviceTableInterface(context);
	    Scan scan = new Scan();
	    scan.setStartRow(HBaseDeviceType.getDeviceCommandRowPrefix(specId));
	    scan.setStopRow(HBaseDeviceType.getEndRowPrefix(specId));
	    scanner = devices.getScanner(scan);

	    List<IDeviceCommand> results = new ArrayList<IDeviceCommand>();
	    for (Result result : scanner) {
		boolean shouldAdd = true;
		byte[] type = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
		byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
		byte[] deleted = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.DELETED);

		if ((deleted != null) && (!includeDeleted)) {
		    shouldAdd = false;
		}

		if ((shouldAdd) && (type != null) && (payload != null)) {
		    results.add(PayloadMarshalerResolver.getInstance().getMarshaler(type).decodeDeviceCommand(payload));
		}
	    }
	    return results;
	} catch (IOException e) {
	    throw new SiteWhereException("Error scanning device command rows.", e);
	} finally {
	    if (scanner != null) {
		scanner.close();
	    }
	    HBaseUtils.closeCleanly(devices);
	}
    }

    /**
     * Get a device command by unique token.
     * 
     * @param context
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public static DeviceCommand getDeviceCommandByToken(IHBaseContext context, String token) throws SiteWhereException {
	byte[] rowkey = context.getDeviceIdManager().getCommandKeys().getValue(token);
	if (rowkey == null) {
	    return null;
	}

	Table devices = null;
	try {
	    devices = getDeviceTableInterface(context);
	    Get get = new Get(rowkey);
	    get.addColumn(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
	    get.addColumn(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
	    Result result = devices.get(get);

	    byte[] type = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
	    byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
	    if ((type == null) || (payload == null)) {
		return null;
	    }

	    return PayloadMarshalerResolver.getInstance().getMarshaler(type).decodeDeviceCommand(payload);
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to load device command by token.", e);
	} finally {
	    HBaseUtils.closeCleanly(devices);
	}
    }

    /**
     * Update an existing device command.
     * 
     * @param context
     * @param deviceType
     * @param command
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static DeviceCommand updateDeviceCommand(IHBaseContext context, IDeviceType deviceType,
	    IDeviceCommand command, IDeviceCommandCreateRequest request) throws SiteWhereException {
	DeviceCommand updated = assertDeviceCommand(context, command.getToken());
	List<IDeviceCommand> existing = listDeviceCommands(context, deviceType, false);
	DeviceManagementPersistence.deviceCommandUpdateLogic(null, request, updated, existing);
	return putDeviceCommandPayload(context, updated);
    }

    /**
     * Delete an existing device command (or mark as deleted if 'force' is not
     * true).
     * 
     * @param context
     * @param command
     * @param force
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceCommand deleteDeviceCommand(IHBaseContext context, IDeviceCommand command, boolean force)
	    throws SiteWhereException {
	DeviceCommand existing = assertDeviceCommand(context, command.getToken());
	existing.setDeleted(true);

	byte[] rowkey = context.getDeviceIdManager().getCommandKeys().getValue(command.getToken());
	if (force) {
	    context.getDeviceIdManager().getSpecificationKeys().delete(command.getToken());
	    Table devices = null;
	    try {
		Delete delete = new Delete(rowkey);
		devices = getDeviceTableInterface(context);
		devices.delete(delete);
	    } catch (IOException e) {
		throw new SiteWhereException("Unable to delete device command.", e);
	    } finally {
		HBaseUtils.closeCleanly(devices);
	    }
	} else {
	    byte[] marker = { (byte) 0x01 };
	    DeviceManagementPersistence.setUpdatedEntityMetadata(existing);
	    byte[] updated = context.getPayloadMarshaler().encodeDeviceCommand(existing);

	    Table devices = null;
	    try {
		devices = getDeviceTableInterface(context);
		Put put = new Put(rowkey);
		HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, updated);
		put.addColumn(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.DELETED, marker);
		devices.put(put);
	    } catch (IOException e) {
		throw new SiteWhereException("Unable to set deleted flag for device command.", e);
	    } finally {
		HBaseUtils.closeCleanly(devices);
	    }
	}
	return existing;
    }

    /**
     * Gets a device command by token or throws an exception if not found.
     * 
     * @param context
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public static DeviceCommand assertDeviceCommand(IHBaseContext context, String token) throws SiteWhereException {
	DeviceCommand existing = getDeviceCommandByToken(context, token);
	if (existing == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceCommandToken, ErrorLevel.ERROR);
	}
	return existing;
    }

    /**
     * Save payload for device command.
     * 
     * @param context
     * @param command
     * @return
     * @throws SiteWhereException
     */
    public static DeviceCommand putDeviceCommandPayload(IHBaseContext context, DeviceCommand command)
	    throws SiteWhereException {
	byte[] rowkey = context.getDeviceIdManager().getCommandKeys().getValue(command.getToken());
	byte[] payload = context.getPayloadMarshaler().encodeDeviceCommand(command);

	Table devices = null;
	try {
	    devices = getDeviceTableInterface(context);
	    Put put = new Put(rowkey);
	    HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
	    devices.put(put);
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to put device command data.", e);
	} finally {
	    HBaseUtils.closeCleanly(devices);
	}

	return command;
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