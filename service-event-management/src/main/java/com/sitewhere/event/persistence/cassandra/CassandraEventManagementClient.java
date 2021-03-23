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

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.UserType;
import com.sitewhere.cassandra.CassandraClient;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Adds support for device event management model using Cassandra for storage.
 */
public class CassandraEventManagementClient extends TenantEngineLifecycleComponent {

    /** Cassandra client */
    private CassandraClient client;

    /** User type for device location */
    private UserType locationType;

    /** User type for device measurement */
    private UserType measurementType;

    /** User type for device alert */
    private UserType alertType;

    /** User type for device command invocation */
    private UserType invocationType;

    /** User type for device command response */
    private UserType responseType;

    /** User type for device state change */
    private UserType stateChangeType;

    /** Prepared statement for inserting a device event by id */
    private PreparedStatement insertDeviceEventById;

    /** Prepared statement for inserting a device event by alternate id */
    private PreparedStatement insertDeviceEventByAltId;

    /** Prepared statement for inserting a device event by assignment */
    private PreparedStatement insertDeviceEventByAssignment;

    /** Prepared statement for inserting a device event by customer */
    private PreparedStatement insertDeviceEventByCustomer;

    /** Prepared statement for inserting a device event by area */
    private PreparedStatement insertDeviceEventByArea;

    /** Prepared statement for inserting a device event by asset */
    private PreparedStatement insertDeviceEventByAsset;

    /** Prepared statement for selecting device events by type for an assignment */
    private PreparedStatement selectEventsByAssignmentForType;

    /** Prepared statement for selecting device events by type for a customer */
    private PreparedStatement selectEventsByCustomerForType;

    /** Prepared statement for selecting device events by type for an area */
    private PreparedStatement selectEventsByAreaForType;

    /** Prepared statement for selecting device events by type for an asset */
    private PreparedStatement selectEventsByAssetForType;

    public CassandraEventManagementClient(CassandraClient client) {
	this.client = client;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	initializeTenant();
	initializePreparedStatements();
    }

