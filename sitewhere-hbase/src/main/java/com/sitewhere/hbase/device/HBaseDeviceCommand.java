/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.device;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.encoder.PayloadMarshalerResolver;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDeviceSpecification;
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
	 * Create a new device command for an existing device specification.
	 * 
	 * @param context
	 * @param spec
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceCommand createDeviceCommand(IHBaseContext context, IDeviceSpecification spec,
			IDeviceCommandCreateRequest request) throws SiteWhereException {
		Long specId = context.getDeviceIdManager().getSpecificationKeys().getValue(spec.getToken());
		if (specId == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceSpecificationToken, ErrorLevel.ERROR);
		}
		String uuid = ((request.getToken() != null) ? request.getToken() : UUID.randomUUID().toString());

		// Use common logic so all backend implementations work the same.
		List<IDeviceCommand> existing = listDeviceCommands(context, spec.getToken(), false);
		DeviceCommand command = SiteWherePersistence.deviceCommandCreateLogic(spec, request, uuid, existing);

		// Create unique row for new device.
		Long nextId = HBaseDeviceSpecification.allocateNextCommandId(context, specId);
		byte[] rowkey = HBaseDeviceSpecification.getDeviceCommandRowKey(specId, nextId);
		context.getDeviceIdManager().getCommandKeys().create(uuid, rowkey);

		return putDeviceCommandPayload(context, command);
	}

	/**
	 * List device commands that match the given criteria.
	 * 
	 * @param context
	 * @param specToken
	 * @param includeDeleted
	 * @return
	 * @throws SiteWhereException
	 */
	public static List<IDeviceCommand> listDeviceCommands(IHBaseContext context, String specToken,
			boolean includeDeleted) throws SiteWhereException {
		List<IDeviceCommand> matches = getFilteredDeviceCommands(context, specToken, includeDeleted);
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
	 * @param specToken
	 * @param includeDeleted
	 * @return
	 * @throws SiteWhereException
	 */
	protected static List<IDeviceCommand> getFilteredDeviceCommands(IHBaseContext context, String specToken,
			boolean includeDeleted) throws SiteWhereException {
		Long specId = context.getDeviceIdManager().getSpecificationKeys().getValue(specToken);
		if (specId == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceSpecificationToken, ErrorLevel.ERROR);
		}

		HTableInterface devices = null;
		ResultScanner scanner = null;

		try {
			devices = getDeviceTableInterface(context);
			Scan scan = new Scan();
			scan.setStartRow(HBaseDeviceSpecification.getDeviceCommandRowPrefix(specId));
			scan.setStopRow(HBaseDeviceSpecification.getEndRowPrefix(specId));
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
					results.add(PayloadMarshalerResolver.getInstance().getMarshaler(type).decodeDeviceCommand(
							payload));
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
	public static DeviceCommand getDeviceCommandByToken(IHBaseContext context, String token)
			throws SiteWhereException {
		byte[] rowkey = context.getDeviceIdManager().getCommandKeys().getValue(token);
		if (rowkey == null) {
			return null;
		}

		HTableInterface devices = null;
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
	 * @param token
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceCommand updateDeviceCommand(IHBaseContext context, String token,
			IDeviceCommandCreateRequest request) throws SiteWhereException {
		DeviceCommand updated = assertDeviceCommand(context, token);
		List<IDeviceCommand> existing = listDeviceCommands(context, updated.getSpecificationToken(), false);
		SiteWherePersistence.deviceCommandUpdateLogic(request, updated, existing);
		return putDeviceCommandPayload(context, updated);
	}

	/**
	 * Delete an existing device command (or mark as deleted if 'force' is not true).
	 * 
	 * @param context
	 * @param token
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceCommand deleteDeviceCommand(IHBaseContext context, String token, boolean force)
			throws SiteWhereException {
		DeviceCommand existing = assertDeviceCommand(context, token);
		existing.setDeleted(true);

		byte[] rowkey = context.getDeviceIdManager().getCommandKeys().getValue(token);
		if (force) {
			context.getDeviceIdManager().getSpecificationKeys().delete(token);
			HTableInterface devices = null;
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
			SiteWherePersistence.setUpdatedEntityMetadata(existing);
			byte[] updated = context.getPayloadMarshaler().encodeDeviceCommand(existing);

			HTableInterface devices = null;
			try {
				devices = getDeviceTableInterface(context);
				Put put = new Put(rowkey);
				HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, updated);
				put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.DELETED, marker);
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
	public static DeviceCommand assertDeviceCommand(IHBaseContext context, String token)
			throws SiteWhereException {
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

		HTableInterface devices = null;
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
	protected static HTableInterface getDeviceTableInterface(IHBaseContext context) throws SiteWhereException {
		return context.getClient().getTableInterface(context.getTenant(), ISiteWhereHBase.DEVICES_TABLE_NAME);
	}
}