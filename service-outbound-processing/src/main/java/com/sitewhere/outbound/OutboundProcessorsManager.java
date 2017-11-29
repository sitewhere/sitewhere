/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.outbound;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.outbound.kafka.KafkaOutboundEventProcessorHost;
import com.sitewhere.outbound.spi.IOutboundEventProcessor;
import com.sitewhere.outbound.spi.IOutboundProcessorsManager;
import com.sitewhere.outbound.spi.microservice.IOutboundProcessingMicroservice;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Manages lifecycle of the list of outbound event processors configured for a
 * tenant.
 * 
 * @author Derek
 */
public class OutboundProcessorsManager extends TenantEngineLifecycleComponent implements IOutboundProcessorsManager {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** List of event sources */
    private List<IOutboundEventProcessor> outboundEventProcessors;

    /** List of host wrappers for outbound processors */
    private List<KafkaOutboundEventProcessorHost> processorHosts = new ArrayList<KafkaOutboundEventProcessorHost>();

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getProcessorHosts().clear();
	for (IOutboundEventProcessor processor : outboundEventProcessors) {
	    // Hook processor to microservice API channels.
	    processor.setDeviceManagement(getMicroservice().getDeviceManagementApiChannel());
	    processor.setDeviceEventManagement(getMicroservice().getDeviceEventManagementApiChannel());

	    // Create host for managing outbound processor.
	    KafkaOutboundEventProcessorHost host = new KafkaOutboundEventProcessorHost(
		    getTenantEngine().getMicroservice(), getTenantEngine(), processor);
	    initializeNestedComponent(host, monitor, true);
	    getProcessorHosts().add(host);
	}
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	for (KafkaOutboundEventProcessorHost host : processorHosts) {
	    startNestedComponent(host, monitor, true);
	}
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	for (KafkaOutboundEventProcessorHost host : processorHosts) {
	    stopNestedComponent(host, monitor);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.processor.IOutboundProcessorsManager#
     * getOutboundEventProcessors()
     */
    @Override
    public List<IOutboundEventProcessor> getOutboundEventProcessors() {
	return outboundEventProcessors;
    }

    public void setOutboundEventProcessors(List<IOutboundEventProcessor> outboundEventProcessors) {
	this.outboundEventProcessors = outboundEventProcessors;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    protected List<KafkaOutboundEventProcessorHost> getProcessorHosts() {
	return processorHosts;
    }

    protected void setProcessorHosts(List<KafkaOutboundEventProcessorHost> processorHosts) {
	this.processorHosts = processorHosts;
    }

    protected IOutboundProcessingMicroservice getMicroservice() {
	return (IOutboundProcessingMicroservice) getTenantEngine().getMicroservice();
    }
}