    /**
     * Initializes tenant keyspace if not already created.
     */
    protected void initializeTenant() throws SiteWhereException {
	// Create keyspace.
	getClient().execute("CREATE KEYSPACE IF NOT EXISTS " + getClient().getConfiguration().getKeyspace()
		+ " WITH replication =  {'class':'SimpleStrategy','replication_factor':'1'}");

	// Use keyspace.
	getClient().execute("USE " + getClient().getConfiguration().getKeyspace() + ";");

	// Create location type.
	getClient().execute("CREATE TYPE IF NOT EXISTS " + getClient().getConfiguration().getKeyspace()
		+ ".sw_location (latitude double, longitude double, elevation double);");
	this.locationType = getClient().getSession().getCluster().getMetadata()
		.getKeyspace(getClient().getConfiguration().getKeyspace()).getUserType("sw_location");

	// Create measurements type.
	getClient().execute("CREATE TYPE IF NOT EXISTS " + getClient().getConfiguration().getKeyspace()
		+ ".sw_measurement (mxname text, mxvalue double);");
	this.measurementType = getClient().getSession().getCluster().getMetadata()
		.getKeyspace(getClient().getConfiguration().getKeyspace()).getUserType("sw_measurement");

	// Create alerts type.
	getClient().execute("CREATE TYPE IF NOT EXISTS " + getClient().getConfiguration().getKeyspace()
		+ ".sw_alert (source tinyint, level tinyint, type text, message text);");
	this.alertType = getClient().getSession().getCluster().getMetadata()
		.getKeyspace(getClient().getConfiguration().getKeyspace()).getUserType("sw_alert");

	// Create command invocation type.
	getClient().execute("CREATE TYPE IF NOT EXISTS " + getClient().getConfiguration().getKeyspace()
		+ ".sw_invocation (initiator tinyint, initiator_id text, target tinyint, target_id text, command_token text, command_params map<text, text>);");
	this.invocationType = getClient().getSession().getCluster().getMetadata()
		.getKeyspace(getClient().getConfiguration().getKeyspace()).getUserType("sw_invocation");

	// Create command response type.
	getClient().execute("CREATE TYPE IF NOT EXISTS " + getClient().getConfiguration().getKeyspace()
		+ ".sw_response (orig_event_id uuid, resp_event_id uuid, response text);");
	this.responseType = getClient().getSession().getCluster().getMetadata()
		.getKeyspace(getClient().getConfiguration().getKeyspace()).getUserType("sw_response");

	// Create state change type.
	getClient().execute("CREATE TYPE IF NOT EXISTS " + getClient().getConfiguration().getKeyspace()
		+ ".sw_state_change (attribute text, type text, previous_state text, new_state text);");
	this.stateChangeType = getClient().getSession().getCluster().getMetadata()
		.getKeyspace(getClient().getConfiguration().getKeyspace()).getUserType("sw_state_change");

	// Create events_by_id table.
	getClient().execute("CREATE TABLE IF NOT EXISTS " + getClient().getConfiguration().getKeyspace()
		+ ".events_by_id (device_id uuid, event_id uuid, alt_id text, event_type tinyint, assignment_id uuid, customer_id uuid, area_id uuid, asset_id uuid, event_date timestamp, received_date timestamp, location frozen<sw_location>, measurement frozen<sw_measurement>, alert frozen<sw_alert>, invocation frozen<sw_invocation>, response frozen<sw_response>, state_change frozen<sw_state_change>, PRIMARY KEY (event_id));");

	// Create events_by_alt_id table.
	getClient().execute("CREATE TABLE IF NOT EXISTS " + getClient().getConfiguration().getKeyspace()
		+ ".events_by_alt_id (device_id uuid, event_id uuid, alt_id text, event_type tinyint, assignment_id uuid, customer_id uuid, area_id uuid, asset_id uuid, event_date timestamp, received_date timestamp, location frozen<sw_location>, measurement frozen<sw_measurement>, alert frozen<sw_alert>, invocation frozen<sw_invocation>, response frozen<sw_response>, state_change frozen<sw_state_change>, PRIMARY KEY (alt_id));");

	// Create events_by_assignment table.
	getClient().execute("CREATE TABLE IF NOT EXISTS " + getClient().getConfiguration().getKeyspace()
		+ ".events_by_assignment (device_id uuid, bucket int, event_id uuid, alt_id text, event_type tinyint, assignment_id uuid, customer_id uuid, area_id uuid, asset_id uuid, event_date timestamp, received_date timestamp, location frozen<sw_location>, measurement frozen<sw_measurement>, alert frozen<sw_alert>, invocation frozen<sw_invocation>, response frozen<sw_response>, state_change frozen<sw_state_change>, PRIMARY KEY ((assignment_id, event_type, bucket), event_date, event_id)) WITH CLUSTERING ORDER BY (event_date desc, event_id asc);");

	// Create events_by_customer table.
	getClient().execute("CREATE TABLE IF NOT EXISTS " + getClient().getConfiguration().getKeyspace()
		+ ".events_by_customer (device_id uuid, bucket int, event_id uuid, alt_id text, event_type tinyint, assignment_id uuid, customer_id uuid, area_id uuid, asset_id uuid, event_date timestamp, received_date timestamp, location frozen<sw_location>, measurement frozen<sw_measurement>, alert frozen<sw_alert>, invocation frozen<sw_invocation>, response frozen<sw_response>, state_change frozen<sw_state_change>, PRIMARY KEY ((customer_id, event_type, bucket), event_date, event_id)) WITH CLUSTERING ORDER BY (event_date desc, event_id asc);");

	// Create events_by_area table.
	getClient().execute("CREATE TABLE IF NOT EXISTS " + getClient().getConfiguration().getKeyspace()
		+ ".events_by_area (device_id uuid, bucket int, event_id uuid, alt_id text, event_type tinyint, assignment_id uuid, customer_id uuid, area_id uuid, asset_id uuid, event_date timestamp, received_date timestamp, location frozen<sw_location>, measurement frozen<sw_measurement>, alert frozen<sw_alert>, invocation frozen<sw_invocation>, response frozen<sw_response>, state_change frozen<sw_state_change>, PRIMARY KEY ((area_id, event_type, bucket), event_date, event_id)) WITH CLUSTERING ORDER BY (event_date desc, event_id asc);");

	// Create events_by_asset table.
	getClient().execute("CREATE TABLE IF NOT EXISTS " + getClient().getConfiguration().getKeyspace()
		+ ".events_by_asset (device_id uuid, bucket int, event_id uuid, alt_id text, event_type tinyint, assignment_id uuid, customer_id uuid, area_id uuid, asset_id uuid, event_date timestamp, received_date timestamp, location frozen<sw_location>, measurement frozen<sw_measurement>, alert frozen<sw_alert>, invocation frozen<sw_invocation>, response frozen<sw_response>, state_change frozen<sw_state_change>, PRIMARY KEY ((asset_id, event_type, bucket), event_date, event_id)) WITH CLUSTERING ORDER BY (event_date desc, event_id asc);");
    }

