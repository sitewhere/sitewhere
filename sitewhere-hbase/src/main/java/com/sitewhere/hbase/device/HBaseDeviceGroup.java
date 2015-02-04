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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.uid.IdManager;
import com.sitewhere.hbase.uid.UniqueIdCounterMap;
import com.sitewhere.hbase.uid.UniqueIdCounterMapRowKeyBuilder;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.common.IFilter;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.request.IDeviceGroupCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchCriteria;

/**
 * HBase specifics for dealing with SiteWhere device groups.
 * 
 * @author Derek
 */
public class HBaseDeviceGroup {

	/** Length of group identifier (subset of 8 byte long) */
	public static final int IDENTIFIER_LENGTH = 4;

	/** Column qualifier for group entry counter */
	public static final byte[] ENTRY_COUNTER = Bytes.toBytes("entryctr");

	/** Used to look up row keys from tokens */
	public static UniqueIdCounterMapRowKeyBuilder KEY_BUILDER = new UniqueIdCounterMapRowKeyBuilder() {

		@Override
		public UniqueIdCounterMap getMap() {
			return IdManager.getInstance().getDeviceGroupKeys();
		}

		@Override
		public byte getTypeIdentifier() {
			return DeviceRecordType.DeviceGroup.getType();
		}

		@Override
		public byte getPrimaryIdentifier() {
			return DeviceGroupRecordType.DeviceGroup.getType();
		}

		@Override
		public int getKeyIdLength() {
			return 4;
		}

		@Override
		public ErrorCode getInvalidKeyErrorCode() {
			return ErrorCode.InvalidDeviceGroupToken;
		}
	};

	/**
	 * Create a device group.
	 * 
	 * @param hbase
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceGroup createDeviceGroup(ISiteWhereHBaseClient hbase,
			IDeviceGroupCreateRequest request) throws SiteWhereException {
		String uuid = null;
		if (request.getToken() != null) {
			uuid = KEY_BUILDER.getMap().useExistingId(request.getToken());
		} else {
			uuid = KEY_BUILDER.getMap().createUniqueId();
		}

		// Use common logic so all backend implementations work the same.
		DeviceGroup group = SiteWherePersistence.deviceGroupCreateLogic(request, uuid);

		Map<byte[], byte[]> qualifiers = new HashMap<byte[], byte[]>();
		byte[] zero = Bytes.toBytes((long) 0);
		qualifiers.put(ENTRY_COUNTER, zero);
		return HBaseUtils.createOrUpdate(hbase, ISiteWhereHBase.DEVICES_TABLE_NAME, group, uuid, KEY_BUILDER,
				qualifiers);
	}

	/**
	 * Update device group information.
	 * 
	 * @param hbase
	 * @param token
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceGroup updateDeviceGroup(ISiteWhereHBaseClient hbase, String token,
			IDeviceGroupCreateRequest request) throws SiteWhereException {
		DeviceGroup updated = assertDeviceGroup(hbase, token);
		SiteWherePersistence.deviceGroupUpdateLogic(request, updated);
		return HBaseUtils.putJson(hbase, ISiteWhereHBase.DEVICES_TABLE_NAME, updated, token, KEY_BUILDER);
	}

	/**
	 * Get a {@link DeviceGroup} by unique token.
	 * 
	 * @param hbase
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceGroup getDeviceGroupByToken(ISiteWhereHBaseClient hbase, String token)
			throws SiteWhereException {
		return HBaseUtils.get(hbase, ISiteWhereHBase.DEVICES_TABLE_NAME, token, KEY_BUILDER,
				DeviceGroup.class);
	}

	/**
	 * Get paged {@link IDeviceGroup} results based on the given search criteria.
	 * 
	 * @param hbase
	 * @param includeDeleted
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceGroup> listDeviceGroups(ISiteWhereHBaseClient hbase,
			boolean includeDeleted, ISearchCriteria criteria) throws SiteWhereException {
		Comparator<DeviceGroup> comparator = new Comparator<DeviceGroup>() {

			public int compare(DeviceGroup a, DeviceGroup b) {
				return -1 * (a.getCreatedDate().compareTo(b.getCreatedDate()));
			}

		};
		IFilter<DeviceGroup> filter = new IFilter<DeviceGroup>() {

			public boolean isExcluded(DeviceGroup item) {
				return false;
			}
		};
		return HBaseUtils.getFilteredList(hbase, ISiteWhereHBase.DEVICES_TABLE_NAME, KEY_BUILDER,
				includeDeleted, IDeviceGroup.class, DeviceGroup.class, filter, criteria, comparator);
	}

	/**
	 * Get paged {@link IDeviceGroup} results for groups that have a given role based on
	 * the given search criteria.
	 * 
	 * @param role
	 * @param hbase
	 * @param includeDeleted
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceGroup> listDeviceGroupsWithRole(ISiteWhereHBaseClient hbase,
			final String role, boolean includeDeleted, ISearchCriteria criteria) throws SiteWhereException {
		Comparator<DeviceGroup> comparator = new Comparator<DeviceGroup>() {

			public int compare(DeviceGroup a, DeviceGroup b) {
				return -1 * (a.getCreatedDate().compareTo(b.getCreatedDate()));
			}

		};
		IFilter<DeviceGroup> filter = new IFilter<DeviceGroup>() {

			public boolean isExcluded(DeviceGroup item) {
				return !item.getRoles().contains(role);
			}
		};
		return HBaseUtils.getFilteredList(hbase, ISiteWhereHBase.DEVICES_TABLE_NAME, KEY_BUILDER,
				includeDeleted, IDeviceGroup.class, DeviceGroup.class, filter, criteria, comparator);
	}

	/**
	 * Aloocates the next available group element id.
	 * 
	 * @param hbase
	 * @param primary
	 * @return
	 * @throws SiteWhereException
	 */
	public static Long allocateNextElementId(ISiteWhereHBaseClient hbase, byte[] primary)
			throws SiteWhereException {
		HTableInterface devices = null;
		try {
			devices = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
			Increment increment = new Increment(primary);
			increment.addColumn(ISiteWhereHBase.FAMILY_ID, ENTRY_COUNTER, 1);
			Result result = devices.increment(increment);
			return Bytes.toLong(result.value());
		} catch (IOException e) {
			throw new SiteWhereException("Unable to allocate next group element id.", e);
		} finally {
			HBaseUtils.closeCleanly(devices);
		}
	}

