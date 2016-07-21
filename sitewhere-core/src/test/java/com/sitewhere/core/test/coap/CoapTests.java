/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.core.test.coap;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.junit.Test;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceRegistrationRequest;
import com.sitewhere.spi.SiteWhereException;

public class CoapTests {

	/** Supplies standard CoAP port */
	private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);

	@Test
	public void testRegisterDevice() {
		CoapClient client = createClientFor("devices");
		DeviceRegistrationRequest registration = new DeviceRegistrationRequest();
		registration.setHardwareId("111-COAP-TEST-444");
		registration.setSpecificationToken("da05f689-2056-4786-ac9f-4f25b406369a");
		registration.setSiteToken("bb105f8d-3150-41f5-b9d1-db04965668d3");
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("ipaddress", "localhost");
		registration.setMetadata(metadata);
		try {
			CoapResponse response =
					client.post(MarshalUtils.marshalJson(registration), MediaTypeRegistry.APPLICATION_JSON);
			System.out.println(response.getCode());
			System.out.println(response.getOptions());
			System.out.println(response.getResponseText());
		} catch (SiteWhereException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRegisterDeviceSimple() {
		CoapClient client = createClientFor("devices");
		String payload =
				"hwid=111-COAP-TEST-555,spec=da05f689-2056-4786-ac9f-4f25b406369a,site=bb105f8d-3150-41f5-b9d1-db04965668d3";
		CoapResponse response = client.post(payload, MediaTypeRegistry.APPLICATION_JSON);
		System.out.println(response.getCode());
		System.out.println(response.getOptions());
		System.out.println(response.getResponseText());
	}

	@Test
	public void testAddDeviceMeasurements() {
		CoapClient client = createClientFor("devices/111-COAP-TEST-444/measurements");
		DeviceMeasurementsCreateRequest mxs = new DeviceMeasurementsCreateRequest();
		mxs.addOrReplaceMeasurement("pwr", 38.23);
		mxs.addOrReplaceMeasurement("fln", 59.95);
		mxs.setEventDate(new Date());
		try {
			CoapResponse response =
					client.post(MarshalUtils.marshalJson(mxs), MediaTypeRegistry.APPLICATION_JSON);
			System.out.println(response.getCode());
			System.out.println(response.getOptions());
			System.out.println(response.getResponseText());
		} catch (SiteWhereException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAddDeviceMeasurementsSimple() {
		CoapClient client = createClientFor("devices/111-COAP-TEST-555/measurements");
		String payload = "pwr=38.23,fln=59.95";
		CoapResponse response = client.post(payload, MediaTypeRegistry.APPLICATION_JSON);
		System.out.println(response.getCode());
		System.out.println(response.getOptions());
		System.out.println(response.getResponseText());
	}

	@Test
	public void testAddDeviceAlert() {
		CoapClient client = createClientFor("devices/111-COAP-TEST-444/alerts");
		DeviceAlertCreateRequest alert = new DeviceAlertCreateRequest();
		alert.setType("alert.test");
		alert.setMessage("Danger! Danger!");
		try {
			CoapResponse response =
					client.post(MarshalUtils.marshalJson(alert), MediaTypeRegistry.APPLICATION_JSON);
			System.out.println(response.getCode());
			System.out.println(response.getOptions());
			System.out.println(response.getResponseText());
		} catch (SiteWhereException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAddDeviceAlertSimple() {
		CoapClient client = createClientFor("devices/111-COAP-TEST-555/alerts");
		String payload = "type=alert.test,message=Danger! Danger!";
		CoapResponse response = client.post(payload, MediaTypeRegistry.APPLICATION_JSON);
		System.out.println(response.getCode());
		System.out.println(response.getOptions());
		System.out.println(response.getResponseText());
	}

	@Test
	public void testAddDeviceLocation() {
		CoapClient client = createClientFor("devices/111-COAP-TEST-444/locations");
		DeviceLocationCreateRequest location = new DeviceLocationCreateRequest();
		location.setLatitude(33.7490);
		location.setLongitude(-84.3880);
		location.setElevation(0.0);
		try {
			CoapResponse response =
					client.post(MarshalUtils.marshalJson(location), MediaTypeRegistry.APPLICATION_JSON);
			System.out.println(response.getCode());
			System.out.println(response.getOptions());
			System.out.println(response.getResponseText());
		} catch (SiteWhereException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAddDeviceLocationSimple() {
		CoapClient client = createClientFor("devices/111-COAP-TEST-555/locations");
		String payload = "lat=33.7490,lon=-84.3880,ele=0.0";
		CoapResponse response = client.post(payload, MediaTypeRegistry.APPLICATION_JSON);
		System.out.println(response.getCode());
		System.out.println(response.getOptions());
		System.out.println(response.getResponseText());
	}

	protected CoapClient createClientFor(String relativeUrl) {
		return new CoapClient("coap://localhost:" + COAP_PORT + "/" + relativeUrl);
	}
}