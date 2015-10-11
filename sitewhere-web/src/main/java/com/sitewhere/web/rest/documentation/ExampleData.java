/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.sitewhere.core.user.ISiteWhereAuthorities;
import com.sitewhere.rest.model.asset.AssetCategory;
import com.sitewhere.rest.model.asset.AssetModule;
import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.asset.LocationAsset;
import com.sitewhere.rest.model.asset.PersonAsset;
import com.sitewhere.rest.model.common.Location;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceElementMapping;
import com.sitewhere.rest.model.device.DeviceSpecification;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.SiteMapData;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.rest.model.device.batch.BatchElement;
import com.sitewhere.rest.model.device.batch.BatchOperation;
import com.sitewhere.rest.model.device.command.CommandParameter;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.rest.model.device.group.DeviceGroupElement;
import com.sitewhere.rest.model.device.streaming.DeviceStream;
import com.sitewhere.rest.model.search.external.SearchProvider;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.rest.model.user.Tenant;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.DeviceContainerPolicy;
import com.sitewhere.spi.device.batch.BatchOperationStatus;
import com.sitewhere.spi.device.batch.ElementProcessingStatus;
import com.sitewhere.spi.device.batch.OperationType;
import com.sitewhere.spi.device.command.ParameterType;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.AlertSource;
import com.sitewhere.spi.device.event.CommandInitiator;
import com.sitewhere.spi.device.event.CommandTarget;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.group.GroupElementType;
import com.sitewhere.spi.user.AccountStatus;

@SuppressWarnings("serial")
public class ExampleData {

	/** Persons asset module */
	public static AssetModule_Persons AM_PERSONS = new AssetModule_Persons();

	/** Devices asset module */
	public static AssetModule_Devices AM_DEVICES = new AssetModule_Devices();

	/** Devices asset category */
	public static AssetCategory_Devices AC_DEVICES = new AssetCategory_Devices();

	/** Persons asset category */
	public static AssetCategory_Persons AC_PERSONS = new AssetCategory_Persons();

	/** Person asset */
	public static Person_Derek ASSET_DEREK = new Person_Derek();

	/** Person asset */
	public static Person_Martin ASSET_MARTIN = new Person_Martin();

	/** Location asset */
	public static Location_Trailer ASSET_TRAILER = new Location_Trailer();

	/** Hardware asset */
	public static Hardware_Caterpillar ASSET_CATERPILLAR = new Hardware_Caterpillar();

	/** Hardware asset */
	public static Hardware_Meitrack ASSET_MEITRACK = new Hardware_Meitrack();

	/** Site */
	public static Site_Construction SITE_CONSTRUCTION = new Site_Construction();

	/** Zone */
	public static Zone_ConstructionSite ZONE_CONSTRUCTION_SITE = new Zone_ConstructionSite();

	/** Zone */
	public static Zone_OffLimits ZONE_OFF_LIMITS = new Zone_OffLimits();

	/** Site */
	public static Site_VehicleTracking SITE_VEHICLE_TRACKING = new Site_VehicleTracking();

	/** Device specification */
	public static Specification_MeiTrack SPEC_MEITRACK = new Specification_MeiTrack();

	/** Specification command */
	public static Command_GetFirmwareVersion COMMAND_GET_FW_VER = new Command_GetFirmwareVersion();

	/** Specification command */
	public static Command_SetReportInterval COMMAND_SET_RPT_INTV = new Command_SetReportInterval();

	/** Device specification */
	public static Specification_HeartMonitor SPEC_HEART_MONITOR = new Specification_HeartMonitor();

	/** Devices */
	public static Device_Tracker TRACKER = new Device_Tracker();

	/** Devices */
	public static Device_Tracker2 TRACKER2 = new Device_Tracker2();

	/** Devices */
	public static Device_HeartMonitor HEART_MONITOR = new Device_HeartMonitor();

	/** Device element mapping */
	public static Mapping_HeartMonitor MAPPING_HEART_MONITOR = new Mapping_HeartMonitor();

	/** Assignment */
	public static Assignment_TrackerToDerek TRACKER_TO_DEREK = new Assignment_TrackerToDerek();

	/** Assignment */
	public static Assignment_TrackerToMartin TRACKER_TO_MARTIN = new Assignment_TrackerToMartin();

	/** Location event */
	public static LocationEvent1 EVENT_LOCATION1 = new LocationEvent1();

	/** Location event */
	public static LocationEvent2 EVENT_LOCATION2 = new LocationEvent2();

	/** Measurement event */
	public static MeasurementEvent1 EVENT_MEASUREMENT1 = new MeasurementEvent1();

	/** Measurement event */
	public static MeasurementEvent2 EVENT_MEASUREMENT2 = new MeasurementEvent2();

