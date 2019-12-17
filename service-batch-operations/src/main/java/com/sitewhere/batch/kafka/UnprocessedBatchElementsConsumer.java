/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;

import com.sitewhere.batch.spi.IBatchOperationManager;
import com.sitewhere.batch.spi.kafka.IUnprocessedBatchElementsConsumer;
import com.sitewhere.batch.spi.microservice.IBatchOperationsTenantEngine;
import com.sitewhere.grpc.client.batch.BatchModelConverter;
import com.sitewhere.grpc.client.batch.BatchModelMarshaler;
import com.sitewhere.grpc.model.BatchModel.GUnprocessedBatchElement;
import com.sitewhere.microservice.kafka.DirectKafkaConsumer;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.rest.model.batch.kafka.UnprocessedBatchElement;
import com.sitewhere.spi.SiteWhereException;

/**
 * Listens on Kafka topic for unprocessed batch elements.
 */
public class UnprocessedBatchElementsConsumer extends DirectKafkaConsumer implements IUnprocessedBatchElementsConsumer {

    /** Consumer id */
    private static String CONSUMER_ID = UUID.randomUUID().toString();

    /** Suffix for group id */
    private static String GROUP_ID_SUFFIX = "unprocessed-batch-element-consumers";

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#
     * getConsumerId()
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
	return getMicroservice().getKafkaTopicNaming().getTenantPrefix(getTenantEngine().getTenantResource())
		+ GROUP_ID_SUFFIX;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#
     * getSourceTopicNames()
     */
    @Override
    public List<String> getSourceTopicNames() throws SiteWhereException {
	List<String> topics = new ArrayList<String>();
	topics.add(getMicroservice().getKafkaTopicNaming()
		.getUnprocessedBatchElementsTopic(getTenantEngine().getTenantResource()));
	return topics;
    }

    /*
     * @see
     * com.sitewhere.microservice.kafka.DirectKafkaConsumer#attemptToProcess(org.
     * apache.kafka.common.TopicPartition, java.util.List)
     */
    @Override
    public void attemptToProcess(TopicPartition topicPartition, List<ConsumerRecord<String, byte[]>> records)
	    throws SiteWhereException {
	for (ConsumerRecord<String, byte[]> record : records) {
	    received(record.key(), record.value());
	}
    }

    /**
     * Handle processing of a single record.
     * 
     * @param key
     * @param message
     * @throws SiteWhereException
     */
    public void received(String key, byte[] message) throws SiteWhereException {
	try {
	    GUnprocessedBatchElement grpc = BatchModelMarshaler.parseUnprocessedBatchElementPayloadMessage(message);
	    UnprocessedBatchElement unprocessed = BatchModelConverter.asApiUnprocessedBatchElement(grpc);
	    if (getLogger().isDebugEnabled()) {
		getLogger().debug("Received unprocessed batch element payload:\n\n"
			+ MarshalUtils.marshalJsonAsPrettyString(unprocessed));
	    }
	    getBatchOperationManager().processBatchElement(unprocessed);
	} catch (SiteWhereException e) {
	    getLogger().error("Unable to parse batch operation payload.", e);
	}
    }

    public IBatchOperationManager getBatchOperationManager() {
	return ((IBatchOperationsTenantEngine) getTenantEngine()).getBatchOperationManager();
    }
}