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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
import com.sitewhere.spi.security.ITenantAwareAuthentication;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;
import com.sitewhere.spi.tenant.ITenant;

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
    protected static final int API_CHANNEL_WAIT_INTERVAL_IN_SECS = 3;

    /** Amount of time to wait before logging warnings about missing API channel */
    protected static final int API_CHANNEL_WARN_INTERVAL_IN_SECS = 30;

    /** Interval at which channels are re-verified */
    protected static final long CHANNEL_VALID_CHECK_INTERVAL_IN_MS = 5 * 1000;

    /** Map of API channels indexed by hostname */
    private Map<String, T> apiChannels = new ConcurrentHashMap<>();

    /** Map of last access to API channels indexed by hostname */
    private Map<String, Long> apiChannelLastAccess = new ConcurrentHashMap<>();

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

    /**
     * Indicates if the given API channel should be considered a match for a given
     * request.
     * 
     * @param tenant
     * @param channel
     * @return
     * @throws SiteWhereException
     */
    protected boolean isApiChannelMatch(ITenant tenant, T channel) throws SiteWhereException {
	return true;
    }

    /*
     * @see com.sitewhere.grpc.model.spi.IApiDemux#getApiChannel()
     */
    @Override
    public T getApiChannel() {
	try {
	    ITenant tenant = null;
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    if ((authentication != null) && (authentication instanceof ITenantAwareAuthentication)) {
		tenant = ((ITenantAwareAuthentication) authentication).getTenant();
	    }
	    return getApiChannelWithConstraints(tenant);
	} catch (SiteWhereException e) {
	    throw new RuntimeException("Unable to get API channel.", e);
	}
    }

    /**
     * Loop through channels to find an available match based on the routing
     * strategy. The matching criteria may be re-implemented in subclasses.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    protected T getApiChannelWithConstraints(ITenant tenant) throws SiteWhereException {
	int channelCount = getApiChannels().size();
	if (channelCount == 0) {
	    throw new ApiChannelNotAvailableException("No API Channels found.");
	}

	while (channelCount > 0) {
	    T selectedChannel = getRoutingStrategy().chooseApiChannel(getApiChannels());
	    Long lastAccess = getApiChannelLastAccess().get(selectedChannel.getHostname());
	    if ((lastAccess == null)
		    || (System.currentTimeMillis() - lastAccess > CHANNEL_VALID_CHECK_INTERVAL_IN_MS)) {
		selectedChannel.waitForChannelAvailable();
		getApiChannelLastAccess().put(selectedChannel.getHostname(), System.currentTimeMillis());
	    }
	    if (isApiChannelMatch(tenant, selectedChannel)) {
		return selectedChannel;
	    }
	    channelCount--;
	}

	throw new ApiChannelNotAvailableException("No API Channel available.");
    }

    /*
     * @see com.sitewhere.grpc.client.spi.IApiDemux#waitForMicroserviceAvailable()
     */
    @Override
    public void waitForMicroserviceAvailable() {
	try {
	    waitForApiChannelAvailable(null);
	} catch (SiteWhereException e) {
	    throw new RuntimeException("Error waiting for microservice to become available.", e);
	}
    }

    /**
     * Wait for an API channel to become available. A tenant may be passed to add
     * extra matching criteria for channel selection.
     * 
     * @param tenant
     * @throws SiteWhereException
     */
    protected void waitForApiChannelAvailable(ITenant tenant) throws SiteWhereException {
	long deadline = System.currentTimeMillis() + (API_CHANNEL_WARN_INTERVAL_IN_SECS * 1000);
	while (true) {
	    try {
		getApiChannelWithConstraints(tenant);
		return;
	    } catch (ApiChannelNotAvailableException e) {
		if ((System.currentTimeMillis() - deadline) < 0) {
		    getLogger().debug(GrpcClientMessages.API_CHANNEL_WAITING_FOR_AVAILABLE, getTargetIdentifier());
		} else {
		    getLogger().warn(GrpcClientMessages.API_CHANNEL_WAITING_FOR_AVAILABLE, getTargetIdentifier());
		}
	    }
	    try {
		Thread.sleep(API_CHANNEL_WAIT_INTERVAL_IN_SECS * 1000);
	    } catch (InterruptedException e) {
		getLogger().warn(GrpcClientMessages.API_CHANNEL_INTERRUPTED_WAITING_FOR_MS);
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
	T toRemove = getApiChannels().remove(host);
	if (toRemove != null) {
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
	return getApiChannels().get(host);
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
		getLogger().debug(GrpcClientMessages.API_CHANNEL_INIT_AFTER_MS_ADDED, getTargetIdentifier(),
			microservice.getHostname());
		try {
		    initializeApiChannel(microservice.getHostname());
		} catch (SiteWhereException e) {
		    getLogger().error(GrpcClientMessages.API_CHANNEL_UNABLE_TO_INIT, microservice.getHostname());
		}
	    } else {
		getLogger().info(GrpcClientMessages.API_CHANNEL_ALREADY_ACTIVE, microservice.getHostname());
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
	for (T channel : getApiChannels().values()) {
	    if (microservice.getHostname().equals(channel.getHostname())) {
		missing.add(channel.getHostname());
	    }
	}
	for (String hostname : missing) {
	    getLogger().info(GrpcClientMessages.API_CHANNEL_REMOVED_AFTER_MS_REMOVED, hostname);
	    try {
		removeApiChannel(hostname);
	    } catch (SiteWhereException e) {
		getLogger().error(GrpcClientMessages.API_CHANNEL_UNABLE_TO_REMOVE, hostname);
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
		    getLogger().error(GrpcClientMessages.API_CHANNEL_FAILED_INIT, getHost());
		    return;
		}

		// Start channel.
		startNestedComponent(channel, monitor, true);
		if (channel.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		    getLogger().error(GrpcClientMessages.API_CHANNEL_FAILED_START, getHost());
		    return;
		}

		T existing = getApiChannels().putIfAbsent(channel.getHostname(), channel);
		if (existing != null) {
		    getLogger().error("Creating API channel for hostname when one already existed.");
		}
	    } catch (SiteWhereException e) {
		getLogger().error(e, GrpcClientMessages.API_CHANNEL_EXCEPTION_ON_CREATE, getHost());
	    } catch (Throwable t) {
		getLogger().error(t, GrpcClientMessages.API_CHANNEL_UNHANDLED_EXCEPTION_ON_CREATE, getHost());
	    }
	}

	public String getHost() {
	    return host;
	}
    }

    /*
     * @see com.sitewhere.grpc.client.spi.IApiDemux#getApiChannels()
     */
    @Override
    public Map<String, T> getApiChannels() {
	return apiChannels;
    }

    public void setApiChannels(Map<String, T> apiChannels) {
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

    protected Map<String, Long> getApiChannelLastAccess() {
	return apiChannelLastAccess;
    }
}