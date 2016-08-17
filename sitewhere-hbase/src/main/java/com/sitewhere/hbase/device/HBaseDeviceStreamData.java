/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.device;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.rest.model.device.event.DeviceStreamData;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceStreamData;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;

/**
 * HBase specifics for dealing with SiteWhere device stream data.
 * 
 * @author Derek
 */
public class HBaseDeviceStreamData {

	/**
	 * Create device stream data by storing it in both the events table and
	 * streams table. The version in the streams table has the full payload
	 * while the version in the events table has a pointer to the streams entry.
	 * 
	 * @param context
	 * @param assignment
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceStreamData createDeviceStreamData(IHBaseContext context, IDeviceAssignment assignment,
			IDeviceStreamDataCreateRequest request) throws SiteWhereException {
		// Save a corresponding event.
		IDeviceStreamData event = HBaseDeviceEvent.createDeviceStreamData(context, assignment, request);

		// Use common create logic and copy event id from event table.
		DeviceStreamData sdata = SiteWherePersistence.deviceStreamDataCreateLogic(assignment, request);
		sdata.setId(event.getId());

		// Save data in streams table.
		byte[] assnKey = context.getDeviceIdManager().getAssignmentKeys().getValue(assignment.getToken());
		byte[] streamKey = HBaseDeviceStream.getDeviceStreamKey(assnKey, request.getStreamId());
		byte[] payload = context.getPayloadMarshaler().encodeDeviceStreamData(sdata);
		byte[] seqnum = Bytes.toBytes(request.getSequenceNumber());

		Table streams = null;
		try {
			streams = getStreamsTableInterface(context);
			Put put = new Put(streamKey);
			put.addColumn(ISiteWhereHBase.FAMILY_ID, seqnum, payload);
			streams.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to store stream data.", e);
		} finally {
			HBaseUtils.closeCleanly(streams);
		}

		return sdata;
	}

	/**
	 * Get existing device stream data.
	 * 
	 * @param context
	 * @param assignment
	 * @param streamId
	 * @param sequenceNumber
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceStreamData getDeviceStreamData(IHBaseContext context, IDeviceAssignment assignment,
			String streamId, long sequenceNumber) throws SiteWhereException {
		byte[] assnKey = context.getDeviceIdManager().getAssignmentKeys().getValue(assignment.getToken());
		byte[] streamKey = HBaseDeviceStream.getDeviceStreamKey(assnKey, streamId);
		byte[] seqnum = Bytes.toBytes(sequenceNumber);

		Table streams = null;
		try {
			streams = getStreamsTableInterface(context);
			Get get = new Get(streamKey);
			get.addColumn(ISiteWhereHBase.FAMILY_ID, seqnum);
			Result result = streams.get(get);
			byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, seqnum);
			if (payload == null) {
				return null;
			} else {
				return context.getPayloadMarshaler().decodeDeviceStreamData(payload);
			}
		} catch (IOException e) {
			throw new SiteWhereException("Unable to get stream data.", e);
		} finally {
			HBaseUtils.closeCleanly(streams);
		}
	}

	/**
	 * Get streams table based on context.
	 * 
	 * @param context
	 * @return
	 * @throws SiteWhereException
	 */
	protected static Table getStreamsTableInterface(IHBaseContext context) throws SiteWhereException {
		return context.getClient().getTableInterface(context.getTenant(), ISiteWhereHBase.STREAMS_TABLE_NAME);
	}
}