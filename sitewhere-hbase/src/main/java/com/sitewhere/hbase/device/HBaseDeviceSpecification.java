/*
 * HBaseDeviceSpecification.java 
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import com.sitewhere.SiteWhere;
import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.device.marshaling.DeviceSpecificationMarshalHelper;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.common.IRowKeyBuilder;
import com.sitewhere.hbase.uid.IdManager;
import com.sitewhere.hbase.uid.UniqueIdCounterMap;
import com.sitewhere.hbase.uid.UniqueIdCounterMapRowKeyBuilder;
import com.sitewhere.rest.model.device.DeviceSpecification;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.common.IFilter;
import com.sitewhere.spi.device.IDeviceManagementCacheProvider;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchCriteria;

/**
 * HBase specifics for dealing with SiteWhere device specifications.
 * 
 * @author Derek
 */
public class HBaseDeviceSpecification {

	/** Length of specification identifier (subset of 8 byte long) */
	public static final int SPEC_IDENTIFIER_LENGTH = 4;

	/** Length of command identifier (subset of 8 byte long) */
	public static final int COMMAND_IDENTIFIER_LENGTH = 4;

	/** Column qualifier for command counter */
	public static final byte[] COMMAND_COUNTER = Bytes.toBytes("commandctr");

	/** Used for cloning device assignment results */
	private static DeviceSpecificationMarshalHelper SPECIFICATION_HELPER =
			new DeviceSpecificationMarshalHelper().setIncludeAsset(true);

