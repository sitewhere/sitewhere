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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.state.IInstanceTopologySnapshot;
import com.sitewhere.spi.microservice.state.IInstanceTopologySnapshotsListener;
import com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesManager;

/**
 * Consumes instance topology snapshot messages from Kafka an distributes them
 * to any interested listeners.
 * 
 * @author Derek
 */
public class InstanceTopologySnapshotsManager extends InstanceTopologySnapshotsKafkaConsumer
	implements IInstanceTopologyUpdatesManager {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** List of listeners */
    private List<IInstanceTopologySnapshotsListener> listeners = new ArrayList<IInstanceTopologySnapshotsListener>();

    public InstanceTopologySnapshotsManager(IMicroservice microservice) {
	super(microservice);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IInstanceTopologySnapshotsKafkaConsumer#
     * onInstanceTopologySnapshot(com.sitewhere.spi.microservice.state.
     * IInstanceTopologySnapshot)
     */
    @Override
    public void onInstanceTopologySnapshot(IInstanceTopologySnapshot snapshot) {
	for (IInstanceTopologySnapshotsListener listener : listeners) {
	    listener.onInstanceTopologySnapshot(snapshot);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesManager#
     * addListener(com.sitewhere.spi.microservice.state.
     * IInstanceTopologySnapshotsListener)
     */
    @Override
    public void addListener(IInstanceTopologySnapshotsListener listener) {
	if (!listeners.contains(listener)) {
	    listeners.add(listener);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesManager#
     * removeListener(com.sitewhere.spi.microservice.state.
     * IInstanceTopologySnapshotsListener)
     */
    @Override
    public void removeListener(IInstanceTopologySnapshotsListener listener) {
	listeners.remove(listener);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}