	/** Alert event */
	public static AlertEvent1 EVENT_ALERT1 = new AlertEvent1();

	/** Alert event */
	public static AlertEvent2 EVENT_ALERT2 = new AlertEvent2();

	/** Command invocation */
	public static Invocation_GetFirmwareVersion INVOCATION_GET_FW_VER = new Invocation_GetFirmwareVersion();

	/** Command invocation */
	public static Invocation_SetReportInterval INVOCATION_SET_RPT_INTV = new Invocation_SetReportInterval();

	/** Command response */
	public static Response_GetFirmwareVersion RESPONSE_GET_FW_VER = new Response_GetFirmwareVersion();

	/** Command response */
	public static Response_SetReportInterval RESPONSE_SET_RPT_INTV = new Response_SetReportInterval();

	/** Stream */
	public static Stream1 STREAM1 = new Stream1();

	/** Stream */
	public static Stream2 STREAM2 = new Stream2();

	/** Assignment */
	public static Assignment_HeartMonitorToDerek HEART_MONITOR_TO_DEREK =
			new Assignment_HeartMonitorToDerek();

	/** Batch operation */
	public static BatchOperation1 BATCH_OPERATION1 = new BatchOperation1();

	/** Batch element */
	public static BatchElement1 BATCH_ELEMENT1 = new BatchElement1();

	/** Batch element */
	public static BatchElement2 BATCH_ELEMENT2 = new BatchElement2();

	/** Batch operation */
	public static BatchOperation2 BATCH_OPERATION2 = new BatchOperation2();

	/** Device group */
	public static DeviceGroup_UnitedStates DEVICEGROUP_UNITEDSTATES = new DeviceGroup_UnitedStates();

	/** Device group */
	public static DeviceGroup_SouthEast DEVICEGROUP_SOUTHEAST = new DeviceGroup_SouthEast();

	/** Device group */
	public static DeviceGroup_NorthEast DEVICEGROUP_NORTHEAST = new DeviceGroup_NorthEast();

	/** Group element */
	public static GroupElement_Southeast GROUPELEMENT_SOUTHEAST = new GroupElement_Southeast();

	/** Group element */
	public static GroupElement_Northeast GROUPELEMENT_NORTHEAST = new GroupElement_Northeast();

	/** Group element */
	public static GroupElement_Tracker GROUPELEMENT_TRACKER = new GroupElement_Tracker();

	/** Search provider */
	public static Search_Solr SEARCH_SOLR = new Search_Solr();

	/** Authority */
	public static Auth_AdminSites AUTH_ADMIN_SITES = new Auth_AdminSites();

	/** Authority */
	public static Auth_AdminUsers AUTH_ADMIN_USERS = new Auth_AdminUsers();

	/** User */
	public static User_Admin USER_ADMIN = new User_Admin();

	/** User */
	public static User_John USER_JOHN = new User_John();

	/** Tenant */
	public static Tenant_Default TENANT_DEFAULT = new Tenant_Default();

	/** Tenant */
	public static Tenant_Merchant1 TENANT_MERCHANT1 = new Tenant_Merchant1();

	public static class Site_Construction extends Site {

		public Site_Construction() {
			setToken("bb105f8d-3150-41f5-b9d1-db04965668d3");
			setName("Construction Site");
			setDescription("Construction site that contains many heavy equipment assets");
			setCreatedBy("admin");
			setCreatedDate(new Date());
			setImageUrl("https://s3.amazonaws.com/sitewhere-demo/construction/construction.jpg");
			SiteMapData map = new SiteMapData();
			map.setType("mapquest");
			Map<String, String> mapMetadata = new HashMap<String, String>();
			mapMetadata.put("centerLatitude", "34.10469794977326");
			mapMetadata.put("centerLongitude", "-84.23966646194458");
			mapMetadata.put("zoomLevel", "15");
			map.setMetadata(mapMetadata);
			setMap(map);
			setMetadata(new HashMap<String, String>());
		}
	}

	public static class Zone_ConstructionSite extends Zone {

		public Zone_ConstructionSite() {
			setToken("6fdaa5bc-a96a-4a65-bf01-db791e038f8b");
			setSiteToken(ExampleData.SITE_CONSTRUCTION.getToken());
			setName("Construction Site");
			setBorderColor("#017112");
			setFillColor("#1db32e");
			setOpacity(0.4);
			getCoordinates().add(new Location(34.10260138703638, -84.24412965774536));
			getCoordinates().add(new Location(34.101837372446774, -84.24243450164795));
			getCoordinates().add(new Location(34.101517550337825, -84.24091100692749));
			getCoordinates().add(new Location(34.10154953265732, -84.238566756248479));
			setCreatedBy("admin");
			setCreatedDate(new Date());
		}
	}

	public static class Zone_OffLimits extends Zone {

