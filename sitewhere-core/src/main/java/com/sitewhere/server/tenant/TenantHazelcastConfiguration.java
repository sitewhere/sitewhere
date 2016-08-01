/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.tenant;

import java.io.ByteArrayInputStream;

import org.apache.log4j.Logger;

import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.util.ServiceLoader;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.resource.IResource;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.server.tenant.ITenantHazelcastConfiguration;

/**
 * Bean that configures the hazelcast instance associated with a SiteWhere
 * server.
 * 
 * @author Derek
 */
public class TenantHazelcastConfiguration extends TenantLifecycleComponent
		implements ITenantHazelcastConfiguration, LifecycleListener {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(TenantHazelcastConfiguration.class);

	/** Hazelcast configuration file name */
	public static final String CONFIG_FILE_NAME = "hazelcast.xml";

	/** Configuration file handle */
	private IResource baseConfiguration;

	/** Overrides group password from configuration file */
	private String groupPassword = "sitewhere";

	/** Singleton hazelcast instance */
	private HazelcastInstance instance;

	public TenantHazelcastConfiguration(IResource baseConfiguration) {
		super(LifecycleComponentType.Other);
		this.baseConfiguration = baseConfiguration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		try {
			Config config = new XmlConfigBuilder(new ByteArrayInputStream(baseConfiguration.getContent())).build();
			config.setInstanceName(getTenant().getId());
			performGroupOverrides(config);
			performSerializationOverrides(config);
			config.setProperty("hazelcast.logging.type", "log4j");

			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(ServiceLoader.class.getClassLoader());
				instance = Hazelcast.newHazelcastInstance(config);
			} finally {
				Thread.currentThread().setContextClassLoader(loader);
			}

			instance.getLifecycleService().addLifecycleListener(this);
			LOGGER.info("Hazelcast instance '" + config.getInstanceName() + "' started.");
		} catch (Exception e) {
			throw new SiteWhereException("Unable to create tenant Hazelcast instance.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
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
	 * If group name or password is specified, override settings from the
	 * configuration file.
	 * 
	 * @param config
	 */
	protected void performGroupOverrides(Config config) {
		GroupConfig group = config.getGroupConfig();
		if (group == null) {
			group = new GroupConfig();
			config.setGroupConfig(group);
		}
		LOGGER.info("Hazelcast group/cluster name is '" + getTenant().getId() + "'.");
		group.setName(getTenant().getId());
		LOGGER.info("Hazelcast group/cluster password is '" + getGroupPassword() + "'.");
		group.setPassword(getGroupPassword());
	}

	protected void performSerializationOverrides(Config config) {
		SerializationConfig serial = config.getSerializationConfig();
		if (serial == null) {
			serial = new SerializationConfig();
			config.setSerializationConfig(serial);
		}
		config.setSerializationConfig(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hazelcast.core.LifecycleListener#stateChanged(com.hazelcast.core.
	 * LifecycleEvent )
	 */
	@Override
	public void stateChanged(LifecycleEvent event) {
		LOGGER.info("Hazelcast lifecycle changed to: " + event.getState().name());
	}

	public HazelcastInstance getHazelcastInstance() {
		return instance;
	}

	public String getGroupPassword() {
		return groupPassword;
	}

	public void setGroupPassword(String groupPassword) {
		this.groupPassword = groupPassword;
	}
}