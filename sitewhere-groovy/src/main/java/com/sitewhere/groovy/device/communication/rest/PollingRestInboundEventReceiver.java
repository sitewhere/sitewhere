/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.groovy.device.communication.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sitewhere.device.communication.PollingInboundEventReceiver;
import com.sitewhere.groovy.GroovyConfiguration;
import com.sitewhere.groovy.device.communication.IGroovyVariables;
import com.sitewhere.spi.SiteWhereException;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

/**
 * Performs polling on a REST endpoint at a given interval.
 * 
 * @author Derek
 */
public class PollingRestInboundEventReceiver extends PollingInboundEventReceiver<byte[]> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(PollingRestInboundEventReceiver.class);

	/** Groovy variable that contains rest client helper class */
	private static final String VAR_REST_CLIENT = "rest";

	/** Groovy variable that contains json payloads to be parsed */
	private static final String VAR_PAYLOADS = "payloads";

	/** Injected global Groovy configuration */
	private GroovyConfiguration configuration;

	/** Path to script used for decoder */
	private String scriptPath;

	/** Base URL used for REST calls */
	private String baseUrl;

	/** Username used for REST calls */
	private String username;

	/** Password used for REST calls */
	private String password;

	/** Helper class for REST operations */
	private RestHelper rest;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		this.rest = new RestHelper(getBaseUrl(), getUsername(), getPassword());
		super.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.communication.PollingInboundEventReceiver#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		super.stop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.communication.PollingInboundEventReceiver#doPoll()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void doPoll() throws SiteWhereException {
		try {
			Binding binding = new Binding();
			List<byte[]> payloads = new ArrayList<byte[]>();
			binding.setVariable(VAR_REST_CLIENT, rest);
			binding.setVariable(VAR_PAYLOADS, payloads);
			binding.setVariable(IGroovyVariables.VAR_LOGGER, LOGGER);
			LOGGER.debug("About to execute '" + getScriptPath() + "'");
			getConfiguration().getGroovyScriptEngine().run(getScriptPath(), binding);
			payloads = (List<byte[]>) binding.getVariable(VAR_PAYLOADS);

			// Process each payload individually.
			for (byte[] payload : payloads) {
				onEventPayloadReceived(payload, null);
			}
		} catch (ResourceException e) {
			throw new SiteWhereException("Unable to access Groovy decoder script.", e);
		} catch (ScriptException e) {
			throw new SiteWhereException("Unable to run Groovy decoder script.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IInboundEventReceiver#onEventPayloadReceived
	 * (java.lang.Object, java.util.Map)
	 */
	@Override
	public void onEventPayloadReceived(byte[] payload, Map<String, String> metadata) {
		getEventSource().onEncodedEventReceived(this, payload, metadata);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "Polling REST Receiver";
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

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}