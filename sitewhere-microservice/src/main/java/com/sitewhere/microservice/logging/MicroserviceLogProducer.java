/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.logging;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.sitewhere.grpc.kafka.model.KafkaModel.GMicroserviceLogMessage;
import com.sitewhere.grpc.model.converter.KafkaModelConverter;
import com.sitewhere.grpc.model.marshaler.KafkaModelMarshaler;
import com.sitewhere.microservice.kafka.MicroserviceKafkaProducer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.logging.IMicroserviceLogMessage;
import com.sitewhere.spi.microservice.logging.IMicroserviceLogProducer;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Kafka producer that pushes microservice log messages onto a shared topic for
 * the instance.
 * 
 * @author Derek
 */
public class MicroserviceLogProducer extends MicroserviceKafkaProducer implements IMicroserviceLogProducer {

    /** Queue for logs that have not been added to topic */
    private BlockingDeque<IMicroserviceLogMessage> queuedLogMessages = new LinkedBlockingDeque<>();

    /** Provides thread pool */
    private ExecutorService executor;

    /*
     * @see
     * com.sitewhere.spi.microservice.logging.IMicroserviceLogProducer#send(com.
     * sitewhere.spi.microservice.logging.IMicroserviceLogMessage)
     */
    @Override
    public void send(IMicroserviceLogMessage log) throws SiteWhereException {
	try {
	    getQueuedLogMessages().put(log);
	} catch (InterruptedException e) {
	    getLogger().error("Unable to add log message for processing.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);
	executor = Executors.newSingleThreadExecutor(new LogProcessorThreadFactory());
	executor.execute(new LogProcessor());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (executor != null) {
	    executor.shutdownNow();
	}
	super.stop(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaProducer#
     * getTargetTopicName()
     */
    @Override
    public String getTargetTopicName() throws SiteWhereException {
	return getMicroservice().getKafkaTopicNaming().getInstanceLoggingTopic();
    }

    /**
     * Handles pushing log messages to Kafka.
     * 
     * @author Derek
     */
    private class LogProcessor implements Runnable {

	/*
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
	    while (true) {
		if (getLifecycleStatus() == LifecycleStatus.Started) {
		    try {
			IMicroserviceLogMessage log = getQueuedLogMessages().take();
			GMicroserviceLogMessage glog = KafkaModelConverter.asGrpcMicroserviceLogMessage(log);
			byte[] payload = KafkaModelMarshaler.buildMicroserviceLogMessage(glog);
			send(log.getMicroserviceContainerId(), payload);
		    } catch (InterruptedException e) {
			getLogger().warn("Log processor thread shutting down.");
			return;
		    } catch (SiteWhereException e) {
			getLogger().error("Error in log processor thread.", e);
		    } catch (Throwable t) {
			getLogger().error("Unhandled exception in log processor thread.", t);
		    }
		} else {
		    try {
			Thread.sleep(1000);
		    } catch (InterruptedException e) {
			getLogger().warn("Log producer thread interrupted.");
			return;
		    }
		}
	    }
	}
    }

    public BlockingDeque<IMicroserviceLogMessage> getQueuedLogMessages() {
	return queuedLogMessages;
    }

    public void setQueuedLogMessages(BlockingDeque<IMicroserviceLogMessage> queuedLogMessages) {
	this.queuedLogMessages = queuedLogMessages;
    }

    /** Used for naming log processor thread */
    private class LogProcessorThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Log Processor " + counter.incrementAndGet());
	}
    }
}
