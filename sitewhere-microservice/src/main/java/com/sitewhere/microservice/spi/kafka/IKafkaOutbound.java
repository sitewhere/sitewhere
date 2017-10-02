package com.sitewhere.microservice.spi.kafka;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Component that produces messages that are sent to a Kafka topic.
 * 
 * @author Derek
 */
public interface IKafkaOutbound extends ILifecycleComponent {

    /**
     * Get application id used in configuration.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getApplicationId() throws SiteWhereException;

    /**
     * Get name of Kafka topic which will receive the messages.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getTargetTopicName() throws SiteWhereException;
}