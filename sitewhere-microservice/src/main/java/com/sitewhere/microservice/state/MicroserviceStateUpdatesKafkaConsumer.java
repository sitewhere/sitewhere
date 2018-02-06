/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.state;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.sitewhere.grpc.kafka.model.KafkaModel.GStateUpdate;
import com.sitewhere.grpc.model.converter.KafkaModelConverter;
import com.sitewhere.grpc.model.marshaling.KafkaModelMarshaler;
import com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.state.IMicroserviceStateUpdatesKafkaConsumer;

/**
 * Base class for Kafka consumers that process state updates for microservices
 * and their managed tenant engines.
 * 
 * @author Derek
 */
public abstract class MicroserviceStateUpdatesKafkaConsumer extends MicroserviceKafkaConsumer
	implements IMicroserviceStateUpdatesKafkaConsumer {

    /** Consumer id */
    private static String CONSUMER_ID = UUID.randomUUID().toString();

    /** Unique group id as each consumer should see all messages */
    private static String GROUP_ID_SUFFIX = UUID.randomUUID().toString();

    public MicroserviceStateUpdatesKafkaConsumer(IMicroservice microservice) {
	super(microservice, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.kafka.IMicroserviceKafkaConsumer#
     * getConsumerId()
     */
    @Override
    public String getConsumerId() throws SiteWhereException {
	return CONSUMER_ID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.kafka.IMicroserviceKafkaConsumer#
     * getConsumerGroupId()
     */
    @Override
    public String getConsumerGroupId() throws SiteWhereException {
	return getMicroservice().getKafkaTopicNaming().getInstancePrefix() + GROUP_ID_SUFFIX;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#
     * getSourceTopicNames()
     */
    @Override
    public List<String> getSourceTopicNames() throws SiteWhereException {
	return Collections.singletonList(getMicroservice().getKafkaTopicNaming().getMicroserviceStateUpdatesTopic());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.kafka.IMicroserviceKafkaConsumer#received(
     * java.lang.String, byte[])
     */
    @Override
    public void received(String key, byte[] message) throws SiteWhereException {
	GStateUpdate update = KafkaModelMarshaler.parseStateUpdateMessage(message);

	switch (update.getStateCase()) {
	case MICROSERVICESTATE: {
	    onMicroserviceStateUpdate(KafkaModelConverter.asApiMicroserviceState(update.getMicroserviceState()));
	    break;
	}
	case TENANTENGINESTATE: {
	    onTenantEngineStateUpdate(KafkaModelConverter.asApiTenantEngineState(update.getTenantEngineState()));
	    break;
	}
	case STATE_NOT_SET: {
	    getLogger().warn("Invalid state message received: " + update.getStateCase().name());
	}
	}
    }
}