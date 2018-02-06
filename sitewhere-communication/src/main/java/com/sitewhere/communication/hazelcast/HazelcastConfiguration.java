/*
b * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.communication.hazelcast;

import java.io.ByteArrayInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.util.ServiceLoader;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.resource.IResource;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Bean that configures the hazelcast instance associated with a SiteWhere
 * server.
 * 
 * @author Derek
 */
public class HazelcastConfiguration extends LifecycleComponent implements IHazelcastConfiguration {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Hazelcast configuration file name */
    public static final String CONFIG_FILE_NAME = "hazelcast.xml";

    /** Configuration file handle */
    private IResource baseConfiguration;

    /** Overrides group name from configuration file */
    private String groupName = "sitewhere";

    /** Overrides group password from configuration file */
    private String groupPassword = "sitewhere";

    /** Singleton hazelcast instance */
    private HazelcastInstance instance;

    public HazelcastConfiguration(IResource baseConfiguration) {
	super(LifecycleComponentType.Other);
	this.baseConfiguration = baseConfiguration;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	try {
	    Config config = new XmlConfigBuilder(new ByteArrayInputStream(baseConfiguration.getContent())).build();
	    config.setInstanceName(getGroupName());
	    HazelcastConfiguration.configureManagementCenter(config);
	    HazelcastConfiguration.performGroupOverrides(config, getGroupName(), getGroupPassword());
	    HazelcastConfiguration.performSerializationOverrides(config);
	    HazelcastConfiguration.performPropertyOverrides(config);

	    ClassLoader loader = Thread.currentThread().getContextClassLoader();
	    try {
		Thread.currentThread().setContextClassLoader(ServiceLoader.class.getClassLoader());
		instance = Hazelcast.newHazelcastInstance(config);
	    } finally {
		Thread.currentThread().setContextClassLoader(loader);
	    }

	    LOGGER.info("Hazelcast instance '" + config.getInstanceName() + "' started.");
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to create Hazelcast instance.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (instance != null) {
	    instance.shutdown();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    /**
     * Configure Hazelcast Management Center.
     * 
     * @param config
     */
    public static void configureManagementCenter(Config config) {
	config.getManagementCenterConfig().setEnabled(true).setUrl("http://localhost:8787/mancenter");
    }

    /**
     * Set up group name and password.
     * 
     * @param config
     * @param groupName
     * @param groupPassword
     */
    public static void performGroupOverrides(Config config, String groupName, String groupPassword) {
	GroupConfig group = config.getGroupConfig();
	if (group == null) {
	    group = new GroupConfig();
	    config.setGroupConfig(group);
	}
	LOGGER.info("Hazelcast group/cluster name is '" + groupName + "'.");
	group.setName(groupName);
	LOGGER.info("Hazelcast group/cluster password is '" + groupPassword + "'.");
	group.setPassword(groupPassword);
    }

    /**
     * Set up serialization configuration.
     * 
     * @param config
     */
    public static void performSerializationOverrides(Config config) {
	SerializationConfig serial = config.getSerializationConfig();
	if (serial == null) {
	    serial = new SerializationConfig();
	    config.setSerializationConfig(serial);
	}
	config.setSerializationConfig(null);
    }

    /**
     * Override properties.
     * 
     * @param config
     */
    public static void performPropertyOverrides(Config config) {
	config.setProperty("hazelcast.logging.type", "log4j2");
	config.setProperty("hazelcast.shutdownhook.enabled", "false");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.hazelcast.IHazelcastConfiguration#
     * getHazelcastInstance()
     */
    public HazelcastInstance getHazelcastInstance() {
	return instance;
    }

    public String getGroupName() {
	return groupName;
    }

    public void setGroupName(String groupName) {
	this.groupName = groupName;
    }

    public String getGroupPassword() {
	return groupPassword;
    }

    public void setGroupPassword(String groupPassword) {
	this.groupPassword = groupPassword;
    }
}