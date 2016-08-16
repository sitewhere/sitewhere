/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.siddhi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.stream.output.StreamCallback;

import com.sitewhere.SiteWhere;
import com.sitewhere.device.DeviceActions;
import com.sitewhere.groovy.GroovyConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.server.tenant.ITenantAware;
import com.sitewhere.spi.tenant.ITenant;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

/**
 * Implementation of {@link StreamCallback} that hands off processing to a
 * groovy script.
 * 
 * @author Derek
 */
public class GroovyStreamProcessor extends StreamCallback implements ITenantAware {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

	/** Groovy variable used for received events */
	private static final String VAR_EVENT = "event";

	/** Groovy variable used for passing SiteWhere device management handle */
	private static final String VAR_DEVICE_MANAGEMENT = "devices";

	/** Groovy variable used for passing SiteWhere action helper */
	private static final String VAR_ACTIONS = "actions";

	/** Groovy variable used for passing logger */
	private static final String VAR_LOGGER = "logger";

	/** Injected global Groovy configuration */
	private GroovyConfiguration configuration;

	/** Tenant set by event processor */
	private ITenant tenant;

	/** Path to script used for decoder */
	private String scriptPath;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wso2.siddhi.core.stream.output.StreamCallback#receive(org.wso2.siddhi
	 * .core. event.Event[])
	 */
	@Override
	public void receive(Event[] events) {
		LOGGER.debug("About to process '" + getScriptPath() + "' with " + events.length + " events.");
		for (Event event : events) {
			try {
				IDeviceManagement dm = SiteWhere.getServer().getDeviceManagement(getTenant());
				IDeviceEventManagement dem = SiteWhere.getServer().getDeviceEventManagement(getTenant());
				DeviceActions actions = new DeviceActions(dm, dem);
				Binding binding = new Binding();
				binding.setVariable(VAR_EVENT, event);
				binding.setVariable(VAR_DEVICE_MANAGEMENT, dm);
				binding.setVariable(VAR_ACTIONS, actions);
				binding.setVariable(VAR_LOGGER, LOGGER);
				try {
					getConfiguration().getGroovyScriptEngine().run(getScriptPath(), binding);
				} catch (ResourceException e) {
					LOGGER.error("Unable to access Groovy decoder script.", e);
				} catch (ScriptException e) {
					LOGGER.error("Unable to run Groovy decoder script.", e);
				}
			} catch (SiteWhereException e) {
				LOGGER.error("Unable to process event.", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.ITenantAware#getTenant()
	 */
	public ITenant getTenant() {
		return tenant;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.ITenantAware#setTenant(com.sitewhere.spi.user.
	 * ITenant)
	 */
	public void setTenant(ITenant tenant) {
		this.tenant = tenant;
	}

	public GroovyConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(GroovyConfiguration configuration) {
		this.configuration = configuration;
	}

	public String getScriptPath() {
		return scriptPath;
	}

	public void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}
}