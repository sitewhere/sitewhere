/*
 * HBaseDeviceNetwork.java 
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.common.MarshalUtils;
import com.sitewhere.hbase.common.Pager;
import com.sitewhere.hbase.uid.IdManager;
import com.sitewhere.hbase.uid.UniqueIdCounterMap;
import com.sitewhere.hbase.uid.UniqueIdCounterMapRowKeyBuilder;
import com.sitewhere.rest.model.device.network.DeviceNetwork;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.network.IDeviceNetwork;
import com.sitewhere.spi.device.request.IDeviceNetworkCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchCriteria;

/**
 * HBase specifics for dealing with SiteWhere device networks.
 * 
 * @author Derek
 */
public class HBaseDeviceNetwork {

	/** Length of device identifier (subset of 8 byte long) */
	public static final int IDENTIFIER_LENGTH = 4;

	/** Column qualifier for network entry counter */
	public static final byte[] ENTRY_COUNTER = Bytes.toBytes("entryctr");

	/** Used to look up row keys from tokens */
	public static UniqueIdCounterMapRowKeyBuilder KEY_BUILDER = new UniqueIdCounterMapRowKeyBuilder() {

		@Override
		public UniqueIdCounterMap getMap() {
			return IdManager.getInstance().getNetworkKeys();
		}

		@Override
		public byte getTypeIdentifier() {
			return DeviceRecordType.DeviceNetwork.getType();
		}

		@Override
		public byte getPrimaryIdentifier() {
			return DeviceNetworkRecordType.DeviceNetwork.getType();
		}

		@Override
		public int getKeyIdLength() {
			return 4;
		}

		@Override
		public ErrorCode getInvalidKeyErrorCode() {
			return ErrorCode.InvalidDeviceNetworkToken;
		}
	};

	/**
	 * Create a device network.
	 * 
	 * @param hbase
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceNetwork createDeviceNetwork(ISiteWhereHBaseClient hbase,
			IDeviceNetworkCreateRequest request) throws SiteWhereException {
		String uuid = null;
		if (request.getToken() != null) {
			uuid = KEY_BUILDER.getMap().useExistingId(request.getToken());
		} else {
			uuid = KEY_BUILDER.getMap().createUniqueId();
		}

		// Use common logic so all backend implementations work the same.
		DeviceNetwork network = SiteWherePersistence.deviceNetworkCreateLogic(request, uuid);

		Map<byte[], byte[]> qualifiers = new HashMap<byte[], byte[]>();
		byte[] zero = Bytes.toBytes((long) 0);
		qualifiers.put(ENTRY_COUNTER, zero);
		return HBaseUtils.create(hbase, ISiteWhereHBase.DEVICES_TABLE_NAME, network, uuid, KEY_BUILDER,
				qualifiers);
	}

	/**
	 * Update device network information.
	 * 
	 * @param hbase
	 * @param token
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceNetwork updateDeviceNetwork(ISiteWhereHBaseClient hbase, String token,
			IDeviceNetworkCreateRequest request) throws SiteWhereException {
		DeviceNetwork updated = assertDeviceNetwork(hbase, token);
		SiteWherePersistence.deviceNetworkUpdateLogic(request, updated);
		return HBaseUtils.putJson(hbase, ISiteWhereHBase.DEVICES_TABLE_NAME, updated, token, KEY_BUILDER);
	}

	/**
	 * Get a {@link DeviceNetwork} by unique token.
	 * 
	 * @param hbase
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceNetwork getDeviceNetworkByToken(ISiteWhereHBaseClient hbase, String token)
			throws SiteWhereException {
		return HBaseUtils.get(hbase, ISiteWhereHBase.DEVICES_TABLE_NAME, token, KEY_BUILDER,
				DeviceNetwork.class);
	}

	/**
	 * Get paged {@link IDeviceNetwork} results based on the given search criteria.
	 * 
	 * @param hbase
	 * @param includeDeleted
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceNetwork> listDeviceNetworks(ISiteWhereHBaseClient hbase,
			boolean includeDeleted, ISearchCriteria criteria) throws SiteWhereException {
		Pager<byte[]> matches =
				HBaseUtils.getFilteredList(hbase, ISiteWhereHBase.DEVICES_TABLE_NAME, KEY_BUILDER,
						includeDeleted, criteria);
		List<IDeviceNetwork> response = new ArrayList<IDeviceNetwork>();
		for (byte[] json : matches.getResults()) {
			response.add(MarshalUtils.unmarshalJson(json, DeviceNetwork.class));
		}
		return new SearchResults<IDeviceNetwork>(response, matches.getTotal());
	}

	/**
	 * Aloocates the next available network element id.
	 * 
	 * @param hbase
	 * @param primary
	 * @return
	 * @throws SiteWhereException
	 */
	public static Long allocateNextElementId(ISiteWhereHBaseClient hbase, byte[] primary)
			throws SiteWhereException {
		HTableInterface networks = null;
		try {
			networks = hbase.getTableInterface(ISiteWhereHBase.DEVICES_TABLE_NAME);
			Increment increment = new Increment(primary);
			increment.addColumn(ISiteWhereHBase.FAMILY_ID, ENTRY_COUNTER, 1);
			Result result = networks.increment(increment);
			return Bytes.toLong(result.value());
		} catch (IOException e) {
			throw new SiteWhereException("Unable to allocate next network element id.", e);
		} finally {
			HBaseUtils.closeCleanly(networks);
		}
	}

	/**
	 * Delete an existing device network.
	 * 
	 * @param hbase
	 * @param token
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceNetwork deleteDeviceNetwork(ISiteWhereHBaseClient hbase, String token, boolean force)
			throws SiteWhereException {
		return HBaseUtils.delete(hbase, ISiteWhereHBase.DEVICES_TABLE_NAME, token, force, KEY_BUILDER,
				DeviceNetwork.class);
	}

	/**
	 * Get a {@link DeviceNetwork} by token or throw an exception if token is not valid.
	 * 
	 * @param hbase
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceNetwork assertDeviceNetwork(ISiteWhereHBaseClient hbase, String token)
			throws SiteWhereException {
		DeviceNetwork existing = getDeviceNetworkByToken(hbase, token);
		if (existing == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceNetworkToken, ErrorLevel.ERROR);
		}
		return existing;
	}

	/**
	 * Get the unique device identifier based on the long value associated with the device
	 * network UUID. This will be a subset of the full 8-bit long value.
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
	 * Get row key for a device network with the given internal id.
	 * 
	 * @param networkId
	 * @return
	 */
	public static byte[] getPrimaryRowKey(Long networkId) {
		ByteBuffer buffer = ByteBuffer.allocate(IDENTIFIER_LENGTH + 2);
		buffer.put(DeviceRecordType.DeviceNetwork.getType());
		buffer.put(getTruncatedIdentifier(networkId));
		buffer.put(DeviceNetworkRecordType.DeviceNetwork.getType());
		return buffer.array();
	}
}