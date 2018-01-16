/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.state;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.rest.model.microservice.state.InstanceMicroservice;
import com.sitewhere.rest.model.microservice.state.InstanceTenantEngine;
import com.sitewhere.rest.model.microservice.state.InstanceTopologyEntry;
import com.sitewhere.rest.model.microservice.state.InstanceTopologySnapshot;
import com.sitewhere.rest.model.microservice.state.MicroserviceState;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.state.IInstanceMicroservice;
import com.sitewhere.spi.microservice.state.IInstanceTenantEngine;
import com.sitewhere.spi.microservice.state.IInstanceTopologyEntry;
import com.sitewhere.spi.microservice.state.IInstanceTopologySnapshot;
import com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesListener;
import com.sitewhere.spi.microservice.state.IMicroserviceDetails;
import com.sitewhere.spi.microservice.state.IMicroserviceState;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.spi.microservice.state.ITopologyStateAggregator;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Kafka consumer that listens for state updates and aggregates them to produce
 * an estimated topology of the SiteWhere instance.
 * 
 * @author Derek
 */
public class TopologyStateAggregator extends MicroserviceStateUpdatesKafkaConsumer implements ITopologyStateAggregator {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Latest inferred instance topology snapshot */
    private IInstanceTopologySnapshot instanceTopologySnapshot = new InstanceTopologySnapshot();

    /** List of listeners for topology updates */
    private List<IInstanceTopologyUpdatesListener> listeners = new CopyOnWriteArrayList<>();

    /** Executor service */
    private ExecutorService executor;

    public TopologyStateAggregator(IMicroservice microservice) {
	super(microservice);
    }

    /*
     * @see com.sitewhere.spi.microservice.state.ITopologyStateAggregator#
     * getTenantEngineState(java.lang.String, java.lang.String)
     */
    @Override
    public List<ITenantEngineState> getTenantEngineState(String identifier, String tenantId) throws SiteWhereException {
	IInstanceTopologyEntry entry = getInstanceTopologySnapshot().getTopologyEntriesByIdentifier().get(identifier);
	if (entry == null) {
	    throw new SiteWhereException("No microservices found for the given identifier.");
	}
	List<ITenantEngineState> result = new ArrayList<>();
	for (IInstanceMicroservice microservice : entry.getMicroservicesByHostname().values()) {
	    IInstanceTenantEngine tenantEngine = microservice.getTenantEngines().get(tenantId);
	    if (tenantEngine != null) {
		result.add(tenantEngine.getLatestState());
	    }
	}
	return result;
    }

