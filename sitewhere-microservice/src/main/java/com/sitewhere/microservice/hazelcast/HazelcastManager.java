/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.hazelcast;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.sitewhere.microservice.IMicroserviceIdentifiers;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.hazelcast.IHazelcastManager;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Default implementation of {@link IHazelcastManager}.
 * 
 * @author Derek
 */
public class HazelcastManager extends LifecycleComponent implements IHazelcastManager {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Microservice */
    private IMicroservice microservice;

    /** Overrides group name from configuration file */
    private String groupName = "sitewhere";

    /** Overrides group password from configuration file */
    private String groupPassword = "sitewhere";

    /** Singleton hazelcast instance */
    private HazelcastInstance hazelcastInstance;

    public HazelcastManager(IMicroservice microservice) {
	this.microservice = microservice;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	try {
	    Config config = new Config();
	    config.setInstanceName(getMicroservice().getHostname());

	    // Set up network discovery.
	    NetworkConfig networkConfig = new NetworkConfig();
	    JoinConfig joinConfig = new JoinConfig();
	    MulticastConfig multicastConfig = new MulticastConfig();
	    multicastConfig.setEnabled(false);
	    joinConfig.setMulticastConfig(multicastConfig);
	    TcpIpConfig tcpIpConfig = new TcpIpConfig();
	    tcpIpConfig.addMember(IMicroserviceIdentifiers.INSTANCE_MANAGEMENT);
	    tcpIpConfig.setEnabled(true);
	    joinConfig.setTcpIpConfig(tcpIpConfig);
	    networkConfig.setJoin(joinConfig);
	    networkConfig.setPortAutoIncrement(false);
	    config.setNetworkConfig(networkConfig);

	    HazelcastManager.configureManagementCenter(config);
	    HazelcastManager.performGroupOverrides(config, getGroupName(), getGroupPassword());
	    HazelcastManager.performPropertyOverrides(config);
	    hazelcastInstance = Hazelcast.newHazelcastInstance(config);
	    getLogger().info("Hazelcast instance '" + config.getInstanceName() + "' started.");
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to create Hazelcast instance.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getHazelcastInstance() != null) {
	    getHazelcastInstance().shutdown();
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.hazelcast.IHazelcastManager#
     * getHazelcastInstance()
     */
    @Override
    public HazelcastInstance getHazelcastInstance() {
	return hazelcastInstance;
    }

    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
	this.hazelcastInstance = hazelcastInstance;
    }

    /*
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
	config.getManagementCenterConfig().setEnabled(false);
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
	group.setName(groupName);
	group.setPassword(groupPassword);
    }

    /**
     * Override properties.
     * 
     * @param config
     */
    public static void performPropertyOverrides(Config config) {
	config.setProperty("hazelcast.logging.type", "slf4j");
	config.setProperty("hazelcast.shutdownhook.enabled", "false");
    }

    protected IMicroservice getMicroservice() {
	return microservice;
    }

    protected void setMicroservice(IMicroservice microservice) {
	this.microservice = microservice;
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