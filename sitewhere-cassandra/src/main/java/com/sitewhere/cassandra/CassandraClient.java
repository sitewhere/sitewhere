/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.UserType;
import com.datastax.driver.core.exceptions.QueryExecutionException;
import com.sitewhere.configuration.instance.cassandra.CassandraConfiguration;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.server.lifecycle.parameters.StringComponentParameter;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponentParameter;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Client used for connecting to and interacting with an Apache Cassandra
 * cluster.
 * 
 * @author Derek
 */
public class CassandraClient extends TenantEngineLifecycleComponent implements IDiscoverableTenantLifecycleComponent {

    /** Cassandra configuration */
    private CassandraConfiguration configuration;

    /** Cassandra cluster reference */
    private Cluster cluster;

    /** Cassandra session */
    private Session session;

    /** User type for device location */
    private UserType locationType;

    /** User type for device measurements */
    private UserType measurementsType;

    /** User type for device alert */
    private UserType alertType;

    /** User type for device command invocation */
    private UserType invocationType;

    /** Prepared statement for inserting a device location by id */
    private PreparedStatement insertDeviceEventById;

    /** Prepared statement for inserting a device location by assignment */
    private PreparedStatement insertDeviceEventByAssignment;

    /** Prepared statement for inserting a device location by area */
    private PreparedStatement insertDeviceEventByArea;

    /** Prepared statement for inserting a device location by asset */
    private PreparedStatement insertDeviceEventByAsset;

    /** Prepared statement for selecting device events by type for an assignment */
    private PreparedStatement selectEventsByAssignmentForType;

    /** Prepared statement for selecting device events by type for an area */
    private PreparedStatement selectEventsByAreaForType;

    /** Contact points parameter */
    private ILifecycleComponentParameter<String> contactPoints;

    /** Keyspace parameter */
    private ILifecycleComponentParameter<String> keyspace;

    /** Bucket length in milliseconds */
    private long bucketLengthInMs = 60 * 60 * 1000;

    public CassandraClient(CassandraConfiguration configuration) {
	this.configuration = configuration;
    }

    /*
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initializeParameters()
     */
    @Override
    public void initializeParameters() throws SiteWhereException {
	// Add contact points.
	this.contactPoints = StringComponentParameter.newBuilder(this, "Contact Points")
		.value(getConfiguration().getContactPoints()).makeRequired().build();
	getParameters().add(contactPoints);

	// Add keyspace.
	this.keyspace = StringComponentParameter.newBuilder(this, "Keyspace").value(getConfiguration().getKeyspace())
		.makeRequired().build();
	getParameters().add(keyspace);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	// Verify that contact points were specified.
	String[] contactPoints = getContactPoints().getValue().split(",");
	if (contactPoints.length == 0) {
	    throw new SiteWhereException("No contact points specified for Cassandra cluster.");
	}

	Cluster.Builder builder = Cluster.builder();
	for (String contactPoint : contactPoints) {
	    builder.addContactPoint(contactPoint.trim());
	}
	PoolingOptions pooling = new PoolingOptions();
	pooling.setMaxRequestsPerConnection(HostDistance.LOCAL, 32768);
	pooling.setMaxRequestsPerConnection(HostDistance.REMOTE, 32768);
	pooling.setMaxQueueSize(32768);
	builder.withPoolingOptions(pooling);
	this.cluster = builder.build();
	this.session = getCluster().connect();

	// Intitialize tenant data constructs.
	initializeTenant();
	initializePreparedStatements();
    }

