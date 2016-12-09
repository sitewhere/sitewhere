/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spring.handler;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.hbase.asset.HBaseAssetManagement;
import com.sitewhere.hbase.device.HBaseDeviceEventManagement;
import com.sitewhere.hbase.device.HBaseDeviceManagement;
import com.sitewhere.hbase.scheduling.HBaseScheduleManagement;
import com.sitewhere.influx.InfluxDbDeviceEventManagement;
import com.sitewhere.mongodb.asset.MongoAssetManagement;
import com.sitewhere.mongodb.device.MongoDeviceEventManagement;
import com.sitewhere.mongodb.device.MongoDeviceManagement;
import com.sitewhere.mongodb.scheduling.MongoScheduleManagement;
import com.sitewhere.server.SiteWhereServerBeans;

/**
 * Parses configuration for tenant datastore entries.
 * 
 * @author Derek
 */
public class TenantDatastoreParser extends AbstractBeanDefinitionParser {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#
     * parseInternal (org.w3c.dom.Element,
     * org.springframework.beans.factory.xml.ParserContext)
     */
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
	List<Element> dsChildren = DomUtils.getChildElements(element);
	for (Element child : dsChildren) {
	    if (!IConfigurationElements.SITEWHERE_CE_TENANT_NS.equals(child.getNamespaceURI())) {
		NamespaceHandler nested = context.getReaderContext().getNamespaceHandlerResolver()
			.resolve(child.getNamespaceURI());
		if (nested != null) {
		    nested.parse(child, context);
		    continue;
		} else {
		    throw new RuntimeException(
			    "Invalid nested element found in 'datastore' section: " + child.toString());
		}
	    }
	    Elements type = Elements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown datastore element: " + child.getLocalName());
	    }
	    switch (type) {
	    case MongoTenantDatastore: {
		parseMongoTenantDatasource(child, context);
		break;
	    }
	    case MongoInfluxDbTenantDatastore: {
		parseMongoInfluxDbTenantDatasource(child, context);
		break;
	    }
	    case HBaseTenantDatastore: {
		parseHBaseTenantDatasource(child, context);
		break;
	    }
	    case EHCacheDeviceManagementCache: {
		break;
	    }
	    case HazelcastCache: {
		break;
	    }
	    case DefaultDeviceModelInitializer: {
		parseDefaultDeviceModelInitializer(child, context);
		break;
	    }
	    case GroovyDeviceModelInitializer: {
		parseGroovyDeviceModelInitializer(child, context);
		break;
	    }
	    case DefaultAssetModelInitializer: {
		parseDefaultAssetModelInitializer(child, context);
		break;
	    }
	    case DefaultScheduleModelInitializer: {
		parseDefaultScheduleModelInitializer(child, context);
		break;
	    }
	    }
	}
	return null;
    }

    /**
     * Add service provider implementations to support MongoDB as tenant
     * datastore..
     * 
     * @param element
     * @param context
     */
    protected void parseMongoTenantDatasource(Element element, ParserContext context) {
	// Register Mongo device management implementation.
	BeanDefinitionBuilder dm = BeanDefinitionBuilder.rootBeanDefinition(MongoDeviceManagement.class);
	dm.addPropertyReference("mongoClient", "mongo");
	context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_DEVICE_MANAGEMENT,
		dm.getBeanDefinition());

	// Register device event management implementation.
	BeanDefinitionBuilder dem = BeanDefinitionBuilder.rootBeanDefinition(MongoDeviceEventManagement.class);
	dem.addPropertyReference("mongoClient", "mongo");
	Attr useBulkEventInserts = element.getAttributeNode("useBulkEventInserts");
	if (useBulkEventInserts != null) {
	    dem.addPropertyValue("useBulkEventInserts", useBulkEventInserts.getValue());
	}

	Attr bulkInsertMaxChunkSize = element.getAttributeNode("bulkInsertMaxChunkSize");
	if (bulkInsertMaxChunkSize != null) {
	    dem.addPropertyValue("bulkInsertMaxChunkSize", bulkInsertMaxChunkSize.getValue());
	}
	context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_DEVICE_EVENT_MANAGEMENT,
		dem.getBeanDefinition());

	// Register Mongo asset management implementation.
	BeanDefinitionBuilder am = BeanDefinitionBuilder.rootBeanDefinition(MongoAssetManagement.class);
	am.addPropertyReference("mongoClient", "mongo");
	context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_ASSET_MANAGEMENT,
		am.getBeanDefinition());

	// Register Mongo schedule management implementation.
	BeanDefinitionBuilder sm = BeanDefinitionBuilder.rootBeanDefinition(MongoScheduleManagement.class);
	sm.addPropertyReference("mongoClient", "mongo");
	context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_SCHEDULE_MANAGEMENT,
		sm.getBeanDefinition());
    }

    /**
     * Add service provider implementations to support a hybid MongoDB/InfluxDB
     * tenant datastore..
     * 
     * @param element
     * @param context
     */
    protected void parseMongoInfluxDbTenantDatasource(Element element, ParserContext context) {
	// Register Mongo device management implementation.
	BeanDefinitionBuilder dm = BeanDefinitionBuilder.rootBeanDefinition(MongoDeviceManagement.class);
	dm.addPropertyReference("mongoClient", "mongo");
	context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_DEVICE_MANAGEMENT,
		dm.getBeanDefinition());

	// Register device event management implementation.
	BeanDefinitionBuilder dem = BeanDefinitionBuilder.rootBeanDefinition(InfluxDbDeviceEventManagement.class);
	Attr connectUrl = element.getAttributeNode("connectUrl");
	if (connectUrl != null) {
	    dem.addPropertyValue("connectUrl", connectUrl.getValue());
	}
	Attr username = element.getAttributeNode("username");
	if (username != null) {
	    dem.addPropertyValue("username", username.getValue());
	}
	Attr password = element.getAttributeNode("password");
	if (password != null) {
	    dem.addPropertyValue("password", password.getValue());
	}
	Attr database = element.getAttributeNode("database");
	if (database != null) {
	    dem.addPropertyValue("database", database.getValue());
	}
	Attr retention = element.getAttributeNode("retention");
	if (retention != null) {
	    dem.addPropertyValue("retention", retention.getValue());
	}
	Attr enableBatch = element.getAttributeNode("enableBatch");
	if (enableBatch != null) {
	    dem.addPropertyValue("enableBatch", enableBatch.getValue());
	}
	Attr batchChunkSize = element.getAttributeNode("batchChunkSize");
	if (retention != null) {
	    dem.addPropertyValue("batchChunkSize", batchChunkSize.getValue());
	}
	Attr batchIntervalMs = element.getAttributeNode("batchIntervalMs");
	if (retention != null) {
	    dem.addPropertyValue("batchIntervalMs", batchIntervalMs.getValue());
	}
	Attr logLevel = element.getAttributeNode("logLevel");
	if (logLevel != null) {
	    dem.addPropertyValue("logLevel", logLevel.getValue());
	}
	context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_DEVICE_EVENT_MANAGEMENT,
		dem.getBeanDefinition());

	// Register Mongo asset management implementation.
	BeanDefinitionBuilder am = BeanDefinitionBuilder.rootBeanDefinition(MongoAssetManagement.class);
	am.addPropertyReference("mongoClient", "mongo");
	context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_ASSET_MANAGEMENT,
		am.getBeanDefinition());

	// Register Mongo schedule management implementation.
	BeanDefinitionBuilder sm = BeanDefinitionBuilder.rootBeanDefinition(MongoScheduleManagement.class);
	sm.addPropertyReference("mongoClient", "mongo");
	context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_SCHEDULE_MANAGEMENT,
		sm.getBeanDefinition());
    }

    /**
     * Parse an HBase datasource configuration and create beans needed to
     * realize it.
     * 
     * @param element
     * @param context
     */
    protected void parseHBaseTenantDatasource(Element element, ParserContext context) {
	// Register HBase device management implementation.
	BeanDefinitionBuilder dm = BeanDefinitionBuilder.rootBeanDefinition(HBaseDeviceManagement.class);
	dm.addPropertyReference("client", "hbase");
	context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_DEVICE_MANAGEMENT,
		dm.getBeanDefinition());

	// Register HBase device event management implementation.
	BeanDefinitionBuilder dem = BeanDefinitionBuilder.rootBeanDefinition(HBaseDeviceEventManagement.class);
	dem.addPropertyReference("client", "hbase");
	context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_DEVICE_EVENT_MANAGEMENT,
		dem.getBeanDefinition());

	// Register HBase asset management implementation.
	BeanDefinitionBuilder am = BeanDefinitionBuilder.rootBeanDefinition(HBaseAssetManagement.class);
	am.addPropertyReference("client", "hbase");
	context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_ASSET_MANAGEMENT,
		am.getBeanDefinition());

	// Register HBase schedule management implementation.
	BeanDefinitionBuilder sm = BeanDefinitionBuilder.rootBeanDefinition(HBaseScheduleManagement.class);
	sm.addPropertyReference("client", "hbase");
	context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_SCHEDULE_MANAGEMENT,
		sm.getBeanDefinition());
    }

    /**
     * Parse configuration for default device model initializer.
     * 
     * @param element
     * @param context
     */
    protected void parseDefaultDeviceModelInitializer(Element element, ParserContext context) {
	LOGGER.warn("Device model initialization is now handled in the tenant template.");
    }

    /**
     * Parse configuration for Groovy device model initializer.
     * 
     * @param element
     * @param context
     */
    protected void parseGroovyDeviceModelInitializer(Element element, ParserContext context) {
	LOGGER.warn("Device model initialization is now handled in the tenant template.");
    }

    /**
     * Parse configuration for default asset model initializer.
     * 
     * @param element
     * @param context
     */
    protected void parseDefaultAssetModelInitializer(Element element, ParserContext context) {
	LOGGER.warn("Asset model initialization is now handled in the tenant template.");
    }

    /**
     * Parse configuration for default schedule model initializer.
     * 
     * @param element
     * @param context
     */
    protected void parseDefaultScheduleModelInitializer(Element element, ParserContext context) {
	LOGGER.warn("Schedule model initialization is now handled in the tenant template.");
    }

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Mongo tenant datastore service providers */
	MongoTenantDatastore("mongo-tenant-datastore"),

	/** Hybrid MongoDB/InfluxDB datastore configuration */
	MongoInfluxDbTenantDatastore("mongo-influxdb-tenant-datastore"),

	/** HBase tenant datastore service providers */
	HBaseTenantDatastore("hbase-tenant-datastore"),

	/** EHCache device mananagement cache provider */
	@Deprecated
	EHCacheDeviceManagementCache("ehcache-device-management-cache"),

	/** Hazelcast cache provider */
	@Deprecated
	HazelcastCache("hazelcast-cache"),

	/** Creates sample data if no device data is present */
	@Deprecated
	DefaultDeviceModelInitializer("default-device-model-initializer"),

	/** Create sample device data based on logic in a Groovy script */
	@Deprecated
	GroovyDeviceModelInitializer("groovy-device-model-initializer"),

	/** Creates sample data if no asset data is present */
	@Deprecated
	DefaultAssetModelInitializer("default-asset-model-initializer"),

	/** Creates sample data if no schedule data is present */
	@Deprecated
	DefaultScheduleModelInitializer("default-schedule-model-initializer");

	/** Event code */
	private String localName;

	private Elements(String localName) {
	    this.localName = localName;
	}

	public static Elements getByLocalName(String localName) {
	    for (Elements value : Elements.values()) {
		if (value.getLocalName().equals(localName)) {
		    return value;
		}
	    }
	    return null;
	}

	public String getLocalName() {
	    return localName;
	}

	public void setLocalName(String localName) {
	    this.localName = localName;
	}
    }
}