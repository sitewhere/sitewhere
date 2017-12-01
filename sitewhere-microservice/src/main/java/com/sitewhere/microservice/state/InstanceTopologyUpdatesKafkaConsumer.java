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

import com.sitewhere.grpc.kafka.model.KafkaModel.GInstanceTopologyUpdate;
import com.sitewhere.grpc.model.converter.KafkaModelConverter;
import com.sitewhere.grpc.model.marshaling.KafkaModelMarshaler;
import com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesKafkaConsumer;

/**
 * Base class for Kafka consumers that process instance topology updates.
 * 
 * @author Derek
 */
public abstract class InstanceTopologyUpdatesKafkaConsumer extends MicroserviceKafkaConsumer
	implements IInstanceTopologyUpdatesKafkaConsumer {

    /** Consumer id */
    private static String CONSUMER_ID = UUID.randomUUID().toString();

    /** Unique group id as each consumer should see all messages */
    private static String GROUP_ID_SUFFIX = UUID.randomUUID().toString();

    public InstanceTopologyUpdatesKafkaConsumer(IMicroservice microservice) {
	super(microservice, null);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#getConsumerId
     * ()
     */
    @Override
    public String getConsumerId() throws SiteWhereException {
	return CONSUMER_ID;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#
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
	return Collections.singletonList(getMicroservice().getKafkaTopicNaming().getInstanceTopologyUpdatesTopic());
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#received(java
     * .lang.String, byte[])
     */
    @Override
    public void received(String key, byte[] message) throws SiteWhereException {
	GInstanceTopologyUpdate update = KafkaModelMarshaler.parseInstanceTopologyUpdateMessage(message);
	onInstanceTopologyUpdate(KafkaModelConverter.asApiInstanceTopologyUpdate(update));
    }
}