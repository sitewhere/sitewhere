/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.communication.test.coap;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.eclipse.californium.core.CaliforniumLogger;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.junit.Before;
import org.junit.Test;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceCommandResponseCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceRegistrationRequest;

public class CoapTests {

    /** Supplies CoAP server location */
    private static final String COAP_SERVER = "192.168.171.129";

    /** Supplies standard CoAP port */
    private static final int COAP_PORT = 8583;

    /** Device token used for operations */
    private static final String DEVICE_TOKEN = "1123902-TEST-234873";

    @Before
    public void setup() {
	CaliforniumLogger.initialize();
	CaliforniumLogger.setLevel(Level.FINE);
    }

    @Test
    public void testRegisterDevice() throws Exception {
	CoapClient client = createClientFor(getBaseDeviceUrl());
	DeviceRegistrationRequest registration = new DeviceRegistrationRequest();
	registration.setDeviceTypeToken("mega2560");
	registration.setAreaToken("peachtree");
	Map<String, String> metadata = new HashMap<String, String>();
	metadata.put("ipaddress", "localhost");
	registration.setMetadata(metadata);
	handleResponse(client.post(MarshalUtils.marshalJson(registration), MediaTypeRegistry.APPLICATION_JSON));
    }

    @Test
    public void testAddDeviceMeasurements() throws Exception {
	CoapClient client = createClientFor(getBaseDeviceUrl() + "/measurements");
	DeviceMeasurementCreateRequest mx = new DeviceMeasurementCreateRequest();
	mx.setName("pwr");
	mx.setValue(38.23);
	mx.setEventDate(new Date());
	handleResponse(client.post(MarshalUtils.marshalJson(mx), MediaTypeRegistry.APPLICATION_JSON));
    }

    @Test
    public void testAddDeviceAlert() throws Exception {
	CoapClient client = createClientFor(getBaseDeviceUrl() + "/alerts");
	DeviceAlertCreateRequest alert = new DeviceAlertCreateRequest();
	alert.setType("alert.test");
	alert.setMessage("Danger! Danger!");
	handleResponse(client.post(MarshalUtils.marshalJson(alert), MediaTypeRegistry.APPLICATION_JSON));
    }

    @Test
    public void testAddDeviceLocation() throws Exception {
	CoapClient client = createClientFor(getBaseDeviceUrl() + "/locations");
	DeviceLocationCreateRequest location = new DeviceLocationCreateRequest();
	location.setLatitude(33.7490);
	location.setLongitude(-84.3880);
	location.setElevation(0.0);
	handleResponse(client.post(MarshalUtils.marshalJson(location), MediaTypeRegistry.APPLICATION_JSON));
    }

    @Test
    public void testAddAcknowledgement() throws Exception {
	CoapClient client = createClientFor(getBaseDeviceUrl() + "/acks");
	DeviceCommandResponseCreateRequest ack = new DeviceCommandResponseCreateRequest();
	ack.setOriginatingEventId(UUID.randomUUID());
	ack.setResponse("Arbitrary string containing response content.");
	ack.setMetadata(new HashMap<String, String>());
	ack.getMetadata().put("meta1", "value");
	handleResponse(client.post(MarshalUtils.marshalJson(ack), MediaTypeRegistry.APPLICATION_JSON));
    }

