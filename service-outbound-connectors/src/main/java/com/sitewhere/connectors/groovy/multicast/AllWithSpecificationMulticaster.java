/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.groovy.multicast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sitewhere.connectors.spi.multicast.IDeviceEventMulticaster;
import com.sitewhere.microservice.groovy.GroovyComponent;
import com.sitewhere.microservice.groovy.GroovyConfiguration;
import com.sitewhere.rest.model.search.device.DeviceSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.tenant.ITenant;

import groovy.lang.Binding;

/**
 * Routes events to all devices that use a given specification. The list of
 * devices is cached and refreshed at an interval to improve performance.
 * 
 * @author Derek
 *
 * @param <T>
 */
public abstract class AllWithSpecificationMulticaster<T> extends GroovyComponent implements IDeviceEventMulticaster<T> {

    /** Interval between refreshing list of devices with specification */
    private static final long REFRESH_INTERVAL_SECS = 60;

    /** Groovy configuration */
    private GroovyConfiguration groovyConfiguration;

    /** Token for specification */
    private String specificationToken;

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
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getSpecificationToken() == null) {
	    throw new RuntimeException("No specification token supplied.");
	}
	executor = Executors.newSingleThreadExecutor();
	executor.execute(new UpdateThread());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.processor.multicast.
     * IDeviceEventMulticaster#
     * calculateRoutes(com.sitewhere.spi.device.event.IDeviceEvent)
     */
    @Override
    public List<T> calculateRoutes(IDeviceEvent event, IDevice device, IDeviceAssignment assignment)
	    throws SiteWhereException {
	List<T> routes = new ArrayList<T>();
	IDeviceManagement dm = getDeviceManagement(getTenantEngine().getTenant());
	for (IDevice targetDevice : matches) {
	    IDeviceAssignment targetAssignment = dm.getDeviceAssignment(targetDevice.getDeviceAssignmentId());
	    Binding binding = new Binding();
	    binding.setVariable("logger", getLogger());
	    binding.setVariable("event", event);
	    binding.setVariable("device", device);
	    binding.setVariable("assignment", assignment);
	    if (device.getDeviceAssignmentId() != null) {
		binding.setVariable("targetAssignment", targetAssignment);
		binding.setVariable("targetDevice", targetDevice);
	    }
	    try {
		Object result = run(binding);
		if (result != null) {
		    routes.add(convertRoute(result));
		}
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to run route calculator script.", e);
	    }
	}
	return routes;
    }

    /**
     * Converts script response into route.
     * 
     * @param scriptResult
     * @return
     * @throws SiteWhereException
     */
    public abstract T convertRoute(Object scriptResult) throws SiteWhereException;

    public GroovyConfiguration getGroovyConfiguration() {
	return groovyConfiguration;
    }

    public void setGroovyConfiguration(GroovyConfiguration groovyConfiguration) {
	this.groovyConfiguration = groovyConfiguration;
    }

    public String getSpecificationToken() {
	return specificationToken;
    }

    public void setSpecificationToken(String specificationToken) {
	this.specificationToken = specificationToken;
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
		    ITenant tenant = AllWithSpecificationMulticaster.this.getTenantEngine().getTenant();
		    String token = AllWithSpecificationMulticaster.this.getSpecificationToken();
		    DeviceSearchCriteria criteria = new DeviceSearchCriteria(token, false, 1, 0, null, null);
		    ISearchResults<IDevice> results = getDeviceManagement(tenant).listDevices(false, criteria);
		    matches = results.getResults();
		    getLogger().debug("Found " + matches.size() + " matches for routing.");
		} catch (SiteWhereException e) {
		    getLogger().error("Unable to list devices for specification.", e);
		}
		try {
		    Thread.sleep(REFRESH_INTERVAL_SECS * 1000);
		} catch (InterruptedException e) {
		    getLogger().info("Update thread shutting down.");
		    return;
		}
	    }
	}
    }

    private IDeviceManagement getDeviceManagement(ITenant tenant) {
	return null;
    }
}