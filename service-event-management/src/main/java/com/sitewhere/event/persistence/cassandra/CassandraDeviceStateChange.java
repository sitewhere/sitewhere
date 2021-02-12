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
import com.datastax.driver.core.UDTValue;
import com.sitewhere.rest.model.device.event.DeviceStateChange;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceStateChange;

/**
 * Handles mapping of device state change event fields to Cassandra records.
 */
public class CassandraDeviceStateChange implements ICassandraEventBinder<IDeviceStateChange> {

    /** Static instance */
    public static final ICassandraEventBinder<IDeviceStateChange> INSTANCE = new CassandraDeviceStateChange();

    // State change field.
    public static final String FIELD_STATE_CHANGE = "state_change";

    // Category field.
    public static final String FIELD_ATTRIBUTE = "attribute";

    // Type field.
    public static final String FIELD_TYPE = "type";

    // Previous state field.
    public static final String FIELD_PREVIOUS_STATE = "previous_state";

    // New state field.
    public static final String FIELD_NEW_STATE = "new_state";

    /*
     * @see
     * com.sitewhere.event.persistence.cassandra.ICassandraEventBinder#bind(com.
     * sitewhere.event.persistence.cassandra.CassandraEventManagementClient,
     * com.datastax.driver.core.BoundStatement,
     * com.sitewhere.spi.device.event.IDeviceEvent)
     */
    @Override
    public void bind(CassandraEventManagementClient client, BoundStatement bound, IDeviceStateChange event)
	    throws SiteWhereException {
	CassandraDeviceStateChange.bindFields(client, bound, event);
    }

    /*
     * @see
     * com.sitewhere.event.persistence.cassandra.ICassandraEventBinder#load(com.
     * sitewhere.event.persistence.cassandra.CassandraEventManagementClient,
     * com.datastax.driver.core.Row)
     */
    @Override
    public IDeviceStateChange load(CassandraEventManagementClient client, Row row) throws SiteWhereException {
	DeviceStateChange event = new DeviceStateChange();
	CassandraDeviceStateChange.loadFields(client, event, row);
	return event;
    }

    /*
     * @see
     * com.sitewhere.event.persistence.cassandra.ICassandraEventBinder#getEventType(
     * )
     */
    @Override
    public DeviceEventType getEventType() {
	return DeviceEventType.StateChange;
    }

    /**
     * Bind fields from a device state change to an existing {@link BoundStatement}.
     * 
     * @param client
     * @param bound
     * @param state
     * @throws SiteWhereException
     */
    public static void bindFields(CassandraEventManagementClient client, BoundStatement bound, IDeviceStateChange state)
	    throws SiteWhereException {
	CassandraDeviceEvent.bindEventFields(bound, state);

	UDTValue udt = client.getStateChangeType().newValue();
	udt.setString(FIELD_ATTRIBUTE, state.getAttribute());
	udt.setString(FIELD_TYPE, state.getType());
	udt.setString(FIELD_PREVIOUS_STATE, state.getPreviousState());
	udt.setString(FIELD_NEW_STATE, state.getNewState());
	bound.setUDTValue(FIELD_STATE_CHANGE, udt);
    }

    /**
     * Load fields from a row into a device state change event.
     * 
     * @param client
     * @param state
     * @param row
     * @throws SiteWhereException
     */
    public static void loadFields(CassandraEventManagementClient client, DeviceStateChange state, Row row)
	    throws SiteWhereException {
	CassandraDeviceEvent.loadEventFields(state, row);

	UDTValue udt = row.getUDTValue(FIELD_STATE_CHANGE);
	state.setAttribute(udt.getString(FIELD_ATTRIBUTE));
	state.setType(udt.getString(FIELD_TYPE));
	state.setPreviousState(udt.getString(FIELD_PREVIOUS_STATE));
	state.setNewState(udt.getString(FIELD_NEW_STATE));
    }
}