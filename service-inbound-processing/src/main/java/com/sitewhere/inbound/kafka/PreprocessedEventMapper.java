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

import com.sitewhere.grpc.event.EventModelConverter;
import com.sitewhere.grpc.model.DeviceEventModel.GDecodedEventPayload;
import com.sitewhere.grpc.model.DeviceEventModel.GPreprocessedEventPayload;
import com.sitewhere.inbound.spi.processing.IInboundProcessingConfiguration;
import com.sitewhere.microservice.kafka.KeyValueMapperComponent;
import com.sitewhere.rest.model.device.event.DeviceEventContext;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.microservice.instance.EventPipelineLogLevel;

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
     * @param device
     * @param assignment
     * @param payload
     * @return
     * @throws SiteWhereException
     */
    protected GPreprocessedEventPayload buildPreProcessedEventPayload(IDevice device, IDeviceAssignment assignment,
	    GDecodedEventPayload payload) throws SiteWhereException {
	GPreprocessedEventPayload.Builder preproc = GPreprocessedEventPayload.newBuilder();

	DeviceEventContext context = new DeviceEventContext();
	context.setSourceId(payload.getSourceId());
	context.setDeviceToken(device.getToken());
	context.setDeviceId(device.getId());
	context.setDeviceTypeId(device.getDeviceTypeId());
	context.setParentDeviceId(device.getParentDeviceId());
	context.setDeviceStatus(device.getStatus());
	context.setDeviceMetadata(device.getMetadata());
	context.setDeviceAssignmentId(assignment.getId());
	context.setCustomerId(assignment.getCustomerId());
	context.setAreaId(assignment.getAreaId());
	context.setAssetId(assignment.getAssetId());
	context.setDeviceAssignmentStatus(assignment.getStatus());
	context.setDeviceAssignmentMetadata(assignment.getMetadata());
	preproc.setContext(EventModelConverter.asGrpcDeviceEventContext(context));
	preproc.setEvent(payload.getEvent());

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
		GPreprocessedEventPayload preproc = buildPreProcessedEventPayload(context.getDevice(), assignment,
			context.getDecodedEventPayload());
		payloads.add(preproc);
	    }
	    KeyValue<UUID, List<GPreprocessedEventPayload>> keyValue = new KeyValue<>(deviceId, payloads);

	    logPipelineEvent(context.getDecodedEventPayload().getSourceId(),
		    context.getDecodedEventPayload().getDeviceToken(), getMicroservice().getIdentifier(),
		    "Forwarding " + payloads.size() + " preprocessed events to inbound events Kafka topic.", null,
		    EventPipelineLogLevel.Debug);

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
