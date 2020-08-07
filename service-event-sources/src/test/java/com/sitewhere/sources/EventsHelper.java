/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources;

import java.math.BigDecimal;
import java.util.Date;

import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.rest.model.device.communication.DeviceRequest;
import com.sitewhere.rest.model.device.communication.DeviceRequest.Type;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceRegistrationRequest;
import com.sitewhere.spi.SiteWhereException;

/**
 * Helper class for generating encoded messages using the SiteWhere GPB format.
 */
public class EventsHelper {

    /**
     * Generate a JSON encoded measurements message for the given device.
     * 
     * @param deviceToken
     * @return
     * @throws SiteWhereException
     */
    public static byte[] generateJsonMeasurementsMessage(String deviceToken) throws SiteWhereException {
	return MarshalUtils.marshalJson(generateMeasurementsRequest(deviceToken));
    }

    /**
     * Generate a protobuf encoded device registration message for the given device.
     * 
     * @param deviceToken
     * @return
     * @throws SiteWhereException
     */
    public static byte[] generateJsonRegistrationMessage(String deviceToken) throws SiteWhereException {
	return MarshalUtils.marshalJson(generateRegistrationRequest(deviceToken));
    }

    /**
     * Generate a device request containing a device measurements create request.
     * 
     * @param deviceToken
     * @return
     * @throws SiteWhereException
     */
    public static DeviceRequest generateMeasurementsRequest(String deviceToken) throws SiteWhereException {
	DeviceRequest request = new DeviceRequest();
	request.setDeviceToken(deviceToken);
	request.setType(Type.DeviceMeasurement);

	DeviceMeasurementCreateRequest mx = new DeviceMeasurementCreateRequest();
	mx.setEventDate(new Date());
	mx.setName("fuel.level");
	mx.setValue(new BigDecimal("123.4"));
	request.setRequest(mx);
	return request;
    }

    /**
     * Generate a device request containing a device registration request.
     * 
     * @param deviceToken
     * @return
     * @throws SiteWhereException
     */
    public static DeviceRequest generateRegistrationRequest(String deviceToken) throws SiteWhereException {
	DeviceRequest request = new DeviceRequest();
	request.setDeviceToken(deviceToken);
	request.setType(Type.RegisterDevice);

	DeviceRegistrationRequest mx = new DeviceRegistrationRequest();
	mx.setDeviceTypeToken("mega2560");
	mx.setAreaToken("peachtree");
	request.setRequest(mx);
	return request;
    }
}