		public Zone_OffLimits() {
			setToken("9707c31a-71b0-4fd2-8e0b-e9c7a5d249e8");
			setSiteToken(ExampleData.SITE_CONSTRUCTION.getToken());
			setName("OffLimits");
			setBorderColor("#990000");
			setFillColor("#660000");
			setOpacity(0.5);
			getCoordinates().add(new Location(34.10260138703638, -84.24412965774536));
			getCoordinates().add(new Location(34.101837372446774, -84.24243450164795));
			getCoordinates().add(new Location(34.101517550337825, -84.24091100692749));
			getCoordinates().add(new Location(34.10154953265732, -84.238566756248479));
			setCreatedBy("admin");
			setCreatedDate(new Date());
		}
	}

	public static class Site_VehicleTracking extends Site {

		public Site_VehicleTracking() {
			setToken("9707c31a-71b0-4fd2-8e0b-e9c7a5d249e83");
			setName("Vehicle Tracking Site");
			setDescription("Site that allows for tracking of a rental fleet");
			setCreatedBy("admin");
			setCreatedDate(new Date());
			setImageUrl("https://s3.amazonaws.com/sitewhere-demo/airport/airport.gif");
			SiteMapData map = new SiteMapData();
			map.setType("mapquest");
			Map<String, String> mapMetadata = new HashMap<String, String>();
			mapMetadata.put("centerLatitude", "34.10469794977326");
			mapMetadata.put("centerLongitude", "-84.23966646194458");
			mapMetadata.put("zoomLevel", "15");
			map.setMetadata(mapMetadata);
			setMap(map);
			setMetadata(new HashMap<String, String>());
		}
	}

	public static class Specification_MeiTrack extends DeviceSpecification {

		public Specification_MeiTrack() {
			setToken("82043707-9e3d-441f-bdcc-33cf0f4f7260");
			setName("MeiTrack GPS");
			setAssetModuleId(ExampleData.AC_DEVICES.getId());
			setAssetId(ExampleData.ASSET_MEITRACK.getId());
			setContainerPolicy(DeviceContainerPolicy.Standalone);
			setCreatedBy("admin");
			setCreatedDate(new Date());
			setAssetImageUrl("https://s3.amazonaws.com/sitewhere-demo/construction/meitrack/mt90.jpg");
		}
	}

	public static class Specification_HeartMonitor extends DeviceSpecification {

		public Specification_HeartMonitor() {
			setToken("2f540b66-b6ab-4fbe-bdf3-ca6aaf103848");
			setName("Heart Monitor");
			setAssetModuleId(ExampleData.AM_PERSONS.getId());
			setAssetId(ExampleData.ASSET_CATERPILLAR.getId());
			setContainerPolicy(DeviceContainerPolicy.Standalone);
			setCreatedBy("admin");
			setCreatedDate(new Date());
			setAssetImageUrl("https://s3.amazonaws.com/sitewhere-demo/construction/health/heartmonitor.jpg");
		}
	}

	public static class Command_GetFirmwareVersion extends DeviceCommand {

		public Command_GetFirmwareVersion() {
			setToken("3c1c61a3-652f-407e-80e7-fcfb13c10624");
			setName("getFirmwareVersion");
			setDescription("Get version of device firmware.");
			setNamespace("http://mycompany.com/devices");
			setSpecificationToken(ExampleData.SPEC_MEITRACK.getToken());
			setCreatedBy("admin");
			setCreatedDate(new Date());
			CommandParameter verbose = new CommandParameter();
			verbose.setName("verbose");
			verbose.setType(ParameterType.Bool);
			verbose.setRequired(false);
			getParameters().add(verbose);
		}
	}

	public static class Invocation_GetFirmwareVersion extends DeviceCommandInvocation {

		public Invocation_GetFirmwareVersion() {
			setId("230983938938");
			setEventType(DeviceEventType.CommandInvocation);
			setSiteToken(SITE_CONSTRUCTION.getToken());
			setDeviceAssignmentToken(TRACKER_TO_DEREK.getToken());
			setAssignmentType(TRACKER_TO_DEREK.getAssignmentType());
			setAssetModuleId(TRACKER_TO_DEREK.getAssetModuleId());
			setAssetId(TRACKER_TO_DEREK.getAssetId());
			setEventDate(new Date());
			setReceivedDate(new Date());

			setInitiator(CommandInitiator.REST);
			setInitiatorId("admin");
			setTarget(CommandTarget.Assignment);
			setCommandToken(ExampleData.COMMAND_GET_FW_VER.getToken());
			getParameterValues().put("verbose", "true");
		}
	}

	public static class Command_SetReportInterval extends DeviceCommand {

