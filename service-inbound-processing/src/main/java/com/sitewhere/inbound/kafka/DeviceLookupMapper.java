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

import java.util.UUID;

import org.apache.kafka.streams.KeyValue;

import com.sitewhere.grpc.model.DeviceEventModel.GDecodedEventPayload;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingMicroservice;
import com.sitewhere.inbound.spi.processing.IInboundProcessingConfiguration;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.kafka.KeyValueMapperComponent;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.microservice.security.SystemUserCallable;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.microservice.instance.EventPipelineLogLevel;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

import io.prometheus.client.Histogram;

/**
 * Attempts to look up device based on supplied token. Builds context based on
 * results.
 */
public class DeviceLookupMapper
	extends KeyValueMapperComponent<String, GDecodedEventPayload, KeyValue<UUID, InboundEventContext>> {

    /** Histogram for device lookup */
    private static final Histogram DEVICE_LOOKUP_TIMER = TenantEngineLifecycleComponent
	    .createHistogramMetric("inbound_device_lookup_timer", "Timer for device lookup on inbound events");

    /** Configuration */
    private IInboundProcessingConfiguration configuration;

    public DeviceLookupMapper(IInboundProcessingConfiguration configuration) {
	this.configuration = configuration;
    }

    /*
     * @see org.apache.kafka.streams.kstream.KeyValueMapper#apply(java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public KeyValue<UUID, InboundEventContext> apply(String deviceToken, GDecodedEventPayload payload) {
	try {
	    InboundEventContext context = new DeviceLookupProcessor(DeviceLookupMapper.this, payload).call();
	    UUID uuid = context.getDevice() != null ? context.getDevice().getId() : new UUID(0, 0);
	    KeyValue<UUID, InboundEventContext> keyValue = new KeyValue<>(uuid, context);
	    return keyValue;
	} catch (Exception e) {
	    logPipelineException(payload.getSourceId(), payload.getDeviceToken(), getMicroservice().getIdentifier(),
		    "Unable to process device lookup.", e, EventPipelineLogLevel.Error);
	    InboundEventContext context = new InboundEventContext(payload);
	    context.setException(e);
	    KeyValue<UUID, InboundEventContext> keyValue = new KeyValue<>(new UUID(0, 0), context);
	    return keyValue;
	}
    }

    /**
     * Runs device lookup in a separate thread.
     */
    private class DeviceLookupProcessor extends SystemUserCallable<InboundEventContext> {

	private GDecodedEventPayload payload;

	public DeviceLookupProcessor(ITenantEngineLifecycleComponent component, GDecodedEventPayload payload) {
	    super(component);
	    this.payload = payload;
	}

	/*
	 * @see com.sitewhere.microservice.security.SystemUserRunnable#runAsSystemUser()
	 */
	@Override
	public InboundEventContext runAsSystemUser() throws SiteWhereException {
	    // Verify that device is registered.
	    final Histogram.Timer deviceLookupTime = DEVICE_LOOKUP_TIMER.labels(getTenantEngine().buildLabels())
		    .startTimer();
	    try {
		InboundEventContext context = new InboundEventContext(payload);
		IDevice existing = getDeviceManagement().getDeviceByToken(payload.getDeviceToken());
		context.setDevice(existing);

		if (existing != null) {
		    logPipelineEvent(payload.getSourceId(), payload.getDeviceToken(), getMicroservice().getIdentifier(),
			    "Located device for device token: Id=" + existing.getId().toString(), null,
			    EventPipelineLogLevel.Debug);
		} else {
		    logPipelineEvent(payload.getSourceId(), payload.getDeviceToken(), getMicroservice().getIdentifier(),
			    "Unable to locate device for token.", null, EventPipelineLogLevel.Warning);
		}

		return context;
	    } finally {
		deviceLookupTime.close();
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