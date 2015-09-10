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
import com.sitewhere.rest.model.asset.PersonAsset;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.SiteMapData;
import com.sitewhere.spi.asset.AssetType;

@SuppressWarnings("serial")
public class ExampleData {

	/** Site */
	public static Site_Construction SITE_CONSTRUCTION = new Site_Construction();

	/** Devices */
	public static Device_Tracker1 TRACKER1 = new Device_Tracker1();

	/** Persons asset module */
	public static AssetModule_Persons AM_PERSONS = new AssetModule_Persons();

	/** Person asset */
	public static Person_Derek ASSET_DEREK = new Person_Derek();

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

	public static class Device_Tracker1 extends Device {

		public Device_Tracker1() {
			setHardwareId("b6daecc5-b0b2-48a8-90ab-4c4a170dd2a0");
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
			setEmailAddress("dadams@sitewhere.com");
			setImageUrl("https://s3.amazonaws.com/sitewhere-demo/people/derek.jpg");
			getProperties().put("phone.number", "777-555-1212");
			getRoles().add("dev");
		}
	}
}