/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.client.spi.IApiChannel;
import com.sitewhere.grpc.client.spi.IApiDemux;
import com.sitewhere.grpc.client.spi.IApiDemuxRoutingStrategy;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.state.IInstanceTopologyEntry;
import com.sitewhere.spi.microservice.state.IInstanceTopologySnapshot;
import com.sitewhere.spi.microservice.state.IInstanceTopologySnapshotsListener;
import com.sitewhere.spi.microservice.state.IMicroserviceDetails;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Demulitiplexes API calls across multiple {@link IApiChannel} in order to
 * provide high availability and increased throughput.
 * 
 * @author Derek
 */
@SuppressWarnings("rawtypes")
public abstract class ApiDemux<T extends IApiChannel> extends TenantEngineLifecycleComponent
	implements IApiDemux<T>, IInstanceTopologySnapshotsListener {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Microservice */
    private IMicroservice microservice;

    /** List of API channels */
    private List<T> apiChannels = new ArrayList<>();

    /** Routing strategy */
    @SuppressWarnings("unchecked")
    private IApiDemuxRoutingStrategy<T> routingStrategy = new RoundRobinDemuxRoutingStrategy();

    /** Executor service */
    private ExecutorService executor;

    public ApiDemux(IMicroservice microservice) {
	this.microservice = microservice;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	executor = Executors.newSingleThreadExecutor();
	getMicroservice().getInstanceTopologyUpdatesManager().addListener(this);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getMicroservice().getInstanceTopologyUpdatesManager().removeListener(this);
	if (executor != null) {
	    executor.shutdown();
	}
    }

    /*
     * @see com.sitewhere.grpc.model.spi.IApiDemux#getApiChannel()
     */
    @Override
    public T getApiChannel() {
	return getRoutingStrategy().chooseApiChannel(getApiChannels());
    }

    /*
     * @see com.sitewhere.grpc.client.spi.IApiDemux#waitForApiChannel()
     */
    @Override
    public T waitForApiChannel() {
	while (true) {
	    try {
		return getApiChannel();
	    } catch (ApiChannelNotAvailableException e) {
		getLogger().info("Waiting for '" + getTargetIdentifier() + "' API channel to become available.");
	    }
	    try {
		Thread.sleep(2000);
	    } catch (InterruptedException e) {
		return null;
	    }
	}

    }

    /*
     * @see
     * com.sitewhere.grpc.model.spi.IApiDemux#initializeApiChannel(java.lang.String)
     */
    @Override
    public void initializeApiChannel(String host) throws SiteWhereException {
	executor.execute(new ApiChannelInitializer(host));
    }

    /*
     * @see
     * com.sitewhere.grpc.model.spi.IApiDemux#removeApiChannel(java.lang.String)
     */
    @Override
    public T removeApiChannel(String host) throws SiteWhereException {
	T toRemove = getApiChannelForHost(host);
	if (toRemove != null) {
	    getApiChannels().remove(toRemove);
	    stopNestedComponent(toRemove, new LifecycleProgressMonitor(
		    new LifecycleProgressContext(1, "Shut down API channel."), getMicroservice()));
	}
	return toRemove;
    }

    /**
     * Get API channel for the given host or null if not found.
     * 
     * @param host
     * @return
     */
    protected T getApiChannelForHost(String host) {
	for (T channel : getApiChannels()) {
	    if (channel.getGrpcChannel().getHostname().equals(host)) {
		return channel;
	    }
	}
	return null;
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologySnapshotsListener#
     * onInstanceTopologySnapshot(com.sitewhere.spi.microservice.state.
     * IInstanceTopologySnapshot)
     */
    @Override
    public void onInstanceTopologySnapshot(IInstanceTopologySnapshot snapshot) {
	detectServiceAdded(snapshot);
	detectServiceRemoved(snapshot);
    }

    /**
     * Detect whether a service was added/scaled.
     * 
     * @param snapshot
     */
    protected void detectServiceAdded(IInstanceTopologySnapshot snapshot) {
	for (IInstanceTopologyEntry entry : snapshot.getTopologyEntries()) {
	    IMicroserviceDetails microservice = entry.getMicroserviceDetails();
	    if (getTargetIdentifier().equals(microservice.getIdentifier())) {
		T existing = getApiChannelForHost(microservice.getHostname());
		if (existing == null) {
		    getLogger().info("Microservice for '" + getTargetIdentifier() + "' discovered at hostname "
			    + microservice.getHostname());
		    try {
			initializeApiChannel(microservice.getHostname());
		    } catch (SiteWhereException e) {
			getLogger().error("Unable to initialize API channel for " + microservice.getHostname() + ".");
		    }
		}
	    }
	}
    }

    /**
     * Detect whether a service was removed.
     * 
     * @param snapshot
     */
    protected void detectServiceRemoved(IInstanceTopologySnapshot snapshot) {
	List<String> missing = new ArrayList<String>();
	for (T channel : getApiChannels()) {
	    IInstanceTopologyEntry match = null;
	    for (IInstanceTopologyEntry entry : snapshot.getTopologyEntries()) {
		if (entry.getMicroserviceDetails().getHostname().equals(channel.getHostname())) {
		    match = entry;
		}
	    }
	    if (match == null) {
		missing.add(channel.getHostname());
	    }
	}
	for (String hostname : missing) {
	    getLogger().info("Detected removal of remote microservice (" + hostname + "). Dropping API channel.");
	    try {
		removeApiChannel(hostname);
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to remove API channel for " + hostname + ".");
	    }
	}
    }

    /**
     * Initializes a new API channel in a separate thread.
     * 
     * @author Derek
     */
    private class ApiChannelInitializer implements Runnable {

	/** Host for channel */
	private String host;

	public ApiChannelInitializer(String host) {
	    this.host = host;
	}

	/*
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
	    try {
		T channel = (T) createApiChannel(getHost());
		ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
			new LifecycleProgressContext(1, "Initialize APi channel."), getMicroservice());

		// Initialize channel.
		initializeNestedComponent(channel, monitor, true);
		if (channel.getLifecycleStatus() == LifecycleStatus.InitializationError) {
		    getLogger().error("Unable to initialize API channel to " + getHost() + ".");
		    return;
		}

		// Start channel.
		startNestedComponent(channel, monitor, true);
		if (channel.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		    getLogger().error("Unable to start API channel to " + getHost() + ".");
		    return;
		}

		getApiChannels().add(channel);
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to create API channel to " + getHost() + ".", e);
	    } catch (Throwable t) {
		getLogger().error("Unhandled exception creating API channel to " + getHost() + ".", t);
	    }
	}

	public String getHost() {
	    return host;
	}
    }

    /*
     * @see com.sitewhere.grpc.model.spi.IApiDemux#getMicroservice()
     */
    @Override
    public IMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IMicroservice microservice) {
	this.microservice = microservice;
    }

    /*
     * @see com.sitewhere.grpc.model.spi.IApiDemux#getApiChannels()
     */
    @Override
    public List<T> getApiChannels() {
	return apiChannels;
    }

    public void setApiChannels(List<T> apiChannels) {
	this.apiChannels = apiChannels;
    }

    /*
     * @see com.sitewhere.grpc.model.spi.IApiDemux#getRoutingStrategy()
     */
    @Override
    public IApiDemuxRoutingStrategy<T> getRoutingStrategy() {
	return routingStrategy;
    }

    public void setRoutingStrategy(IApiDemuxRoutingStrategy<T> routingStrategy) {
	this.routingStrategy = routingStrategy;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}