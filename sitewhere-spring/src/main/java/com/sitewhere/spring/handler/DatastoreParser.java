/*
 * DatastoresParser.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spring.handler;

import java.util.List;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.server.SiteWhereServerBeans;

/**
 * Parses configuration data for the SiteWhere datastore section.
 * 
 * @author Derek
 */
public class DatastoreParser extends AbstractBeanDefinitionParser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#parseInternal
	 * (org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
		List<Element> dsChildren = DomUtils.getChildElements(element);
		for (Element child : dsChildren) {
			Elements type = Elements.getByLocalName(child.getLocalName());
			if (type == null) {
				throw new RuntimeException("Unknown datastore element: " + child.getLocalName());
			}
			switch (type) {
			case Mongo: {
				parseMongoDatasource(child, context);
				break;
			}
			case HBase: {
				parseHBaseDatasource(child, context);
				break;
			}
			case EHCacheDeviceManagementCache: {
				parseEHCacheDeviceManagementCache(child, context);
				break;
			}
			}
		}
		return null;
	}

	/**
	 * Parse a MongoDB datasource configuration and create beans needed to realize it.
	 * 
	 * @param element
	 * @param context
	 */
	protected void parseMongoDatasource(Element element, ParserContext context) {
		// Register client bean.
		BeanDefinitionBuilder client =
				BeanDefinitionBuilder.rootBeanDefinition("com.sitewhere.mongodb.SiteWhereMongoClient");
		Attr hostname = element.getAttributeNode("hostname");
		if (hostname != null) {
			client.addPropertyValue("hostname", hostname.getValue());
		}
		Attr port = element.getAttributeNode("port");
		if (port != null) {
			client.addPropertyValue("port", port.getValue());
		}
		Attr databaseName = element.getAttributeNode("databaseName");
		if (databaseName != null) {
			client.addPropertyValue("databaseName", databaseName.getValue());
		}
		context.getRegistry().registerBeanDefinition("mongo", client.getBeanDefinition());

		// Register Mongo device management implementation.
		BeanDefinitionBuilder dm =
				BeanDefinitionBuilder.rootBeanDefinition("com.sitewhere.mongodb.device.MongoDeviceManagement");
		dm.addPropertyReference("mongoClient", "mongo");
		context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_DEVICE_MANAGEMENT,
				dm.getBeanDefinition());

		// Register Mongo user management implementation.
		BeanDefinitionBuilder um =
				BeanDefinitionBuilder.rootBeanDefinition("com.sitewhere.mongodb.user.MongoUserManagement");
		um.addPropertyReference("mongoClient", "mongo");
		context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_USER_MANAGEMENT,
				um.getBeanDefinition());
	}

	/**
	 * Parse an HBase datasource configuration and create beans needed to realize it.
	 * 
	 * @param element
	 * @param context
	 */
	protected void parseHBaseDatasource(Element element, ParserContext context) {
		// Register client bean.
		BeanDefinitionBuilder client =
				BeanDefinitionBuilder.rootBeanDefinition("com.sitewhere.hbase.DefaultHBaseClient");
		Attr quorum = element.getAttributeNode("quorum");
		if (quorum != null) {
			client.addPropertyValue("quorum", quorum.getValue());
		}
		context.getRegistry().registerBeanDefinition("hbase", client.getBeanDefinition());

		// Register HBase device management implementation.
		BeanDefinitionBuilder dm =
				BeanDefinitionBuilder.rootBeanDefinition("com.sitewhere.hbase.device.HBaseDeviceManagement");
		dm.addPropertyReference("client", "hbase");
		context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_DEVICE_MANAGEMENT,
				dm.getBeanDefinition());

		// Register HBase user management implementation.
		BeanDefinitionBuilder um =
				BeanDefinitionBuilder.rootBeanDefinition("com.sitewhere.hbase.user.HBaseUserManagement");
		um.addPropertyReference("client", "hbase");
		context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_USER_MANAGEMENT,
				um.getBeanDefinition());
	}

	/**
	 * Parse configuration for the EHCache device management cache provider.
	 * 
	 * @param element
	 * @param context
	 */
	protected void parseEHCacheDeviceManagementCache(Element element, ParserContext context) {
		BeanDefinitionBuilder cache =
				BeanDefinitionBuilder.rootBeanDefinition("com.sitewhere.ehcache.DeviceManagementCacheProvider");
		context.getRegistry().registerBeanDefinition(
				SiteWhereServerBeans.BEAN_DEVICE_MANAGEMENT_CACHE_PROVIDER, cache.getBeanDefinition());
	}

	/**
	 * Status code for AVRMC message.
	 * 
	 * @author Derek
	 */
	public static enum Elements {

		/** Mongo datastore and service providers */
		Mongo("mongo-datastore"),

		/** HBase datastore and service providers */
		HBase("hbase-datastore"),

		/** EHCache device mananagement cache provider */
		EHCacheDeviceManagementCache("ehcache-device-management-cache");

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