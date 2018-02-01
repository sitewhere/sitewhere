/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.influxdb;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.LogLevel;
import org.influxdb.InfluxDBFactory;

import com.sitewhere.configuration.instance.influxdb.InfluxConfiguration;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.server.lifecycle.parameters.StringComponentParameter;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponentParameter;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Client used for connecting to and interacting with an InfluxDB server.
 * 
 * @author Derek
 */
public class InfluxDbClient extends TenantEngineLifecycleComponent implements IDiscoverableTenantLifecycleComponent {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** InfluxDB configuration parameters */
    private InfluxConfiguration configuration;

    /** InfluxDB handle */
    private InfluxDB influx;

    /** Hostname parameter */
    private ILifecycleComponentParameter<String> hostname;

    /** Database parameter */
    private ILifecycleComponentParameter<String> database;

    public InfluxDbClient(InfluxConfiguration configuration) {
	this.configuration = configuration;
    }

    /*
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initializeParameters()
     */
    @Override
    public void initializeParameters() throws SiteWhereException {
	// Add hostname.
	this.hostname = StringComponentParameter.newBuilder(this, "Hostname").value(getConfiguration().getHostname())
		.makeRequired().build();
	getParameters().add(hostname);

	// Add database.
	this.database = StringComponentParameter.newBuilder(this, "Database").value(getConfiguration().getDatabase())
		.makeRequired().build();
	getParameters().add(database);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	String connectionUrl = "http://" + getHostname().getValue() + ":" + getConfiguration().getPort();
	this.influx = InfluxDBFactory.connect(connectionUrl, getConfiguration().getUsername(),
		getConfiguration().getPassword());
	influx.createDatabase(getDatabase().getValue());
	if (getConfiguration().isEnableBatch()) {
	    influx.enableBatch(getConfiguration().getBatchChunkSize(), getConfiguration().getBatchIntervalMs(),
		    TimeUnit.MILLISECONDS);
	}
	influx.setLogLevel(convertLogLevel(getConfiguration().getLogLevel()));
    }

    /**
     * Convert log level setting to expected enum value.
     * 
     * @param level
     * @return
     */
    protected LogLevel convertLogLevel(String level) {
	if ((level == null) || (level.equalsIgnoreCase("none"))) {
	    return LogLevel.NONE;
	} else if (level.equalsIgnoreCase("basic")) {
	    return LogLevel.BASIC;
	} else if (level.equalsIgnoreCase("headers")) {
	    return LogLevel.HEADERS;
	} else if (level.equalsIgnoreCase("full")) {
	    return LogLevel.FULL;
	}
	return LogLevel.NONE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent#
     * isRequired()
     */
    @Override
    public boolean isRequired() {
	return true;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    public InfluxConfiguration getConfiguration() {
	return configuration;
    }

    public void setConfiguration(InfluxConfiguration configuration) {
	this.configuration = configuration;
    }

    public InfluxDB getInflux() {
	return influx;
    }

    public void setInflux(InfluxDB influx) {
	this.influx = influx;
    }

    public ILifecycleComponentParameter<String> getHostname() {
	return hostname;
    }

    public void setHostname(ILifecycleComponentParameter<String> hostname) {
	this.hostname = hostname;
    }

    public ILifecycleComponentParameter<String> getDatabase() {
	return database;
    }

    public void setDatabase(ILifecycleComponentParameter<String> database) {
	this.database = database;
    }
}