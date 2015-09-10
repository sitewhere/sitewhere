/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.sitewhere.rest.model.asset.AssetModule;
import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.asset.LocationAsset;
import com.sitewhere.rest.model.asset.PersonAsset;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceSpecification;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.SiteMapData;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.DeviceContainerPolicy;

@SuppressWarnings("serial")
public class ExampleData {

	/** Persons asset module */
	public static AssetModule_Persons AM_PERSONS = new AssetModule_Persons();

	/** Person asset */
	public static Person_Derek ASSET_DEREK = new Person_Derek();

	/** Person asset */
	public static Person_Martin ASSET_MARTIN = new Person_Martin();

	/** Location asset */
	public static Location_Trailer ASSET_TRAILER = new Location_Trailer();

	/** Hardware asset */
	public static Hardware_Caterpillar ASSET_CATERPILLAR = new Hardware_Caterpillar();

	/** Site */
	public static Site_Construction SITE_CONSTRUCTION = new Site_Construction();

	/** Device specification */
	public static Specification_MeiTrack SPEC_MEITRACK = new Specification_MeiTrack();

	/** Device specification */
	public static Specification_HeartMonitor SPEC_HEART_MONITOR = new Specification_HeartMonitor();

	/** Devices */
	public static Device_Tracker1 TRACKER1 = new Device_Tracker1();

	/** Devices */
	public static Device_HeartMonitor HEART_MONITOR = new Device_HeartMonitor();

	/** Assignment */
	public static Assignment_TrackerToDerek TRACKER_TO_DEREK = new Assignment_TrackerToDerek();

	/** Assignment */
	public static Assignment_HeartMonitorToDerek HEART_MONITOR_TO_DEREK =
			new Assignment_HeartMonitorToDerek();

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

	public static class Specification_MeiTrack extends DeviceSpecification {

		public Specification_MeiTrack() {
			setToken("82043707-9e3d-441f-bdcc-33cf0f4f7260");
			setName("MeiTrack GPS");
			setAssetModuleId("fs-devices");
			setAssetId("175");
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
			setAssetModuleId("fs-devices");
			setAssetId("333");
			setContainerPolicy(DeviceContainerPolicy.Standalone);
			setCreatedBy("admin");
			setCreatedDate(new Date());
			setAssetImageUrl("https://s3.amazonaws.com/sitewhere-demo/construction/health/heartmonitor.jpg");
		}
	}

	public static class Device_Tracker1 extends Device {

		public Device_Tracker1() {
			setHardwareId("b6daecc5-b0b2-48a8-90ab-4c4a170dd2a0");
			setSiteToken(ExampleData.SITE_CONSTRUCTION.getToken());
			setSpecificationToken(ExampleData.SPEC_MEITRACK.getToken());
			setComments("Equipment tracker.");
			setAssetId("175");
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
			setAssetId("333");
			setCreatedBy("admin");
			setCreatedDate(new Date());
		}
	}

	public static class Assignment_TrackerToDerek extends DeviceAssignment {

		public Assignment_TrackerToDerek() {
			setToken("1ad74fe3-2cbf-443f-aede-9ec70a9a4ab5");
			setDeviceHardwareId(ExampleData.TRACKER1.getHardwareId());
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

	public static class Assignment_TrackerToMartin extends DeviceAssignment {

		public Assignment_TrackerToMartin() {
			setToken("1ad74fe3-2cbf-443f-aede-9ec70a9a4ab5");
			setDeviceHardwareId(ExampleData.TRACKER1.getHardwareId());
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

	public static class AssetModule_Persons extends AssetModule {

		public AssetModule_Persons() {
			setId("ac-persons");
			setName("Default Identity Management");
			setAssetType(AssetType.Person);
		}
	}

	public static class Person_Derek extends PersonAsset {

		public Person_Derek() {
			setId("derek");
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

	public static class Location_Trailer extends LocationAsset {

		public Location_Trailer() {
			setId("ct1");
			setName("Construction Trailer");
			setType(AssetType.Location);
			setImageUrl("https://s3.amazonaws.com/sitewhere-demo/construction/trailer.jpg");
			setLatitude(33.755);
			setLongitude(-84.39);
		}
	}
}