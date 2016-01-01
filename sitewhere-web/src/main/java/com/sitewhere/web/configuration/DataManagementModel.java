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
		builder.description("Configure the datastore and related aspects such as caching and "
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

		builder.description("Store tenant data using a MongoDB database. Note that the "
				+ "global datastore must be configured to use MongoDB if this tenant datastore is to "
				+ "be used. Most core MongoDB settings are configured at the global level.");
		builder.attributeGroup("bulk", "Bulk Insert for Events");
		builder.attribute((new AttributeNode.Builder("Use bulk inserts", "useBulkEventInserts",
				AttributeType.Boolean).description(
				"Use the MongoDB bulk insert API to add " + "events in groups and improve performance.").group(
				"bulk").build()));
		builder.attribute((new AttributeNode.Builder("Bulk insert max chunk size", "bulkInsertMaxChunkSize",
				AttributeType.Integer).description(
				"Maximum number of records to send "
						+ "in a single bulk insert (if bulk inserts are enabled).").group("bulk").build()));
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
		builder.description("Store tenant data using tables in an HBase instance. Note that the "
				+ "global datastore must be configured to use HBase if this tenant datastore is to "
				+ "be used. Most core HBase settings are configured at the global level.");
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
		builder.description("Cache device management data using Hazelcast distributed maps. "
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
		builder.description("Cache device management data using EHCache. Note that this "
				+ "cache is not intended for use on clustered installations.");
		builder.attributeGroup("max", "Max Entries in Cache");
		builder.attributeGroup("ttl", "Cache Entry Time to Live");

		builder.attribute((new AttributeNode.Builder("Site cache max entries", "siteCacheMaxEntries",
				AttributeType.Integer).description("Maximum number entries in site cache.").defaultValue(
				"1000").group("max").build()));
		builder.attribute((new AttributeNode.Builder("Specification cache max entries",
				"deviceSpecificationCacheMaxEntries", AttributeType.Integer).description(
				"Maximum number entries in device specification cache.").defaultValue("1000").group("max").build()));
		builder.attribute((new AttributeNode.Builder("Device cache max entries", "deviceCacheMaxEntries",
				AttributeType.Integer).description("Maximum number entries in device cache.").defaultValue(
				"10000").group("max").build()));
		builder.attribute((new AttributeNode.Builder("Assignment cache max entries",
				"deviceAssignmentCacheMaxEntries", AttributeType.Integer).description(
				"Maximum number entries in device assignment cache.").defaultValue("10000").group("max").build()));

		builder.attribute((new AttributeNode.Builder("Site cache TTL seconds", "siteCacheTtl",
				AttributeType.Integer).description("Maximum time to live (in seconds) for site cache.").defaultValue(
				"6000").group("ttl").build()));
		builder.attribute((new AttributeNode.Builder("Specification cache TTL seconds",
				"deviceSpecificationCacheTtl", AttributeType.Integer).description(
				"Maximum time to live (in seconds) for device specification cache.").defaultValue("6000").group(
				"ttl").build()));
		builder.attribute((new AttributeNode.Builder("Device cache TTL seconds", "deviceCacheTtl",
				AttributeType.Integer).description("Maximum time to live (in seconds) for device cache.").defaultValue(
				"600").group("ttl").build()));
		builder.attribute((new AttributeNode.Builder("Assignment cache TTL seconds",
				"deviceAssignmentCacheTtl", AttributeType.Integer).description(
				"Maximum time to live (in seconds) for device assignment cache.").defaultValue("600").group(
				"ttl").build()));
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
		builder.description("This component creates sample data when no existing device data "
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
		builder.description("This component creates sample data when no existing asset data "
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
		builder.description("This component creates sample data when no existing schedule data "
				+ "is detected in the datastore. It provides examples of both simple and cron-based "
				+ "schedules that are commonly used.");
		return builder.build();
	}
}