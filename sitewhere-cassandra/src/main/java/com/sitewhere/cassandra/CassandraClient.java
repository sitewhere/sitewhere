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
import com.datastax.driver.core.Session;
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
    }

    /**
     * Execute a query in the current Cassandra session.
     * 
     * @param query
     * @throws SiteWhereException
     */
    public void execute(String query) throws SiteWhereException {
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
}