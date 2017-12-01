/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.state;

/**
 * Consumes instance topology update messages from Kafka an distributes them to
 * any interested listeners.
 * 
 * @author Derek
 */
public interface IInstanceTopologyUpdatesManager extends IInstanceTopologyUpdatesKafkaConsumer {

    /**
     * Add a listener.
     * 
     * @param listener
     */
    public void addListener(IInstanceTopologyUpdatesListener listener);

    /**
     * Remove a listener.
     * 
     * @param listener
     */
    public void removeListener(IInstanceTopologyUpdatesListener listener);
}