		public Command_SetReportInterval() {
			setToken("2a3a344d-f09b-44a7-b36b-afb04993eb414");
			setName("setReportInterval");
			setDescription("Set the device reporting interval (in seconds).");
			setNamespace("http://mycompany.com/devices");
			setSpecificationToken(ExampleData.SPEC_MEITRACK.getToken());
			setCreatedBy("admin");
			setCreatedDate(new Date());
			CommandParameter interval = new CommandParameter();
			interval.setName("interval");
			interval.setType(ParameterType.Int32);
			interval.setRequired(true);
			getParameters().add(interval);
			CommandParameter reboot = new CommandParameter();
			reboot.setName("reboot");
			reboot.setType(ParameterType.Bool);
			reboot.setRequired(false);
			getParameters().add(reboot);
		}
	}

	public static class Invocation_SetReportInterval extends DeviceCommandInvocation {

		public Invocation_SetReportInterval() {
			setId("239402938454");
			setEventType(DeviceEventType.CommandInvocation);
			setSiteToken(SITE_CONSTRUCTION.getToken());
			setDeviceAssignmentToken(TRACKER_TO_DEREK.getToken());
			setAssignmentType(TRACKER_TO_DEREK.getAssignmentType());
			setAssetModuleId(TRACKER_TO_DEREK.getAssetModuleId());
			setAssetId(TRACKER_TO_DEREK.getAssetId());
			setEventDate(new Date());
			setReceivedDate(new Date());

			setInitiator(CommandInitiator.REST);
			setInitiatorId("admin");
			setTarget(CommandTarget.Assignment);
			setCommandToken(ExampleData.COMMAND_SET_RPT_INTV.getToken());
			getParameterValues().put("interval", "60");
			getParameterValues().put("reboot", "true");
		}
	}

	public static class Response_SetReportInterval extends DeviceCommandResponse {

		public Response_SetReportInterval() {
			setId("287494894849");
			setEventType(DeviceEventType.CommandResponse);
			setSiteToken(SITE_CONSTRUCTION.getToken());
			setDeviceAssignmentToken(TRACKER_TO_DEREK.getToken());
			setAssignmentType(TRACKER_TO_DEREK.getAssignmentType());
			setAssetModuleId(TRACKER_TO_DEREK.getAssetModuleId());
			setAssetId(TRACKER_TO_DEREK.getAssetId());
			setEventDate(new Date());
			setReceivedDate(new Date());

			setOriginatingEventId(ExampleData.INVOCATION_SET_RPT_INTV.getId());
			setResponseEventId(ExampleData.EVENT_ALERT1.getId());
		}
	}

	public static class Response_GetFirmwareVersion extends DeviceCommandResponse {

		public Response_GetFirmwareVersion() {
			setId("254449849440");
			setEventType(DeviceEventType.CommandResponse);
			setSiteToken(SITE_CONSTRUCTION.getToken());
			setDeviceAssignmentToken(TRACKER_TO_DEREK.getToken());
			setAssignmentType(TRACKER_TO_DEREK.getAssignmentType());
			setAssetModuleId(TRACKER_TO_DEREK.getAssetModuleId());
			setAssetId(TRACKER_TO_DEREK.getAssetId());
			setEventDate(new Date());
			setReceivedDate(new Date());

			setOriginatingEventId(ExampleData.INVOCATION_GET_FW_VER.getId());
			setResponse("Firmware 1.1.0 (1.1.0.201512310800)");
		}
	}

	public static class Device_Tracker extends Device {

		public Device_Tracker() {
			setHardwareId("b6daecc5-b0b2-48a8-90ab-4c4a170dd2a0");
			setSiteToken(ExampleData.SITE_CONSTRUCTION.getToken());
			setSpecificationToken(ExampleData.SPEC_MEITRACK.getToken());
			setAssignmentToken("1ad74fe3-2cbf-443f-aede-9ec70a9a4ab5");
			setComments("Equipment tracker.");
			setCreatedBy("admin");
			setCreatedDate(new Date());
		}
	}

	public static class Device_Tracker2 extends Device {

		public Device_Tracker2() {
			setHardwareId("02efd10-da41-4b06-81fe-d478188daf0e");
			setSiteToken(ExampleData.SITE_CONSTRUCTION.getToken());
			setSpecificationToken(ExampleData.SPEC_MEITRACK.getToken());
			setComments("Equipment tracker 2.");
			setCreatedBy("admin");
			setCreatedDate(new Date());
		}
	}

	public static class Device_HeartMonitor extends Device {

		public Device_HeartMonitor() {
			setHardwareId("bfca26f1-2b33-449c-8335-78ff5852e326");
			setSiteToken(ExampleData.SITE_CONSTRUCTION.getToken());
			setSpecificationToken(ExampleData.SPEC_HEART_MONITOR.getToken());
			setComments("Tracks vital statistics including heart rate.");
			setCreatedBy("admin");
			setCreatedDate(new Date());
		}
	}

