/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.groovy;

import groovy.util.GroovyScriptEngine;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.sitewhere.SiteWhere;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Global configuration for Groovy scripting support.
 * 
 * @author Derek
 */
public class GroovyConfiguration extends TenantLifecycleComponent implements InitializingBean {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(GroovyConfiguration.class);

	/** Bean name where global Groovy configuration is expected */
	public static final String GROOVY_CONFIGURATION_BEAN = "swGroovyConfiguration";

	/** Path relative to configuration root where Groovy scripts are stored */
	private static final String GROOVY_REL_SCRIPT_PATH = "groovy";

	/** Groovy script engine */
	private GroovyScriptEngine groovyScriptEngine;

	/** Configures script root to an external URL */
	private String externalScriptRoot;

	/** Field for setting GSE verbose flag */
	private boolean verbose = false;

	/** Field for setting GSE debug flag */
	private boolean debug = false;

	public GroovyConfiguration() {
		super(LifecycleComponentType.Other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		SiteWhere.getServer().getRegisteredLifecycleComponents().add(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		try {
			if (getExternalScriptRoot() != null) {
				groovyScriptEngine = new GroovyScriptEngine(getExternalScriptRoot());
				LOGGER.info("Groovy will load scripts relative to external URL: " + getExternalScriptRoot());
			} else {
				File root = SiteWhere.getServer().getConfigurationResolver().getConfigurationRoot();
				File scriptPath = new File(root, GROOVY_REL_SCRIPT_PATH);
				if (!scriptPath.exists()) {
					throw new SiteWhereException("Groovy configured, but scripts path does not exist.");
				}
				groovyScriptEngine = new GroovyScriptEngine(scriptPath.getAbsolutePath());
				LOGGER.info("Groovy will load scripts relative to: " + scriptPath.getAbsolutePath());
			}

			groovyScriptEngine.getConfig().setVerbose(isVerbose());
			groovyScriptEngine.getConfig().setDebug(isDebug());
			LOGGER.info("Groovy script engine configured with (verbose:" + isVerbose() + ") (debug:"
					+ isDebug() + ").");
		} catch (IOException e) {
			throw new SiteWhereException("Unable to configure Groovy script engine.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
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

	public GroovyScriptEngine getGroovyScriptEngine() {
		return groovyScriptEngine;
	}

	public void setGroovyScriptEngine(GroovyScriptEngine groovyScriptEngine) {
		this.groovyScriptEngine = groovyScriptEngine;
	}

	public String getExternalScriptRoot() {
		return externalScriptRoot;
	}

	public void setExternalScriptRoot(String externalScriptRoot) {
		if ((externalScriptRoot != null) && (!externalScriptRoot.endsWith("/"))) {
			externalScriptRoot += "/";
		}
		this.externalScriptRoot = externalScriptRoot;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}