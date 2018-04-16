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
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.sitewhere.grpc.client.spi.ApiNotAvailableException;
import com.sitewhere.rest.model.microservice.state.InstanceMicroservice;
import com.sitewhere.rest.model.microservice.state.InstanceTenantEngine;
import com.sitewhere.rest.model.microservice.state.InstanceTopologyEntry;
import com.sitewhere.rest.model.microservice.state.InstanceTopologySnapshot;
import com.sitewhere.rest.model.microservice.state.MicroserviceState;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.state.IInstanceMicroservice;
import com.sitewhere.spi.microservice.state.IInstanceTenantEngine;
import com.sitewhere.spi.microservice.state.IInstanceTopologyEntry;
import com.sitewhere.spi.microservice.state.IInstanceTopologySnapshot;
import com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesListener;
import com.sitewhere.spi.microservice.state.IMicroserviceDetails;
import com.sitewhere.spi.microservice.state.IMicroserviceState;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.spi.microservice.state.ITopologyStateAggregator;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Kafka consumer that listens for state updates and aggregates them to produce
 * an estimated topology of the SiteWhere instance.
 * 
 * @author Derek
 */
public class TopologyStateAggregator extends MicroserviceStateUpdatesKafkaConsumer implements ITopologyStateAggregator {

    /** Interval at which tenant engine will be checked */
    private static final long TENANT_ENGINE_CHECK_INTERVAL = 2 * 1000;

    /** Latest inferred instance topology snapshot */
    private IInstanceTopologySnapshot instanceTopologySnapshot = new InstanceTopologySnapshot();

    /** List of listeners for topology updates */
    private List<IInstanceTopologyUpdatesListener> listeners = new CopyOnWriteArrayList<>();

    /*
     * @see com.sitewhere.spi.microservice.state.ITopologyStateAggregator#
     * getTenantEngineState(java.lang.String, java.util.UUID)
     */
    @Override
    public List<ITenantEngineState> getTenantEngineState(MicroserviceIdentifier identifier, UUID tenantId)
	    throws SiteWhereException {
	List<ITenantEngineState> result = new ArrayList<>();
	IInstanceTopologyEntry entry = getInstanceTopologySnapshot().getTopologyEntriesByIdentifier().get(identifier);
	if (entry == null) {
	    return result;
	}
	for (IInstanceMicroservice microservice : entry.getMicroservicesByHostname().values()) {
	    IInstanceTenantEngine tenantEngine = microservice.getTenantEngines().get(tenantId);
	    if (tenantEngine != null) {
		result.add(tenantEngine.getLatestState());
	    }
	}
	return result;
    }