    /**
     * Initialize prepared statements.
     * 
     * @throws SiteWhereException
     */
    protected void initializePreparedStatements() throws SiteWhereException {
	this.insertDeviceEventById = getClient().getSession().prepare("insert into "
		+ getClient().getConfiguration().getKeyspace()
		+ ".events_by_id (device_id, event_id, alt_id, event_type, assignment_id, customer_id, area_id, asset_id, event_date, received_date, location, measurement, alert, invocation, response, state_change) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	this.insertDeviceEventByAltId = getClient().getSession().prepare("insert into "
		+ getClient().getConfiguration().getKeyspace()
		+ ".events_by_alt_id (device_id, event_id, alt_id, event_type, assignment_id, customer_id, area_id, asset_id, event_date, received_date, location, measurement, alert, invocation, response, state_change) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	this.insertDeviceEventByAssignment = getClient().getSession().prepare("insert into "
		+ getClient().getConfiguration().getKeyspace()
		+ ".events_by_assignment (device_id, bucket, event_id, alt_id, event_type, assignment_id, customer_id, area_id, asset_id, event_date, received_date, location, measurement, alert, invocation, response, state_change) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	this.insertDeviceEventByCustomer = getClient().getSession().prepare("insert into "
		+ getClient().getConfiguration().getKeyspace()
		+ ".events_by_customer (device_id, bucket, event_id, alt_id, event_type, assignment_id, customer_id, area_id, asset_id, event_date, received_date, location, measurement, alert, invocation, response, state_change) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	this.insertDeviceEventByArea = getClient().getSession().prepare("insert into "
		+ getClient().getConfiguration().getKeyspace()
		+ ".events_by_area (device_id, bucket, event_id, alt_id, event_type, assignment_id, customer_id, area_id, asset_id, event_date, received_date, location, measurement, alert, invocation, response, state_change) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	this.insertDeviceEventByAsset = getClient().getSession().prepare("insert into "
		+ getClient().getConfiguration().getKeyspace()
		+ ".events_by_asset (device_id, bucket, event_id, alt_id, event_type, assignment_id, customer_id, area_id, asset_id, event_date, received_date, location, measurement, alert, invocation, response, state_change) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	this.selectEventsByAssignmentForType = getClient().getSession().prepare("select * from "
		+ getClient().getConfiguration().getKeyspace()
		+ ".events_by_assignment where assignment_id=? and event_type=? and bucket=? and event_date >= ? and event_date <= ?");
	this.selectEventsByCustomerForType = getClient().getSession().prepare("select * from "
		+ getClient().getConfiguration().getKeyspace()
		+ ".events_by_customer where customer_id=? and event_type=? and bucket=? and event_date >= ? and event_date <= ?");
	this.selectEventsByAreaForType = getClient().getSession().prepare("select * from "
		+ getClient().getConfiguration().getKeyspace()
		+ ".events_by_area where area_id=? and event_type=? and bucket=? and event_date >= ? and event_date <= ?");
	this.selectEventsByAssetForType = getClient().getSession().prepare("select * from "
		+ getClient().getConfiguration().getKeyspace()
		+ ".events_by_asset where asset_id=? and event_type=? and bucket=? and event_date >= ? and event_date <= ?");
    }

