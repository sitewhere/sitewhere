/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;

import com.sitewhere.inbound.processing.InboundPayloadProcessingLogic;
import com.sitewhere.inbound.spi.kafka.IDecodedEventsConsumer;
import com.sitewhere.inbound.spi.processing.IInboundProcessingConfiguration;
import com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Listens on Kafka topic for decoded events, making them available for inbound
 * processing.
 * 
 * @author Derek
 */
public class DecodedEventsConsumer extends MicroserviceKafkaConsumer implements IDecodedEventsConsumer {

    /** Consumer id */
    private static String CONSUMER_ID = UUID.randomUUID().toString();

    /** Suffix for group id */
    private static String GROUP_ID_SUFFIX = "decoded-event-consumers";

    /** Get settings for inbound processing */
    private IInboundProcessingConfiguration configuration;

    /** Inbound payload processing logic */
    private InboundPayloadProcessingLogic inboundPayloadProcessingLogic = new InboundPayloadProcessingLogic();

    public DecodedEventsConsumer(IInboundProcessingConfiguration configuration) {
	this.configuration = configuration;
    }

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
	return getMicroservice().getKafkaTopicNaming().getTenantPrefix(getTenantEngine().getTenant()) + GROUP_ID_SUFFIX;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#
     * getSourceTopicNames()
     */
    @Override
    public List<String> getSourceTopicNames() throws SiteWhereException {
	List<String> topics = new ArrayList<String>();
	topics.add(getMicroservice().getKafkaTopicNaming()
		.getEventSourceDecodedEventsTopic(getTenantEngine().getTenant()));
	topics.add(
		getMicroservice().getKafkaTopicNaming().getInboundReprocessEventsTopic(getTenantEngine().getTenant()));
	return topics;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize inbound processing logic.
	init.addInitializeStep(this, getInboundPayloadProcessingLogic(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getComponentName());

	// Start inbound processing logic.
	start.addStartStep(this, getInboundPayloadProcessingLogic(), true);

	// Execute startup steps.
	start.execute(monitor);

	getLogger().info("Allocating " + getConfiguration().getProcessingThreadCount()
		+ " threads for inbound event processing.");
	super.start(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer#stop(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.stop(monitor);

	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop inbound processing logic.
	stop.addStopStep(this, getInboundPayloadProcessingLogic());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#process(org.
     * apache.kafka.common.TopicPartition, java.util.List)
     */
    @Override
    public void process(TopicPartition topicPartition, List<ConsumerRecord<String, byte[]>> records) {
	new InboundEventPayloadProcessor(getTenantEngine(), records).run();
    }

    /*
     * @see com.sitewhere.inbound.spi.kafka.IDecodedEventsConsumer#
     * getInboundPayloadProcessingLogic()
     */
    @Override
    public InboundPayloadProcessingLogic getInboundPayloadProcessingLogic() {
	return inboundPayloadProcessingLogic;
    }

    public void setInboundPayloadProcessingLogic(InboundPayloadProcessingLogic inboundPayloadProcessingLogic) {
	this.inboundPayloadProcessingLogic = inboundPayloadProcessingLogic;
    }

    /**
     * Processor that unmarshals a decoded event and forwards it for registration
     * verification.
     * 
     * @author Derek
     */
    protected class InboundEventPayloadProcessor extends SystemUserRunnable {

	/** List of records to process for partition */
	private List<ConsumerRecord<String, byte[]>> records;

	public InboundEventPayloadProcessor(IMicroserviceTenantEngine tenantEngine,
		List<ConsumerRecord<String, byte[]>> records) {
	    super(tenantEngine.getMicroservice(), tenantEngine.getTenant());
	    this.records = records;
	}

	/*
	 * @see com.sitewhere.microservice.security.SystemUserRunnable#
	 * runAsSystemUser()
	 */
	@Override
	public void runAsSystemUser() throws SiteWhereException {
	    getInboundPayloadProcessingLogic().process(records);
	    getConsumer().commitAsync(new OffsetCommitCallback() {
		public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception e) {
		    if (e != null) {
			getLogger().error("Commit failed for offsets " + offsets, e);
		    }
		}
	    });
	}
    }

    public IInboundProcessingConfiguration getConfiguration() {
	return configuration;
    }

    public void setConfiguration(IInboundProcessingConfiguration configuration) {
	this.configuration = configuration;
    }
}