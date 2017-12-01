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
import com.sitewhere.spi.microservice.state.IInstanceTopologyUpdate;
import com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesListener;
import com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesManager;

/**
 * Consumes instance topology update messages from Kafka an distributes them to
 * any interested listeners.
 * 
 * @author Derek
 */
public class InstanceTopologyUpdatesManager extends InstanceTopologyUpdatesKafkaConsumer
	implements IInstanceTopologyUpdatesManager {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** List of listeners */
    private List<IInstanceTopologyUpdatesListener> listeners = new ArrayList<IInstanceTopologyUpdatesListener>();

    public InstanceTopologyUpdatesManager(IMicroservice microservice) {
	super(microservice);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesKafkaConsumer#
     * onInstanceTopologyUpdate(com.sitewhere.spi.microservice.state.
     * IInstanceTopologyUpdate)
     */
    @Override
    public void onInstanceTopologyUpdate(IInstanceTopologyUpdate update) {
	for (IInstanceTopologyUpdatesListener listener : listeners) {
	    listener.onInstanceTopologyUpdated(update);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesManager#
     * addListener(com.sitewhere.spi.microservice.state.
     * IInstanceTopologyUpdatesListener)
     */
    @Override
    public void addListener(IInstanceTopologyUpdatesListener listener) {
	if (!listeners.contains(listener)) {
	    listeners.add(listener);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesManager#
     * removeListener(com.sitewhere.spi.microservice.state.
     * IInstanceTopologyUpdatesListener)
     */
    @Override
    public void removeListener(IInstanceTopologyUpdatesListener listener) {
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