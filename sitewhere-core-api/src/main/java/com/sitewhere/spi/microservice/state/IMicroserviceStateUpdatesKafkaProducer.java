package com.sitewhere.spi.microservice.state;

import com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaProducer;

/**
 * Kafka producer that reports state updates for microservices and their managed
 * tenant engines.
 * 
 * @author Derek
 */
public interface IMicroserviceStateUpdatesKafkaProducer extends IMicroserviceKafkaProducer {
}