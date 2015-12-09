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
 * Specifies model used for tenant configuration user interface.
 * 
 * @author Derek
 */
public class TenantConfigurationModel extends ConfigurationModel {

	public TenantConfigurationModel() {
		setLocalName("tenant-configuration");
		setName("Tenant Configuration");
		setDescription("Provides a model for all aspects of tenant configuration.");
		getElements().add(createDatastoreContainer());
	}

	/**
	 * Create the container for datastore information.
	 * 
	 * @return
	 */
	protected ElementNode createDatastoreContainer() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Datastore Configuration",
						TenantConfigurationParser.Elements.TenantDatastore.getLocalName(),
						ElementRole.Top_Persistence);
		builder.setDescription("Configure the datastore and related aspects such as caching and "
				+ "data model initialization.");
		builder.addElement(createMongoTenantDatastoreElement());
		builder.addElement(createHBaseTenantDatastoreElement());
		builder.addElement(createEHCacheElement());
		builder.addElement(createHazelcastCacheElement());
		return builder.build();
	}

	/**
	 * Create element configuration for MonogoDB tenant datastore.
	 * 
	 * @return
	 */
	protected ElementNode createMongoTenantDatastoreElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Tenant Datastore (MongoDB)",
						TenantDatastoreParser.Elements.MongoTenantDatastore.getLocalName(),
						ElementRole.Persistence_Datastore);
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
				new ElementNode.Builder("Tenant Datastore (HBase)",
						TenantDatastoreParser.Elements.HBaseTenantDatastore.getLocalName(),
						ElementRole.Persistence_Datastore);
		return builder.build();
	}

	/**
	 * Create element configuration for EHCache cache.
	 * 
	 * @return
	 */
	protected ElementNode createEHCacheElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Caching (EHCache)",
						TenantDatastoreParser.Elements.EHCacheDeviceManagementCache.getLocalName(),
						ElementRole.Persistence_CacheProvider);
		return builder.build();
	}

	/**
	 * Create element configuration for Hazelcast cache.
	 * 
	 * @return
	 */
	protected ElementNode createHazelcastCacheElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Caching (Hazelcast)",
						TenantDatastoreParser.Elements.HazelcastCache.getLocalName(),
						ElementRole.Persistence_CacheProvider);
		return builder.build();
	}
}