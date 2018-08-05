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
import com.sitewhere.server.lifecycle.parameters.StringComponentParameter;
import com.sitewhere.sources.InboundEventReceiver;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponentParameter;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Inbound event receiver that acts as a client for an Azure Event Hub.
 * 
 * @author Derek
 */
public class EventHubInboundEventReceiver extends InboundEventReceiver<byte[]> {

    /** Consumer group name */
    private String consumerGroupName;

    /** Namespace name */
    private String namespaceName;

    /** Event Hub name */
    private String eventHubName;

    /** SAS key name */
    private String sasKeyName;

    /** SAS key */
    private String sasKey;

    /** Storage connection string */
    private String storageConnectionString;

    /** Storage container name */
    private String storageContainerName;

    /** Host name prefix */
    private String hostNamePrefix;

    /** Consumer group name parameter */
    private ILifecycleComponentParameter<String> consumerGroupNameParameter;

    /** Namespace name parameter */
    private ILifecycleComponentParameter<String> namespaceNameParameter;

    /** Event Hub name parameter */
    private ILifecycleComponentParameter<String> eventHubNameParameter;

    /** SAS key name parameter */
    private ILifecycleComponentParameter<String> sasKeyNameParameter;

    /** SAS key parameter */
    private ILifecycleComponentParameter<String> sasKeyParameter;

    /** Storage connection string parameter */
    private ILifecycleComponentParameter<String> storageConnectionStringParameter;

    /** Storage container name parameter */
    private ILifecycleComponentParameter<String> storageContainerNameParameter;

    /** Host name prefix parameter */
    private ILifecycleComponentParameter<String> hostNamePrefixParameter;

    /** Connection string builder */
    private ConnectionStringBuilder connectionStringBuilder;

    /** Event processor host */
    private EventProcessorHost eventProcessorHost;

    /*
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initializeParameters()
     */
    @Override
    public void initializeParameters() throws SiteWhereException {
	// Add consumer group name.
	this.consumerGroupNameParameter = StringComponentParameter.newBuilder(this, "Consumer Group Name")
		.value(getConsumerGroupName()).makeRequired().build();
	getParameters().add(consumerGroupNameParameter);

	// Add namespace name.
	this.namespaceNameParameter = StringComponentParameter.newBuilder(this, "Namespace Name")
		.value(getNamespaceName()).makeRequired().build();
	getParameters().add(namespaceNameParameter);

	// Add Event Hub name.
	this.eventHubNameParameter = StringComponentParameter.newBuilder(this, "Event Hub Name")
		.value(getEventHubName()).makeRequired().build();
	getParameters().add(eventHubNameParameter);

	// Add SAS key name.
	this.sasKeyNameParameter = StringComponentParameter.newBuilder(this, "SAS Key Name").value(getSasKeyName())
		.makeRequired().build();
	getParameters().add(sasKeyNameParameter);

	// Add SAS key.
	this.sasKeyParameter = StringComponentParameter.newBuilder(this, "SAS Key").value(getSasKey()).makeRequired()
		.build();
	getParameters().add(sasKeyParameter);

	// Add Storage connection string.
	this.storageConnectionStringParameter = StringComponentParameter.newBuilder(this, "Storage Connection String")
		.value(getStorageConnectionString()).makeRequired().build();
	getParameters().add(storageConnectionStringParameter);

	// Add Storage container name.
	this.storageContainerNameParameter = StringComponentParameter.newBuilder(this, "Storage Container Name")
		.value(getStorageContainerName()).makeRequired().build();
	getParameters().add(storageContainerNameParameter);

	// Add Host name prefix.
	this.hostNamePrefixParameter = StringComponentParameter.newBuilder(this, "Host Name Prefix")
		.value(getHostNamePrefix()).makeRequired().build();
	getParameters().add(hostNamePrefixParameter);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Use parameters to build connection string.
	this.connectionStringBuilder = new ConnectionStringBuilder().setNamespaceName(namespaceNameParameter.getValue())
		.setEventHubName(eventHubNameParameter.getValue()).setSasKeyName(sasKeyNameParameter.getValue())
		.setSasKey(sasKeyParameter.getValue());

	// Use parameters to build event processor host.
	this.eventProcessorHost = new EventProcessorHost(
		EventProcessorHost.createHostName(hostNamePrefixParameter.getValue()), eventHubNameParameter.getValue(),
		consumerGroupNameParameter.getValue(), connectionStringBuilder.toString(),
		storageConnectionStringParameter.getValue(), storageContainerNameParameter.getValue());
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
     * 
     * @author Derek
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

    protected ConnectionStringBuilder getConnectionStringBuilder() {
	return connectionStringBuilder;
    }

    protected EventProcessorHost getEventProcessorHost() {
	return eventProcessorHost;
    }

    public String getConsumerGroupName() {
	return consumerGroupName;
    }

    public void setConsumerGroupName(String consumerGroupName) {
	this.consumerGroupName = consumerGroupName;
    }

    public String getNamespaceName() {
	return namespaceName;
    }

    public void setNamespaceName(String namespaceName) {
	this.namespaceName = namespaceName;
    }

    public String getEventHubName() {
	return eventHubName;
    }

    public void setEventHubName(String eventHubName) {
	this.eventHubName = eventHubName;
    }

    public String getSasKeyName() {
	return sasKeyName;
    }

    public void setSasKeyName(String sasKeyName) {
	this.sasKeyName = sasKeyName;
    }

    public String getSasKey() {
	return sasKey;
    }

    public void setSasKey(String sasKey) {
	this.sasKey = sasKey;
    }

    public String getStorageConnectionString() {
	return storageConnectionString;
    }

    public void setStorageConnectionString(String storageConnectionString) {
	this.storageConnectionString = storageConnectionString;
    }

    public String getStorageContainerName() {
	return storageContainerName;
    }

    public void setStorageContainerName(String storageContainerName) {
	this.storageContainerName = storageContainerName;
    }

    public String getHostNamePrefix() {
	return hostNamePrefix;
    }

    public void setHostNamePrefix(String hostNamePrefix) {
	this.hostNamePrefix = hostNamePrefix;
    }
}