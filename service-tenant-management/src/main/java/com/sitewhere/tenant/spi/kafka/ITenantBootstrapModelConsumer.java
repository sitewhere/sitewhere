package com.sitewhere.tenant.spi.kafka;

import com.sitewhere.microservice.spi.kafka.IMicroserviceKafkaConsumer;

/**
 * Kafka consumer that reacts to updates in the tenant model.
 * 
 * @author Derek
 */
public interface ITenantBootstrapModelConsumer extends IMicroserviceKafkaConsumer {
}