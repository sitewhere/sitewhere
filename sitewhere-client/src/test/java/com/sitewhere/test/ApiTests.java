/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sitewhere.rest.client.SiteWhereClient;
import com.sitewhere.rest.model.asset.AssetReference;
import com.sitewhere.rest.model.common.Location;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.rest.model.device.batch.BatchOperation;
import com.sitewhere.rest.model.device.event.DeviceEventBatch;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceCommandInvocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.rest.model.device.request.DeviceAssignmentCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupElementCreateRequest;
import com.sitewhere.rest.model.device.request.ZoneCreateRequest;
import com.sitewhere.rest.model.device.streaming.DeviceStream;
import com.sitewhere.rest.model.search.AssetSearchResults;
import com.sitewhere.rest.model.search.DateRangeSearchCriteria;
import com.sitewhere.rest.model.search.DeviceAssignmentSearchResults;
import com.sitewhere.rest.model.search.DeviceCommandSearchResults;
import com.sitewhere.rest.model.search.DeviceGroupElementSearchResults;
import com.sitewhere.rest.model.search.DeviceGroupSearchResults;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.system.Version;
import com.sitewhere.spi.ISiteWhereClient;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.AlertSource;
import com.sitewhere.spi.device.event.CommandInitiator;
import com.sitewhere.spi.device.event.CommandTarget;
import com.sitewhere.spi.device.group.GroupElementType;
import com.sitewhere.spi.error.ErrorCode;

/**
 * Test cases for client API calls.
 * 
 * @author dadams
 */
public class ApiTests {

    /** Device specification id used in tests */
    public static final String TEST_SPECIFICATION_TOKEN = "293749827342243827349";

    /** Hardware id used for test cases */
    public static final String TEST_HARDWARE_ID = "12356789-TEST-123";

    /** Asset id for testing */
    public static final String TEST_ASSET_ID = "174";

    /** Site token used in tests */
    public static final String TEST_SITE_TOKEN = "22223793-3028-4114-86ba-aefc7d05369f";

    /** Android device specification token */
    public static final String ANDROID_SPEC_TOKEN = "d2604433-e4eb-419b-97c7-88efe9b2cd41";

    /** SiteWhere client */
    private ISiteWhereClient client;

    @Before
    public void setup() {
	this.client = new SiteWhereClient();
    }

    @Test
    public void testConnectivity() throws SiteWhereException {
	SiteWhereClient client = new SiteWhereClient();
	Version version = client.getSiteWhereVersion();
	System.out.println("SiteWhere version is " + version.getVersionIdentifier() + ".");
    }

