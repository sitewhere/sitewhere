/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.state;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import com.sitewhere.grpc.client.common.converter.KafkaModelConverter;
import com.sitewhere.grpc.client.common.marshaler.KafkaModelMarshaler;
import com.sitewhere.grpc.kafka.model.KafkaModel.GStateUpdate;
import com.sitewhere.microservice.kafka.AckPolicy;
import com.sitewhere.microservice.kafka.MicroserviceKafkaProducer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.state.IMicroserviceState;
import com.sitewhere.spi.microservice.state.IMicroserviceStateElement;
import com.sitewhere.spi.microservice.state.IMicroserviceStateUpdatesKafkaProducer;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Kafka producer for state updates in microservices and their managed tenant
 * engines.
 * 
 * @author Derek
 */
public class MicroserviceStateUpdatesKafkaProducer extends MicroserviceKafkaProducer
	implements IMicroserviceStateUpdatesKafkaProducer {

    /** List of tenant ids waiting for an engine to be created */
    private BlockingDeque<IMicroserviceStateElement> updatesQueue = new LinkedBlockingDeque<>();

    /** Executor service for sending updates */
    ExecutorService updatesService;

    public MicroserviceStateUpdatesKafkaProducer() {
	super(AckPolicy.FireAndForget);
    }

    /*
     * @see com.sitewhere.microservice.kafka.MicroserviceKafkaProducer#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);
	this.updatesService = Executors.newSingleThreadExecutor();
	getUpdatesService().execute(new UpdatesProcessor());
    }

    /*
     * @see
     * com.sitewhere.microservice.kafka.MicroserviceKafkaProducer#stop(com.sitewhere
     * .spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getUpdatesService() != null) {
	    getUpdatesService().shutdownNow();
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IMicroserviceStateUpdatesKafkaProducer#
     * send(com.sitewhere.spi.microservice.state.IMicroserviceState)
     */
    @Override
    public void send(IMicroserviceState state) throws SiteWhereException {
	getUpdatesQueue().add(state);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IMicroserviceStateUpdatesKafkaProducer#
     * send(com.sitewhere.spi.microservice.state.ITenantEngineState)
     */
    @Override
    public void send(ITenantEngineState state) throws SiteWhereException {
	getUpdatesQueue().add(state);
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaProducer#
     * getTargetTopicName()
     */
    @Override
    public String getTargetTopicName() throws SiteWhereException {
	return getMicroservice().getKafkaTopicNaming().getMicroserviceStateUpdatesTopic();
    }

    /**
     * Thread that waits for Kafka to become available.
     */
    private class UpdatesProcessor implements Runnable {

	@Override
	public void run() {
	    getLogger().info("Waiting for Kafka to become available before sending state updates...");
	    try {
		getKafkaAvailable().await();
	    } catch (InterruptedException e1) {
		getLogger().info("Interrupted while waiting for Kafka to become available.");
		return;
	    }
	    while (true) {
		try {
		    // Get next tenant id from the queue and look up the tenant.
		    IMicroserviceStateElement element = getUpdatesQueue().take();
		    if (element instanceof IMicroserviceState) {
			GStateUpdate update = KafkaModelConverter
				.asGrpcGenericStateUpdate((IMicroserviceState) element);
			byte[] payload = KafkaModelMarshaler.buildStateUpdateMessage(update);
			if (getLifecycleStatus() == LifecycleStatus.Started) {
			    getLogger().trace("Sending microservice state update.");
			    send(element.getMicroservice().getIdentifier(), payload);
			} else {
			    getLogger().debug("Skipping microservice state update. Kafka producer not started.");
			}
		    } else if (element instanceof ITenantEngineState) {
			GStateUpdate update = KafkaModelConverter
				.asGrpcGenericStateUpdate((ITenantEngineState) element);
			byte[] payload = KafkaModelMarshaler.buildStateUpdateMessage(update);
			if (getLifecycleStatus() == LifecycleStatus.Started) {
			    getLogger().trace("Sending tenant engine state update.");
			    send(element.getMicroservice().getIdentifier(), payload);
			} else {
			    getLogger().debug("Skipping tenant engine state update. Kafka producer not started.");
			}
		    }
		} catch (InterruptedException e) {
		    getLogger().info("Microservice state updates thread shutting down.");
		    return;
		} catch (Throwable e) {
		    getLogger().error("Unable to deliver microservice element state.", e);
		}
	    }
	}
    }

    protected ExecutorService getUpdatesService() {
	return updatesService;
    }

    protected void setUpdatesService(ExecutorService updatesService) {
	this.updatesService = updatesService;
    }

    protected BlockingDeque<IMicroserviceStateElement> getUpdatesQueue() {
	return updatesQueue;
    }

    protected void setUpdatesQueue(BlockingDeque<IMicroserviceStateElement> updatesQueue) {
	this.updatesQueue = updatesQueue;
    }
}