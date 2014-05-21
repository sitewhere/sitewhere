/*
 * DefaultDeviceModelInitializer.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.device;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.geo.GeoUtils;
import com.sitewhere.rest.model.common.Location;
import com.sitewhere.rest.model.device.DeviceSpecification;
import com.sitewhere.rest.model.device.command.CommandParameter;
import com.sitewhere.rest.model.device.element.DeviceElementSchema;
import com.sitewhere.rest.model.device.element.DeviceSlot;
import com.sitewhere.rest.model.device.element.DeviceUnit;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceCommandInvocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceCommandResponseCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceRegistrationRequest;
import com.sitewhere.rest.model.device.request.DeviceAssignmentCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCommandCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupElementCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceSpecificationCreateRequest;
import com.sitewhere.rest.model.device.request.SiteCreateRequest;
import com.sitewhere.rest.model.device.request.ZoneCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.server.asset.filesystem.FileSystemDeviceAssetModule;
import com.sitewhere.server.asset.filesystem.FileSystemHardwareAssetModule;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.DeviceContainerPolicy;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.ISiteMapMetadata;
import com.sitewhere.spi.device.IZone;
import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.ParameterType;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.CommandInitiator;
import com.sitewhere.spi.device.event.CommandStatus;
import com.sitewhere.spi.device.event.CommandTarget;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.group.GroupElementType;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;
import com.sitewhere.spi.server.device.IDeviceModelInitializer;
import com.vividsolutions.jts.algorithm.MinimumBoundingCircle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.util.AffineTransformation;

/**
 * Used to load a default site/devices/assignments/events so that there is demo data in
 * the system. The server only offers this functionality if no sites already exist.
 * 
 * @author Derek
 */
