package com.sitewhere.tenant.kafka;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.sitewhere.microservice.kafka.MicroserviceProducer;
import com.sitewhere.microservice.spi.instance.IInstanceSettings;
import com.sitewhere.microservice.spi.kafka.IKafkaTopicNaming;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.tenant.spi.kafka.ITenantModelProducer;

/**
 * Component that produces tenant model update message on a well-known Kafka
 * topic.
 * 
 * @author Derek
 */
public class TenantModelProducer extends MicroserviceProducer implements ITenantModelProducer {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Injected reference to instance settings */
    @Autowired
    private IInstanceSettings instanceSettings;

    /** Injected reference to Kafka topic naming */
    @Autowired
    private IKafkaTopicNaming kafkaTopicNaming;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.kafka.IKafkaProducer#getTargetTopicName()
     */
    @Override
    public String getTargetTopicName() throws SiteWhereException {
	return getKafkaTopicNaming().getTenantUpdatesTopic();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.kafka.MicroserviceProducer#getInstanceSettings
     * ()
     */
    @Override
    public IInstanceSettings getInstanceSettings() {
	return instanceSettings;
    }

    public void setInstanceSettings(IInstanceSettings instanceSettings) {
	this.instanceSettings = instanceSettings;
    }

    public IKafkaTopicNaming getKafkaTopicNaming() {
	return kafkaTopicNaming;
    }

    public void setKafkaTopicNaming(IKafkaTopicNaming kafkaTopicNaming) {
	this.kafkaTopicNaming = kafkaTopicNaming;
    }
}