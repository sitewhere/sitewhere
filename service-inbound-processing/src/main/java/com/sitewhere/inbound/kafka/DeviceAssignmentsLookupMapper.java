/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import com.sitewhere.spi.microservice.instance.EventPipelineLogLevel;
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

		logPipelineEvent(context.getDecodedEventPayload().getSourceId(),
			context.getDecodedEventPayload().getDeviceToken(), getMicroservice().getIdentifier(),
			"Found " + assignments.size() + " assignments for device.", null, EventPipelineLogLevel.Debug);

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
