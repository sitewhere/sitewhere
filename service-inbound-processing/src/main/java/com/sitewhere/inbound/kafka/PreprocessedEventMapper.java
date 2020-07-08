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

import com.sitewhere.grpc.common.CommonModelConverter;
import com.sitewhere.grpc.model.DeviceEventModel.GDecodedEventPayload;
import com.sitewhere.grpc.model.DeviceEventModel.GPreprocessedEventPayload;
import com.sitewhere.inbound.spi.processing.IInboundProcessingConfiguration;
import com.sitewhere.microservice.kafka.KeyValueMapperComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;

/**
 * Uses list of device assignments to build a list of preprocessed event
 * payloads.
 */
public class PreprocessedEventMapper
	extends KeyValueMapperComponent<UUID, InboundEventContext, KeyValue<UUID, List<GPreprocessedEventPayload>>> {

    /** Configuration */
    private IInboundProcessingConfiguration configuration;

    public PreprocessedEventMapper(IInboundProcessingConfiguration configuration) {
	this.configuration = configuration;
    }

    /**
     * Build preprocessed payload by adding assignment details to decoded event.
     * 
     * @param assignment
     * @param event
     * @return
     * @throws SiteWhereException
     */
    protected GPreprocessedEventPayload buildPreProcessedEventPayload(IDeviceAssignment assignment,
	    GDecodedEventPayload event) throws SiteWhereException {
	GPreprocessedEventPayload.Builder preproc = GPreprocessedEventPayload.newBuilder();
	preproc.setSourceId(event.getSourceId());
	preproc.setDeviceToken(event.getDeviceToken());
	preproc.setEvent(event.getEvent());
	preproc.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(assignment.getId()));
	preproc.setDeviceId(CommonModelConverter.asGrpcUuid(assignment.getDeviceId()));
	return preproc.build();
    }

    /*
     * @see org.apache.kafka.streams.kstream.KeyValueMapper#apply(java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public KeyValue<UUID, List<GPreprocessedEventPayload>> apply(UUID deviceId, InboundEventContext context) {
	try {
	    List<GPreprocessedEventPayload> payloads = new ArrayList<>();
	    for (IDeviceAssignment assignment : context.getDeviceAssignments()) {
		GPreprocessedEventPayload preproc = buildPreProcessedEventPayload(assignment,
			context.getDecodedEventPayload());
		payloads.add(preproc);
	    }
	    KeyValue<UUID, List<GPreprocessedEventPayload>> keyValue = new KeyValue<>(deviceId, payloads);
	    return keyValue;
	} catch (SiteWhereException e) {
	    KeyValue<UUID, List<GPreprocessedEventPayload>> keyValue = new KeyValue<>(deviceId, new ArrayList<>());
	    return keyValue;
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
}
