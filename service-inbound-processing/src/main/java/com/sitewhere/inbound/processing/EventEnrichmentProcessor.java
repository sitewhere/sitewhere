/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.processing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.inbound.spi.microservice.IInboundProcessingMicroservice;
import com.sitewhere.inbound.spi.processing.IEventEnrichmentProcessor;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.microservice.kafka.payload.IInboundEventPayload;

/**
 * Processing node that receives events from the inbound processing stream and
 * attempts to enrich the event data by making the necessary requests to the
 * device management subsystem.
 * 
 * In cases where the hardware id for a device can not be found, the device is
 * considered unregistered and is put on a topic for the registration service.
 * Otherwise, details such as device and assignment information are added to the
 * payload and the enriched data is passed to another queue for further
 * processing.
 * 
 * @author Derek
 */
public class EventEnrichmentProcessor extends TenantLifecycleComponent implements IEventEnrichmentProcessor {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Handle to inbound processing microservice */
    private IInboundProcessingMicroservice microservice;

    public EventEnrichmentProcessor(IInboundProcessingMicroservice microservice) {
	this.microservice = microservice;
    }

    /*
     * @see
     * com.sitewhere.inbound.spi.processing.IEventEnrichmentProcessor#process(
     * com.sitewhere.spi.microservice.kafka.payload.IInboundEventPayload)
     */
    @Override
    public void process(IInboundEventPayload payload) throws SiteWhereException {
	IDevice device = getMicroservice().getDeviceManagementApiChannel()
		.getDeviceByHardwareId(payload.getHardwareId());
	if (device == null) {
	    getLogger().info("Device " + payload.getHardwareId() + " is not registered.");
	} else {
	    getLogger().info("Device " + payload.getHardwareId() + " is registered.");
	}
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    public IInboundProcessingMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IInboundProcessingMicroservice microservice) {
	this.microservice = microservice;
    }
}