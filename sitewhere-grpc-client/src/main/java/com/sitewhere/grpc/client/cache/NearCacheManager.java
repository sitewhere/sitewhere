/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionConfig.MaxSizePolicy;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.config.NearCacheConfig.LocalUpdatePolicy;
import com.hazelcast.core.HazelcastInstance;
import com.sitewhere.grpc.client.spi.cache.ICacheProvider;
import com.sitewhere.grpc.client.spi.cache.INearCacheManager;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.hazelcast.IHazelcastManager;
import com.sitewhere.spi.microservice.state.IInstanceMicroservice;
import com.sitewhere.spi.microservice.state.IInstanceTopologyEntry;
import com.sitewhere.spi.microservice.state.IInstanceTopologySnapshot;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Manages a Hazelcast client with a near cache that is connected to remote
 * Hazelcast instance which provides the underlying data.
 * 
 * @author Derek
 */
public class NearCacheManager extends LifecycleComponent implements INearCacheManager {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(NearCacheManager.class);

    /** Owning microservice */
    private IMicroservice owner;

    /** Type of cache to connect to */
    private MicroserviceIdentifier target;

    /** Cache providers served by this manager */
    private ICacheProvider<?, ?>[] cacheProviders;

    /** Wrapped Hazelcast client instance */
    private HazelcastInstance hazelcastInstance;

    /** For background threads */
    private ExecutorService executor;

    public NearCacheManager(IMicroservice owner, MicroserviceIdentifier target) {
	this.owner = owner;
	this.target = target;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	executor = Executors.newSingleThreadExecutor();
	executor.execute(new TopologyMonitor());
    }

    /**
     * Establishes Hazelcast connection with known list of live microservices.
     * 
     * @param members
     * @throws Exception
     */
    protected void connect(List<String> members) throws Exception {
	ClientConfig config = new ClientConfig();
	config.getGroupConfig().setName(IHazelcastManager.GROUP_NAME).setPassword(IHazelcastManager.GROUP_PASSWORD);
	config.getNetworkConfig().setAddresses(members);
	config.setProperty("hazelcast.logging.type", "slf4j");

	// Create the default near cache configuration.
	config.addNearCacheConfig(createNearCacheConfig());

	hazelcastInstance = HazelcastClient.newHazelcastClient(config);
	getLogger().info("Hazelcast client for near cache started.");
    }

    protected NearCacheConfig createNearCacheConfig() {
	// Configure eviction.
	EvictionConfig evictionConfig = new EvictionConfig();
	evictionConfig.setMaximumSizePolicy(MaxSizePolicy.ENTRY_COUNT).setEvictionPolicy(EvictionPolicy.LRU)
		.setSize(10000);

	// Configure near cache.
	return new NearCacheConfig().setName("default").setInMemoryFormat(InMemoryFormat.BINARY)
		.setInvalidateOnChange(true).setTimeToLiveSeconds(60).setMaxIdleSeconds(20)
		.setEvictionConfig(evictionConfig).setCacheLocalEntries(false)
		.setLocalUpdatePolicy(LocalUpdatePolicy.INVALIDATE);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (executor != null) {
	    executor.shutdownNow();
	}
	if (getHazelcastInstance() != null) {
	    getHazelcastInstance().shutdown();
	}
    }

    /**
     * Waits for other microservices to enter the topology before establishing
     * Hazelcast grid.
     * 
     * @author Derek
     */
    public class TopologyMonitor implements Runnable {

	@Override
	public void run() {
	    while (true) {
		IInstanceTopologySnapshot topology = getOwner().getTopologyStateAggregator()
			.getInstanceTopologySnapshot();
		if (topology != null) {
		    Map<MicroserviceIdentifier, IInstanceTopologyEntry> byIdent = topology
			    .getTopologyEntriesByIdentifier();
		    IInstanceTopologyEntry allForTarget = byIdent.get(getTarget());
		    if (allForTarget != null) {
			List<String> members = new ArrayList<>();
			Map<String, IInstanceMicroservice> byHostname = allForTarget.getMicroservicesByHostname();
			for (String hostname : byHostname.keySet()) {
			    String member = hostname + ":" + IHazelcastManager.HZ_PORT;
			    members.add(member);
			}

			// Only attempt to connect if members were found.
			if (members.size() > 0) {
			    try {
				connect(members);
				return;
			    } catch (SiteWhereException e) {
				getLogger().error("Error connecting near cache to remote.", e);
			    } catch (IllegalStateException e) {
				getLogger().debug("Unable to connect near cache to remote.", e);
			    } catch (Throwable e) {
				getLogger().warn("Unhandled exception connecting near cache to remote.", e);
			    }
			}
		    }
		}
		try {
		    Thread.sleep(2000);
		} catch (InterruptedException e) {
		    getLogger().warn("Hazelcast topology monitor thread shutting down.");
		    return;
		}
	    }
	}
    }

    /*
     * @see com.sitewhere.grpc.client.spi.cache.INearCacheManager#getOwner()
     */
    @Override
    public IMicroservice getOwner() {
	return owner;
    }

    public void setOwner(IMicroservice owner) {
	this.owner = owner;
    }

    /*
     * @see com.sitewhere.grpc.client.spi.cache.INearCacheManager#getTarget()
     */
    @Override
    public MicroserviceIdentifier getTarget() {
	return target;
    }

    public void setTarget(MicroserviceIdentifier target) {
	this.target = target;
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.cache.INearCacheManager#getCacheProviders()
     */
    @Override
    public ICacheProvider<?, ?>[] getCacheProviders() {
	return cacheProviders;
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.cache.INearCacheManager#setCacheProviders(com.
     * sitewhere.spi.cache.ICacheProvider[])
     */
    @Override
    public void setCacheProviders(ICacheProvider<?, ?>... cacheProviders) {
	this.cacheProviders = cacheProviders;
    }

    /*
     * @see com.sitewhere.spi.microservice.hazelcast.IHazelcastProvider#
     * getHazelcastInstance()
     */
    @Override
    public HazelcastInstance getHazelcastInstance() {
	return hazelcastInstance;
    }

    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
	this.hazelcastInstance = hazelcastInstance;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
	return LOGGER;
    }
}