	/**
	 * Delete an existing device group.
	 * 
	 * @param hbase
	 * @param token
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceGroup deleteDeviceGroup(ISiteWhereHBaseClient hbase, String token, boolean force)
			throws SiteWhereException {
		// If actually deleting group, delete all group elements.
		if (force) {
			HBaseDeviceGroupElement.deleteElements(hbase, token);
		}
		return HBaseUtils.delete(hbase, ISiteWhereHBase.DEVICES_TABLE_NAME, token, force, KEY_BUILDER,
				DeviceGroup.class);
	}

	/**
	 * Get a {@link DeviceGroup} by token or throw an exception if token is not valid.
	 * 
	 * @param hbase
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceGroup assertDeviceGroup(ISiteWhereHBaseClient hbase, String token)
			throws SiteWhereException {
		DeviceGroup existing = getDeviceGroupByToken(hbase, token);
		if (existing == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceGroupToken, ErrorLevel.ERROR);
		}
		return existing;
	}

	/**
	 * Get the unique device identifier based on the long value associated with the device
	 * group UUID. This will be a subset of the full 8-bit long value.
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] getTruncatedIdentifier(Long value) {
		byte[] bytes = Bytes.toBytes(value);
		byte[] result = new byte[IDENTIFIER_LENGTH];
		System.arraycopy(bytes, bytes.length - IDENTIFIER_LENGTH, result, 0, IDENTIFIER_LENGTH);
		return result;
	}

	/**
	 * Get row key for a device group with the given internal id.
	 * 
	 * @param groupId
	 * @return
	 */
	public static byte[] getPrimaryRowKey(Long groupId) {
		ByteBuffer buffer = ByteBuffer.allocate(IDENTIFIER_LENGTH + 2);
		buffer.put(DeviceRecordType.DeviceGroup.getType());
		buffer.put(getTruncatedIdentifier(groupId));
		buffer.put(DeviceGroupRecordType.DeviceGroup.getType());
		return buffer.array();
	}
}