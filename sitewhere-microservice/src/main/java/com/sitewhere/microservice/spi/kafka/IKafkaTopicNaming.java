package com.sitewhere.microservice.spi.kafka;

/**
 * Provides names for Kafka topics used in SiteWhere.
 * 
 * @author Derek
 */
public interface IKafkaTopicNaming {

    /**
     * Get prefix that uniquely identifies SiteWhere instance.
     * 
     * @return
     */
    public String getInstancePrefix();

    /**
     * Get topic name for tracking tenant model updates.
     * 
     * @return
     */
    public String getTenantUpdatesTopic();
}