    /**
     * Initializes tenant keyspace if not already created.
     */
    protected void initializeTenant() throws SiteWhereException {
	// Create keyspace.
	execute("CREATE KEYSPACE IF NOT EXISTS " + getKeyspace().getValue()
		+ " WITH replication =  {'class':'SimpleStrategy','replication_factor':'1'}");

	// Use keyspace.
	execute("USE " + getKeyspace().getValue() + ";");

	// Create location type.
	execute("CREATE TYPE IF NOT EXISTS " + getKeyspace().getValue()
		+ ".sw_location (latitude double, longitude double, elevation double);");
	this.locationType = getSession().getCluster().getMetadata().getKeyspace(getKeyspace().getValue())
		.getUserType("sw_location");

	// Create measurements type.
	execute("CREATE TYPE IF NOT EXISTS " + getKeyspace().getValue()
		+ ".sw_measurements (mx_values map<text, double>);");
	this.measurementsType = getSession().getCluster().getMetadata().getKeyspace(getKeyspace().getValue())
		.getUserType("sw_measurements");

	// Create alerts type.
	execute("CREATE TYPE IF NOT EXISTS " + getKeyspace().getValue()
		+ ".sw_alert (source tinyint, level tinyint, type text, message text);");
	this.alertType = getSession().getCluster().getMetadata().getKeyspace(getKeyspace().getValue())
		.getUserType("sw_alert");

	// Create command invocation type.
	execute("CREATE TYPE IF NOT EXISTS " + getKeyspace().getValue()
		+ ".sw_invocation (initiator tinyint, initiator_id text, target tinyint, target_id text, command_token text, command_params map<text, text>);");
	this.invocationType = getSession().getCluster().getMetadata().getKeyspace(getKeyspace().getValue())
		.getUserType("sw_invocation");

	// Create events_by_id table.
	execute("CREATE TABLE IF NOT EXISTS " + getKeyspace().getValue()
		+ ".events_by_id (device_id uuid, event_id uuid, alt_id text, event_type tinyint, assignment_id uuid, area_id uuid, asset_id uuid, event_date timestamp, received_date timestamp, location frozen<sw_location>, measurements frozen<sw_measurements>, alert frozen<sw_alert>, invocation frozen<sw_invocation>, PRIMARY KEY (event_id));");

	// Create events_by_assignment table.
	execute("CREATE TABLE IF NOT EXISTS " + getKeyspace().getValue()
		+ ".events_by_assignment (device_id uuid, bucket int, event_id uuid, alt_id text, event_type tinyint, assignment_id uuid, area_id uuid, asset_id uuid, event_date timestamp, received_date timestamp, location frozen<sw_location>, measurements frozen<sw_measurements>, alert frozen<sw_alert>, invocation frozen<sw_invocation>, PRIMARY KEY ((assignment_id, event_type, bucket), event_date, event_id)) WITH CLUSTERING ORDER BY (event_date desc, event_id asc);");

	// Create events_by_area table.
	execute("CREATE TABLE IF NOT EXISTS " + getKeyspace().getValue()
		+ ".events_by_area (device_id uuid, bucket int, event_id uuid, alt_id text, event_type tinyint, assignment_id uuid, area_id uuid, asset_id uuid, event_date timestamp, received_date timestamp, location frozen<sw_location>, measurements frozen<sw_measurements>, alert frozen<sw_alert>, invocation frozen<sw_invocation>, PRIMARY KEY ((area_id, event_type, bucket), event_date, event_id)) WITH CLUSTERING ORDER BY (event_date desc, event_id asc);");

	// Create events_by_asset table.
	execute("CREATE TABLE IF NOT EXISTS " + getKeyspace().getValue()
		+ ".events_by_asset (device_id uuid, bucket int, event_id uuid, alt_id text, event_type tinyint, assignment_id uuid, area_id uuid, asset_id uuid, event_date timestamp, received_date timestamp, location frozen<sw_location>, measurements frozen<sw_measurements>, alert frozen<sw_alert>, invocation frozen<sw_invocation>, PRIMARY KEY ((asset_id, event_type, bucket), event_date, event_id)) WITH CLUSTERING ORDER BY (event_date desc, event_id asc);");
    }