	public static class Mapping_HeartMonitor extends DeviceElementMapping {

		public Mapping_HeartMonitor() {
			setHardwareId(ExampleData.HEART_MONITOR.getHardwareId());
			setDeviceElementSchemaPath("/sensors/vitals");
		}
	}

	public static class Assignment_TrackerToDerek extends DeviceAssignment {

		public Assignment_TrackerToDerek() {
			setToken("1ad74fe3-2cbf-443f-aede-9ec70a9a4ab5");
			setDeviceHardwareId(ExampleData.TRACKER.getHardwareId());
			setSiteToken(ExampleData.SITE_CONSTRUCTION.getToken());
			setAssignmentType(DeviceAssignmentType.Associated);
			setAssetModuleId(ExampleData.AM_PERSONS.getId());
			setAssetId(ExampleData.ASSET_DEREK.getId());
			setActiveDate(new Date());
			setCreatedBy("admin");
			setCreatedDate(new Date());
			setStatus(DeviceAssignmentStatus.Active);
		}
	}

	public static class Assignment_TrackerToMartin extends DeviceAssignment {

		public Assignment_TrackerToMartin() {
			setToken("b91b8f1a-7040-475a-bf05-275418b335d8");
			setDeviceHardwareId(ExampleData.TRACKER.getHardwareId());
			setSiteToken(ExampleData.SITE_CONSTRUCTION.getToken());
			setAssignmentType(DeviceAssignmentType.Associated);
			setAssetModuleId(ExampleData.AM_PERSONS.getId());
			setAssetId(ExampleData.ASSET_MARTIN.getId());
			setActiveDate(new Date());
			setCreatedBy("admin");
			setCreatedDate(new Date());
			setStatus(DeviceAssignmentStatus.Released);
			setReleasedDate(new Date());
		}
	}

	public static class LocationEvent1 extends DeviceLocation {

		public LocationEvent1() {
			setId("203948023656");
			setEventType(DeviceEventType.Location);
			setSiteToken(SITE_CONSTRUCTION.getToken());
			setDeviceAssignmentToken(TRACKER_TO_DEREK.getToken());
			setAssignmentType(TRACKER_TO_DEREK.getAssignmentType());
			setAssetModuleId(TRACKER_TO_DEREK.getAssetModuleId());
			setAssetId(TRACKER_TO_DEREK.getAssetId());
			setEventDate(new Date());
			setReceivedDate(new Date());
			setLatitude(34.103270338359664);
			setLongitude(-84.23874458667342);
			setElevation(0.0);
		}
	}

	public static class LocationEvent2 extends DeviceLocation {

		public LocationEvent2() {
			setId("230984233904");
			setEventType(DeviceEventType.Location);
			setSiteToken(SITE_CONSTRUCTION.getToken());
			setDeviceAssignmentToken(TRACKER_TO_DEREK.getToken());
			setAssignmentType(TRACKER_TO_DEREK.getAssignmentType());
			setAssetModuleId(TRACKER_TO_DEREK.getAssetModuleId());
			setAssetId(TRACKER_TO_DEREK.getAssetId());
			setEventDate(new Date());
			setReceivedDate(new Date());
			setLatitude(34.10345084984494);
			setLongitude(-84.23983484847486);
			setElevation(0.1);
		}
	}

	public static class MeasurementEvent1 extends DeviceMeasurements {

		public MeasurementEvent1() {
			setId("234203504574");
			setEventType(DeviceEventType.Measurements);
			setSiteToken(SITE_CONSTRUCTION.getToken());
			setDeviceAssignmentToken(TRACKER_TO_DEREK.getToken());
			setAssignmentType(TRACKER_TO_DEREK.getAssignmentType());
			setAssetModuleId(TRACKER_TO_DEREK.getAssetModuleId());
			setAssetId(TRACKER_TO_DEREK.getAssetId());
			setEventDate(new Date());
			setReceivedDate(new Date());
			addOrReplaceMeasurement("fuel.level", 32.68);
			addOrReplaceMeasurement("engine.temperature", 86.21);
		}
	}

	public static class MeasurementEvent2 extends DeviceMeasurements {

		public MeasurementEvent2() {
			setId("230948239489");
			setEventType(DeviceEventType.Measurements);
			setSiteToken(SITE_CONSTRUCTION.getToken());
			setDeviceAssignmentToken(TRACKER_TO_DEREK.getToken());
			setAssignmentType(TRACKER_TO_DEREK.getAssignmentType());
			setAssetModuleId(TRACKER_TO_DEREK.getAssetModuleId());
			setAssetId(TRACKER_TO_DEREK.getAssetId());
			setEventDate(new Date());
			setReceivedDate(new Date());
			addOrReplaceMeasurement("fuel.level", 33.78);
			addOrReplaceMeasurement("engine.temperature", 84.26);
		}
	}

