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

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.junit.Test;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceCommandResponseCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceRegistrationRequest;

public class CoapTests {

    /** Supplies CoAP server location */
    private static final String COAP_SERVER = "192.168.171.129";

    /** Supplies standard CoAP port */
    private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);

    @Test
    public void testRegisterDevice() {
	CoapClient client = createClientFor("devices");
	DeviceRegistrationRequest registration = new DeviceRegistrationRequest();
	registration.setDeviceToken("111-COAP-TEST-444");
	registration.setDeviceTypeToken("da05f689-2056-4786-ac9f-4f25b406369a");
	registration.setAreaToken("bb105f8d-3150-41f5-b9d1-db04965668d3");
	Map<String, String> metadata = new HashMap<String, String>();
	metadata.put("ipaddress", "localhost");
	registration.setMetadata(metadata);
	CoapResponse response = client.post(MarshalUtils.marshalJson(registration), MediaTypeRegistry.APPLICATION_JSON);
	System.out.println(response.getCode());
	System.out.println(response.getOptions());
	System.out.println(response.getResponseText());
    }

    @Test
    public void testRegisterDeviceSimple() {
	CoapClient client = createClientFor("devices");
	String payload = "hwid=111-COAP-TEST-555,spec=da05f689-2056-4786-ac9f-4f25b406369a,site=bb105f8d-3150-41f5-b9d1-db04965668d3";
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
	CoapResponse response = client.post(MarshalUtils.marshalJson(mxs), MediaTypeRegistry.APPLICATION_JSON);
	System.out.println(response.getCode());
	System.out.println(response.getOptions());
	System.out.println(response.getResponseText());
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
	CoapResponse response = client.post(MarshalUtils.marshalJson(alert), MediaTypeRegistry.APPLICATION_JSON);
	System.out.println(response.getCode());
	System.out.println(response.getOptions());
	System.out.println(response.getResponseText());
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
	CoapResponse response = client.post(MarshalUtils.marshalJson(location), MediaTypeRegistry.APPLICATION_JSON);
	System.out.println(response.getCode());
	System.out.println(response.getOptions());
	System.out.println(response.getResponseText());
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

    @Test
    public void testAddAcknowledgement() {
	CoapClient client = createClientFor("devices/82aed708-f49f-4d19-a58f-5668339595d9/acks");
	DeviceCommandResponseCreateRequest ack = new DeviceCommandResponseCreateRequest();
	ack.setOriginatingEventId(UUID.randomUUID());
	ack.setResponse("Arbitrary string containing response content.");
	ack.setMetadata(new HashMap<String, String>());
	ack.getMetadata().put("meta1", "value");
	CoapResponse response = client.post(MarshalUtils.marshalJson(ack), MediaTypeRegistry.APPLICATION_JSON);
	System.out.println(response.getCode());
	System.out.println(response.getOptions());
	System.out.println(response.getResponseText());
    }

    @Test
    public void sendSilecticaRules() {
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
    protected void sendSilecticaRuleAck(String rule) {
	CoapClient client = createClientFor("devices/00173B1200210024/acks");
	DeviceCommandResponseCreateRequest ack = new DeviceCommandResponseCreateRequest();
	ack.setOriginatingEventId(UUID.randomUUID());
	ack.setResponse(rule);
	CoapResponse response = client.post(MarshalUtils.marshalJson(ack), MediaTypeRegistry.APPLICATION_JSON);
	System.out.println(response.getCode());
	System.out.println(response.getOptions());
	System.out.println(response.getResponseText());
    }

    /** Send Silectica rule as an ack (payload only version) */
    @Test
    public void sendSilecticaRuleAckPayloadOnly() {
	CoapClient client = createClientFor("devices/00173B1200210024/acks");
	String rule = "{\"rule\":{\"r\":0,\"n\":\"netup\",\"v\":\"Networking Up\",\"s\":\"dis\",\"rc\":0,\"c\":[{\"n\":\"s/sys/net/ucup\",\"v\":1,\"r\":\"eq\",\"l\":\"or\",\"dt\":1}]},\"ruleQty\":6,\"OEId\":\"manualtest\"}";
	try {
	    CoapResponse response = client.post(rule.getBytes(), MediaTypeRegistry.APPLICATION_JSON);
	    System.out.println(response.getCode());
	    System.out.println(response.getOptions());
	    System.out.println(response.getResponseText());
	    Thread.sleep(100);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

    @Test
    public void testAddAcknowledgementSimple() {
	CoapClient client = createClientFor("devices/82aed708-f49f-4d19-a58f-5668339595d9/acks");
	String payload = "orig=1234567890,response=Arbitrary string containing response content.,m:meta1=value";
	CoapResponse response = client.post(payload, MediaTypeRegistry.APPLICATION_JSON);
	System.out.println(response.getCode());
	System.out.println(response.getOptions());
	System.out.println(response.getResponseText());
    }

    protected CoapClient createClientFor(String relativeUrl) {
	return new CoapClient("coap://" + COAP_SERVER + ":" + COAP_PORT + "/" + relativeUrl);
    }
}