    /**
     * Initialize prepared statements.
     * 
     * @throws SiteWhereException
     */
    protected void initializePreparedStatements() throws SiteWhereException {
	this.insertDeviceEventById = getSession().prepare("insert into " + getKeyspace().getValue()
		+ ".events_by_id (device_id, event_id, alt_id, event_type, assignment_id, area_id, asset_id, event_date, received_date, location, measurements, alert, invocation) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	this.insertDeviceEventByAssignment = getSession().prepare("insert into " + getKeyspace().getValue()
		+ ".events_by_assignment (device_id, bucket, event_id, alt_id, event_type, assignment_id, area_id, asset_id, event_date, received_date, location, measurements, alert, invocation) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	this.insertDeviceEventByArea = getSession().prepare("insert into " + getKeyspace().getValue()
		+ ".events_by_area (device_id, bucket, event_id, alt_id, event_type, assignment_id, area_id, asset_id, event_date, received_date, location, measurements, alert, invocation) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	this.insertDeviceEventByAsset = getSession().prepare("insert into " + getKeyspace().getValue()
		+ ".events_by_asset (device_id, bucket, event_id, alt_id, event_type, assignment_id, area_id, asset_id, event_date, received_date, location, measurements, alert, invocation) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	this.selectEventsByAssignmentForType = getSession().prepare("select * from " + getKeyspace().getValue()
		+ ".events_by_assignment where assignment_id=? and event_type=? and bucket=? and event_date >= ? and event_date <= ?");
	this.selectEventsByAreaForType = getSession().prepare("select * from " + getKeyspace().getValue()
		+ ".events_by_area where area_id=? and event_type=? and bucket=? and event_date >= ? and event_date <= ?");
    }

    /**
     * Execute a query in the current Cassandra session.
     * 
     * @param query
     * @throws SiteWhereException
     */
    protected void execute(String query) throws SiteWhereException {
	try {
	    getSession().execute(query);
	} catch (QueryExecutionException e) {
	    throw new SiteWhereException("Query execution failed.", e);
	}
    }

    /**
     * Get value that allows events to be grouped into buckets based on date.
     * 
     * @param dateInMs
     * @return
     */
    public int getBucketValue(long dateInMs) {
	return (int) (dateInMs / getBucketLengthInMs());
    }

    /*
     * @see
     * com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent#
     * isRequired()
     */
    @Override
    public boolean isRequired() {
	return true;
    }

    public CassandraConfiguration getConfiguration() {
	return configuration;
    }

    public void setConfiguration(CassandraConfiguration configuration) {
	this.configuration = configuration;
    }

    public ILifecycleComponentParameter<String> getContactPoints() {
	return contactPoints;
    }

    public void setContactPoints(ILifecycleComponentParameter<String> contactPoints) {
	this.contactPoints = contactPoints;
    }

    public ILifecycleComponentParameter<String> getKeyspace() {
	return keyspace;
    }

    public void setKeyspace(ILifecycleComponentParameter<String> keyspace) {
	this.keyspace = keyspace;
    }

    public long getBucketLengthInMs() {
	return bucketLengthInMs;
    }

    public void setBucketLengthInMs(long bucketLengthInMs) {
	this.bucketLengthInMs = bucketLengthInMs;
    }

    public Cluster getCluster() {
	return cluster;
    }

    public void setCluster(Cluster cluster) {
	this.cluster = cluster;
    }

    public Session getSession() {
	return session;
    }

    public void setSession(Session session) {
	this.session = session;
    }

    public UserType getLocationType() {
	return locationType;
    }

    public void setLocationType(UserType locationType) {
	this.locationType = locationType;
    }

    public UserType getMeasurementsType() {
	return measurementsType;
    }

    public void setMeasurementsType(UserType measurementsType) {
	this.measurementsType = measurementsType;
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

    public PreparedStatement getInsertDeviceEventById() {
	return insertDeviceEventById;
    }

    public void setInsertDeviceEventById(PreparedStatement insertDeviceEventById) {
	this.insertDeviceEventById = insertDeviceEventById;
    }

    public PreparedStatement getInsertDeviceEventByAssignment() {
	return insertDeviceEventByAssignment;
    }

    public void setInsertDeviceEventByAssignment(PreparedStatement insertDeviceEventByAssignment) {
	this.insertDeviceEventByAssignment = insertDeviceEventByAssignment;
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

    public PreparedStatement getSelectEventsByAreaForType() {
	return selectEventsByAreaForType;
    }

    public void setSelectEventsByAreaForType(PreparedStatement selectEventsByAreaForType) {
	this.selectEventsByAreaForType = selectEventsByAreaForType;
    }
}