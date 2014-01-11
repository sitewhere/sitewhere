/*
 * LargeDataset.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.sitewhere.rest.model.common.Location;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAlert;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceLocation;
import com.sitewhere.rest.model.device.DeviceMeasurements;
import com.sitewhere.rest.model.device.DeviceSpecification;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.rest.model.device.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceAssignmentCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceSpecificationCreateRequest;
import com.sitewhere.rest.model.device.request.SiteCreateRequest;
import com.sitewhere.rest.model.device.request.ZoneCreateRequest;
import com.sitewhere.rest.service.SiteWhereClient;
import com.sitewhere.spi.ISiteWhereClient;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.DeviceAssignmentType;

/**
 * Tests that can be used to populate a SiteWhere backend with large amounts of data
 * useful for performance analysis.
 * 
 * @author Derek
 */
public class LargeDataset {

	/** SiteWhere client */
	private ISiteWhereClient client;

	/** Number of devices to create */
	public static final int NUM_SITES = 1;

	/** Number of devices to create */
	public static final int ASSIGNMENTS_PER_SITE = 3;

	/** Number of events per assignment */
	public static final int EVENTS_PER_ASSIGNMENT = 150;

	/** Number of devices to create */
	public static final int ZONES_PER_SITE = 5;

	/** Image URL assocaited with sites */
	public static final String SITE_IMAGE_URL =
			"http://i.telegraph.co.uk/multimedia/archive/01809/satellite_1809335c.jpg";

	@Before
	public void setup() {
		this.client = new SiteWhereClient();
	}

	@Test
	public void createData() throws SiteWhereException {
		List<Site> sites = createSites();
		DeviceSpecification spec = createDeviceSpecification();
		for (Site site : sites) {
			List<Device> devices = createDevices(spec);
			for (Device device : devices) {
				DeviceAssignment assignment = createDeviceAssignment(device, site);
				Calendar cal = Calendar.getInstance();
				cal.roll(Calendar.HOUR, false);
				Date hourAgo = cal.getTime();
				createDeviceAlerts(assignment, hourAgo);
				createDeviceMeasurements(assignment, hourAgo);
				createDeviceLocation(assignment, hourAgo);
			}
			createZones(site);
		}
	}

	/**
	 * Create a site.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public List<Site> createSites() throws SiteWhereException {
		List<Site> results = new ArrayList<Site>();
		for (int x = 0; x < NUM_SITES; x++) {
			SiteCreateRequest siteCreate = new SiteCreateRequest();
			siteCreate.setName("Test Site");
			siteCreate.setDescription("Sample description for Test Site.");
			siteCreate.setImageUrl(SITE_IMAGE_URL);
			siteCreate.getMap().setType("mapquest");
			results.add(client.createSite(siteCreate));
		}
		return results;
	}

	/**
	 * Create a device specification.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public DeviceSpecification createDeviceSpecification() throws SiteWhereException {
		DeviceSpecificationCreateRequest request = new DeviceSpecificationCreateRequest();
		request.setAssetId("200");
		return client.createDeviceSpecification(request);
	}

	/**
	 * Create devices.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public List<Device> createDevices(DeviceSpecification spec) throws SiteWhereException {
		List<Device> results = new ArrayList<Device>();
		for (int x = 0; x < ASSIGNMENTS_PER_SITE; x++) {
			DeviceCreateRequest request = new DeviceCreateRequest();
			request.setHardwareId(UUID.randomUUID().toString());
			request.setSpecificationToken(spec.getToken());
			request.setComments("Test device " + x + ".");
			results.add(client.createDevice(request));
		}
		return results;
	}

	/**
	 * Create a device assignment.
	 * 
	 * @param device
	 * @param site
	 * @return
	 * @throws SiteWhereException
	 */
	public DeviceAssignment createDeviceAssignment(Device device, Site site) throws SiteWhereException {
		DeviceAssignmentCreateRequest request = new DeviceAssignmentCreateRequest();
		request.setDeviceHardwareId(device.getHardwareId());
		request.setSiteToken(site.getToken());
		request.setAssignmentType(DeviceAssignmentType.Person);
		request.setAssetId("1");
		return client.createDeviceAssignment(request);
	}

	/**
	 * Create device alerts associated with an assignment.
	 * 
	 * @param assignment
	 * @return
	 * @throws SiteWhereException
	 */
	public List<DeviceAlert> createDeviceAlerts(DeviceAssignment assignment, Date start)
			throws SiteWhereException {
		long current = start.getTime();
		List<DeviceAlert> results = new ArrayList<DeviceAlert>();
		for (int x = 0; x < EVENTS_PER_ASSIGNMENT; x++) {
			DeviceAlertCreateRequest request = new DeviceAlertCreateRequest();
			request.setType("fire.alarm");
			request.setMessage("Fire alarm has been triggered on the third floor.");
			request.setEventDate(new Date(current));
			results.add(client.createDeviceAlert(assignment.getToken(), request));
			current += 10000;
		}
		return results;
	}

	/**
	 * Create device measurements associated with an assignment.
	 * 
	 * @param assignment
	 * @return
	 * @throws SiteWhereException
	 */
	public List<DeviceMeasurements> createDeviceMeasurements(DeviceAssignment assignment, Date start)
			throws SiteWhereException {
		long current = start.getTime();
		List<DeviceMeasurements> results = new ArrayList<DeviceMeasurements>();
		for (int x = 0; x < EVENTS_PER_ASSIGNMENT; x++) {
			DeviceMeasurementsCreateRequest request = new DeviceMeasurementsCreateRequest();
			request.addOrReplaceMeasurement("engine.temperature", 145.0);
			request.setEventDate(new Date(current));
			results.add(client.createDeviceMeasurements(assignment.getToken(), request));
			current += 10000;
		}
		return results;
	}

	/**
	 * Create device location associated with an assignment.
	 * 
	 * @param assignment
	 * @return
	 * @throws SiteWhereException
	 */
	public List<DeviceLocation> createDeviceLocation(DeviceAssignment assignment, Date start)
			throws SiteWhereException {
		long current = start.getTime();
		List<DeviceLocation> results = new ArrayList<DeviceLocation>();
		for (int x = 0; x < EVENTS_PER_ASSIGNMENT; x++) {
			DeviceLocationCreateRequest request = new DeviceLocationCreateRequest();
			request.setLatitude(33.7550);
			request.setLongitude(-84.3900);
			request.setElevation(0.0);
			request.setEventDate(new Date(current));
			results.add(client.createDeviceLocation(assignment.getToken(), request));
			current += 10000;
		}
		return results;
	}

	/**
	 * Create zones for a site.
	 * 
	 * @param site
	 * @return
	 * @throws SiteWhereException
	 */
	public List<Zone> createZones(Site site) throws SiteWhereException {
		List<Zone> results = new ArrayList<Zone>();
		for (int x = 0; x < ZONES_PER_SITE; x++) {
			ZoneCreateRequest request = new ZoneCreateRequest();
			request.setName("Zone " + x);
			request.setBorderColor("black");
			request.setFillColor("blue");
			request.setOpacity(0.4);
			List<Location> coords = new ArrayList<Location>();
			coords.add(new Location(33.7550, -84.3900));
			coords.add(new Location(32.4922, -84.9403));
			coords.add(new Location(34.2600, -85.1850));
			request.setCoordinates(coords);
			results.add(client.createZone(site.getToken(), request));
		}
		return results;
	}
}