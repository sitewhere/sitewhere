/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.groovy.filter;

import com.sitewhere.connectors.filter.DeviceEventFilter;
import com.sitewhere.connectors.spi.IDeviceEventFilter;
import com.sitewhere.microservice.groovy.GroovyComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

import groovy.lang.Binding;

/**
 * Implementatoin of {@link IDeviceEventFilter} that uses a Groovy script to
 * determine whether events should be included or filtered. If the script
 * returns true, the event is excluded. If the script return false, the event is
 * included.
 * 
 * @author Derek
 */
public class GroovyFilter extends DeviceEventFilter {

    /** Unique script id to execute */
    private String scriptId;

    /** Wrapped Groovy component */
    private GroovyComponent groovyComponent;

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);

	// Create nested Groovy component and pass configuration.
	this.groovyComponent = new GroovyComponent();
	getGroovyComponent().setScriptId(getScriptId());
	getGroovyComponent().setNumThreads(1);
	initializeNestedComponent(getGroovyComponent(), monitor, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.event.processor.filter.DeviceEventFilter#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	// Start nested Groovy component.
	startNestedComponent(getGroovyComponent(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.connectors.FilteredOutboundConnector#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.stop(monitor);

	// Stop nested Groovy component.
	stopNestedComponent(getGroovyComponent(), monitor);
    }

    /*
     * @see
     * com.sitewhere.outbound.spi.IDeviceEventFilter#isFiltered(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceEvent)
     */
    @Override
    public boolean isFiltered(IDeviceEventContext context, IDeviceEvent event) throws SiteWhereException {
	Binding binding = new Binding();
	binding.setVariable("logger", getLogger());
	binding.setVariable("context", context);
	binding.setVariable("event", event);
	return (boolean) getGroovyComponent().run(binding);
    }

    public String getScriptId() {
	return scriptId;
    }

    public void setScriptId(String scriptId) {
	this.scriptId = scriptId;
    }

    public GroovyComponent getGroovyComponent() {
	return groovyComponent;
    }

    public void setGroovyComponent(GroovyComponent groovyComponent) {
	this.groovyComponent = groovyComponent;
    }
}