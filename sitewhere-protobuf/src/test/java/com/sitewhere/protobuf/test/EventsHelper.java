/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.protobuf.test;

import java.util.Date;

import com.sitewhere.device.communication.protobuf.ProtobufDeviceEventEncoder;
import com.sitewhere.rest.model.device.communication.DecodedDeviceRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;

/**
 * Helper class for generating encoded messages using the SiteWhere GPB format.
 * 
 * @author Derek
 */
public class EventsHelper {

	/**
	 * Generate an encoded measurements message for the given hardware id.
	 * 
	 * @param hardwareId
	 * @return
	 * @throws SiteWhereException
	 */
	public static byte[] generateEncodedMeasurementsMessage(String hardwareId) throws SiteWhereException {
		DecodedDeviceRequest<IDeviceMeasurementsCreateRequest> request =
				new DecodedDeviceRequest<IDeviceMeasurementsCreateRequest>();
		request.setHardwareId(hardwareId);

		DeviceMeasurementsCreateRequest mx = new DeviceMeasurementsCreateRequest();
		mx.setEventDate(new Date());
		mx.addOrReplaceMeasurement("fuel.level", 123.4);
		request.setRequest(mx);

		return (new ProtobufDeviceEventEncoder()).encode(request);
	}
}
