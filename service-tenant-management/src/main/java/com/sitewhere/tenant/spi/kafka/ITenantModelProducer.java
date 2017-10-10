package com.sitewhere.tenant.spi.kafka;

import com.sitewhere.microservice.spi.kafka.IMicroserviceProducer;

/**
 * Kafka producer that makes tenant model updates available to a well known
 * topic.
 * 
 * @author Derek
 */
public interface ITenantModelProducer extends IMicroserviceProducer {
}