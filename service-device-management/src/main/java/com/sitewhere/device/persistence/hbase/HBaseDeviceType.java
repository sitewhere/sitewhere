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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.device.persistence.DeviceManagementPersistence;
import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.common.IRowKeyBuilder;
import com.sitewhere.hbase.uid.UniqueIdCounterMap;
import com.sitewhere.hbase.uid.UniqueIdCounterMapRowKeyBuilder;
import com.sitewhere.rest.model.device.DeviceType;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.common.IFilter;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.request.IDeviceTypeCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchCriteria;

/**
 * HBase specifics for dealing with SiteWhere device types.
 * 
 * @author Derek
 */
public class HBaseDeviceType {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    /** Length of device type identifier (subset of 8 byte long) */
    public static final int DEVICE_TYPE_IDENTIFIER_LENGTH = 4;

    /** Length of command identifier (subset of 8 byte long) */
    public static final int COMMAND_IDENTIFIER_LENGTH = 4;

    /** Column qualifier for command counter */
    public static final byte[] COMMAND_COUNTER = Bytes.toBytes("commandctr");

    /** Used to look up row keys from tokens */
    public static IRowKeyBuilder KEY_BUILDER = new UniqueIdCounterMapRowKeyBuilder() {

	@Override
	public UniqueIdCounterMap getMap(IHBaseContext context) {
	    return context.getDeviceIdManager().getSpecificationKeys();
	}

	@Override
	public byte getTypeIdentifier() {
	    return DeviceRecordType.DeviceSpecification.getType();
	}

	@Override
	public byte getPrimaryIdentifier() {
	    return DeviceSpecificationRecordType.DeviceSpecification.getType();
	}

	@Override
	public int getKeyIdLength() {
	    return 4;
	}

	@Override
	public void throwInvalidKey() throws SiteWhereException {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	}
    };

    /**
     * Create a device type.
     * 
     * @param context
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceType createDeviceType(IHBaseContext context, IDeviceTypeCreateRequest request)
	    throws SiteWhereException {
	String uuid = null;
	if (request.getToken() != null) {
	    uuid = context.getDeviceIdManager().getSpecificationKeys().useExistingId(request.getToken());
	} else {
	    uuid = context.getDeviceIdManager().getSpecificationKeys().createUniqueId();
	}

	// Use common logic so all backend implementations work the same.
	DeviceType deviceType = DeviceManagementPersistence.deviceTypeCreateLogic(request, uuid);

	Map<byte[], byte[]> qualifiers = new HashMap<byte[], byte[]>();
	byte[] maxLong = Bytes.toBytes(Long.MAX_VALUE);
	qualifiers.put(COMMAND_COUNTER, maxLong);
	return HBaseUtils.createOrUpdate(context, context.getPayloadMarshaler(), ISiteWhereHBase.DEVICES_TABLE_NAME,
		deviceType, uuid, KEY_BUILDER, qualifiers);
    }

    /**
     * Get a device type by unique token.
     * 
     * @param context
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public static DeviceType getDeviceTypeByToken(IHBaseContext context, String token) throws SiteWhereException {
	return HBaseUtils.get(context, ISiteWhereHBase.DEVICES_TABLE_NAME, token, KEY_BUILDER, DeviceType.class);
    }

    /**
     * Update an existing device type.
     * 
     * @param context
     * @param token
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceType updateDeviceType(IHBaseContext context, IDeviceType deviceType,
	    IDeviceTypeCreateRequest request) throws SiteWhereException {
	DeviceManagementPersistence.deviceTypeUpdateLogic(request, (DeviceType) deviceType);
	return HBaseUtils.put(context, context.getPayloadMarshaler(), ISiteWhereHBase.DEVICES_TABLE_NAME, deviceType,
		deviceType.getToken(), KEY_BUILDER);
    }

    /**
     * List device types that match the given search criteria.
     * 
     * @param context
     * @param includeDeleted
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IDeviceType> listDeviceTypes(IHBaseContext context, boolean includeDeleted,
	    ISearchCriteria criteria) throws SiteWhereException {
	Comparator<DeviceType> comparator = new Comparator<DeviceType>() {

	    public int compare(DeviceType a, DeviceType b) {
		return -1 * (a.getCreatedDate().compareTo(b.getCreatedDate()));
	    }

	};
	IFilter<DeviceType> filter = new IFilter<DeviceType>() {

	    public boolean isExcluded(DeviceType item) {
		return false;
	    }
	};
	return HBaseUtils.getFilteredList(context, ISiteWhereHBase.DEVICES_TABLE_NAME, KEY_BUILDER, includeDeleted,
		IDeviceType.class, DeviceType.class, filter, criteria, comparator);
    }

    /**
     * Either deletes a device type or marks it as deleted.
     * 
     * @param context
     * @param token
     * @param force
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceType deleteDeviceType(IHBaseContext context, IDeviceType deviceType, boolean force)
	    throws SiteWhereException {
	return HBaseUtils.delete(context, context.getPayloadMarshaler(), ISiteWhereHBase.DEVICES_TABLE_NAME,
		deviceType.getToken(), force, KEY_BUILDER, DeviceType.class);
    }

    /**
     * Get a device type by token or throw an exception if not found.
     * 
     * @param context
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public static DeviceType assertDeviceType(IHBaseContext context, String token) throws SiteWhereException {
	DeviceType existing = getDeviceTypeByToken(context, token);
	if (existing == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	}
	return existing;
    }

    /**
     * Get the unique device identifier based on the long value associated with the
     * device UUID. This will be a subset of the full 8-bit long value.
     * 
     * @param value
     * @return
     */
    public static byte[] getTruncatedIdentifier(Long value) {
	byte[] bytes = Bytes.toBytes(value);
	byte[] result = new byte[DEVICE_TYPE_IDENTIFIER_LENGTH];
	System.arraycopy(bytes, bytes.length - DEVICE_TYPE_IDENTIFIER_LENGTH, result, 0, DEVICE_TYPE_IDENTIFIER_LENGTH);
	return result;
    }