    @Test
    public void sendSilecticaRules() throws Exception {
	sendSilecticaRuleAck(
		"{\"rule\":{\"r\":0,\"n\":\"netup\",\"v\":\"Networking Up\",\"s\":\"dis\",\"rc\":0,\"c\":[{\"n\":\"s/sys/net/ucup\",\"v\":1,\"r\":\"eq\",\"l\":\"or\",\"dt\":1}]},\"ruleQty\":6,\"OEId\":\"manualtest\"}");
	sendSilecticaRuleAck(
		"{\"rule\":{\"r\":1,\"n\":\"nflsh0\",\"v\":\"Net Up Flash Rly 0\",\"s\":\"dis\",\"rc\":0,\"c\":[{\"n\":\"evt/netup\",\"v\":0,\"r\":\"eq\",\"l\":\"or\",\"dt\":0},{\"n\":\"a/light/fix/rly\",\"v\":0,\"r\":\"eq\",\"l\":\"or\",\"dt\":0}],\"a\":[{\"n\":\"a/light/fix/rly\",\"v\":1,\"hg\":\"loop0\",\"dt\":2},{\"n\":\"a/light/fix/rly\",\"v\":0,\"hg\":\"loop0\",\"dt\":0},{\"n\":\"evt/nflsh1\",\"v\":\"dis\",\"hg\":\"loop0\",\"dt\":0},{\"n\":\"evt/nflsh0\",\"v\":\"dis\",\"hg\":\"loop0\",\"dt\":0}]},\"ruleQty\":6,\"OEId\":\"manualtest\"}");
	sendSilecticaRuleAck(
		"{\"rule\":{\"r\":2,\"n\":\"nflsh1\",\"v\":\"Net Up Flash Rly 1\",\"s\":\"dis\",\"rc\":0,\"c\":[{\"n\":\"evt/netup\",\"v\":0,\"r\":\"eq\",\"l\":\"or\",\"dt\":0},{\"n\":\"a/light/fix/rly\",\"v\":1,\"r\":\"eq\",\"l\":\"or\",\"dt\":0}],\"a\":[{\"n\":\"a/light/fix/rly\",\"v\":0,\"hg\":\"loop0\",\"dt\":2},{\"n\":\"a/light/fix/rly\",\"v\":1,\"hg\":\"loop0\",\"dt\":0},{\"n\":\"evt/nflsh0\",\"v\":\"dis\",\"hg\":\"loop0\",\"dt\":0},{\"n\":\"evt/nflsh1\",\"v\":\"dis\",\"hg\":\"loop0\",\"dt\":0}]},\"ruleQty\":6,\"OEId\":\"manualtest\"}");
	sendSilecticaRuleAck(
		"{\"rule\":{\"r\":3,\"n\":\"swreg\",\"v\":\"SiteWhere Registration\",\"s\":\"dis\",\"rc\":0,\"c\":[{\"n\":\"s/sys/net/ucup\",\"v\":1,\"r\":\"eq\",\"l\":\"or\",\"dt\":0}],\"a\":[{\"n\":\"s/app/swreg/msg\",\"v\":{\"spec\":\"23b3b92e-1b8d-44f6-8d8d-7b79783ad90b\"},\"hg\":\"loop0\",\"dt\":0},{\"n\":\"s/app/swreg/msg\",\"v\":{\"site\":\"idf-2016-1\"},\"hg\":\"loop0\",\"dt\":0},{\"n\":\"s/app/swreg/msg\",\"v\":{},\"hg\":\"swapp\",\"dt\":0},{\"n\":\"evt/swreg\",\"v\":\"dis\",\"hg\":\"loop0\",\"dt\":0}]},\"ruleQty\":6,\"OEId\":\"manualtest\"}");
	sendSilecticaRuleAck(
		"{\"rule\":{\"r\":4,\"n\":\"swlog\",\"v\":\"SiteWhere Datalogger\",\"s\":\"dis\",\"rc\":0,\"c\":[{\"n\":\"s/sys/net/ucup\",\"v\":1,\"r\":\"eq\",\"l\":\"or\",\"dt\":0}],\"a\":[{\"n\":\"s/app/swlog/msg\",\"v\":[\"s/light/fix/dim\",\"s/ener/fix/pwr\"],\"hg\":\"swapp\",\"dt\":10},{\"n\":\"s/app/swlog/msg\",\"v\":[\"a/light/fix/rly\",\"s/ener/fix/vln\"],\"hg\":\"swapp\",\"dt\":10},{\"n\":\"s/app/swlog/msg\",\"v\":[\"s/temp/mod/ta\",\"s/temp/fix/ta\"],\"hg\":\"swapp\",\"dt\":10},{\"n\":\"s/app/swlog/msg\",\"v\":\"s/ener/fix/fln\",\"hg\":\"swapp\",\"dt\":10}]},\"ruleQty\":6,\"OEId\":\"manualtest\"}");
	sendSilecticaRuleAck(
		"{\"rule\":{\"r\":5,\"n\":\"swrules\",\"v\":\"SiteWhere Ack w/ SN Rul\",\"s\":\"act\",\"rc\":0,\"c\":[{\"n\":\"s/sys/net/ucup\",\"v\":1,\"r\":\"eq\",\"l\":\"an\",\"dt\":0},{\"n\":\"s/app/swrule/send\",\"v\":1,\"r\":\"eq\",\"l\":\"or\",\"dt\":1}],\"a\":[{\"n\":\"s/app/swrule/msg\",\"v\":{},\"hg\":\"swapp\",\"dt\":1}]},\"ruleQty\":6,\"OEId\":\"manualtest\"}");
    }

    /** Send Silectica rule as an ack */
    protected void sendSilecticaRuleAck(String rule) throws Exception {
	CoapClient client = createClientFor(getBaseDeviceUrl() + "/acks");
	DeviceCommandResponseCreateRequest ack = new DeviceCommandResponseCreateRequest();
	ack.setOriginatingEventId(UUID.randomUUID());
	ack.setResponse(rule);
	handleResponse(client.post(MarshalUtils.marshalJson(ack), MediaTypeRegistry.APPLICATION_JSON));
    }

    /** Send Silectica rule as an ack (payload only version) */
    @Test
    public void sendSilecticaRuleAckPayloadOnly() throws Exception {
	CoapClient client = createClientFor("devices/00173B1200210024/acks");
	String rule = "{\"rule\":{\"r\":0,\"n\":\"netup\",\"v\":\"Networking Up\",\"s\":\"dis\",\"rc\":0,\"c\":[{\"n\":\"s/sys/net/ucup\",\"v\":1,\"r\":\"eq\",\"l\":\"or\",\"dt\":1}]},\"ruleQty\":6,\"OEId\":\"manualtest\"}";
	handleResponse(client.post(rule.getBytes(), MediaTypeRegistry.APPLICATION_JSON));
    }

    /**
     * Get base URL for interacting with device.
     * 
     * @return
     */
    private static String getBaseDeviceUrl() {
	return "devices/" + DEVICE_TOKEN;
    }

    /**
     * Create a CoAP client for the given URL.
     * 
     * @param relativeUrl
     * @return
     */
    private static CoapClient createClientFor(String relativeUrl) {
	return new CoapClient("coap://" + COAP_SERVER + ":" + COAP_PORT + "/" + relativeUrl);
    }

    /**
     * Handle a CoAP response.
     * 
     * @param response
     * @throws Exception
     */
    private static void handleResponse(CoapResponse response) throws Exception {
	if (response != null) {
	    System.out.println(response.getCode());
	    System.out.println(response.getOptions());
	    System.out.println(response.getResponseText());
	} else {
	    throw new Exception("Response was null.");
	}
    }
}