    public UserType getLocationType() {
	return locationType;
    }

    public void setLocationType(UserType locationType) {
	this.locationType = locationType;
    }

    public UserType getMeasurementType() {
	return measurementType;
    }

    public void setMeasurementType(UserType measurementType) {
	this.measurementType = measurementType;
    }

    public UserType getAlertType() {
	return alertType;
    }

    public void setAlertType(UserType alertType) {
	this.alertType = alertType;
    }

    public UserType getInvocationType() {
	return invocationType;
    }

    public void setInvocationType(UserType invocationType) {
	this.invocationType = invocationType;
    }

    public UserType getResponseType() {
	return responseType;
    }

    public UserType getStateChangeType() {
	return stateChangeType;
    }

    public void setStateChangeType(UserType stateChangeType) {
	this.stateChangeType = stateChangeType;
    }

    public void setResponseType(UserType responseType) {
	this.responseType = responseType;
    }

    public PreparedStatement getInsertDeviceEventById() {
	return insertDeviceEventById;
    }

    public void setInsertDeviceEventById(PreparedStatement insertDeviceEventById) {
	this.insertDeviceEventById = insertDeviceEventById;
    }

    public PreparedStatement getInsertDeviceEventByAltId() {
	return insertDeviceEventByAltId;
    }

    public void setInsertDeviceEventByAltId(PreparedStatement insertDeviceEventByAltId) {
	this.insertDeviceEventByAltId = insertDeviceEventByAltId;
    }

    public PreparedStatement getInsertDeviceEventByAssignment() {
	return insertDeviceEventByAssignment;
    }

    public void setInsertDeviceEventByAssignment(PreparedStatement insertDeviceEventByAssignment) {
	this.insertDeviceEventByAssignment = insertDeviceEventByAssignment;
    }

    public PreparedStatement getInsertDeviceEventByCustomer() {
	return insertDeviceEventByCustomer;
    }

    public void setInsertDeviceEventByCustomer(PreparedStatement insertDeviceEventByCustomer) {
	this.insertDeviceEventByCustomer = insertDeviceEventByCustomer;
    }

    public PreparedStatement getInsertDeviceEventByArea() {
	return insertDeviceEventByArea;
    }

    public void setInsertDeviceEventByArea(PreparedStatement insertDeviceEventByArea) {
	this.insertDeviceEventByArea = insertDeviceEventByArea;
    }

    public PreparedStatement getInsertDeviceEventByAsset() {
	return insertDeviceEventByAsset;
    }

    public void setInsertDeviceEventByAsset(PreparedStatement insertDeviceEventByAsset) {
	this.insertDeviceEventByAsset = insertDeviceEventByAsset;
    }

    public PreparedStatement getSelectEventsByAssignmentForType() {
	return selectEventsByAssignmentForType;
    }

    public void setSelectEventsByAssignmentForType(PreparedStatement selectEventsByAssignmentForType) {
	this.selectEventsByAssignmentForType = selectEventsByAssignmentForType;
    }

    public PreparedStatement getSelectEventsByCustomerForType() {
	return selectEventsByCustomerForType;
    }

    public void setSelectEventsByCustomerForType(PreparedStatement selectEventsByCustomerForType) {
	this.selectEventsByCustomerForType = selectEventsByCustomerForType;
    }

    public PreparedStatement getSelectEventsByAreaForType() {
	return selectEventsByAreaForType;
    }

    public void setSelectEventsByAreaForType(PreparedStatement selectEventsByAreaForType) {
	this.selectEventsByAreaForType = selectEventsByAreaForType;
    }

    public PreparedStatement getSelectEventsByAssetForType() {
	return selectEventsByAssetForType;
    }

    public void setSelectEventsByAssetForType(PreparedStatement selectEventsByAssetForType) {
	this.selectEventsByAssetForType = selectEventsByAssetForType;
    }

    public CassandraClient getClient() {
	return client;
    }

    public void setClient(CassandraClient client) {
	this.client = client;
    }
}