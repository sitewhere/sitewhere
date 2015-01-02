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
import com.sitewhere.hbase.uid.IdManager;
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
	 * @param spec
	 * @param hbase
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceCommand createDeviceCommand(ISiteWhereHBaseClient hbase, IDeviceSpecification spec,
			IDeviceCommandCreateRequest request) throws SiteWhereException {
		Long specId = IdManager.getInstance().getSpecificationKeys().getValue(spec.getToken());
		if (specId == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceSpecificationToken, ErrorLevel.ERROR);
		}
		String uuid = ((request.getToken() != null) ? request.getToken() : UUID.randomUUID().toString());

		// Use common logic so all backend implementations work the same.
		List<IDeviceCommand> existing = listDeviceCommands(hbase, spec.getToken(), false);
		DeviceCommand command = SiteWherePersistence.deviceCommandCreateLogic(spec, request, uuid, existing);

		// Create unique row for new device.
		Long nextId = HBaseDeviceSpecification.allocateNextCommandId(hbase, specId);
		byte[] rowkey = HBaseDeviceSpecification.getDeviceCommandRowKey(specId, nextId);
		IdManager.getInstance().getCommandKeys().create(uuid, rowkey);

		return putDeviceCommandJson(hbase, command);
	}

	/**
	 * List device commands that match the given criteria.
	 * 
	 * @param hbase
	 * @param specToken
	 * @param includeDeleted
	 * @return
	 * @throws SiteWhereException
	 */
	public static List<IDeviceCommand> listDeviceCommands(ISiteWhereHBaseClient hbase, String specToken,
			boolean includeDeleted) throws SiteWhereException {
		List<byte[]> matches = getFilteredDeviceCommands(hbase, specToken, includeDeleted);
		List<IDeviceCommand> response = new ArrayList<IDeviceCommand>();
		for (byte[] json : matches) {
			response.add(MarshalUtils.unmarshalJson(json, DeviceCommand.class));
		}
		Collections.sort(response, new Comparator<IDeviceCommand>() {

			@Override
			public int compare(IDeviceCommand a, IDeviceCommand b) {
				return a.getCreatedDate().compareTo(b.getCreatedDate());
			}
		});
		return response;
	}

	/**
	 * Get device commands that correspond to the given criteria.
	 * 
	 * @param hbase
	 * @param specToken
	 * @param includeDeleted
	 * @return
	 * @throws SiteWhereException
	 */
	protected static List<byte[]> getFilteredDeviceCommands(ISiteWhereHBaseClient hbase, String specToken,
			boolean includeDeleted) throws SiteWhereException {
		Long specId = IdManager.getInstance().getSpecificationKeys().getValue(specToken);
		if (specId == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceSpecificationToken, ErrorLevel.ERROR);
		}

		HTableInterface devices = null;
		ResultScanner scanner = null;

		try {
			devices = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
			Scan scan = new Scan();
			scan.setStartRow(HBaseDeviceSpecification.getDeviceCommandRowPrefix(specId));
			scan.setStopRow(HBaseDeviceSpecification.getEndRowPrefix(specId));
			scanner = devices.getScanner(scan);

			List<byte[]> results = new ArrayList<byte[]>();
			for (Result result : scanner) {
				boolean shouldAdd = true;
				byte[] json = null;
				for (KeyValue column : result.raw()) {
					byte[] qualifier = column.getQualifier();
					if ((Bytes.equals(ISiteWhereHBase.DELETED, qualifier)) && (!includeDeleted)) {
						shouldAdd = false;
					}
					if (Bytes.equals(ISiteWhereHBase.JSON_CONTENT, qualifier)) {
						json = column.getValue();
					}
				}
				if ((shouldAdd) && (json != null)) {
					results.add(json);
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
	 * @param hbase
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceCommand getDeviceCommandByToken(ISiteWhereHBaseClient hbase, String token)
			throws SiteWhereException {
		byte[] rowkey = IdManager.getInstance().getCommandKeys().getValue(token);
		if (rowkey == null) {
			return null;
		}

		HTableInterface devices = null;
		try {
			devices = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
			Get get = new Get(rowkey);
			get.addColumn(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT);
			Result result = devices.get(get);
			if (result.size() != 1) {
				throw new SiteWhereException("Expected one JSON entry for device command and found: "
						+ result.size());
			}
			return MarshalUtils.unmarshalJson(result.value(), DeviceCommand.class);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to load device command by token.", e);
		} finally {
			HBaseUtils.closeCleanly(devices);
		}
	}

	/**
	 * Update an existing device command.
	 * 
	 * @param hbase
	 * @param token
	 * @param request
	 * @param existing
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceCommand updateDeviceCommand(ISiteWhereHBaseClient hbase, String token,
			IDeviceCommandCreateRequest request) throws SiteWhereException {
		DeviceCommand updated = assertDeviceCommand(hbase, token);
		List<IDeviceCommand> existing = listDeviceCommands(hbase, updated.getSpecificationToken(), false);
		SiteWherePersistence.deviceCommandUpdateLogic(request, updated, existing);
		return putDeviceCommandJson(hbase, updated);
	}

	/**
	 * Delete an existing device command (or mark as deleted if 'force' is not true).
	 * 
	 * @param hbase
	 * @param token
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceCommand deleteDeviceCommand(ISiteWhereHBaseClient hbase, String token, boolean force)
			throws SiteWhereException {
		DeviceCommand existing = assertDeviceCommand(hbase, token);
		existing.setDeleted(true);

		byte[] rowkey = IdManager.getInstance().getCommandKeys().getValue(token);
		if (force) {
			IdManager.getInstance().getSpecificationKeys().delete(token);
			HTableInterface devices = null;
			try {
				Delete delete = new Delete(rowkey);
				devices = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
				devices.delete(delete);
			} catch (IOException e) {
				throw new SiteWhereException("Unable to delete device command.", e);
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
				Put put = new Put(rowkey);
				put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, updated);
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
	 * @param hbase
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceCommand assertDeviceCommand(ISiteWhereHBaseClient hbase, String token)
			throws SiteWhereException {
		DeviceCommand existing = getDeviceCommandByToken(hbase, token);
		if (existing == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceCommandToken, ErrorLevel.ERROR);
		}
		return existing;
	}

	/**
	 * Save the JSON representation of a device command.
	 * 
	 * @param hbase
	 * @param specToken
	 * @param command
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceCommand putDeviceCommandJson(ISiteWhereHBaseClient hbase, DeviceCommand command)
			throws SiteWhereException {
		byte[] rowkey = IdManager.getInstance().getCommandKeys().getValue(command.getToken());
		byte[] json = MarshalUtils.marshalJson(command);

		HTableInterface devices = null;
		try {
			devices = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
			Put put = new Put(rowkey);
			put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, json);
			devices.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to put device command data.", e);
		} finally {
			HBaseUtils.closeCleanly(devices);
		}

		return command;
	}
}