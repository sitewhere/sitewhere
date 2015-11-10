/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.groovy.device.communication.multicaster;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.groovy.GroovyConfiguration;
import com.sitewhere.rest.model.search.device.DeviceSearchCriteria;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.processor.multicast.IDeviceEventMulticaster;
import com.sitewhere.spi.device.event.processor.multicast.IDeviceRouteBuilder;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.user.ITenant;

/**
 * Routes events to all devices that use a given specification. The list of devices is
 * cached and refreshed at an interval to improve performance.
 * 
 * @author Derek
 *
 * @param <T>
 */
public class AllWithSpecificationMulticaster<T> extends TenantLifecycleComponent implements
		IDeviceEventMulticaster<T> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(AllWithSpecificationMulticaster.class);

	/** Interval between refreshing list of devices with specification */
	private static final long REFRESH_INTERVAL_SECS = 60;

	/** Injected global Groovy configuration */
	private GroovyConfiguration configuration;

	/** Token for specification */
	private String specificationToken;

	/** Path to filtering script */
	private String filterScriptPath;

	/** Executor for refresh thread */
	private ExecutorService executor;

	/** Cached list of matches that determine routes */
	private List<IDevice> matches = new ArrayList<IDevice>();

	public AllWithSpecificationMulticaster() {
		super(LifecycleComponentType.OutboundEventProcessorFilter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		if (getSpecificationToken() == null) {
			throw new RuntimeException("No specification token supplied.");
		}
		executor = Executors.newSingleThreadExecutor();
		executor.execute(new UpdateThread());
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
	 * @see com.sitewhere.spi.device.event.processor.multicast.IDeviceEventMulticaster#
	 * calculateRoutes(com.sitewhere.spi.device.event.IDeviceEvent,
	 * com.sitewhere.spi.device.event.processor.multicast.IDeviceRouteBuilder)
	 */
	@Override
	public List<T> calculateRoutes(IDeviceEvent event, IDeviceRouteBuilder<T> builder)
			throws SiteWhereException {
		List<T> routes = new ArrayList<T>();
		IDeviceManagement dm = SiteWhere.getServer().getDeviceManagement(getTenant());
		for (IDevice device : matches) {
			if (getFilterScriptPath() != null) {
				try {
					Binding binding = new Binding();
					binding.setVariable("logger", getLogger());
					binding.setVariable("event", event);
					IDeviceAssignment eventAssignment =
							dm.getDeviceAssignmentByToken(event.getDeviceAssignmentToken());
					binding.setVariable("eventAssignment", eventAssignment);
					binding.setVariable("eventDevice",
							dm.getDeviceByHardwareId(eventAssignment.getDeviceHardwareId()));
					if (device.getAssignmentToken() != null) {
						IDeviceAssignment assignment =
								dm.getDeviceAssignmentByToken(device.getAssignmentToken());
						binding.setVariable("targetAssignment", assignment);
						binding.setVariable("targetDevice",
								dm.getDeviceByHardwareId(assignment.getDeviceHardwareId()));
					}
					try {
						Object result =
								getConfiguration().getGroovyScriptEngine().run(getFilterScriptPath(), binding);
						if (!(result instanceof Boolean)) {
							LOGGER.error("Groovy script returned non-boolean result.");
							return routes;
						}
						// Script result indicated event should be filtered for device.
						if (!((Boolean) result).booleanValue()) {
							continue;
						}
					} catch (ResourceException e) {
						LOGGER.error("Unable to access Groovy decoder script.", e);
					} catch (ScriptException e) {
						LOGGER.error("Unable to run Groovy decoder script.", e);
					}
				} catch (SiteWhereException e) {
					LOGGER.error("Unable to process event.", e);
				}
			}

			routes.add(builder.build(device).getRoute());
		}
		return routes;
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

	public String getSpecificationToken() {
		return specificationToken;
	}

	public void setSpecificationToken(String specificationToken) {
		this.specificationToken = specificationToken;
	}

	public String getFilterScriptPath() {
		return filterScriptPath;
	}

	public void setFilterScriptPath(String filterScriptPath) {
		this.filterScriptPath = filterScriptPath;
	}

	public GroovyConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(GroovyConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Thread that updates the list of devices with specification.
	 * 
	 * @author Derek
	 */
	private class UpdateThread implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					ITenant tenant = AllWithSpecificationMulticaster.this.getTenant();
					String token = AllWithSpecificationMulticaster.this.getSpecificationToken();
					DeviceSearchCriteria criteria =
							new DeviceSearchCriteria(token, null, false, 1, 0, null, null);
					ISearchResults<IDevice> results =
							SiteWhere.getServer().getDeviceManagement(tenant).listDevices(false, criteria);
					matches = results.getResults();
					LOGGER.debug("Found " + matches.size() + " matches for routing.");
				} catch (SiteWhereException e) {
					LOGGER.error("Unable to list devices for specification.", e);
				}
				try {
					Thread.sleep(REFRESH_INTERVAL_SECS * 1000);
				} catch (InterruptedException e) {
					LOGGER.info("Update thread shutting down.");
					return;
				}
			}
		}
	}
}