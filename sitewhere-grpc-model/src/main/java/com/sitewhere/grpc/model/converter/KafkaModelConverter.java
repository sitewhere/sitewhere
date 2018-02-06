/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.model.converter;

import java.util.ArrayList;

import com.sitewhere.grpc.kafka.model.KafkaModel.GEnrichedEventPayload;
import com.sitewhere.grpc.kafka.model.KafkaModel.GInboundEventPayload;
import com.sitewhere.grpc.kafka.model.KafkaModel.GLifecycleStatus;
import com.sitewhere.grpc.kafka.model.KafkaModel.GMicroserviceState;
import com.sitewhere.grpc.kafka.model.KafkaModel.GPersistedEventPayload;
import com.sitewhere.grpc.kafka.model.KafkaModel.GStateUpdate;
import com.sitewhere.grpc.kafka.model.KafkaModel.GTenantEngineState;
import com.sitewhere.grpc.model.CommonModel.GOptionalString;
import com.sitewhere.rest.model.microservice.kafka.payload.EnrichedEventPayload;
import com.sitewhere.rest.model.microservice.kafka.payload.InboundEventPayload;
import com.sitewhere.rest.model.microservice.kafka.payload.PersistedEventPayload;
import com.sitewhere.rest.model.microservice.state.MicroserviceState;
import com.sitewhere.rest.model.microservice.state.TenantEngineState;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.kafka.payload.IEnrichedEventPayload;
import com.sitewhere.spi.microservice.kafka.payload.IInboundEventPayload;
import com.sitewhere.spi.microservice.kafka.payload.IPersistedEventPayload;
import com.sitewhere.spi.microservice.state.IMicroserviceState;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Convert model objects passed on Kafka topics.
 * 
 * @author Derek
 */
public class KafkaModelConverter {

