package com.sitewhere.tenant.kafka;

import com.sitewhere.microservice.kafka.KafkaOutbound;
import com.sitewhere.microservice.spi.kafka.KafkaTopicNames;
import com.sitewhere.spi.SiteWhereException;

/**
 * Component that produces Kafka messages
 * 
 * @author Derek
 */
public class TenantModelProducer extends KafkaOutbound {

    /** Application id */
    private static final String APPLICATION_ID = "tenant-model-producer";

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.kafka.IKafkaProducer#getApplicationId()
     */
    @Override
    public String getApplicationId() throws SiteWhereException {
	return APPLICATION_ID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.kafka.IKafkaProducer#getTargetTopicName()
     */
    @Override
    public String getTargetTopicName() throws SiteWhereException {
	return KafkaTopicNames.getGlobalModelUpdatesTopicName();
    }
}