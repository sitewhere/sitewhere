/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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
	    getLogger().error("Unable to process device lookup.", e);
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