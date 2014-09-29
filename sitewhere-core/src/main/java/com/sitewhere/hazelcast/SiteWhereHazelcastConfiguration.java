/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hazelcast;

import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;

/**
 * Bean that configures the hazelcast instance associated with a SiteWhere server.
 * 
 * @author Derek
 */
public class SiteWhereHazelcastConfiguration implements InitializingBean, LifecycleListener {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(SiteWhereHazelcastConfiguration.class);

	/** Hazelcast XML configuration */
	private static final String DEFAULT_FILE_NAME = "hazelcast.xml";

	/** Configuration file name */
	private String configFileName = DEFAULT_FILE_NAME;

	/** Singleton hazelcast instance */
	private HazelcastInstance instance;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		LOGGER.info("Starting Hazelcast instance ...");
		File sitewhere = SiteWhereServer.getSiteWhereConfigFolder();
		File configFile = new File(sitewhere, getConfigFileName());
		if (!configFile.exists()) {
			throw new SiteWhereException("Hazelcast configuration file not found. Looking in: "
					+ configFile.getAbsolutePath());
		}
		Config config = new XmlConfigBuilder(new FileInputStream(configFile)).build();
		instance = Hazelcast.newHazelcastInstance(config);
		instance.getLifecycleService().addLifecycleListener(this);
		LOGGER.info("Hazelcast instance started.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hazelcast.core.LifecycleListener#stateChanged(com.hazelcast.core.LifecycleEvent
	 * )
	 */
	@Override
	public void stateChanged(LifecycleEvent event) {
		LOGGER.info("Hazelcast lifecycle changed to: " + event.getState().name());
	}

	public HazelcastInstance getHazelcastInstance() {
		return instance;
	}

	public String getConfigFileName() {
		return configFileName;
	}

	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}
}