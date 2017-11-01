package com.sitewhere.sources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.microservice.kafka.MicroserviceKafkaProducer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Kafka producer for a stream of decoded events produced by all event sources
 * for a tenant.
 * 
 * @author Derek
 */
public class DecodedEventsProducer extends MicroserviceKafkaProducer {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Microservice */
    private IMicroservice microservice;

    /** Tenant */
    private ITenant tenant;

    public DecodedEventsProducer(IMicroservice microservice, ITenant tenant) {
	this.microservice = microservice;
	this.tenant = tenant;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaProducer#
     * getInstanceSettings()
     */
    @Override
    public IInstanceSettings getInstanceSettings() {
	return getMicroservice().getInstanceSettings();
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaProducer#
     * getTargetTopicName()
     */
    @Override
    public String getTargetTopicName() throws SiteWhereException {
	return getMicroservice().getKafkaTopicNaming().getEventSourceDecodedEventsTopic(getTenant());
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    public IMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IMicroservice microservice) {
	this.microservice = microservice;
    }

    public ITenant getTenant() {
	return tenant;
    }

    public void setTenant(ITenant tenant) {
	this.tenant = tenant;
    }
}