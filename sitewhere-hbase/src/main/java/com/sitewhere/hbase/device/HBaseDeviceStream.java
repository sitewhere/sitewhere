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

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.log4j.Logger;

import com.sitewhere.Tracer;
import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.common.Pager;
import com.sitewhere.hbase.encoder.PayloadMarshalerResolver;
import com.sitewhere.hbase.uid.IdManager;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.streaming.DeviceStream;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.debug.TracerCategory;

/**
 * HBase specifics for dealing with SiteWhere device streams.
 * 
 * @author Derek
 */
public class HBaseDeviceStream {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(HBaseDeviceStream.class);

	/**
	 * Create a new {@link IDeviceStream}.
	 * 
	 * @param context
	 * @param assignmentToken
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceStream createDeviceStream(IHBaseContext context, String assignmentToken,
			IDeviceStreamCreateRequest request) throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "createDeviceStream (HBase)", LOGGER);
		try {
			// Verify that the assignment token is valid.
			DeviceAssignment assignment = HBaseDeviceAssignment.getDeviceAssignment(context, assignmentToken);
			byte[] assnKey = IdManager.getInstance().getAssignmentKeys().getValue(assignmentToken);
			if (assignment == null) {
				throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
			}

			// Verify that the device stream does not exist.
			DeviceStream stream =
					HBaseDeviceStream.getDeviceStream(context, assignmentToken, request.getStreamId());
			if (stream != null) {
				throw new SiteWhereSystemException(ErrorCode.DuplicateStreamId, ErrorLevel.ERROR);
			}

			byte[] streamKey = getDeviceStreamKey(assnKey, request.getStreamId());

			DeviceStream newStream = SiteWherePersistence.deviceStreamCreateLogic(assignment, request);
			byte[] payload = context.getPayloadMarshaler().encode(newStream);

			HTableInterface sites = null;
			try {
				sites = context.getClient().getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
				Put put = new Put(streamKey);
				HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
				sites.put(put);
			} catch (IOException e) {
				throw new SiteWhereException("Unable to create device stream.", e);
			} finally {
				HBaseUtils.closeCleanly(sites);
			}

			return newStream;
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * Get a {@link DeviceStream} based on assignment and stream id.
	 * 
	 * @param context
	 * @param assignmentToken
	 * @param streamId
	 * @return
	 * @throws SiteWhereException
	 */
	public static DeviceStream getDeviceStream(IHBaseContext context, String assignmentToken, String streamId)
			throws SiteWhereException {
		byte[] assnKey = IdManager.getInstance().getAssignmentKeys().getValue(assignmentToken);
		if (assnKey == null) {
			return null;
		}
		byte[] streamKey = getDeviceStreamKey(assnKey, streamId);

		HTableInterface sites = null;
		try {
			sites = context.getClient().getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
			Get get = new Get(streamKey);
			HBaseUtils.addPayloadFields(get);
			Result result = sites.get(get);

			byte[] type = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
			byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
			if ((type == null) || (payload == null)) {
				return null;
			}

			return PayloadMarshalerResolver.getInstance().getMarshaler(type).decodeDeviceStream(payload);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to load device stream by token.", e);
		} finally {
			HBaseUtils.closeCleanly(sites);
		}
	}

	/**
	 * List all device streams for an assignment.
	 * 
	 * @param context
	 * @param assignmentToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static ISearchResults<IDeviceStream> listDeviceStreams(IHBaseContext context,
			String assignmentToken, ISearchCriteria criteria) throws SiteWhereException {
		byte[] assnKey = IdManager.getInstance().getAssignmentKeys().getValue(assignmentToken);
		if (assnKey == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
		}

		HTableInterface sites = null;
		ResultScanner scanner = null;
		try {
			sites = context.getClient().getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
			Scan scan = new Scan();
			scan.setStartRow(HBaseDeviceAssignment.getStreamRowkey(assnKey));
			scan.setStopRow(HBaseDeviceAssignment.getEndMarkerKey(assnKey));
			scanner = sites.getScanner(scan);

			Pager<IDeviceStream> pager = new Pager<IDeviceStream>(criteria);
			for (Result result : scanner) {
				byte[] payloadType = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
				byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);

				if ((payloadType != null) && (payload != null)) {
					pager.process((IDeviceStream) PayloadMarshalerResolver.getInstance().getMarshaler(
							payloadType).decodeDeviceStream(payload));
				}
			}
			return new SearchResults<IDeviceStream>(pager.getResults(), pager.getTotal());
		} catch (IOException e) {
			throw new SiteWhereException("Error scanning device stream rows.", e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
			HBaseUtils.closeCleanly(sites);
		}
	}

	/**
	 * Get a
	 * 
	 * @param assnKey
	 * @param streamId
	 * @return
	 */
	public static byte[] getDeviceStreamKey(byte[] assnKey, String streamId) {
		byte[] streamBase = HBaseDeviceAssignment.getStreamRowkey(assnKey);
		byte[] streamIdBytes = streamId.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(streamBase.length + streamIdBytes.length);
		buffer.put(streamBase);
		buffer.put(streamIdBytes);
		return buffer.array();
	}
}