    /**
     * Convert inbound event payload from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static InboundEventPayload asApiInboundEventPayload(GInboundEventPayload grpc) throws SiteWhereException {
	InboundEventPayload api = new InboundEventPayload();
	api.setSourceId(grpc.getSourceId());
	api.setHardwareId(grpc.getHardwareId());
	api.setOriginator(grpc.hasOriginator() ? grpc.getOriginator().getValue() : null);
	api.setEventCreateRequest(EventModelConverter.asApiDeviceEventCreateRequest(grpc.getEvent()));
	return api;
    }

    /**
     * Convert inbound event payload from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GInboundEventPayload asGrpcInboundEventPayload(IInboundEventPayload api) throws SiteWhereException {
	GInboundEventPayload.Builder grpc = GInboundEventPayload.newBuilder();
	grpc.setSourceId(api.getSourceId());
	grpc.setHardwareId(api.getHardwareId());
	if (api.getOriginator() != null) {
	    grpc.setOriginator(GOptionalString.newBuilder().setValue(api.getOriginator()));
	}
	grpc.setEvent(EventModelConverter.asGrpcDeviceEventCreateRequest(api.getEventCreateRequest()));
	return grpc.build();
    }

    /**
     * Convert persisted event payload from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static PersistedEventPayload asApiPersisedEventPayload(GPersistedEventPayload grpc)
	    throws SiteWhereException {
	PersistedEventPayload api = new PersistedEventPayload();
	api.setDeviceId(CommonModelConverter.asApiUuid(grpc.getDeviceId()));
	api.setEvent(EventModelConverter.asApiGenericDeviceEvent(grpc.getEvent()));
	return api;
    }

    /**
     * Convert persisted event payload from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GPersistedEventPayload asGrpcPersistedEventPayload(IPersistedEventPayload api)
	    throws SiteWhereException {
	GPersistedEventPayload.Builder grpc = GPersistedEventPayload.newBuilder();
	grpc.setDeviceId(CommonModelConverter.asGrpcUuid(api.getDeviceId()));
	grpc.setEvent(EventModelConverter.asGrpcGenericDeviceEvent(api.getEvent()));
	return grpc.build();
    }

    /**
     * Convert enriched event payload from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static EnrichedEventPayload asApiEnrichedEventPayload(GEnrichedEventPayload grpc) throws SiteWhereException {
	EnrichedEventPayload api = new EnrichedEventPayload();
	api.setEventContext(EventModelConverter.asApiDeviceEventContext(grpc.getContext()));
	api.setEvent(EventModelConverter.asApiGenericDeviceEvent(grpc.getEvent()));
	return api;
    }

    /**
     * Convert enriched event payload from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GEnrichedEventPayload asGrpcEnrichedEventPayload(IEnrichedEventPayload api)
	    throws SiteWhereException {
	GEnrichedEventPayload.Builder grpc = GEnrichedEventPayload.newBuilder();
	grpc.setContext(EventModelConverter.asGrpcDeviceEventContext(api.getEventContext()));
	grpc.setEvent(EventModelConverter.asGrpcGenericDeviceEvent(api.getEvent()));
	return grpc.build();
    }

    /**
     * Convert lifecycle status from API to GRPC.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static LifecycleStatus asApiLifecycleStatus(GLifecycleStatus grpc) throws SiteWhereException {
	switch (grpc) {
	case LIFECYCLE_STATUS_INITIALIZING:
	    return LifecycleStatus.Initializing;
	case LIFECYCLE_STATUS_INITIALIZATION_ERROR:
	    return LifecycleStatus.InitializationError;
	case LIFECYCLE_STATUS_STOPPED:
	    return LifecycleStatus.Stopped;
	case LIFECYCLE_STATUS_STARTING:
	    return LifecycleStatus.Starting;
	case LIFECYCLE_STATUS_STARTED:
	    return LifecycleStatus.Started;
	case LIFECYCLE_STATUS_PAUSING:
	    return LifecycleStatus.Pausing;
	case LIFECYCLE_STATUS_PAUSED:
	    return LifecycleStatus.Paused;
	case LIFECYCLE_STATUS_STOPPING:
	    return LifecycleStatus.Stopping;
	case LIFECYCLE_STATUS_TERMINATING:
	    return LifecycleStatus.Terminating;
	case LIFECYCLE_STATUS_TERMINATED:
	    return LifecycleStatus.Terminated;
	case LIFECYCLE_STATUS_ERROR:
	    return LifecycleStatus.LifecycleError;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown lifecycle status: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert lifecycle status from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GLifecycleStatus asGrpcLifecycleStatus(LifecycleStatus api) throws SiteWhereException {
	switch (api) {
	case Initializing:
	    return GLifecycleStatus.LIFECYCLE_STATUS_INITIALIZING;
	case InitializationError:
	    return GLifecycleStatus.LIFECYCLE_STATUS_INITIALIZATION_ERROR;
	case Stopped:
	    return GLifecycleStatus.LIFECYCLE_STATUS_STOPPED;
	case Starting:
	    return GLifecycleStatus.LIFECYCLE_STATUS_STARTING;
	case Started:
	    return GLifecycleStatus.LIFECYCLE_STATUS_STARTED;
	case Pausing:
	    return GLifecycleStatus.LIFECYCLE_STATUS_PAUSING;
	case Paused:
	    return GLifecycleStatus.LIFECYCLE_STATUS_PAUSED;
	case Stopping:
	    return GLifecycleStatus.LIFECYCLE_STATUS_STOPPING;
	case Terminating:
	    return GLifecycleStatus.LIFECYCLE_STATUS_TERMINATING;
	case Terminated:
	    return GLifecycleStatus.LIFECYCLE_STATUS_TERMINATED;
	case LifecycleError:
	    return GLifecycleStatus.LIFECYCLE_STATUS_ERROR;
	}
	throw new SiteWhereException("Unknown lifecycle status: " + api.name());
    }

    /**
     * Convert microservice state from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static MicroserviceState asApiMicroserviceState(GMicroserviceState grpc) throws SiteWhereException {
	MicroserviceState api = new MicroserviceState();
	api.setMicroservice(MicroserviceModelConverter.asApiMicroserviceDetails(grpc.getMicroservice()));
	api.setLifecycleStatus(KafkaModelConverter.asApiLifecycleStatus(grpc.getStatus()));
	return api;
    }

    /**
     * Convert microservice state from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GMicroserviceState asGrpcMicroserviceState(IMicroserviceState api) throws SiteWhereException {
	GMicroserviceState.Builder grpc = GMicroserviceState.newBuilder();
	grpc.setMicroservice(MicroserviceModelConverter.asGrpcMicroserviceDetails(api.getMicroservice()));
	grpc.setStatus(KafkaModelConverter.asGrpcLifecycleStatus(api.getLifecycleStatus()));
	return grpc.build();
    }

    /**
     * Convert tenant engine state from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static TenantEngineState asApiTenantEngineState(GTenantEngineState grpc) throws SiteWhereException {
	TenantEngineState api = new TenantEngineState();
	api.setMicroservice(MicroserviceModelConverter.asApiMicroserviceDetails(grpc.getMicroservice()));
	api.setTenantId(grpc.getTenantId());
	api.setLifecycleStatus(KafkaModelConverter.asApiLifecycleStatus(grpc.getStatus()));
	if (grpc.getErrorList().size() > 0) {
	    api.setLifecycleErrorStack(new ArrayList<String>());
	    for (String error : grpc.getErrorList()) {
		api.getLifecycleErrorStack().add(error);
	    }
	}
	return api;
    }

    /**
     * Convert tenant engine state from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GTenantEngineState asGrpcTenantEngineState(ITenantEngineState api) throws SiteWhereException {
	GTenantEngineState.Builder grpc = GTenantEngineState.newBuilder();
	grpc.setMicroservice(MicroserviceModelConverter.asGrpcMicroserviceDetails(api.getMicroservice()));
	grpc.setTenantId(api.getTenantId());
	grpc.setStatus(KafkaModelConverter.asGrpcLifecycleStatus(api.getLifecycleStatus()));
	if (api.getLifecycleErrorStack() != null) {
	    for (String error : api.getLifecycleErrorStack()) {
		grpc.addError(error);
	    }
	}
	return grpc.build();
    }

    /**
     * Convert microservice state to generic state update.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GStateUpdate asGrpcGenericStateUpdate(IMicroserviceState api) throws SiteWhereException {
	GStateUpdate.Builder grpc = GStateUpdate.newBuilder();
	grpc.setMicroserviceState(KafkaModelConverter.asGrpcMicroserviceState(api));
	return grpc.build();
    }

    /**
     * Convert tenant engine state to generic state update.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GStateUpdate asGrpcGenericStateUpdate(ITenantEngineState api) throws SiteWhereException {
	GStateUpdate.Builder grpc = GStateUpdate.newBuilder();
	grpc.setTenantEngineState(KafkaModelConverter.asGrpcTenantEngineState(api));
	return grpc.build();
    }
}