	public static class AlertEvent1 extends DeviceAlert {

		public AlertEvent1() {
			setId("239472398473");
			setEventType(DeviceEventType.Alert);
			setSiteToken(SITE_CONSTRUCTION.getToken());
			setDeviceAssignmentToken(TRACKER_TO_DEREK.getToken());
			setAssignmentType(TRACKER_TO_DEREK.getAssignmentType());
			setAssetModuleId(TRACKER_TO_DEREK.getAssetModuleId());
			setAssetId(TRACKER_TO_DEREK.getAssetId());
			setEventDate(new Date());
			setReceivedDate(new Date());
			setSource(AlertSource.Device);
			setLevel(AlertLevel.Warning);
			setType("engine.overheat");
			setMessage("Engine temperature is at top of operating range.");
		}
	}

	public static class AlertEvent2 extends DeviceAlert {

		public AlertEvent2() {
			setId("230493483398");
			setEventType(DeviceEventType.Alert);
			setSiteToken(SITE_CONSTRUCTION.getToken());
			setDeviceAssignmentToken(TRACKER_TO_DEREK.getToken());
			setAssignmentType(TRACKER_TO_DEREK.getAssignmentType());
			setAssetModuleId(TRACKER_TO_DEREK.getAssetModuleId());
			setAssetId(TRACKER_TO_DEREK.getAssetId());
			setEventDate(new Date());
			setReceivedDate(new Date());
			setSource(AlertSource.Device);
			setLevel(AlertLevel.Critical);
			setType("fuel.level");
			setMessage("Fuel level is critical.");
		}
	}

	public static class Stream1 extends DeviceStream {

		public Stream1() {
			setAssignmentToken(TRACKER_TO_DEREK.getToken());
			setStreamId("video-stream-1");
			setContentType("video/mpeg");
			setCreatedBy("admin");
			setCreatedDate(new Date());
		}
	}

	public static class Stream2 extends DeviceStream {

		public Stream2() {
			setAssignmentToken(TRACKER_TO_DEREK.getToken());
			setStreamId("video-stream-2");
			setContentType("video/mpeg");
			setCreatedBy("admin");
			setCreatedDate(new Date());
		}
	}

	public static class Assignment_HeartMonitorToDerek extends DeviceAssignment {

		public Assignment_HeartMonitorToDerek() {
			setToken("d8c5c9f8-2b48-448d-89a4-4906ced63779");
			setDeviceHardwareId(ExampleData.HEART_MONITOR.getHardwareId());
			setSiteToken(ExampleData.SITE_CONSTRUCTION.getToken());
			setAssignmentType(DeviceAssignmentType.Associated);
			setAssetModuleId(ExampleData.AM_PERSONS.getId());
			setAssetId(ExampleData.ASSET_DEREK.getId());
			setActiveDate(new Date());
			setCreatedBy("admin");
			setCreatedDate(new Date());
			setStatus(DeviceAssignmentStatus.Active);
		}
	}

	public static class AssetModule_Devices extends AssetModule {

		public AssetModule_Devices() {
			setId("ac-devices");
			setName("Default Device Management");
			setAssetType(AssetType.Device);
		}
	}

	public static class AssetModule_Persons extends AssetModule {

		public AssetModule_Persons() {
			setId("ac-persons");
			setName("Default Identity Management");
			setAssetType(AssetType.Person);
		}
	}

	public static class AssetCategory_Devices extends AssetCategory {

		public AssetCategory_Devices() {
			setId("my-devices");
			setName("My Devices");
			setAssetType(AssetType.Device);
		}
	}

	public static class AssetCategory_Persons extends AssetCategory {

		public AssetCategory_Persons() {
			setId("developers");
			setName("Developers");
			setAssetType(AssetType.Person);
		}
	}

	public static class Device_MeiTrack extends HardwareAsset {

		public Device_MeiTrack() {
			setId("1");
			setName("MeiTrack Location Tracker");
			setType(AssetType.Hardware);
			setImageUrl("https://s3.amazonaws.com/sitewhere-demo/construction/cat/cat-320e.jpg");
			setSku("M-TRACK");
			setDescription("Tracking device.");
		}
	}

	public static class Person_Derek extends PersonAsset {

		public Person_Derek() {
			setId("derek");
			setName("Derek Adams");
			setUserName("dadams");
			setEmailAddress("dadams@demoserver.com");
			setImageUrl("https://s3.amazonaws.com/sitewhere-demo/people/derek.jpg");
			getProperties().put("phone.number", "777-555-1212");
			getRoles().add("dev");
		}
	}

	public static class Person_Martin extends PersonAsset {