	/** Used to look up row keys from tokens */
	public static IRowKeyBuilder KEY_BUILDER = new UniqueIdCounterMapRowKeyBuilder() {

		@Override
		public UniqueIdCounterMap getMap() {
			return IdManager.getInstance().getSpecificationKeys();
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
		public ErrorCode getInvalidKeyErrorCode() {
			return ErrorCode.InvalidDeviceSpecificationToken;
		}
	};

	/**
	 * Create a device specification.
	 * 
	 * @param hbase
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceSpecification createDeviceSpecification(ISiteWhereHBaseClient hbase,
			IDeviceSpecificationCreateRequest request, IDeviceManagementCacheProvider cache)
			throws SiteWhereException {
		String uuid = null;
		if (request.getToken() != null) {
			uuid = IdManager.getInstance().getSpecificationKeys().useExistingId(request.getToken());
		} else {
			uuid = IdManager.getInstance().getSpecificationKeys().createUniqueId();
		}

		// Use common logic so all backend implementations work the same.
		DeviceSpecification specification =
				SiteWherePersistence.deviceSpecificationCreateLogic(request, uuid);

		Map<byte[], byte[]> qualifiers = new HashMap<byte[], byte[]>();
		byte[] maxLong = Bytes.toBytes(Long.MAX_VALUE);
		qualifiers.put(COMMAND_COUNTER, maxLong);
		IDeviceSpecification created =
				HBaseUtils.create(hbase, ISiteWhereHBase.DEVICES_TABLE_NAME, specification, uuid,
						KEY_BUILDER, qualifiers);
		if (cache != null) {
			cache.getDeviceSpecificationCache().put(uuid, created);
		}
		return created;
	}

	/**
	 * Get a device specification by unique token.
	 * 
	 * @param hbase
	 * @param token
	 * @param cache
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceSpecification getDeviceSpecificationByToken(ISiteWhereHBaseClient hbase,
			String token, IDeviceManagementCacheProvider cache) throws SiteWhereException {
		if (cache != null) {
			IDeviceSpecification result = cache.getDeviceSpecificationCache().get(token);
			if (result != null) {
				return SPECIFICATION_HELPER.convert(result, SiteWhere.getServer().getAssetModuleManager());
			}
		}
		DeviceSpecification found =
				HBaseUtils.get(hbase, ISiteWhereHBase.DEVICES_TABLE_NAME, token, KEY_BUILDER,
						DeviceSpecification.class);
		if ((cache != null) && (found != null)) {
			cache.getDeviceSpecificationCache().put(token, found);
		}
		return found;
	}

	/**
	 * Update an existing device specification.
	 * 
	 * @param hbase
	 * @param token
	 * @param request
	 * @param cache
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceSpecification updateDeviceSpecification(ISiteWhereHBaseClient hbase, String token,
			IDeviceSpecificationCreateRequest request, IDeviceManagementCacheProvider cache)
			throws SiteWhereException {
		DeviceSpecification updated = assertDeviceSpecification(hbase, token, cache);
		SiteWherePersistence.deviceSpecificationUpdateLogic(request, updated);
		if (cache != null) {
			cache.getDeviceSpecificationCache().put(token, updated);
		}
		return HBaseUtils.putJson(hbase, ISiteWhereHBase.DEVICES_TABLE_NAME, updated, token, KEY_BUILDER);
	}

	/**
	 * List device specifications that match the given search criteria.
	 * 
	 * @param hbase
	 * @param includeDeleted
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceSpecification> listDeviceSpecifications(ISiteWhereHBaseClient hbase,
			boolean includeDeleted, ISearchCriteria criteria) throws SiteWhereException {
		Comparator<DeviceSpecification> comparator = new Comparator<DeviceSpecification>() {

			public int compare(DeviceSpecification a, DeviceSpecification b) {
				return -1 * (a.getCreatedDate().compareTo(b.getCreatedDate()));
			}

		};
		IFilter<DeviceSpecification> filter = new IFilter<DeviceSpecification>() {

			public boolean isExcluded(DeviceSpecification item) {
				return false;
			}
		};
		return HBaseUtils.getFilteredList(hbase, ISiteWhereHBase.DEVICES_TABLE_NAME, KEY_BUILDER,
				includeDeleted, IDeviceSpecification.class, DeviceSpecification.class, filter, criteria,
				comparator);
	}

	/**
	 * Either deletes a device specification or marks it as deleted.
	 * 
	 * @param hbase
	 * @param token
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceSpecification deleteDeviceSpecification(ISiteWhereHBaseClient hbase, String token,
			boolean force) throws SiteWhereException {
		return HBaseUtils.delete(hbase, ISiteWhereHBase.DEVICES_TABLE_NAME, token, force, KEY_BUILDER,
				DeviceSpecification.class);
	}

	/**
	 * Get a device specification by token or throw an exception if not found.
	 * 
	 * @param hbase
	 * @param token
	 * @param cache
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceSpecification assertDeviceSpecification(ISiteWhereHBaseClient hbase, String token,
			IDeviceManagementCacheProvider cache) throws SiteWhereException {
		DeviceSpecification existing = getDeviceSpecificationByToken(hbase, token, cache);
		if (existing == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceSpecificationToken, ErrorLevel.ERROR);
		}
		return existing;
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
		byte[] result = new byte[SPEC_IDENTIFIER_LENGTH];
		System.arraycopy(bytes, bytes.length - SPEC_IDENTIFIER_LENGTH, result, 0, SPEC_IDENTIFIER_LENGTH);
		return result;
	}

	/**
	 * Allocate the next command id and return the new value. (Each id is less than the
	 * last)
	 * 
	 * @param hbase
	 * @param specId
	 * @return
	 * @throws SiteWhereException
	 */
	public static Long allocateNextCommandId(ISiteWhereHBaseClient hbase, Long specId)
			throws SiteWhereException {
		byte[] primary = getPrimaryRowKey(specId);
		HTableInterface devices = null;
		try {
			devices = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
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
	 * Get row key for a device specification with the given id.
	 * 
	 * @param specificationId
	 * @return
	 */
	public static byte[] getPrimaryRowKey(Long specificationId) {
		ByteBuffer buffer = ByteBuffer.allocate(SPEC_IDENTIFIER_LENGTH + 2);
		buffer.put(DeviceRecordType.DeviceSpecification.getType());
		buffer.put(getTruncatedIdentifier(specificationId));
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
		System.arraycopy(bytes, bytes.length - COMMAND_IDENTIFIER_LENGTH, result, 0,
				COMMAND_IDENTIFIER_LENGTH);
		return result;
	}

	/**
	 * Get prefix for a device command row for the given specification.
	 * 
	 * @param specificationId
	 * @return
	 */
	public static byte[] getDeviceCommandRowPrefix(Long specificationId) {
		ByteBuffer buffer = ByteBuffer.allocate(1 + SPEC_IDENTIFIER_LENGTH + 1);
		buffer.put(DeviceRecordType.DeviceSpecification.getType());
		buffer.put(getTruncatedIdentifier(specificationId));
		buffer.put(DeviceSpecificationRecordType.DeviceCommand.getType());
		return buffer.array();
	}

	/**
	 * Get prefix after the last defined specification record type.
	 * 
	 * @param specificationId
	 * @return
	 */
	public static byte[] getEndRowPrefix(Long specificationId) {
		ByteBuffer buffer = ByteBuffer.allocate(1 + SPEC_IDENTIFIER_LENGTH + 1);
		buffer.put(DeviceRecordType.DeviceSpecification.getType());
		buffer.put(getTruncatedIdentifier(specificationId));
		buffer.put(DeviceSpecificationRecordType.End.getType());
		return buffer.array();
	}

	/**
	 * Get row key for a device command based on spec and unique id.
	 * 
	 * @param specId
	 * @param value
	 * @return
	 */
	public static byte[] getDeviceCommandRowKey(Long specId, Long value) {
		byte[] prefix = getDeviceCommandRowPrefix(specId);
		byte[] uid = getCommandIdentifier(value);
		ByteBuffer buffer = ByteBuffer.allocate(prefix.length + uid.length);
		buffer.put(prefix);
		buffer.put(uid);
		return buffer.array();
	}
}