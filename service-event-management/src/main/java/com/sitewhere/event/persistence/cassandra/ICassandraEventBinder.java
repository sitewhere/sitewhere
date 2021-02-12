/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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