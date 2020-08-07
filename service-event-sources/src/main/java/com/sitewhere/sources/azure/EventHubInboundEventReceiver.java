/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.azure;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventprocessorhost.CloseReason;
import com.microsoft.azure.eventprocessorhost.EventProcessorHost;
import com.microsoft.azure.eventprocessorhost.EventProcessorOptions;
import com.microsoft.azure.eventprocessorhost.ExceptionReceivedEventArgs;
import com.microsoft.azure.eventprocessorhost.IEventProcessor;
import com.microsoft.azure.eventprocessorhost.PartitionContext;
import com.sitewhere.sources.InboundEventReceiver;
import com.sitewhere.sources.configuration.eventsource.azure.EventHubConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Inbound event receiver that acts as a client for an Azure Event Hub.
 */
public class EventHubInboundEventReceiver extends InboundEventReceiver<byte[]> {

    /** Configuration */
    private EventHubConfiguration configuration;

    /** Connection string builder */
    private ConnectionStringBuilder connectionStringBuilder;

    /** Event processor host */
    private EventProcessorHost eventProcessorHost;

    public EventHubInboundEventReceiver(EventHubConfiguration configuration) {
	this.configuration = configuration;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Use parameters to build connection string.
	this.connectionStringBuilder = new ConnectionStringBuilder()
		.setNamespaceName(getConfiguration().getNamespaceName())
		.setEventHubName(getConfiguration().getEventHubName()).setSasKeyName(getConfiguration().getSasKeyName())
		.setSasKey(getConfiguration().getSasKey());

	// Use parameters to build event processor host.
	this.eventProcessorHost = new EventProcessorHost(
		EventProcessorHost.createHostName(getConfiguration().getHostNamePrefix()),
		getConfiguration().getEventHubName(), getConfiguration().getConsumerGroupName(),
		connectionStringBuilder.toString(), getConfiguration().getStorageConnectionString(),
		getConfiguration().getStorageContainerName());
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	EventProcessorOptions options = new EventProcessorOptions();
	options.setExceptionNotification(new Consumer<ExceptionReceivedEventArgs>() {

	    @Override
	    public void accept(ExceptionReceivedEventArgs t) {
		getLogger().info("SAMPLE: Host " + t.getHostname() + " received general error notification during "
			+ t.getAction() + ": " + t.getException().toString());
	    }
	});

	try {
	    getEventProcessorHost().registerEventProcessor(EventProcessor.class, options).get();
	} catch (InterruptedException e) {
	    throw new SiteWhereException("Interrupted while registering event processor");
	} catch (ExecutionException e) {
	    throw new SiteWhereException("Unable to register Event Hub event processor.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getEventProcessorHost().unregisterEventProcessor();
    }

    /**
     * Event processor implementation.
     */
    public static class EventProcessor implements IEventProcessor {

	/** Static logger instance */
	private static Logger LOGGER = LoggerFactory.getLogger(EventProcessor.class);

	private int checkpointBatchingCount = 0;

	/*
	 * @see
	 * com.microsoft.azure.eventprocessorhost.IEventProcessor#onOpen(com.microsoft.
	 * azure.eventprocessorhost.PartitionContext)
	 */
	@Override
	public void onOpen(PartitionContext context) throws Exception {
	    LOGGER.info("Partition " + context.getPartitionId() + " is opening");
	}

	/*
	 * @see
	 * com.microsoft.azure.eventprocessorhost.IEventProcessor#onClose(com.microsoft.
	 * azure.eventprocessorhost.PartitionContext,
	 * com.microsoft.azure.eventprocessorhost.CloseReason)
	 */
	@Override
	public void onClose(PartitionContext context, CloseReason reason) throws Exception {
	    LOGGER.info("Partition " + context.getPartitionId() + " is closing for reason " + reason.toString());
	}

	/*
	 * @see
	 * com.microsoft.azure.eventprocessorhost.IEventProcessor#onError(com.microsoft.
	 * azure.eventprocessorhost.PartitionContext, java.lang.Throwable)
	 */
	@Override
	public void onError(PartitionContext context, Throwable error) {
	    LOGGER.info("Partition " + context.getPartitionId() + " onError: " + error.toString());
	}

	/*
	 * @see
	 * com.microsoft.azure.eventprocessorhost.IEventProcessor#onEvents(com.microsoft
	 * .azure.eventprocessorhost.PartitionContext, java.lang.Iterable)
	 */
	@Override
	public void onEvents(PartitionContext context, Iterable<EventData> events) throws Exception {
	    LOGGER.info("Partition " + context.getPartitionId() + " got event batch");
	    int eventCount = 0;
	    for (EventData data : events) {
		try {
		    LOGGER.info("SAMPLE (" + context.getPartitionId() + "," + data.getSystemProperties().getOffset()
			    + "," + data.getSystemProperties().getSequenceNumber() + "): "
			    + new String(data.getBytes(), "UTF8"));
		    eventCount++;
		    this.checkpointBatchingCount++;
		    if ((checkpointBatchingCount % 5) == 0) {
			LOGGER.info("SAMPLE: Partition " + context.getPartitionId() + " checkpointing at "
				+ data.getSystemProperties().getOffset() + ","
				+ data.getSystemProperties().getSequenceNumber());
			context.checkpoint(data).get();
		    }
		} catch (Exception e) {
		    LOGGER.error("Processing failed for an event: " + e.toString(), e);
		}
	    }
	    LOGGER.info("Partition " + context.getPartitionId() + " batch size was " + eventCount + " for host "
		    + context.getOwner());
	}
    }

    protected EventHubConfiguration getConfiguration() {
	return configuration;
    }

    protected ConnectionStringBuilder getConnectionStringBuilder() {
	return connectionStringBuilder;
    }

    protected EventProcessorHost getEventProcessorHost() {
	return eventProcessorHost;
    }
}