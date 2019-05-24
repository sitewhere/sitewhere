/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.state;

import com.sitewhere.grpc.client.common.converter.KafkaModelConverter;
import com.sitewhere.grpc.client.common.marshaler.KafkaModelMarshaler;
import com.sitewhere.grpc.kafka.model.KafkaModel.GStateUpdate;
import com.sitewhere.microservice.kafka.AckPolicy;
import com.sitewhere.microservice.kafka.MicroserviceKafkaProducer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.state.IMicroserviceState;
import com.sitewhere.spi.microservice.state.IMicroserviceStateUpdatesKafkaProducer;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Kafka producer for state updates in microservices and their managed tenant
 * engines.
 * 
 * @author Derek
 */
public class MicroserviceStateUpdatesKafkaProducer extends MicroserviceKafkaProducer
	implements IMicroserviceStateUpdatesKafkaProducer {

    public MicroserviceStateUpdatesKafkaProducer() {
	super(AckPolicy.FireAndForget);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IMicroserviceStateUpdatesKafkaProducer#
     * send(com.sitewhere.spi.microservice.state.IMicroserviceState)
     */
    @Override
    public void send(IMicroserviceState state) throws SiteWhereException {
	GStateUpdate update = KafkaModelConverter.asGrpcGenericStateUpdate(state);
	byte[] payload = KafkaModelMarshaler.buildStateUpdateMessage(update);
	if (getLifecycleStatus() == LifecycleStatus.Started) {
	    getLogger().trace("Sending microservice state update.");
	    send(state.getMicroservice().getIdentifier(), payload);
	} else {
	    getLogger().debug("Skipping microservice state update. Kafka producer not started.");
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IMicroserviceStateUpdatesKafkaProducer#
     * send(com.sitewhere.spi.microservice.state.ITenantEngineState)
     */
    @Override
    public void send(ITenantEngineState state) throws SiteWhereException {
	GStateUpdate update = KafkaModelConverter.asGrpcGenericStateUpdate(state);
	byte[] payload = KafkaModelMarshaler.buildStateUpdateMessage(update);
	if (getLifecycleStatus() == LifecycleStatus.Started) {
	    getLogger().trace("Sending tenant engine state update.");
	    send(state.getMicroservice().getIdentifier(), payload);
	} else {
	    getLogger().debug("Skipping tenant engine state update. Kafka producer not started.");
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaProducer#
     * getTargetTopicName()
     */
    @Override
    public String getTargetTopicName() throws SiteWhereException {
	return getMicroservice().getKafkaTopicNaming().getMicroserviceStateUpdatesTopic();
    }
}