    /*
     * @see com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);
	executor = Executors.newSingleThreadExecutor();
    }

    /*
     * @see
     * com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer#stop(com.sitewhere
     * .spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.stop(monitor);
	if (executor != null) {
	    executor.shutdownNow();
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IMicroserviceStateUpdatesKafkaConsumer#
     * onMicroserviceStateUpdate(com.sitewhere.spi.microservice.state.
     * IMicroserviceState)
     */
    @Override
    public void onMicroserviceStateUpdate(IMicroserviceState state) {
	if ((state.getLifecycleStatus() == LifecycleStatus.Terminating)
		|| (state.getLifecycleStatus() == LifecycleStatus.Terminated)) {
	    removeMicroservice(state);
	} else {
	    getOrCreateMicroservice(state);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IMicroserviceStateUpdatesKafkaConsumer#
     * onTenantEngineStateUpdate(com.sitewhere.spi.microservice.state.
     * ITenantEngineState)
     */
    @Override
    public void onTenantEngineStateUpdate(ITenantEngineState updated) {
	MicroserviceState placeholder = new MicroserviceState();
	placeholder.setMicroserviceDetails(updated.getMicroserviceDetails());
	placeholder.setLifecycleStatus(LifecycleStatus.Started);

	// Get microservice and existing engine (creating if necessary.
	IInstanceMicroservice microservice = getOrCreateMicroservice(placeholder);
	IInstanceTenantEngine engine = microservice.getTenantEngines().get(updated.getTenantId());
	if (engine == null) {
	    InstanceTenantEngine created = new InstanceTenantEngine();
	    created.setLatestState(updated);
	    created.setLastUpdated(System.currentTimeMillis());
	    microservice.getTenantEngines().put(updated.getTenantId(), created);
	    engine = created;
	    onTenantEngineAdded(microservice.getLatestState(), updated);
	} else {
	    ITenantEngineState existing = engine.getLatestState();
	    ((InstanceTenantEngine) engine).setLatestState(updated);
	    ((InstanceTenantEngine) engine).setLastUpdated(System.currentTimeMillis());

	    // Handle terminated tenant engine.
	    if ((updated.getLifecycleStatus() == LifecycleStatus.Terminating)
		    || (updated.getLifecycleStatus() == LifecycleStatus.Terminated)) {
		microservice.getTenantEngines().remove(existing.getTenantId());
		onTenantEngineRemoved(microservice.getLatestState(), existing);
	    } else if (existing.getLifecycleStatus() != updated.getLifecycleStatus()) {
		onTenantEngineUpdated(microservice.getLatestState(), existing, updated);
	    }
	}
    }

    /**
     * Get microservice record for given state or create one if not already
     * registered.
     * 
     * @param updated
     * @return
     */
    protected IInstanceMicroservice getOrCreateMicroservice(IMicroserviceState updated) {
	IInstanceMicroservice microservice = getMicroservice(updated);
	if (microservice == null) {
	    microservice = addMicroservice(updated);
	} else {
	    IMicroserviceState existing = microservice.getLatestState();
	    ((InstanceMicroservice) microservice).setLatestState(updated);
	    if (existing.getLifecycleStatus() != updated.getLifecycleStatus()) {
		onMicroserviceUpdated(existing, updated);
	    }
	}
	((InstanceMicroservice) microservice).setLastUpdated(System.currentTimeMillis());
	return microservice;
    }

    /**
     * Get existing microservice record from topology or null if no match.
     * 
     * @param state
     * @return
     */
    protected IInstanceMicroservice getMicroservice(IMicroserviceState state) {
	IMicroserviceDetails microservice = state.getMicroserviceDetails();
	IInstanceTopologyEntry entry = getInstanceTopologySnapshot().getTopologyEntriesByIdentifier()
		.get(microservice.getIdentifier());
	if (entry == null) {
	    return null;
	}
	return entry.getMicroservicesByHostname().get(microservice.getHostname());
    }

    /**
     * Add record for microservice based on state.
     * 
     * @param state
     * @return
     */
    protected IInstanceMicroservice addMicroservice(IMicroserviceState state) {
	IMicroserviceDetails details = state.getMicroserviceDetails();
	IInstanceTopologyEntry entry = getInstanceTopologySnapshot().getTopologyEntriesByIdentifier()
		.get(details.getIdentifier());
	if (entry == null) {
	    entry = new InstanceTopologyEntry();
	    getInstanceTopologySnapshot().getTopologyEntriesByIdentifier().put(details.getIdentifier(), entry);
	}

	// Create record for microservice.
	InstanceMicroservice microservice = new InstanceMicroservice();
	microservice.setLatestState(state);
	entry.getMicroservicesByHostname().put(details.getHostname(), microservice);

	getLogger().debug(
		"Detected addition of microservice (" + details.getIdentifier() + ":" + details.getHostname() + ").");
	onMicroserviceAdded(state);

	return microservice;
    }

    /**
     * Remove a microservice that has been terminated.
     * 
     * @param state
     */
    protected void removeMicroservice(IMicroserviceState state) {
	IMicroserviceDetails microservice = state.getMicroserviceDetails();
	getLogger().debug("Detected termination of microservice (" + microservice.getIdentifier() + ":"
		+ microservice.getHostname() + ").");
	IInstanceTopologyEntry entry = getInstanceTopologySnapshot().getTopologyEntriesByIdentifier()
		.get(microservice.getIdentifier());
	if (entry != null) {
	    IInstanceMicroservice removed = entry.getMicroservicesByHostname().remove(microservice.getHostname());
	    if (removed != null) {
		getLogger().info("Removed terminated microservice (" + microservice.getIdentifier() + ":"
			+ microservice.getHostname() + ").");
		onMicroserviceRemoved(state);
	    }
	}
    }

    /**
     * Get printable version of microservice details.
     * 
     * @param microservice
     * @return
     */
    protected String microserviceId(IMicroserviceState microservice) {
	return microservice.getMicroserviceDetails().getIdentifier() + ":"
		+ microservice.getMicroserviceDetails().getHostname();
    }

    /**
     * Get printable versio of microservice/tenant engine details.
     * 
     * @param microservice
     * @param tenantEngine
     * @return
     */
    protected String tenantId(IMicroserviceState microservice, ITenantEngineState tenantEngine) {
	return microservice.getMicroserviceDetails().getIdentifier() + ":"
		+ microservice.getMicroserviceDetails().getHostname() + ":" + tenantEngine.getTenantId();
    }

    /**
     * Notify listeners that microservice was added.
     * 
     * @param microservice
     */
    protected void onMicroserviceAdded(IMicroserviceState microservice) {
	getLogger().debug("Microservice added for '" + microserviceId(microservice) + "'.");
	executor.execute(new Runnable() {

	    @Override
	    public void run() {
		for (IInstanceTopologyUpdatesListener listener : listeners) {
		    listener.onMicroserviceAdded(microservice);
		}
	    }
	});
    }

    /**
     * Notify listeners that microservice was updated.
     * 
     * @param previous
     * @param updated
     */
    protected void onMicroserviceUpdated(IMicroserviceState previous, IMicroserviceState updated) {
	getLogger().debug("Microservice updated for '" + microserviceId(previous) + "'. State is "
		+ updated.getLifecycleStatus().name() + ".");
	executor.execute(new Runnable() {

	    @Override
	    public void run() {
		for (IInstanceTopologyUpdatesListener listener : listeners) {
		    listener.onMicroserviceUpdated(previous, updated);
		}
	    }
	});
    }

    /**
     * Notify listeners that microservice was removed.
     * 
     * @param microservice
     */
    protected void onMicroserviceRemoved(IMicroserviceState microservice) {
	getLogger().debug("Microservice removed for '" + microserviceId(microservice) + "'.");
	executor.execute(new Runnable() {

	    @Override
	    public void run() {
		for (IInstanceTopologyUpdatesListener listener : listeners) {
		    listener.onMicroserviceRemoved(microservice);
		}
	    }
	});
    }

    /**
     * Notify listeners that tenant engine was added.
     * 
     * @param microservice
     * @param tenantEngine
     */
    protected void onTenantEngineAdded(IMicroserviceState microservice, ITenantEngineState tenantEngine) {
	getLogger().debug("Tenant engine added for '" + tenantId(microservice, tenantEngine) + "'.");
	executor.execute(new Runnable() {

	    @Override
	    public void run() {
		for (IInstanceTopologyUpdatesListener listener : listeners) {
		    listener.onTenantEngineAdded(microservice, tenantEngine);
		}
	    }
	});
    }

    /**
     * Notify listeners that tenant engine was updated.
     * 
     * @param microservice
     * @param previous
     * @param updated
     */
    protected void onTenantEngineUpdated(IMicroserviceState microservice, ITenantEngineState previous,
	    ITenantEngineState updated) {
	getLogger().debug("Tenant engine updated for '" + tenantId(microservice, previous) + "'. State is "
		+ updated.getLifecycleStatus().name() + ".");
	executor.execute(new Runnable() {

	    @Override
	    public void run() {
		for (IInstanceTopologyUpdatesListener listener : listeners) {
		    listener.onTenantEngineUpdated(microservice, previous, updated);
		}
	    }
	});
    }

    /**
     * Notify listeners that tenant engine was removed.
     * 
     * @param microservice
     * @param tenantEngine
     */
    protected void onTenantEngineRemoved(IMicroserviceState microservice, ITenantEngineState tenantEngine) {
	getLogger().debug("Tenant engine removed for '" + tenantId(microservice, tenantEngine) + "'.");
	executor.execute(new Runnable() {

	    @Override
	    public void run() {
		for (IInstanceTopologyUpdatesListener listener : listeners) {
		    listener.onTenantEngineRemoved(microservice, tenantEngine);
		}
	    }
	});
    }

    /*
     * @see com.sitewhere.spi.microservice.state.ITopologyStateAggregator#
     * addInstanceTopologyUpdatesListener(com.sitewhere.spi.microservice.state.
     * IInstanceTopologyUpdatesListener)
     */
    @Override
    public void addInstanceTopologyUpdatesListener(IInstanceTopologyUpdatesListener listener) {
	listeners.add(listener);
    }

    /*
     * @see com.sitewhere.spi.microservice.state.ITopologyStateAggregator#
     * removeInstanceTopologyUpdatesListener(com.sitewhere.spi.microservice.state.
     * IInstanceTopologyUpdatesListener)
     */
    @Override
    public void removeInstanceTopologyUpdatesListener(IInstanceTopologyUpdatesListener listener) {
	listeners.remove(listener);
    }

    /*
     * @see com.sitewhere.spi.microservice.state.ITopologyStateAggregator#
     * getInstanceTopologySnapshot()
     */
    @Override
    public IInstanceTopologySnapshot getInstanceTopologySnapshot() {
	return instanceTopologySnapshot;
    }

    public void setInstanceTopologySnapshot(IInstanceTopologySnapshot instanceTopologySnapshot) {
	this.instanceTopologySnapshot = instanceTopologySnapshot;
    }

    public List<IInstanceTopologyUpdatesListener> getListeners() {
	return listeners;
    }

    public void setListeners(List<IInstanceTopologyUpdatesListener> listeners) {
	this.listeners = listeners;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}