    @Test
    public void testDeviceCRUD() throws SiteWhereException {
	// Delete device if it already exists.
	try {
	    client.getDeviceByHardwareId(TEST_HARDWARE_ID);
	    client.deleteDevice(TEST_HARDWARE_ID, true);
	} catch (SiteWhereException e) {
	    // Ignore missing device since we wanted it deleted.
	}

	// Test initial create.
	DeviceCreateRequest request = new DeviceCreateRequest();
	request.setHardwareId(TEST_HARDWARE_ID);
	request.setSpecificationToken(TEST_SPECIFICATION_TOKEN);
	request.setComments("This is a test device.");
	Map<String, String> metadata = new HashMap<String, String>();
	metadata.put("name1", "value1");
	metadata.put("name2", "value2");
	request.setMetadata(metadata);
	Device device = client.createDevice(request);
	Assert.assertNotNull("Device create returned null.", device);
	Assert.assertEquals("Metadata not stored properly.", 2, device.getMetadata().size());

	// Test get by hardware id.
	try {
	    device = client.getDeviceByHardwareId(TEST_HARDWARE_ID);
	} catch (SiteWhereException e) {
	    Assert.fail("Device should exist, but not found by handware id.");
	}

	// Test update.
	DeviceCreateRequest update = new DeviceCreateRequest();
	update.setComments("Updated.");
	metadata = new HashMap<String, String>();
	metadata.put("name1", "value1");
	update.setMetadata(metadata);
	device = client.updateDevice(TEST_HARDWARE_ID, update);
	Assert.assertEquals("Updated.", device.getComments());
	Assert.assertEquals("Metadata not updated properly.", 1, device.getMetadata().size());
	Assert.assertNotNull("Updated date not set.", device.getUpdatedDate());

	// Should not allow hardware id to be updated.
	try {
	    update = new DeviceCreateRequest();
	    update.setHardwareId("xxx");
	    client.updateDevice(TEST_HARDWARE_ID, update);
	    Assert.fail("Device update allowed update of hardware id.");
	} catch (SiteWhereSystemException e) {
	    verifyErrorCode(e, ErrorCode.DeviceHardwareIdCanNotBeChanged);
	}

	// Test duplicate.
	try {
	    device = client.createDevice(request);
	    Assert.fail("Create device allowed duplicate.");
	} catch (SiteWhereException e) {
	    verifyErrorCode(e, ErrorCode.DuplicateHardwareId);
	}

	// Create a device assignment.
	DeviceAssignmentCreateRequest assnRequest = new DeviceAssignmentCreateRequest();
	assnRequest.setAssignmentType(DeviceAssignmentType.Associated);
	assnRequest.setAssetReference(new AssetReference.Builder("testAssetModuleId", TEST_ASSET_ID).build());
	assnRequest.setDeviceHardwareId(device.getHardwareId());
	metadata = new HashMap<String, String>();
	metadata.put("name1", "value1");
	metadata.put("name2", "value2");
	assnRequest.setMetadata(metadata);
	DeviceAssignment assignment = client.createDeviceAssignment(assnRequest);
	Assert.assertNotNull("Assignment token was null.", assignment.getToken());
	Assert.assertEquals("Assignment metadata count incorrect.", 2, assignment.getMetadata().size());

	// Test get assignment by token.
	assignment = client.getDeviceAssignmentByToken(assignment.getToken());
	Assert.assertNotNull("Assignment by token returned null.", assignment);

	// Test getting current assignment for a device.
	DeviceAssignment currAssignment = client.getCurrentAssignmentForDevice(TEST_HARDWARE_ID);
	Assert.assertEquals("Current device assignment is incorrect.", assignment.getToken(),
		currAssignment.getToken());

	// Verify that an assignment can not be created for a device if one is
	// already
	// assigned.
	try {
	    assignment = client.createDeviceAssignment(assnRequest);
	} catch (SiteWhereException e) {
	    verifyErrorCode(e, ErrorCode.DeviceAlreadyAssigned);
	}

	// Delete device.
	device = client.deleteDevice(TEST_HARDWARE_ID, true);
	Assert.assertNotNull(device);
    }

    @Test
    public void testDeviceEventBatch() throws SiteWhereException {
	SiteWhereClient client = new SiteWhereClient("http://localhost:9090/sitewhere/api/", "admin", "password");
	DeviceEventBatch batch = new DeviceEventBatch();
	batch.setHardwareId("5a95f3f2-96f0-47f9-b98d-f5c081d01948");
	DeviceMeasurementsCreateRequest mx = new DeviceMeasurementsCreateRequest();
	mx.setEventDate(new Date());
	mx.addOrReplaceMeasurement("test", 123.4);
	Map<String, String> metadata = new HashMap<String, String>();
	metadata.put("test", "value");
	mx.setMetadata(metadata);
	batch.getMeasurements().add(mx);
	client.addDeviceEventBatch("5a95f3f2-96f0-47f9-b98d-f5c081d01948", batch);
    }

    @Test
    public void testGetAssignmentsForAsset() throws SiteWhereException {
	SiteWhereClient client = new SiteWhereClient();
	DeviceAssignmentSearchResults results = client.getAssignmentsForAsset("bb105f8d-3150-41f5-b9d1-db04965668d3",
		"fs-persons", "1", DeviceAssignmentStatus.Active, new SearchCriteria(1, 0));
	System.out.println("Found " + results.getNumResults() + " assignments for asset.");
    }

    @Test
    public void testListAssets() throws SiteWhereException {
	SiteWhereClient client = new SiteWhereClient();
	AssetSearchResults results = client.getAssetsByModuleId("fs-persons", "d");
	System.out.println("Found " + results.getNumResults() + " assets matching the criteria.");
    }

