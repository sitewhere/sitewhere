/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.groovy.device.event.processor;

import org.apache.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.device.event.processor.FilteredOutboundEventProcessor;
import com.sitewhere.groovy.GroovyConfiguration;
import com.sitewhere.hazelcast.SiteWhereHazelcastConfiguration;
import com.sitewhere.rest.model.device.event.request.scripting.DeviceEventRequestBuilder;
import com.sitewhere.rest.model.device.event.scripting.DeviceEventSupport;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

/**
 * Outbound event processor that uses a Groovy script to process events.
 * 
 * @author Derek
 */
public class GroovyEventProcessor extends FilteredOutboundEventProcessor {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(GroovyEventProcessor.class);

	/** Injected Groovy configuration */
	private GroovyConfiguration configuration;

	/** Common Hazelcast configuration */
	private SiteWhereHazelcastConfiguration hazelcast;

	/** Relative path to Groovy script */
	private String scriptPath;

	/** Supports building various types of device events */
	private DeviceEventRequestBuilder eventsBuilder;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		// Required for filters.
		super.start();

		if (getConfiguration() == null) {
			throw new SiteWhereException("No Groovy configuration provided.");
		}

		this.eventsBuilder =
				new DeviceEventRequestBuilder(SiteWhere.getServer().getDeviceEventManagement(getTenant()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#
	 * onMeasurementsNotFiltered(com.sitewhere.spi.device.event.IDeviceMeasurements)
	 */
	@Override
	public void onMeasurementsNotFiltered(IDeviceMeasurements measurements) throws SiteWhereException {
		processEvent(measurements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#
	 * onLocationNotFiltered(com.sitewhere.spi.device.event.IDeviceLocation)
	 */
	@Override
	public void onLocationNotFiltered(IDeviceLocation location) throws SiteWhereException {
		processEvent(location);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#
	 * onAlertNotFiltered(com.sitewhere.spi.device.event.IDeviceAlert)
	 */
	@Override
	public void onAlertNotFiltered(IDeviceAlert alert) throws SiteWhereException {
		processEvent(alert);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#
	 * onStateChangeNotFiltered(com.sitewhere.spi.device.event.IDeviceStateChange)
	 */
	@Override
	public void onStateChangeNotFiltered(IDeviceStateChange state) throws SiteWhereException {
		processEvent(state);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#
	 * onCommandInvocationNotFiltered(com.sitewhere.spi.device.event.
	 * IDeviceCommandInvocation)
	 */
	@Override
	public void onCommandInvocationNotFiltered(IDeviceCommandInvocation invocation)
			throws SiteWhereException {
		processEvent(invocation);
	}

	@Override
	public void onCommandResponseNotFiltered(IDeviceCommandResponse response) throws SiteWhereException {
		processEvent(response);
	}

	/**
	 * Perform custom event processing in a Groovy script.
	 * 
	 * @param event
	 * @throws SiteWhereException
	 */
	protected void processEvent(IDeviceEvent event) throws SiteWhereException {
		Binding binding = new Binding();
		binding.setVariable("logger", getLogger());
		binding.setVariable("event", new DeviceEventSupport(event));
		binding.setVariable("eventBuilder", eventsBuilder);
		binding.setVariable("hazelcast", getHazelcast().getHazelcastInstance());

		try {
			getConfiguration().getGroovyScriptEngine().run(getScriptPath(), binding);
		} catch (ResourceException e) {
			throw new SiteWhereException("Unable to access Groovy script. " + e.getMessage(), e);
		} catch (ScriptException e) {
			throw new SiteWhereException("Unable to run Groovy script.", e);
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

	public GroovyConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(GroovyConfiguration configuration) {
		this.configuration = configuration;
	}

	public SiteWhereHazelcastConfiguration getHazelcast() {
		return hazelcast;
	}

	public void setHazelcast(SiteWhereHazelcastConfiguration hazelcast) {
		this.hazelcast = hazelcast;
	}

	public String getScriptPath() {
		return scriptPath;
	}

	public void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}
}