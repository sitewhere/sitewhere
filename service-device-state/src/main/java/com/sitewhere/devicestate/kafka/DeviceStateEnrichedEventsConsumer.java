/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;

import com.sitewhere.devicestate.processing.DeviceStateProcessingLogic;
import com.sitewhere.devicestate.spi.kafka.IDeviceStateEnrichedEventsConsumer;
import com.sitewhere.devicestate.spi.processing.IDeviceStateProcessingLogic;
import com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

/**
 * Kafka consumer that consumes records from the inbound enriched events topic
 * and applies device state management logic to the events.
 */
public class DeviceStateEnrichedEventsConsumer extends MicroserviceKafkaConsumer
	implements IDeviceStateEnrichedEventsConsumer {

    /** Consumer id */
    private static String CONSUMER_ID = UUID.randomUUID().toString();

    /** Suffix for group id */
    private static String GROUP_ID_SUFFIX = "device-state-consumers";

    /** Device state processing logic */
    private IDeviceStateProcessingLogic deviceStateProcessingLogic;

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
	topics.add(
		getMicroservice().getKafkaTopicNaming().getOutboundEventsTopic(getTenantEngine().getTenantResource()));
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

	// Create processing logic component.
	this.deviceStateProcessingLogic = new DeviceStateProcessingLogic();

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize device state processing logic.
	init.addInitializeStep(this, getDeviceStateProcessingLogic(), true);

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

	// Start device state processing logic.
	start.addStartStep(this, getDeviceStateProcessingLogic(), true);

	// Execute startup steps.
	start.execute(monitor);

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

	// Stop device state logic.
	stop.addStopStep(this, getDeviceStateProcessingLogic());

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
	new DeviceStateProcessor(getTenantEngine(), records).run();
    }

    /*
     * @see com.sitewhere.devicestate.spi.kafka.IDeviceStateEnrichedEventsConsumer#
     * getDeviceStateProcessingLogic()
     */
    @Override
    public IDeviceStateProcessingLogic getDeviceStateProcessingLogic() {
	return deviceStateProcessingLogic;
    }

    public void setDeviceStateProcessingLogic(IDeviceStateProcessingLogic deviceStateProcessingLogic) {
	this.deviceStateProcessingLogic = deviceStateProcessingLogic;
    }

    /**
     * Processor that unmarshals an enriched event and forwards it to outbound
     * connector implementation.
     */
    protected class DeviceStateProcessor extends SystemUserRunnable {

	/** List of records to process for partition */
	private List<ConsumerRecord<String, byte[]>> records;

	public DeviceStateProcessor(IMicroserviceTenantEngine<?> tenantEngine,
		List<ConsumerRecord<String, byte[]>> records) {
	    super(tenantEngine.getMicroservice(), tenantEngine.getTenantResource());
	    this.records = records;
	}

	/*
	 * @see com.sitewhere.microservice.security.SystemUserRunnable#
	 * runAsSystemUser()
	 */
	@Override
	public void runAsSystemUser() throws SiteWhereException {
	    getDeviceStateProcessingLogic().process(records);
	    getConsumer().commitAsync(new OffsetCommitCallback() {
		public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception e) {
		    if (e != null) {
			getLogger().error("Commit failed for offsets " + offsets, e);
		    }
		}
	    });
	}
    }
}