    @Test
    public void testCreateZone() throws SiteWhereException {
	SiteWhereClient client = new SiteWhereClient();
	ZoneCreateRequest request = new ZoneCreateRequest();
	request.setName("My Test Zone");
	List<Location> coords = new ArrayList<Location>();
	coords.add(new Location(30.0, -85.0));
	coords.add(new Location(30.0, -90.0));
	coords.add(new Location(35.0, -90.0));
	coords.add(new Location(35.0, -85.0));
	request.setCoordinates(coords);
	Zone results = client.createZone(TEST_SITE_TOKEN, request);
	System.out.println("Created zone: " + results.getName());
	SearchResults<Zone> search = client.listZonesForSite(TEST_SITE_TOKEN);
	System.out.println("Found " + search.getNumResults() + " results.");
    }

    @Test
    public void testListDevices() throws SiteWhereException {
	SiteWhereClient client = new SiteWhereClient();
	Calendar cal = Calendar.getInstance();
	cal.roll(Calendar.MONTH, -1);
	Date monthAgo = cal.getTime();
	DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(1, 100, monthAgo, new Date());
	SearchResults<Device> devices = client.listDevices(false, false, true, true, criteria);
	System.out.println("Found " + devices.getNumResults() + " devices.");
    }

    @Test
    public void testCreateEvents() throws SiteWhereException {
	SiteWhereClient client = new SiteWhereClient();
	String assignment = "483e6ca1-026c-49c4-960b-402ced283f8b";

	DeviceLocationCreateRequest location = new DeviceLocationCreateRequest();
	location.setLatitude(33.7490);
	location.setLongitude(-84.3880);
	location.setElevation(0.0);
	json(client.createDeviceLocation(assignment, location));

	DeviceMeasurementsCreateRequest mxs = new DeviceMeasurementsCreateRequest();
	mxs.addOrReplaceMeasurement("fuel.level", 77.0);
	json(client.createDeviceMeasurements(assignment, mxs));

	DeviceAlertCreateRequest alert = new DeviceAlertCreateRequest();
	alert.setType("engine.overheat");
	alert.setLevel(AlertLevel.Error);
	alert.setSource(AlertSource.Device);
	alert.setMessage("Engine is about to overheat.");
	json(client.createDeviceAlert(assignment, alert));

	DeviceCommandInvocationCreateRequest command = new DeviceCommandInvocationCreateRequest();
	command.setInitiator(CommandInitiator.REST);
	command.setInitiatorId("admin");
	command.setCommandToken("ddf46ea2-bd91-4e40-a62d-18808b3827dc");
	command.getParameterValues().put("color", "#ff0000");
	command.setTarget(CommandTarget.Assignment);
	command.setTargetId("483e6ca1-026c-49c4-960b-402ced283f8b");
	json(client.createDeviceCommandInvocation(assignment, command));

	DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(1, 1, null, null);
	json(client.listDeviceLocations(assignment, criteria));
	json(client.listDeviceMeasurements(assignment, criteria));
	json(client.listDeviceAlerts(assignment, criteria));
	json(client.listDeviceCommandInvocations(assignment, criteria));
    }

    /**
     * Write object as JSON.
     * 
     * @param object
     * @throws SiteWhereException
     */
    public static void json(Object object) throws SiteWhereException {
	try {
	    ObjectMapper mapper = new ObjectMapper();
	    mapper.enable(SerializationFeature.INDENT_OUTPUT);
	    System.out.println(mapper.writeValueAsString(object));
	} catch (JsonProcessingException e) {
	    throw new SiteWhereException("Could not marshal object as JSON: " + object.getClass().getName(), e);
	}
    }

    @Test
    public void testAddStreamData() throws SiteWhereException {
	SiteWhereClient client = new SiteWhereClient();
	DeviceStream stream = client.getDeviceStream("870c60a8-3ae4-41f5-8004-dfa2fa900b97", "voice-channel");
	Assert.assertNotNull(stream);
	client.addDeviceStreamData("870c60a8-3ae4-41f5-8004-dfa2fa900b97", "voice-channel", 1,
		"This is a chunk of data written to the device stream".getBytes());
    }