		public Person_Martin() {
			setId("martin");
			setName("Martin Weber");
			setUserName("mweber");
			setEmailAddress("martin@demoserver.com");
			setImageUrl("https://s3.amazonaws.com/sitewhere-demo/people/martin.jpg");
			getProperties().put("phone.number", "777-770-1212");
			getRoles().add("busdev");
		}
	}

	public static class Hardware_Caterpillar extends HardwareAsset {

		public Hardware_Caterpillar() {
			setId("303");
			setName("Caterpillar 320E L Excavator");
			setType(AssetType.Hardware);
			setImageUrl("https://s3.amazonaws.com/sitewhere-demo/construction/cat/cat-320e.jpg");
			setSku("CAT-320E-L");
			setDescription("A great all-around excavator, the 320E is easy to maneuver, yet powerful enough to handle tough jobs. Lift capacity has been improved by 5 percent on the standard machine and up to 20 percent on the Heavy Lift configuration. A lower emissions engine boosts fuel efficiency, while other new features enhance safety and productivity, cut service time and reduce operating costs.");
			getProperties().put("operating.weight", "54450");
			getProperties().put("manufacturer", "Caterpillar");
			getProperties().put("net.power", "153");
			getProperties().put("fuel.tank", "108.3");
		}
	}

	public static class Hardware_Meitrack extends HardwareAsset {

		public Hardware_Meitrack() {
			setId("175");
			setName("MeiTrack MT90");
			setType(AssetType.Device);
			setImageUrl("https://s3.amazonaws.com/sitewhere-demo/construction/meitrack/mt90.jpg");
			setSku("MT90");
			setDescription("MT90 is a waterproof GPS personal tracker suitable for lone workers, kids, aged, pet, assets, vehicle and fleet management.");
			getProperties().put("manufacturer", "MeiTrack");
			getProperties().put("weight", "1.000");
			getProperties().put("sos.button", "true");
		}
	}

	public static class Location_Trailer extends LocationAsset {

		public Location_Trailer() {
			setId("ct1");
			setName("Construction Trailer");
			setType(AssetType.Location);
			setImageUrl("https://s3.amazonaws.com/sitewhere-demo/construction/trailer.jpg");
			setLatitude(33.755);
			setLongitude(-84.39);
			getProperties().put("worksite.id", "GA-ATL-101-Peachtree");
		}
	}

	public static class BatchOperation1 extends BatchOperation {

		public BatchOperation1() {
			setCreatedDate(new Date());
			setCreatedBy("admin");
			setUpdatedDate(new Date());
			setUpdatedBy("system");
			setToken("27f65236-ae80-40fe-8634-3a9781077754");
			setOperationType(OperationType.InvokeCommand);
			getParameters().put("commandToken", ExampleData.COMMAND_SET_RPT_INTV.getToken());
			getMetadata().put("interval", "60");
			getMetadata().put("reboot", "true");
			setProcessingStatus(BatchOperationStatus.FinishedSuccessfully);
			setProcessingStartedDate(new Date());
			setProcessingEndedDate(new Date());
		}
	}

	public static class BatchElement1 extends BatchElement {

		public BatchElement1() {
			setBatchOperationToken(ExampleData.BATCH_OPERATION1.getToken());
			setIndex(0);
			setHardwareId(ExampleData.TRACKER.getHardwareId());
			setProcessingStatus(ElementProcessingStatus.Succeeded);
			setProcessedDate(new Date());
			getMetadata().put("invocation", ExampleData.INVOCATION_SET_RPT_INTV.getId());
		}
	}

	public static class BatchElement2 extends BatchElement {

		public BatchElement2() {
			setBatchOperationToken(ExampleData.BATCH_OPERATION1.getToken());
			setIndex(1);
			setHardwareId(ExampleData.HEART_MONITOR.getHardwareId());
			setProcessingStatus(ElementProcessingStatus.Unprocessed);
		}
	}

	public static class BatchOperation2 extends BatchOperation {

		public BatchOperation2() {
			setCreatedDate(new Date());
			setCreatedBy("admin");
			setUpdatedDate(new Date());
			setUpdatedBy("system");
			setToken("dcb73dd2-4b3f-4c00-a73a-61b974cae6a9");
			setOperationType(OperationType.InvokeCommand);
			getParameters().put("commandToken", ExampleData.COMMAND_GET_FW_VER.getToken());
			getMetadata().put("verbose", "true");
			setProcessingStatus(BatchOperationStatus.FinishedSuccessfully);
			setProcessingStartedDate(new Date());
			setProcessingEndedDate(new Date());
		}
	}

	public static class DeviceGroup_UnitedStates extends DeviceGroup {

		public DeviceGroup_UnitedStates() {
			setCreatedBy("admin");
			setCreatedDate(new Date());
			setToken("38745f7f-b5c6-477a-8388-48844ee38862");
			setName("United States");
			setDescription("Devices in the United States region.");
			setRoles(Arrays.asList(new String[] { "region", "americas" }));
		}
	}

