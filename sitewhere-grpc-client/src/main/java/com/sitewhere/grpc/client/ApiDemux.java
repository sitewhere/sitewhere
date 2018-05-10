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

import com.sitewhere.grpc.client.spi.IApiChannel;
import com.sitewhere.grpc.client.spi.IApiDemux;
import com.sitewhere.grpc.client.spi.IApiDemuxRoutingStrategy;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.state.IInstanceMicroservice;
import com.sitewhere.spi.microservice.state.IInstanceTopologyEntry;
import com.sitewhere.spi.microservice.state.IInstanceTopologySnapshot;
import com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesListener;
import com.sitewhere.spi.microservice.state.IMicroserviceDetails;
import com.sitewhere.spi.microservice.state.IMicroserviceState;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
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
	implements IApiDemux<T>, IInstanceTopologyUpdatesListener {

    /** Amount of time to wait between check for available API channel */
    private static final int API_CHANNEL_WAIT_INTERVAL_IN_SECS = 3;

    /** List of API channels */
    private List<T> apiChannels = new ArrayList<>();

    /** Routing strategy */
    @SuppressWarnings("unchecked")
    private IApiDemuxRoutingStrategy<T> routingStrategy = new RoundRobinDemuxRoutingStrategy();

    /** Executor service */
    private ExecutorService executor;

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	executor = Executors.newSingleThreadExecutor();
	getMicroservice().getTopologyStateAggregator().addInstanceTopologyUpdatesListener(this);
	bootstrapFromExistingTopology();
    }

    /**
     * Since the topology updates manager may have already captured information
     * before the listener was registered, loop through existing members and
     * register them.
     */
    protected void bootstrapFromExistingTopology() {
	IInstanceTopologySnapshot topology = getMicroservice().getTopologyStateAggregator()
		.getInstanceTopologySnapshot();
	for (IInstanceTopologyEntry entry : topology.getTopologyEntriesByIdentifier().values()) {
	    for (IInstanceMicroservice microservice : entry.getMicroservicesByHostname().values()) {
		detectServiceAdded(microservice.getLatestState());
	    }
	}
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getMicroservice().getTopologyStateAggregator() != null) {
	    getMicroservice().getTopologyStateAggregator().removeInstanceTopologyUpdatesListener(this);
	}
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
		Thread.sleep(API_CHANNEL_WAIT_INTERVAL_IN_SECS * 1000);
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
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesListener#
     * onMicroserviceAdded(com.sitewhere.spi.microservice.state.IMicroserviceState)
     */
    @Override
    public void onMicroserviceAdded(IMicroserviceState state) {
	detectServiceAdded(state);
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesListener#
     * onMicroserviceUpdated(com.sitewhere.spi.microservice.state.
     * IMicroserviceState, com.sitewhere.spi.microservice.state.IMicroserviceState)
     */
    @Override
    public void onMicroserviceUpdated(IMicroserviceState previous, IMicroserviceState updated) {
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesListener#
     * onMicroserviceRemoved(com.sitewhere.spi.microservice.state.
     * IMicroserviceState)
     */
    @Override
    public void onMicroserviceRemoved(IMicroserviceState state) {
	detectServiceRemoved(state.getMicroservice());
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesListener#
     * onTenantEngineAdded(com.sitewhere.spi.microservice.state.IMicroserviceState,
     * com.sitewhere.spi.microservice.state.ITenantEngineState)
     */
    @Override
    public void onTenantEngineAdded(IMicroserviceState microservice, ITenantEngineState tenantEngine) {
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesListener#
     * onTenantEngineUpdated(com.sitewhere.spi.microservice.state.
     * IMicroserviceState, com.sitewhere.spi.microservice.state.ITenantEngineState,
     * com.sitewhere.spi.microservice.state.ITenantEngineState)
     */
    @Override
    public void onTenantEngineUpdated(IMicroserviceState microservice, ITenantEngineState previous,
	    ITenantEngineState updated) {
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesListener#
     * onTenantEngineRemoved(com.sitewhere.spi.microservice.state.
     * IMicroserviceState, com.sitewhere.spi.microservice.state.ITenantEngineState)
     */
    @Override
    public void onTenantEngineRemoved(IMicroserviceState microservice, ITenantEngineState tenantEngine) {
    }

    /**
     * Detect whether a service was added/scaled.
     * 
     * @param snapshot
     */
    protected void detectServiceAdded(IMicroserviceState state) {
	IMicroserviceDetails microservice = state.getMicroservice();
	if (getTargetIdentifier().equals(microservice.getIdentifier())) {
	    T existing = getApiChannelForHost(microservice.getHostname());
	    if (existing == null) {
		getLogger().info("Microservice for '" + getTargetIdentifier() + "' at hostname "
			+ microservice.getHostname() + " added. Initializing API channel.");
		try {
		    initializeApiChannel(microservice.getHostname());
		} catch (SiteWhereException e) {
		    getLogger().error("Unable to initialize API channel for " + microservice.getHostname() + ".");
		}
	    } else {
		getLogger().info("API channel already active for host " + microservice.getHostname() + ".");
	    }
	}
    }

    /**
     * Detect whether a service was removed.
     * 
     * @param snapshot
     */
    protected void detectServiceRemoved(IMicroserviceDetails microservice) {
	List<String> missing = new ArrayList<String>();
	for (T channel : getApiChannels()) {
	    if (microservice.getHostname().equals(channel.getHostname())) {
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
}