    @Test
    public void sendBatchCommandInvocation() throws SiteWhereException {
	SiteWhereClient client = new SiteWhereClient();
	List<Device> androids = getDevicesForSpecification(ANDROID_SPEC_TOKEN);
	List<String> hwids = new ArrayList<String>();
	for (Device device : androids) {
	    hwids.add(device.getHardwareId());
	}
	Map<String, String> parameters = new HashMap<String, String>();
	parameters.put("color", "#ff0000");
	BatchOperation op = client.createBatchCommandInvocation(null, "17340bb1-8673-4fc9-8ed0-4f818acedaa5",
		parameters, hwids);
	System.out.println("Created operation: " + op.getToken());
    }

    @Test
    public void listCommandsForSpecification() throws SiteWhereException {
	SiteWhereClient client = new SiteWhereClient();
	DeviceCommandSearchResults results = client.listDeviceCommands(ANDROID_SPEC_TOKEN, true);
	System.out.println("Found " + results.getNumResults() + " commands.");
    }

    @Test
    public void testDeviceGroups() throws SiteWhereException {
	SiteWhereClient client = new SiteWhereClient();
	DeviceGroupCreateRequest create = new DeviceGroupCreateRequest();
	create.setToken(UUID.randomUUID().toString());
	create.setName("Test Group");
	List<String> roles = new ArrayList<String>();
	create.setRoles(roles);
	DeviceGroup group = client.createDeviceGroup(create);
	DeviceGroup match = client.getDeviceGroupByToken(group.getToken());
	Assert.assertNotNull(match);
	Assert.assertEquals(group.getName(), match.getName());
	DeviceGroupElementCreateRequest elm1 = new DeviceGroupElementCreateRequest();
	elm1.setType(GroupElementType.Device);
	elm1.setElementId("07ecf9f0-2786-48c8-ba1e-ec48a87fa104");
	List<DeviceGroupElementCreateRequest> elms = new ArrayList<DeviceGroupElementCreateRequest>();
	elms.add(elm1);
	client.addDeviceGroupElements(group.getToken(), elms);
	DeviceGroupElementSearchResults found = client.listDeviceGroupElements(group.getToken(), true,
		new SearchCriteria(1, 0));
	Assert.assertEquals(found.getNumResults(), 1);
	DeviceGroupElementSearchResults removed = client.deleteDeviceGroupElements(group.getToken(), elms);
	Assert.assertEquals(removed.getNumResults(), 1);
	DeviceGroupSearchResults before = client.listDeviceGroups(null, new SearchCriteria(1, 0), false);
	DeviceGroup deleted = client.deleteDeviceGroup(group.getToken());
	Assert.assertNotNull(deleted);
	DeviceGroupSearchResults after = client.listDeviceGroups(null, new SearchCriteria(1, 0), false);
	Assert.assertEquals(before.getNumResults(), after.getNumResults() + 1);
    }

    /**
     * Get all devices for a given specification. NOTE: Logic only looks at the
     * first 100 devices.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected List<Device> getDevicesForSpecification(String token) throws SiteWhereException {
	SiteWhereClient client = new SiteWhereClient();
	DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(1, 100, null, null);
	SearchResults<Device> devices = client.listDevices(false, true, true, true, criteria);
	List<Device> results = new ArrayList<Device>();
	for (Device device : devices.getResults()) {
	    if (device.getSpecificationToken().equals(token)) {
		results.add(device);
	    }
	}
	return results;
    }

    /**
     * Verifies that
     * 
     * @param e
     */
    protected void verifyErrorCode(SiteWhereException e, ErrorCode code) {
	if (e instanceof SiteWhereSystemException) {
	    SiteWhereSystemException sw = (SiteWhereSystemException) e;
	    if (code != sw.getCode()) {
		Assert.fail("Unexpected error code returned. Expected " + code.getCode() + " but got: " + sw.getCode());
	    }
	} else {
	    Assert.fail("Unexpected exception: " + e.getMessage());
	}
    }
}