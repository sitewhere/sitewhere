/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.test;

import java.util.Date;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;

import com.sitewhere.rest.client.SiteWhereClient;
import com.sitewhere.rest.model.device.request.DeviceStreamCreateRequest;
import com.sitewhere.rest.model.search.DateRangeSearchCriteria;
import com.sitewhere.spi.SiteWhereException;

/**
 * Tests related to device streams.
 * 
 * @author Derek
 */
public class StreamTests {

	/** Device assignment token */
	public static final String ASSN_TOKEN = "6b8d692f-e50e-4beb-85bf-c196ac3b0913";

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	@Test
	public void testCreateStreamAndReadWrite() throws SiteWhereException {
		SiteWhereClient client = new SiteWhereClient();

		String streamId = UUID.randomUUID().toString();
		DeviceStreamCreateRequest screate = new DeviceStreamCreateRequest();
		screate.setStreamId(streamId);
		screate.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		screate.setEventDate(new Date());
		client.createDeviceStream(ASSN_TOKEN, screate);

		byte[] chunk1 = "This is the first chunk of data.".getBytes();
		byte[] chunk2 = "This is the second chunk of data.".getBytes();
		byte[] chunk3 = "This is the third chunk of data.".getBytes();

		int sequenceNumber = 0;
		client.addDeviceStreamData(ASSN_TOKEN, streamId, sequenceNumber++, chunk1);
		client.addDeviceStreamData(ASSN_TOKEN, streamId, sequenceNumber++, chunk2);
		client.addDeviceStreamData(ASSN_TOKEN, streamId, sequenceNumber++, chunk3);

		sequenceNumber = 0;
		byte[] result1 = client.getDeviceStreamData(ASSN_TOKEN, streamId, sequenceNumber++);
		byte[] result2 = client.getDeviceStreamData(ASSN_TOKEN, streamId, sequenceNumber++);
		byte[] result3 = client.getDeviceStreamData(ASSN_TOKEN, streamId, sequenceNumber++);

		Assert.assertArrayEquals(chunk1, result1);
		Assert.assertArrayEquals(chunk2, result2);
		Assert.assertArrayEquals(chunk3, result3);

		byte[] result4 = client.getDeviceStreamData(ASSN_TOKEN, streamId, sequenceNumber++);
		Assert.assertNull(result4);

		// Required since there is buffering in the HBase implementation.
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
		}

		DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(1, 0, null, null);
		byte[] all = client.listDeviceStreamData(ASSN_TOKEN, streamId, criteria);
		System.out.println(new String(all));
	}

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
}