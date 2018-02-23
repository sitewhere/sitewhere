/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources;

import java.util.Date;

import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceRegistrationRequest;
import com.sitewhere.sources.decoder.protobuf.ProtobufDeviceEventEncoder;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;

/**
 * Helper class for generating encoded messages using the SiteWhere GPB format.
 * 
 * @author Derek
 */
public class EventsHelper {

    /**
     * Generate an encoded measurements message for the given hardware id.
     * 
     * @param deviceToken
     * @return
     * @throws SiteWhereException
     */
    public static byte[] generateEncodedMeasurementsMessage(String deviceToken) throws SiteWhereException {
	DecodedDeviceRequest<IDeviceMeasurementsCreateRequest> request = new DecodedDeviceRequest<IDeviceMeasurementsCreateRequest>();
	request.setDeviceToken(deviceToken);

	DeviceMeasurementsCreateRequest mx = new DeviceMeasurementsCreateRequest();
	mx.setEventDate(new Date());
	mx.addOrReplaceMeasurement("fuel.level", 123.4);
	request.setRequest(mx);

	return (new ProtobufDeviceEventEncoder()).encode(request);
    }

    /**
     * Generate an encoded measurements message for the given hardware id.
     * 
     * @return
     * @throws SiteWhereException
     */
    public static byte[] generateEncodedRegistrationMessage() throws SiteWhereException {
	DecodedDeviceRequest<IDeviceRegistrationRequest> request = new DecodedDeviceRequest<IDeviceRegistrationRequest>();
	request.setDeviceToken("e5cd9ed7-f974-400f-bfa7-bf2df17211b2");

	DeviceRegistrationRequest mx = new DeviceRegistrationRequest();
	mx.setDeviceToken("e5cd9ed7-f974-400f-bfa7-bf2df17211b2");
	mx.setAreaToken("bb105f8d-3150-41f5-b9d1-db04965668d3");
	mx.setDeviceTypeToken("d2604433-e4eb-419b-97c7-88efe9b2cd41");
	mx.setEventDate(new Date());
	request.setRequest(mx);

	return (new ProtobufDeviceEventEncoder()).encode(request);
    }
}