public class DefaultDeviceModelInitializer implements IDeviceModelInitializer {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DefaultDeviceModelInitializer.class);

	/** Prefix for create site log message */
	public static final String PREFIX_CREATE_SITE = "[Create Site]";

	/** Prefix for create device log message */
	public static final String PREFIX_CREATE_DEVICE = "[Create Device]";

	/** Prefix for create assignment log message */
	public static final String PREFIX_CREATE_ASSIGNMENT = "[Create Assignment]";

	/** Prefix for create zone log message */
	public static final String PREFIX_CREATE_ZONE = "[Create Zone]";

	/** Prefix for create event log message */
	public static final String PREFIX_CREATE_EVENTS = "[Create Events]";

	/** Number of devices to create */
	public static final int NUM_SITES = 1;

	/** Number of device groups to create */
	public static final int NUM_DEVICE_GROUPS = 3;

	/** Number of devices/assignments to create */
	public static final int ASSIGNMENTS_PER_SITE = 15;

	/** Number of events per assignment */
	public static final int EVENTS_PER_ASSIGNMENT = 75;

	/** Number of events per assignment */
	public static final int LOCATIONS_PER_ASSIGNMENT = 40;

	/** Minimum engine temp */
	public static final double MIN_TEMP = 80;

	/** Maximum engine temp */
	public static final double MAX_TEMP = 200;

	/** Temp at which a warning alert will be generated */
	public static final double WARN_TEMP = 160;

	/** Temp at which an error alert will be generated */
	public static final double ERROR_TEMP = 180;

	/** Temp at which a critical alert will be generated */
	public static final double CRITICAL_TEMP = 190;

	/** Image URL assocaited with sites */
	public static final String SITE_IMAGE_URL =
			"https://s3.amazonaws.com/sitewhere-demo/construction/construction.jpg";

	/** Namespace for common core commands */
	public static final String SITEWHERE_COMMON_NAMESPACE = "http://sitewhere/common";

	/** Namespace for Raspberry Pi commands */
	public static final String RASPBERRY_PI_NAMESPACE = "http://raspberrypi/example";

	/** Namespace for Android commands */
	public static final String ANDROID_NAMESPACE = "http://android/example";

	/** Information for available device specifications */
	public static final SpecificationDetails[] SPECIFICATION_INFO = {
			new SpecificationDetails("173", "Android Default", "d2604433-e4eb-419b-97c7-88efe9b2cd41"),
			new SpecificationDetails("174", "Raspberrry Pi Default", "7dfd6d63-5e8d-4380-be04-fc5c73801dfb"),
			new SpecificationDetails("175", "MeiTrack MT90 Default", "82043707-9e3d-441f-bdcc-33cf0f4f7260"),
			new SpecificationDetails("176", "Gateway Default", "75126a52-0607-4cca-b995-df40e73a707b") };

	/** Available device specifications */
	protected IDeviceSpecification[] deviceSpecifications;

	/** Map of commands for each specification token */
	protected Map<String, List<IDeviceCommand>> commandsBySpecToken =
			new HashMap<String, List<IDeviceCommand>>();

	/** Available choices for devices/assignments that track location */
	protected static AssignmentChoice[] LOCATION_TRACKERS = {
			new AssignmentChoice("Equipment Tracker", FileSystemHardwareAssetModule.MODULE_ID, "300"),
			new AssignmentChoice("Equipment Tracker", FileSystemHardwareAssetModule.MODULE_ID, "301"),
			new AssignmentChoice("Equipment Tracker", FileSystemHardwareAssetModule.MODULE_ID, "302"),
			new AssignmentChoice("Equipment Tracker", FileSystemHardwareAssetModule.MODULE_ID, "303"),
			new AssignmentChoice("Equipment Tracker", FileSystemHardwareAssetModule.MODULE_ID, "304") };

	/** Role for 'light equipment' groups */
	protected static final String GRP_ROLE_LIGHT_EQUIPMENT = "light-equipment";

	/** Role for 'heavy equipment' groups */
	protected static final String GRP_ROLE_HEAVY_EQUIPMENT = "heavy-equipment";

	/** Role lists that can be applied to device groups */
	protected static String[][] GRP_ROLE_LISTS = {
			new String[] { GRP_ROLE_LIGHT_EQUIPMENT },
			new String[] { GRP_ROLE_HEAVY_EQUIPMENT } };

	/** Role for 'first team' members */
	protected static final String GRP_ELM_ROLE_FIRST_TEAM = "first-team";

	/** Role for 'second team' members */
	protected static final String GRP_ELM_ROLE_SECOND_TEAM = "second-team";

	/** Role for 'earth movers' members */
	protected static final String GRP_ELM_ROLE_EARTH_MOVERS_TEAM = "earth-movers";

	/** Role lists that can be applied to device group elements */
	protected static String[][] GRP_ELEMENT_ROLE_LISTS = {
			new String[] { GRP_ELM_ROLE_FIRST_TEAM },
			new String[] { GRP_ELM_ROLE_SECOND_TEAM },
			new String[] { GRP_ELM_ROLE_FIRST_TEAM, GRP_ELM_ROLE_EARTH_MOVERS_TEAM },
			new String[] { GRP_ELM_ROLE_EARTH_MOVERS_TEAM },
			new String[] { GRP_ELM_ROLE_SECOND_TEAM, GRP_ELM_ROLE_EARTH_MOVERS_TEAM }, };

	/** Locations that determine zone edges */
	protected List<Location> zoneLocations;

	/** Device management implementation */
	protected IDeviceManagement deviceManagement;

	/** Indiates whether model should be initialized if no console is available for input */
	private boolean initializeIfNoConsole = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.device.IDeviceModelInitializer#initialize(com.sitewhere
	 * .spi.device.IDeviceManagement)
	 */
	@Override
	public void initialize(IDeviceManagement deviceManagement) throws SiteWhereException {
		this.deviceManagement = deviceManagement;

		// Use the system account for logging "created by" on created elements.
		SecurityContextHolder.getContext().setAuthentication(SiteWhereServer.getSystemAuthentication());

		// Coordinates for edges of zone.
		zoneLocations = new ArrayList<Location>();
		zoneLocations.add(new Location(34.10260138703638, -84.24412965774536));
		zoneLocations.add(new Location(34.101837372446774, -84.24243450164795));
		zoneLocations.add(new Location(34.101517550337825, -84.24091100692749));
		zoneLocations.add(new Location(34.10154953265732, -84.23856675624847));
		zoneLocations.add(new Location(34.10153176473365, -84.23575580120087));
		zoneLocations.add(new Location(34.10409030732968, -84.23689305782318));
		zoneLocations.add(new Location(34.104996439280704, -84.23700034618376));
		zoneLocations.add(new Location(34.10606246444614, -84.23700034618376));
		zoneLocations.add(new Location(34.107691680235604, -84.23690915107727));

		// Create device specifications.
		this.deviceSpecifications = createDeviceSpecifications();

		List<ISite> sites = createSites();
		for (ISite site : sites) {
			List<IDeviceAssignment> assignments = createAssignments(site);
			for (int groupIndex = 0; groupIndex < NUM_DEVICE_GROUPS; groupIndex++) {
				List<IDeviceGroupElementCreateRequest> requests =
						new ArrayList<IDeviceGroupElementCreateRequest>();
				IDeviceGroup group = createDeviceGroup(groupIndex);
				for (IDeviceAssignment assignment : assignments) {
					DeviceGroupElementCreateRequest request = new DeviceGroupElementCreateRequest();
					request.setType(GroupElementType.Device);
					request.setElementId(assignment.getDeviceHardwareId());
					request.setRoles(getRandomDeviceGroupElementRoleList());
					requests.add(request);
				}
				getDeviceManagement().addDeviceGroupElements(group.getToken(), requests);
				// testListAndRemoveNetworkElements(group);
			}
		}

		SecurityContextHolder.getContext().setAuthentication(null);
	}

	/**
	 * Test API calls for listing and removing group elements.
	 * 
	 * @param group
	 * @throws SiteWhereException
	 */
	protected void testListAndRemoveNetworkElements(IDeviceGroup group) throws SiteWhereException {
		SearchResults<IDeviceGroupElement> groupElements =
				getDeviceManagement().listDeviceGroupElements(group.getToken(), new SearchCriteria(0, 10));
		LOGGER.info("Matched " + groupElements.getResults().size() + " group elements.");

		List<IDeviceGroupElementCreateRequest> delete = new ArrayList<IDeviceGroupElementCreateRequest>();
		for (IDeviceGroupElement current : groupElements.getResults()) {
			DeviceGroupElementCreateRequest delElm = new DeviceGroupElementCreateRequest();
			delElm.setType(current.getType());
			delElm.setElementId(current.getElementId());
			delete.add(delElm);
		}
		List<IDeviceGroupElement> deleted =
				getDeviceManagement().removeDeviceGroupElements(group.getToken(), delete);
		LOGGER.info("Deleted " + deleted.size() + " group elements.");

		groupElements =
				getDeviceManagement().listDeviceGroupElements(group.getToken(), new SearchCriteria(0, 100));
		LOGGER.info("Remaining was " + groupElements.getResults().size() + " group elements.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.IModelInitializer#isInitializeIfNoConsole()
	 */
	public boolean isInitializeIfNoConsole() {
		return initializeIfNoConsole;
	}

	public void setInitializeIfNoConsole(boolean initializeIfNoConsole) {
		this.initializeIfNoConsole = initializeIfNoConsole;
	}

	/**
	 * Create device specifications.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceSpecification[] createDeviceSpecifications() throws SiteWhereException {
		IDeviceSpecification[] results = new DeviceSpecification[SPECIFICATION_INFO.length];
		int index = 0;
		for (SpecificationDetails details : SPECIFICATION_INFO) {
			DeviceSpecificationCreateRequest request = new DeviceSpecificationCreateRequest();
			request.setAssetModuleId(FileSystemDeviceAssetModule.MODULE_ID);
			request.setAssetId(details.getAssetId());
			request.setName(details.getName());
			request.setToken(details.getUuid());

			// Add fields for gateway.
			if (details.getUuid().equals("75126a52-0607-4cca-b995-df40e73a707b")) {
				request.setContainerPolicy(DeviceContainerPolicy.Composite);
				DeviceElementSchema schema = createDeviceElementSchema();
				request.setDeviceElementSchema(schema);
			} else {
				request.setContainerPolicy(DeviceContainerPolicy.Standalone);
			}

			IDeviceSpecification spec = getDeviceManagement().createDeviceSpecification(request);
			createDeviceCommands(spec);
			results[index] = spec;
			index++;
		}
		return results;
	}

	/**
	 * Create a sample schema for a composite device.
	 * 
	 * @return
	 */
	protected DeviceElementSchema createDeviceElementSchema() {
		DeviceElementSchema schema = new DeviceElementSchema();

		// Create a bus with two slots.
		DeviceUnit bus1 = new DeviceUnit();
		bus1.setPath("bus1");
		DeviceSlot slot1 = new DeviceSlot();
		slot1.setPath("slot1");
		bus1.getDeviceSlots().add(slot1);
		DeviceSlot slot2 = new DeviceSlot();
		slot2.setPath("slot2");
		bus1.getDeviceSlots().add(slot2);
		schema.getDeviceUnits().add(bus1);

		// Create a top-level slot.
		DeviceSlot s1 = new DeviceSlot();
		s1.setPath("slot1");
		schema.getDeviceSlots().add(s1);
		return schema;
	}

	/**
	 * Create device commands that may be added to specifications.
	 * 
	 * @throws SiteWhereException
	 */
	public void createDeviceCommands(IDeviceSpecification spec) throws SiteWhereException {
		List<IDeviceCommand> commands = new ArrayList<IDeviceCommand>();

		DeviceCommandCreateRequest cmdPing = new DeviceCommandCreateRequest();
		cmdPing.setNamespace(SITEWHERE_COMMON_NAMESPACE);
		cmdPing.setName("ping");
		cmdPing.setDescription("Send a 'ping' request to the device to verify it can be reached.");
		commands.add(getDeviceManagement().createDeviceCommand(spec, cmdPing));

		DeviceCommandCreateRequest testEvents = new DeviceCommandCreateRequest();
		testEvents.setNamespace(SITEWHERE_COMMON_NAMESPACE);
		testEvents.setName("testEvents");
		testEvents.setDescription("Send a request that results in sample events being returned.");
		commands.add(getDeviceManagement().createDeviceCommand(spec, testEvents));

		if (spec.getToken().equals("d2604433-e4eb-419b-97c7-88efe9b2cd41")) {
			DeviceCommandCreateRequest changebg = new DeviceCommandCreateRequest();
			changebg.setNamespace(ANDROID_NAMESPACE);
			changebg.setName("changeBackground");
			changebg.setDescription("Change background color of application.");
			changebg.getParameters().add(new CommandParameter("color", ParameterType.String, true));
			commands.add(getDeviceManagement().createDeviceCommand(spec, changebg));
		}

		if (spec.getToken().equals("7dfd6d63-5e8d-4380-be04-fc5c73801dfb")) {
			DeviceCommandCreateRequest hello = new DeviceCommandCreateRequest();
			hello.setNamespace(RASPBERRY_PI_NAMESPACE);
			hello.setName("helloWorld");
			hello.setDescription("Request a 'hello world' response from device.");
			hello.getParameters().add(new CommandParameter("greeting", ParameterType.String, true));
			hello.getParameters().add(new CommandParameter("loud", ParameterType.Bool, true));
			commands.add(getDeviceManagement().createDeviceCommand(spec, hello));
		}

		commandsBySpecToken.put(spec.getToken(), commands);
	}

	/**
	 * Get a random device specification from the list.
	 * 
	 * @return
	 */
	public IDeviceSpecification getRandomDeviceSpecification() {
		int index = (int) Math.floor(Math.random() * SPECIFICATION_INFO.length);
		return deviceSpecifications[index];
	}

	/**
	 * Create example sites.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public List<ISite> createSites() throws SiteWhereException {
		List<ISite> results = new ArrayList<ISite>();
		for (int x = 0; x < NUM_SITES; x++) {
			SiteCreateRequest request = new SiteCreateRequest();
			request.setName("Construction Site " + (x + 1));
			request.setDescription("A construction site with many high-value assets that should "
					+ "not be taken offsite. The system provides location tracking for the assets and notifies "
					+ "administrators if any of the assets move outside of the general site area or "
					+ "into areas where they are not allowed.");
			request.setImageUrl(SITE_IMAGE_URL);
			request.getMap().setType("mapquest");
			request.getMap().addOrReplaceMetadata(ISiteMapMetadata.MAP_CENTER_LATITUDE, "34.10469794977326");
			request.getMap().addOrReplaceMetadata(ISiteMapMetadata.MAP_CENTER_LONGITUDE, "-84.23966646194458");
			request.getMap().addOrReplaceMetadata(ISiteMapMetadata.MAP_ZOOM_LEVEL, "15");
			ISite site = getDeviceManagement().createSite(request);
			results.add(site);
			LOGGER.info(PREFIX_CREATE_SITE + " " + request.getName());

			// Create a zone for the site.
			createZone(site);
		}
		return results;
	}

	/**
	 * Create the construction zone.
	 * 
	 * @param site
	 * @return
	 * @throws SiteWhereException
	 */
	public IZone createZone(ISite site) throws SiteWhereException {
		ZoneCreateRequest request = new ZoneCreateRequest();
		request.setName("Construction Site");
		request.setBorderColor("#017112");
		request.setFillColor("#1db32e");
		request.setOpacity(0.4);
		request.setCoordinates(zoneLocations);
		IZone zone = getDeviceManagement().createZone(site, request);
		LOGGER.info(PREFIX_CREATE_ZONE + " " + zone.getToken());
		return zone;
	}

	/**
	 * Create a device group.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceGroup createDeviceGroup(int index) throws SiteWhereException {
		DeviceGroupCreateRequest request = new DeviceGroupCreateRequest();
		request.setToken(UUID.randomUUID().toString());
		request.setName("Construction Equipment Group " + (index + 1));
		request.setDescription("Device group that contains contruction equipment assets.");
		request.setRoles(getRandomDeviceGroupRoleList());
		return getDeviceManagement().createDeviceGroup(request);
	}

	/**
	 * Create devices for a site and assign them.
	 * 
	 * @param site
	 * @return
	 * @throws SiteWhereException
	 */
	public List<IDeviceAssignment> createAssignments(ISite site) throws SiteWhereException {
		Date before = new Date(System.currentTimeMillis() - (2 * 60 * 60 * 1000));
		List<IDeviceAssignment> results = new ArrayList<IDeviceAssignment>();
		for (int x = 0; x < ASSIGNMENTS_PER_SITE; x++) {
			AssignmentChoice assnChoice = getRandomAssignmentChoice();
			IDeviceSpecification specification = getRandomDeviceSpecification();
			List<IDeviceCommand> commands = commandsBySpecToken.get(specification.getToken());

			// Create device.
			DeviceCreateRequest request = new DeviceCreateRequest();
			request.setHardwareId(UUID.randomUUID().toString());
			request.setSpecificationToken(specification.getToken());
			request.setComments(assnChoice.getDeviceDescriptionBase() + " " + (x + 1) + ".");
			IDevice device = getDeviceManagement().createDevice(request);
			LOGGER.info(PREFIX_CREATE_DEVICE + " " + device.getHardwareId());

			// Create assignment.
			DeviceAssignmentCreateRequest assnRequest = new DeviceAssignmentCreateRequest();
			assnRequest.setAssignmentType(DeviceAssignmentType.Associated);
			assnRequest.setAssetModuleId(assnChoice.getAssignmentAssetModuleId());
			assnRequest.setAssetId(assnChoice.getAssignmentAssetId());
			assnRequest.setDeviceHardwareId(device.getHardwareId());
			assnRequest.setSiteToken(site.getToken());
			assnRequest.addOrReplaceMetadata("serialNumber", UUID.randomUUID().toString());
			IDeviceAssignment assignment = getDeviceManagement().createDeviceAssignment(assnRequest);
			LOGGER.info(PREFIX_CREATE_ASSIGNMENT + " " + assignment.getToken());

			// Create events for assignment.
			createDeviceMeasurements(assignment, before);
			createDeviceLocations(assignment, before);
			List<IDeviceCommandInvocation> invocations =
					createDeviceCommandInvocations(assignment, before, commands);
			createDeviceCommandResponses(assignment, before, invocations);
			createDeviceStateChanges(assignment, specification, before);

			results.add(assignment);
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
	protected List<IDeviceMeasurements> createDeviceMeasurements(IDeviceAssignment assignment, Date start)
			throws SiteWhereException {
		long current = start.getTime();
		double temp = MIN_TEMP;
		double fuel = 100;
		double delta = 4;
		double mult = 6;
		int measurementCount = 0;
		int alertCount = 0;
		List<IDeviceMeasurements> results = new ArrayList<IDeviceMeasurements>();
		for (int x = 0; x < EVENTS_PER_ASSIGNMENT; x++) {
			// Simulate temperature changes.
			temp = temp + (delta + ((Math.random() * mult * 2) - mult));
			temp = Math.round(temp * 100.0) / 100.0;
			if ((temp > MAX_TEMP) || (temp < MIN_TEMP)) {
				delta = -delta;
			}

			// Simulate fuel changes.
			fuel -= (Math.random() * 2);
			fuel = Math.round(fuel * 100.0) / 100.0;
			if (fuel < 0) {
				fuel = 0;
			}

			// Store current temperature measurement.
			DeviceMeasurementsCreateRequest mreq = new DeviceMeasurementsCreateRequest();
			mreq.addOrReplaceMeasurement("engine.temperature", temp);
			mreq.addOrReplaceMeasurement("fuel.level", fuel);
			mreq.setEventDate(new Date(current));
			results.add(getDeviceManagement().addDeviceMeasurements(assignment.getToken(), mreq, true));
			measurementCount++;

			// Create alerts based on current temperature.
			if (temp > WARN_TEMP) {
				DeviceAlertCreateRequest areq = new DeviceAlertCreateRequest();
				areq.setType("engine.overheat");
				areq.setEventDate(new Date(current));
				areq.setMessage("Engine temperature is at top of operating range.");
				areq.setLevel(AlertLevel.Warning);
				if (temp > ERROR_TEMP) {
					areq.setMessage("Engine temperature is at a dangerous level.");
					areq.setLevel(AlertLevel.Error);
				} else if (temp > CRITICAL_TEMP) {
					areq.setMessage("Engine temperature critical. Shutting down.");
					areq.setLevel(AlertLevel.Critical);
					break;
				}
				getDeviceManagement().addDeviceAlert(assignment.getToken(), areq, true);
				alertCount++;
			}

			current += 10000;
		}
		LOGGER.info(PREFIX_CREATE_EVENTS + " " + measurementCount + " measurements. " + alertCount
				+ " alerts.");
		return results;
	}

	/**
	 * Create device locations in a path near the main zone.
	 * 
	 * @param assignment
	 * @param start
	 * @return
	 * @throws SiteWhereException
	 */
	protected List<IDeviceLocation> createDeviceLocations(IDeviceAssignment assignment, Date date)
			throws SiteWhereException {
		long current = date.getTime();
		Polygon zone = GeoUtils.createPolygonForLocations(zoneLocations);
		Point centroid = zone.getCentroid();

		// Calculate length of steps between locations based on bounding circle.
		MinimumBoundingCircle circle = new MinimumBoundingCircle(zone);
		double step = circle.getRadius() / 10;

		double cx = centroid.getX();
		double cy = centroid.getY();
		double deltaX = (Math.sqrt(Math.random()) * step * 2) - step;
		double deltaY = (Math.sqrt(Math.random()) * step * 2) - step;

		// Used to rotate deltas to turn path and stay inside polygon.
		AffineTransformation xform = new AffineTransformation();
		xform.rotate(Math.toRadians(22.5));

		List<IDeviceLocation> results = new ArrayList<IDeviceLocation>();
		GeometryFactory factory = new GeometryFactory();
		for (int x = 0; x < LOCATIONS_PER_ASSIGNMENT; x++) {
			boolean foundNext = false;

			// Add a little randomness to path.
			double waver = ((Math.random() * 20) - 10.0);
			AffineTransformation waverXform = new AffineTransformation();
			waverXform.rotate(Math.toRadians(waver));
			Coordinate waverDelta = new Coordinate(deltaX, deltaY);
			waverXform.transform(waverDelta, waverDelta);
			deltaX = waverDelta.x;
			deltaY = waverDelta.y;

			while (!foundNext) {
				Coordinate start = new Coordinate(cx, cy);
				Coordinate end = new Coordinate(cx + deltaX, cy + deltaY);
				Coordinate[] lineCoords = { start, end };
				LineString line = factory.createLineString(lineCoords);
				if (zone.contains(line)) {
					DeviceLocationCreateRequest request = new DeviceLocationCreateRequest();
					request.setLatitude(end.y);
					request.setLongitude(end.x);
					request.setElevation(0.0);
					request.setEventDate(new Date(current));
					IDeviceLocation created =
							getDeviceManagement().addDeviceLocation(assignment.getToken(), request, true);
					results.add(created);

					cx = cx + deltaX;
					cy = cy + deltaY;
					foundNext = true;
				} else {
					// Rotate deltas and try again.
					Coordinate delta = new Coordinate(deltaX, deltaY);
					xform.transform(delta, delta);
					deltaX = delta.x;
					deltaY = delta.y;
				}
			}
			current += 30000;
		}
		LOGGER.info(PREFIX_CREATE_EVENTS + " " + results.size() + " locations. ");
		return results;
	}

	/**
	 * Create command invocations for an assignment.
	 * 
	 * @param assignment
	 * @param date
	 * @param commands
	 * @return
	 * @throws SiteWhereException
	 */
	protected List<IDeviceCommandInvocation> createDeviceCommandInvocations(IDeviceAssignment assignment,
			Date date, List<IDeviceCommand> commands) throws SiteWhereException {
		long current = date.getTime();
		List<IDeviceCommandInvocation> invocations = new ArrayList<IDeviceCommandInvocation>();
		for (IDeviceCommand command : commands) {
			DeviceCommandInvocationCreateRequest request = new DeviceCommandInvocationCreateRequest();
			request.setCommandToken(command.getToken());
			request.setInitiator(CommandInitiator.REST);
			request.setInitiatorId("system");
			request.setTarget(CommandTarget.Assignment);
			request.setTargetId(assignment.getToken());
			request.setStatus(CommandStatus.Pending);
			request.setEventDate(new Date(current));
			Map<String, String> values = new HashMap<String, String>();
			for (ICommandParameter param : command.getParameters()) {
				values.put(param.getName(), getSampleValue(param.getType()));
			}
			request.setParameterValues(values);
			invocations.add(getDeviceManagement().addDeviceCommandInvocation(assignment.getToken(), command,
					request));
			current += 30000;
		}
		return invocations;
	}

	/**
	 * Create command responses for each invocation.
	 * 
	 * @param assignment
	 * @param date
	 * @param invocations
	 * @return
	 * @throws SiteWhereException
	 */
	protected List<IDeviceCommandResponse> createDeviceCommandResponses(IDeviceAssignment assignment,
			Date date, List<IDeviceCommandInvocation> invocations) throws SiteWhereException {
		long current = date.getTime();
		List<IDeviceCommandResponse> responses = new ArrayList<IDeviceCommandResponse>();
		for (IDeviceCommandInvocation invocation : invocations) {
			DeviceCommandResponseCreateRequest request = new DeviceCommandResponseCreateRequest();
			request.setOriginatingEventId(invocation.getId());
			request.setResponse("ACK");
			request.setEventDate(new Date(current));
			responses.add(getDeviceManagement().addDeviceCommandResponse(assignment.getToken(), request));
			current += 30000;
		}
		return responses;
	}

	/**
	 * Create example state change events.
	 * 
	 * @param assignment
	 * @param start
	 * @return
	 * @throws SiteWhereException
	 */
	protected List<IDeviceStateChange> createDeviceStateChanges(IDeviceAssignment assignment,
			IDeviceSpecification specification, Date start) throws SiteWhereException {
		List<IDeviceStateChange> stateChanges = new ArrayList<IDeviceStateChange>();
		DeviceRegistrationRequest register = new DeviceRegistrationRequest();
		register.setHardwareId(assignment.getDeviceHardwareId());
		register.setSpecificationToken(specification.getToken());
		register.setReplyTo("SiteWhere/devices/" + assignment.getDeviceHardwareId());
		register.setEventDate(start);
		stateChanges.add(getDeviceManagement().addDeviceStateChange(assignment.getToken(), register));
		return stateChanges;
	}

	/**
	 * Get a sample value based on datatype.
	 * 
	 * @param type
	 * @return
	 */
	protected String getSampleValue(ParameterType type) {
		switch (type) {
		case Bool:
			return "true";
		case Double:
			return "0.0";
		case Float:
			return "0.0";
		case String:
			return "test";
		default:
			return "0";
		}
	}

	/**
	 * Gets a random location tracker assignment choice entry.
	 * 
	 * @return
	 */
	protected AssignmentChoice getRandomAssignmentChoice() {
		int slot = (int) Math.floor(LOCATION_TRACKERS.length * Math.random());
		return LOCATION_TRACKERS[slot];
	}

	/**
	 * Gets a random role list for a device group.
	 * 
	 * @return
	 */
	protected List<String> getRandomDeviceGroupRoleList() {
		int slot = (int) Math.floor(GRP_ROLE_LISTS.length * Math.random());
		String[] roles = GRP_ROLE_LISTS[slot];
		return Arrays.asList(roles);
	}

	/**
	 * Gets a random role list for a device group element.
	 * 
	 * @return
	 */
	protected List<String> getRandomDeviceGroupElementRoleList() {
		int slot = (int) Math.floor(GRP_ELEMENT_ROLE_LISTS.length * Math.random());
		String[] roles = GRP_ELEMENT_ROLE_LISTS[slot];
		return Arrays.asList(roles);
	}

	/**
	 * Internal class for creating a device specification.
	 * 
	 * @author Derek
	 */
	private static class SpecificationDetails {

		private String assetId;

		private String name;

		private String uuid;

		public SpecificationDetails(String assetId, String name, String uuid) {
			this.assetId = assetId;
			this.name = name;
			this.uuid = uuid;
		}

		public String getAssetId() {
			return assetId;
		}

		public String getName() {
			return name;
		}

		public String getUuid() {
			return uuid;
		}
	}

	/**
	 * Internal class for choosing device/asset assignments that make sense.
	 * 
	 * @author Derek
	 */
	private static class AssignmentChoice {

		private String devDescBase;
		private String assnAssetModuleId;
		private String assnAssetId;

		public AssignmentChoice(String devDescBase, String assnAssetModuleId, String assnAssetId) {
			this.devDescBase = devDescBase;
			this.assnAssetModuleId = assnAssetModuleId;
			this.assnAssetId = assnAssetId;
		}

		protected String getDeviceDescriptionBase() {
			return devDescBase;
		}

		protected String getAssignmentAssetModuleId() {
			return assnAssetModuleId;
		}

		protected String getAssignmentAssetId() {
			return assnAssetId;
		}
	}

	protected IDeviceManagement getDeviceManagement() {
		return deviceManagement;
	}

	protected void setDeviceManagement(IDeviceManagement deviceManagement) {
		this.deviceManagement = deviceManagement;
	}
}