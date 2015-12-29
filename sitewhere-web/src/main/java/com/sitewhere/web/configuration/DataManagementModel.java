/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.configuration;

import com.sitewhere.spring.handler.TenantConfigurationParser;
import com.sitewhere.spring.handler.TenantDatastoreParser;
import com.sitewhere.web.configuration.model.AttributeNode;
import com.sitewhere.web.configuration.model.AttributeType;
import com.sitewhere.web.configuration.model.ConfigurationModel;
import com.sitewhere.web.configuration.model.ElementNode;
import com.sitewhere.web.configuration.model.ElementRole;

/**
 * Configuration model for data management elements.
 * 
 * @author Derek
 */
public class DataManagementModel extends ConfigurationModel {

	public DataManagementModel() {
		addElement(createDataManagement());

		// Datastore implementations.
		addElement(createMongoTenantDatastoreElement());
		addElement(createHBaseTenantDatastoreElement());

		// Cache implementations.
		addElement(createHazelcastCacheElement());
		addElement(createEHCacheElement());

		// Model initializers.
		addElement(createDefaultDeviceModelInitializerElement());
		addElement(createDefaultAssetModelInitializerElement());
		addElement(createDefaultScheduleModelInitializerElement());
	}

	/**
	 * Create the container for datastore information.
	 * 
	 * @return
	 */
	protected ElementNode createDataManagement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Data Management",
						TenantConfigurationParser.Elements.TenantDatastore.getLocalName(), "database",
						ElementRole.DataManagement);
		builder.setDescription("Configure the datastore and related aspects such as caching and "
				+ "data model initialization.");
		return builder.build();
	}

	/**
	 * Create element configuration for MonogoDB tenant datastore.
	 * 
	 * @return
	 */
	protected ElementNode createMongoTenantDatastoreElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("MongoDB Tenant Datastore",
						TenantDatastoreParser.Elements.MongoTenantDatastore.getLocalName(), "database",
						ElementRole.DataManagement_Datastore);

		builder.setDescription("Store tenant data using a MongoDB database.");
		builder.addAttribute((new AttributeNode.Builder("Use bulk inserts", "useBulkEventInserts",
				AttributeType.Boolean).setDescription("Use the MongoDB bulk insert API to add "
				+ "events in groups and improve performance.").build()));
		builder.addAttribute((new AttributeNode.Builder("Bulk insert max chunk size",
				"bulkInsertMaxChunkSize", AttributeType.Integer).setDescription("Maximum number of records to send "
				+ "in a single bulk insert (if bulk inserts are enabled).").build()));
		return builder.build();
	}

	/**
	 * Create element configuration for HBase tenant datastore.
	 * 
	 * @return
	 */
	protected ElementNode createHBaseTenantDatastoreElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("HBase Tenant Datastore",
						TenantDatastoreParser.Elements.HBaseTenantDatastore.getLocalName(), "database",
						ElementRole.DataManagement_Datastore);
		builder.setDescription("Store tenant data using tables in an HBase instance.");
		return builder.build();
	}

	/**
	 * Create element configuration for Hazelcast cache.
	 * 
	 * @return
	 */
	protected ElementNode createHazelcastCacheElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Hazelcast Distributed Cache Provider",
						TenantDatastoreParser.Elements.HazelcastCache.getLocalName(), "folder-open-o",
						ElementRole.DataManagement_CacheProvider);
		builder.setDescription("Cache device management data using Hazelcast distributed maps. "
				+ "This cache allows data to be shared between clustered SiteWhere instances.");
		return builder.build();
	}

	/**
	 * Create element configuration for EHCache cache.
	 * 
	 * @return
	 */
	protected ElementNode createEHCacheElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("EHCache Cache Provider",
						TenantDatastoreParser.Elements.EHCacheDeviceManagementCache.getLocalName(),
						"folder-open-o", ElementRole.DataManagement_CacheProvider);
		builder.setDescription("Cache device management data using EHCache. Note that this "
				+ "cache is not intended for use on clustered installations.");
		builder.addAttribute((new AttributeNode.Builder("Site cache max entries", "siteCacheMaxEntries",
				AttributeType.Integer).setDescription("Maximum number entries in site cache.").setDefaultValue(
				"1000").setGroup("Cache Max Entries").build()));
		builder.addAttribute((new AttributeNode.Builder("Specification cache max entries",
				"deviceSpecificationCacheMaxEntries", AttributeType.Integer).setDescription(
				"Maximum number entries in device specification cache.").setDefaultValue("1000").setGroup(
				"Cache Max Entries").build()));
		builder.addAttribute((new AttributeNode.Builder("Device cache max entries", "deviceCacheMaxEntries",
				AttributeType.Integer).setDescription("Maximum number entries in device cache.").setDefaultValue(
				"10000").setGroup("Cache Max Entries").build()));
		builder.addAttribute((new AttributeNode.Builder("Assignment cache max entries",
				"deviceAssignmentCacheMaxEntries", AttributeType.Integer).setDescription(
				"Maximum number entries in device assignment cache.").setDefaultValue("10000").setGroup(
				"Cache Max Entries").build()));

		builder.addAttribute((new AttributeNode.Builder("Site cache TTL seconds", "siteCacheTtl",
				AttributeType.Integer).setDescription("Maximum time to live (in seconds) for site cache.").setDefaultValue(
				"6000").setGroup("Cache Time to Live").build()));
		builder.addAttribute((new AttributeNode.Builder("Specification cache TTL seconds",
				"deviceSpecificationCacheTtl", AttributeType.Integer).setDescription(
				"Maximum time to live (in seconds) for device specification cache.").setDefaultValue("6000").setGroup(
				"Cache Time to Live").build()));
		builder.addAttribute((new AttributeNode.Builder("Device cache TTL seconds", "deviceCacheTtl",
				AttributeType.Integer).setDescription("Maximum time to live (in seconds) for device cache.").setDefaultValue(
				"600").setGroup("Cache Time to Live").build()));
		builder.addAttribute((new AttributeNode.Builder("Assignment cache TTL seconds",
				"deviceAssignmentCacheTtl", AttributeType.Integer).setDescription(
				"Maximum time to live (in seconds) for device assignment cache.").setDefaultValue("600").setGroup(
				"Cache Time to Live").build()));
		return builder.build();
	}

	/**
	 * Create element configuration for device model initializer.
	 * 
	 * @return
	 */
	protected ElementNode createDefaultDeviceModelInitializerElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Default Device Model Initializer",
						TenantDatastoreParser.Elements.DefaultDeviceModelInitializer.getLocalName(), "flash",
						ElementRole.DataManagement_DeviceModelInitializer);
		builder.setDescription("This component creates sample data when no existing device data "
				+ "is detected in the datastore. A site with device specifications, devices, "
				+ "assignments, events and other example data is created on instance startup.");
		return builder.build();
	}

	/**
	 * Create element configuration for device model initializer.
	 * 
	 * @return
	 */
	protected ElementNode createDefaultAssetModelInitializerElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Default Asset Model Initializer",
						TenantDatastoreParser.Elements.DefaultAssetModelInitializer.getLocalName(), "flash",
						ElementRole.DataManagement_AssetModelInitializer);
		builder.setDescription("This component creates sample data when no existing asset data "
				+ "is detected in the datastore. If using the <strong>device model initializer</strong> "
				+ "this component should be used as well so that assets in the sample data can be resolved.");
		return builder.build();
	}

	/**
	 * Create element configuration for device model initializer.
	 * 
	 * @return
	 */
	protected ElementNode createDefaultScheduleModelInitializerElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Default Schedule Model Initializer",
						TenantDatastoreParser.Elements.DefaultScheduleModelInitializer.getLocalName(),
						"flash", ElementRole.DataManagement_ScheduleModelInitializer);
		builder.setDescription("This component creates sample data when no existing schedule data "
				+ "is detected in the datastore. It provides examples of both simple and cron-based "
				+ "schedules that are commonly used.");
		return builder.build();
	}
}