	public static class DeviceGroup_SouthEast extends DeviceGroup {

		public DeviceGroup_SouthEast() {
			setCreatedBy("admin");
			setCreatedDate(new Date());
			setToken("24288cbd-e8aa-4b35-a6b3-27e24a123718");
			setName("Southeast");
			setDescription("Devices in the southeast region.");
			setRoles(Arrays.asList(new String[] { "region", "americas" }));
		}
	}

	public static class GroupElement_Southeast extends DeviceGroupElement {

		public GroupElement_Southeast() {
			setGroupToken(ExampleData.DEVICEGROUP_UNITEDSTATES.getToken());
			setIndex(0);
			setType(GroupElementType.Group);
			setElementId(ExampleData.DEVICEGROUP_SOUTHEAST.getToken());
			getRoles().add("region");
		}
	}

	public static class GroupElement_Northeast extends DeviceGroupElement {

		public GroupElement_Northeast() {
			setGroupToken(ExampleData.DEVICEGROUP_UNITEDSTATES.getToken());
			setIndex(1);
			setType(GroupElementType.Group);
			setElementId(ExampleData.DEVICEGROUP_NORTHEAST.getToken());
			getRoles().add("region");
		}
	}

	public static class GroupElement_Tracker extends DeviceGroupElement {

		public GroupElement_Tracker() {
			setGroupToken(ExampleData.DEVICEGROUP_UNITEDSTATES.getToken());
			setIndex(2);
			setType(GroupElementType.Device);
			setElementId(ExampleData.TRACKER.getHardwareId());
			getRoles().add("standalone");
		}
	}

	public static class DeviceGroup_NorthEast extends DeviceGroup {

		public DeviceGroup_NorthEast() {
			setCreatedBy("admin");
			setCreatedDate(new Date());
			setToken("8e560981-f3a4-4f31-ada4-c72e69984179");
			setName("Northeast");
			setDescription("Devices in the northeast region.");
			setRoles(Arrays.asList(new String[] { "region", "americas" }));
		}
	}

	public static class Search_Solr extends SearchProvider {

		public Search_Solr() {
			setId("solr");
			setName("Apache Solr");
		}
	}

	public static class User_Admin extends User {

		public User_Admin() {
			setUsername("admin");
			setFirstName("Admin");
			setLastName("User");
			setHashedPassword("5baa61e4c9b93f3f0682250b6cf8331b7ee68fd8");
			setStatus(AccountStatus.Active);
			getAuthorities().add(ISiteWhereAuthorities.AUTH_ADMIN_SITES);
			getAuthorities().add(ISiteWhereAuthorities.AUTH_ADMIN_USERS);
			getAuthorities().add(ISiteWhereAuthorities.AUTH_AUTHENTICATED_USER);
			setCreatedBy("system");
			setCreatedDate(new Date());
			setLastLogin(new Date());
		}
	}

	public static class User_John extends User {
		

		public User_John() {
			setUsername("jdoe");
			setFirstName("John");
			setLastName("Doe");
			setHashedPassword("5baa61e4c9b93f3f0682250b6cf8331b7ee68fd8");
			setStatus(AccountStatus.Locked);
			getAuthorities().add(ISiteWhereAuthorities.AUTH_AUTHENTICATED_USER);
			setCreatedBy("admin");
			setCreatedDate(new Date());
			setLastLogin(new Date());
			getMetadata().put("phone.number", "777-555-1212");
		}
	}

	public static class Auth_AdminSites extends GrantedAuthority {

		public Auth_AdminSites() {
			setAuthority(ISiteWhereAuthorities.AUTH_ADMIN_SITES);
			setDescription("Administer sites");
		}
	}

	public static class Auth_AdminUsers extends GrantedAuthority {

		public Auth_AdminUsers() {
			setAuthority(ISiteWhereAuthorities.AUTH_ADMIN_USERS);
			setDescription("Administer users");
		}
	}

	public static class Tenant_Default extends Tenant {

		public Tenant_Default() {
			setId("default");
			setName("Default Tenant");
			setAuthenticationToken("sitewhere1234567890");
			setLogoUrl("https://s3.amazonaws.com/sitewhere-demo/sitewhere-small.png");
			getAuthorizedUserIds().add("admin");
			setCreatedBy("admin");
			setCreatedDate(new Date());
		}
	}

	public static class Tenant_Merchant1 extends Tenant {

		public Tenant_Merchant1() {
			setId("merchant1");
			setName("Merchant1");
			setAuthenticationToken("m1-349384344839");
			setLogoUrl("https://s3.amazonaws.com/sitewhere-demo/sitewhere-small.png");
			getAuthorizedUserIds().add("admin");
			setCreatedBy("admin");
			setCreatedDate(new Date());
		}
	}
}