    /*
     * @see com.sitewhere.spi.microservice.state.ITopologyStateAggregator#
     * waitForTenantEngineAvailable(java.lang.String, java.util.UUID, long,
     * java.util.concurrent.TimeUnit, long)
     */
    @Override
    public void waitForTenantEngineAvailable(MicroserviceIdentifier identifier, UUID tenantId, long duration,
	    TimeUnit unit, long logMessageDelay) throws SiteWhereException {
	long start = System.currentTimeMillis();
	long deadline = start + unit.toMillis(duration);
	long logAfter = start + unit.toMillis(logMessageDelay);
	while ((System.currentTimeMillis() - deadline) < 0) {
	    try {
		List<ITenantEngineState> engines = getTenantEngineState(identifier, tenantId);
		for (ITenantEngineState engine : engines) {
		    if (engine.getLifecycleStatus() == LifecycleStatus.Started) {
			return;
		    }
		}
		if ((System.currentTimeMillis() - logAfter) > 0) {
		    if (engines.size() > 0) {
			getLogger().info("Tenant engine/s found. Waiting for started state.");
		    } else {
			getLogger().info("No tenant engine/s found. Waiting for startup.");
		    }
		}
		Thread.sleep(TENANT_ENGINE_CHECK_INTERVAL);
	    } catch (Exception e) {
		throw new ApiNotAvailableException("Unhandled exception waiting for tenant engine to become available.",
			e);
	    }
	}
	throw new ApiNotAvailableException("Tenant engine not available within timeout period.");
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IMicroserviceStateUpdatesKafkaConsumer#
     * onMicroserviceStateUpdate(com.sitewhere.spi.microservice.state.
     * IMicroserviceState)
     */
    @Override
    public void onMicroserviceStateUpdate(IMicroserviceState state) {
	getLogger().debug("Received microservice update from '" + state.getMicroservice().getIdentifier() + "'.");
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
	getLogger().debug("Received tenant update from '" + updated.getMicroservice().getIdentifier() + "'.");
	MicroserviceState placeholder = new MicroserviceState();
	placeholder.setMicroservice(updated.getMicroservice());
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
	    } else {
		getLogger().debug(
			"Ignored update of '" + updated.getMicroservice().getIdentifier() + "' due to same status.");
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
	IMicroserviceDetails microservice = state.getMicroservice();
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
	IMicroserviceDetails details = state.getMicroservice();
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
	IMicroserviceDetails microservice = state.getMicroservice();
	getLogger().debug("Detected termination of microservice (" + microservice.getIdentifier() + ":"
		+ microservice.getHostname() + ").");
	IInstanceTopologyEntry entry = getInstanceTopologySnapshot().getTopologyEntriesByIdentifier()
		.get(microservice.getIdentifier());
	if (entry != null) {
	    IInstanceMicroservice removed = entry.getMicroservicesByHostname().remove(microservice.getHostname());
	    if (removed != null) {
		getLogger().debug("Removed terminated microservice (" + microservice.getIdentifier() + ":"
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
	return microservice.getMicroservice().getIdentifier() + ":" + microservice.getMicroservice().getHostname();
    }

    /**
     * Get printable version of microservice/tenant engine details.
     * 
     * @param microservice
     * @param tenantEngine
     * @return
     */
    protected String tenantId(IMicroserviceState microservice, ITenantEngineState tenantEngine) {
	return microservice.getMicroservice().getIdentifier() + ":" + microservice.getMicroservice().getHostname() + ":"
		+ tenantEngine.getTenantId();
    }

    /**
     * Notify listeners that microservice was added.
     * 
     * @param microservice
     */
    protected void onMicroserviceAdded(IMicroserviceState microservice) {
	for (IInstanceTopologyUpdatesListener listener : listeners) {
	    listener.onMicroserviceAdded(microservice);
	}
    }

    /**
     * Notify listeners that microservice was updated.
     * 
     * @param previous
     * @param updated
     */
    protected void onMicroserviceUpdated(IMicroserviceState previous, IMicroserviceState updated) {
	for (IInstanceTopologyUpdatesListener listener : listeners) {
	    listener.onMicroserviceUpdated(previous, updated);
	}
    }

    /**
     * Notify listeners that microservice was removed.
     * 
     * @param microservice
     */
    protected void onMicroserviceRemoved(IMicroserviceState microservice) {
	for (IInstanceTopologyUpdatesListener listener : listeners) {
	    listener.onMicroserviceRemoved(microservice);
	}
    }

    /**
     * Notify listeners that tenant engine was added.
     * 
     * @param microservice
     * @param tenantEngine
     */
    protected void onTenantEngineAdded(IMicroserviceState microservice, ITenantEngineState tenantEngine) {
	for (IInstanceTopologyUpdatesListener listener : listeners) {
	    listener.onTenantEngineAdded(microservice, tenantEngine);
	}
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
	for (IInstanceTopologyUpdatesListener listener : listeners) {
	    listener.onTenantEngineUpdated(microservice, previous, updated);
	}
    }

    /**
     * Notify listeners that tenant engine was removed.
     * 
     * @param microservice
     * @param tenantEngine
     */
    protected void onTenantEngineRemoved(IMicroserviceState microservice, ITenantEngineState tenantEngine) {
	for (IInstanceTopologyUpdatesListener listener : listeners) {
	    listener.onTenantEngineRemoved(microservice, tenantEngine);
	}
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
}