    /**
     * Allocate the next command id and return the new value. (Each id is less than
     * the last)
     * 
     * @param context
     * @param deviceTypeId
     * @return
     * @throws SiteWhereException
     */
    public static Long allocateNextCommandId(IHBaseContext context, Long deviceTypeId) throws SiteWhereException {
	byte[] primary = getPrimaryRowKey(deviceTypeId);
	Table devices = null;
	try {
	    devices = getDeviceTableInterface(context);
	    Increment increment = new Increment(primary);
	    increment.addColumn(ISiteWhereHBase.FAMILY_ID, COMMAND_COUNTER, -1);
	    Result result = devices.increment(increment);
	    return Bytes.toLong(result.value());
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to allocate next command id.", e);
	} finally {
	    HBaseUtils.closeCleanly(devices);
	}
    }

    /**
     * Get row key for a device type with the given id.
     * 
     * @param deviceTypeId
     * @return
     */
    public static byte[] getPrimaryRowKey(Long deviceTypeId) {
	ByteBuffer buffer = ByteBuffer.allocate(DEVICE_TYPE_IDENTIFIER_LENGTH + 2);
	buffer.put(DeviceRecordType.DeviceSpecification.getType());
	buffer.put(getTruncatedIdentifier(deviceTypeId));
	buffer.put(DeviceSpecificationRecordType.DeviceSpecification.getType());
	return buffer.array();
    }

    /**
     * Get the unique command identifier based on the long value associated with the
     * command UUID. This will be a subset of the full 8-bit long value.
     * 
     * @param value
     * @return
     */
    public static byte[] getCommandIdentifier(Long value) {
	byte[] bytes = Bytes.toBytes(value);
	byte[] result = new byte[COMMAND_IDENTIFIER_LENGTH];
	System.arraycopy(bytes, bytes.length - COMMAND_IDENTIFIER_LENGTH, result, 0, COMMAND_IDENTIFIER_LENGTH);
	return result;
    }

    /**
     * Get prefix for a device command row for the given device type.
     * 
     * @param deviceTypeId
     * @return
     */
    public static byte[] getDeviceCommandRowPrefix(Long deviceTypeId) {
	ByteBuffer buffer = ByteBuffer.allocate(1 + DEVICE_TYPE_IDENTIFIER_LENGTH + 1);
	buffer.put(DeviceRecordType.DeviceSpecification.getType());
	buffer.put(getTruncatedIdentifier(deviceTypeId));
	buffer.put(DeviceSpecificationRecordType.DeviceCommand.getType());
	return buffer.array();
    }

    /**
     * Get prefix after the last defined device type record type.
     * 
     * @param deviceTypeId
     * @return
     */
    public static byte[] getEndRowPrefix(Long deviceTypeId) {
	ByteBuffer buffer = ByteBuffer.allocate(1 + DEVICE_TYPE_IDENTIFIER_LENGTH + 1);
	buffer.put(DeviceRecordType.DeviceSpecification.getType());
	buffer.put(getTruncatedIdentifier(deviceTypeId));
	buffer.put(DeviceSpecificationRecordType.End.getType());
	return buffer.array();
    }

    /**
     * Get row key for a device command based on device type and unique id.
     * 
     * @param deviceTypeId
     * @param value
     * @return
     */
    public static byte[] getDeviceCommandRowKey(Long deviceTypeId, Long value) {
	byte[] prefix = getDeviceCommandRowPrefix(deviceTypeId);
	byte[] uid = getCommandIdentifier(value);
	ByteBuffer buffer = ByteBuffer.allocate(prefix.length + uid.length);
	buffer.put(prefix);
	buffer.put(uid);
	return buffer.array();
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