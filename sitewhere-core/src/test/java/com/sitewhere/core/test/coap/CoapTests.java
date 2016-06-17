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
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceRegistrationRequest;
import com.sitewhere.spi.SiteWhereException;

public class CoapTests {

	/** Supplies standard CoAP port */
	private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);

	@Test
	public void testRegisterDevice() {
		CoapClient client = createClientFor("default/devices");
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
	public void testAddDeviceMeasurements() {
		CoapClient client = createClientFor("default/devices/111-COAP-TEST-444/measurements");
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

	protected CoapClient createClientFor(String relativeUrl) {
		return new CoapClient("coap://localhost:" + COAP_PORT + "/" + relativeUrl);
	}
}