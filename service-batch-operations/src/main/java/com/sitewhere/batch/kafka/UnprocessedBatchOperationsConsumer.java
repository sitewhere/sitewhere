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
package com.sitewhere.batch.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;

import com.sitewhere.batch.spi.IBatchOperationManager;
import com.sitewhere.batch.spi.kafka.IUnprocessedBatchOperationsConsumer;
import com.sitewhere.batch.spi.microservice.IBatchOperationsTenantEngine;
import com.sitewhere.grpc.batch.BatchModelConverter;
import com.sitewhere.grpc.batch.BatchModelMarshaler;
import com.sitewhere.grpc.model.BatchModel.GUnprocessedBatchOperation;
import com.sitewhere.microservice.kafka.DirectKafkaConsumer;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.rest.model.batch.kafka.UnprocessedBatchOperation;
import com.sitewhere.spi.SiteWhereException;

/**
 * Listens on Kafka topic for unprocessed batch operations.
 */
public class UnprocessedBatchOperationsConsumer extends DirectKafkaConsumer
	implements IUnprocessedBatchOperationsConsumer {

    /** Consumer id */
    private static String CONSUMER_ID = UUID.randomUUID().toString();

    /** Suffix for group id */
    private static String GROUP_ID_SUFFIX = "unprocessed-batch-operation-consumers";

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
		.getUnprocessedBatchOperationsTopic(getTenantEngine().getTenantResource()));
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
	    GUnprocessedBatchOperation grpc = BatchModelMarshaler.parseUnprocessedBatchOperationPayloadMessage(message);
	    UnprocessedBatchOperation unprocessed = BatchModelConverter.asApiUnprocessedBatchOperation(grpc);
	    if (getLogger().isDebugEnabled()) {
		getLogger().debug("Received unprocessed batch operation payload:\n\n"
			+ MarshalUtils.marshalJsonAsPrettyString(unprocessed));
	    }
	    getBatchOperationManager().initializeBatchOperation(unprocessed);
	} catch (SiteWhereException e) {
	    getLogger().error("Unable to parse batch operation payload.", e);
	}
    }

    public IBatchOperationManager getBatchOperationManager() {
	return ((IBatchOperationsTenantEngine) getTenantEngine()).getBatchOperationManager();
    }
}