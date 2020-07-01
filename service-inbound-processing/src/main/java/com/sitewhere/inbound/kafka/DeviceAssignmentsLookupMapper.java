/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.kafka.streams.KeyValue;

import com.sitewhere.inbound.spi.microservice.IInboundProcessingMicroservice;
import com.sitewhere.inbound.spi.processing.IInboundProcessingConfiguration;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.kafka.KeyValueMapperComponent;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.microservice.security.SystemUserCallable;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

import io.prometheus.client.Histogram;

/**
 * Attempts to look up active device assignements based on supplied device id.
 * Builds context based on results.
 */
public class DeviceAssignmentsLookupMapper
	extends KeyValueMapperComponent<UUID, InboundEventContext, KeyValue<UUID, InboundEventContext>> {

    /** Histogram for assignment lookup */
    private static final Histogram ASSIGNMENT_LOOKUP_TIMER = TenantEngineLifecycleComponent
	    .createHistogramMetric("inbound_assignment_lookup_timer", "Timer for assignment lookup on inbound events");

    /** Configuration */
    private IInboundProcessingConfiguration configuration;

    public DeviceAssignmentsLookupMapper(IInboundProcessingConfiguration configuration) {
	this.configuration = configuration;
    }

    /*
     * @see org.apache.kafka.streams.kstream.KeyValueMapper#apply(java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public KeyValue<UUID, InboundEventContext> apply(UUID deviceId, InboundEventContext context) {
	try {
	    InboundEventContext updated = new DeviceAssignmentsLookupProcessor(DeviceAssignmentsLookupMapper.this,
		    context).call();
	    KeyValue<UUID, InboundEventContext> keyValue = new KeyValue<>(deviceId, updated);
	    return keyValue;
	} catch (Exception e) {
	    getLogger().error("Unable to process device assignment lookup.", e);
	    InboundEventContext errored = new InboundEventContext(context.getDecodedEventPayload());
	    errored.setDevice(context.getDevice());
	    errored.setException(e);
	    errored.setDeviceAssignments(new ArrayList<>());
	    KeyValue<UUID, InboundEventContext> keyValue = new KeyValue<>(deviceId, context);
	    return keyValue;
	}
    }

    /**
     * Runs device assignment lookup in a separate thread.
     */
    private class DeviceAssignmentsLookupProcessor extends SystemUserCallable<InboundEventContext> {

	private InboundEventContext context;

	public DeviceAssignmentsLookupProcessor(ITenantEngineLifecycleComponent component,
		InboundEventContext context) {
	    super(component);
	    this.context = context;
	}

	/*
	 * @see com.sitewhere.microservice.security.SystemUserRunnable#runAsSystemUser()
	 */
	@Override
	public InboundEventContext runAsSystemUser() throws SiteWhereException {
	    final Histogram.Timer assignmentLookupTime = ASSIGNMENT_LOOKUP_TIMER.labels(getTenantEngine().buildLabels())
		    .startTimer();
	    try {
		List<? extends IDeviceAssignment> assignments = getDeviceManagement()
			.getActiveDeviceAssignments(context.getDevice().getId());
		InboundEventContext updated = new InboundEventContext(context.getDecodedEventPayload());
		updated.setDevice(context.getDevice());
		updated.setDeviceAssignments(assignments);
		return updated;
	    } finally {
		assignmentLookupTime.close();
	    }
	}
    }

    /**
     * Get inbound processing configuration.
     * 
     * @return
     */
    protected IInboundProcessingConfiguration getConfiguration() {
	return configuration;
    }

    /**
     * Get device management implementation.
     * 
     * @return
     */
    protected IDeviceManagement getDeviceManagement() {
	return ((IInboundProcessingMicroservice) getTenantEngine().getMicroservice()).getDeviceManagement();
    }
}
