/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.state;

/**
 * Kafka consumer that listens for state updates and aggregates them to produce
 * an estimated topology of the SiteWhere instance.
 * 
 * @author Derek
 */
public interface ITopologyStateAggregator extends IMicroserviceStateUpdatesKafkaConsumer {

    /**
     * Get latest instance topology snapshot.
     * 
     * @return
     */
    public IInstanceTopologySnapshot getInstanceTopologySnapshot();

    /**
     * Add listener for instance topology updates.
     * 
     * @param listener
     */
    public void addInstanceTopologyUpdatesListener(IInstanceTopologyUpdatesListener listener);

    /**
     * Remove listener for instance topology updates.
     * 
     * @param listener
     */
    public void removeInstanceTopologyUpdatesListener(IInstanceTopologyUpdatesListener listener);
}