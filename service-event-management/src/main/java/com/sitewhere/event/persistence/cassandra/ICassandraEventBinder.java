/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.cassandra;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Row;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceEvent;

/**
 * Interface for objects that handle binding event data into Cassandra bound
 * statements.
 *
 * @param <T>
 */
public interface ICassandraEventBinder<I extends IDeviceEvent> {

    /**
     * Binds fields values from a device event into a bound statement.
     * 
     * @param client
     * @param bound
     * @param event
     * @throws SiteWhereException
     */
    public void bind(CassandraEventManagementClient client, BoundStatement bound, I event) throws SiteWhereException;

    /**
     * Load fields from a {@link Row} into an event.
     * 
     * @param client
     * @param row
     * @throws SiteWhereException
     */
    public I load(CassandraEventManagementClient client, Row row) throws SiteWhereException;

    /**
     * Get event type this binder corresponds to.
     * 
     * @return
     */
    public DeviceEventType getEventType();
}