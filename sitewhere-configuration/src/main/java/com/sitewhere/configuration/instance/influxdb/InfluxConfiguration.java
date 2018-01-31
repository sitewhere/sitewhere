/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.instance.influxdb;

/**
 * Common configuration settings for an InfluxDB client.
 * 
 * @author Derek
 */
public class InfluxConfiguration {

    /** Connection URL */
    private String connectUrl = "http://localhost:8086";

    /** Username */
    private String username = "root";

    /** Password */
    private String password = "root";

    /** Database name */
    private String database = "sitewhere";

    /** Retention policy */
    private String retention = "autogen";

    /** Indicates if batch delivery is enabled */
    private boolean enableBatch = true;

    /** Max records in batch */
    private int batchChunkSize = 2000;

    /** Max time to wait for sending batch */
    private int batchIntervalMs = 100;

    /** Log level */
    private String logLevel;

    public String getConnectUrl() {
	return connectUrl;
    }

    public void setConnectUrl(String connectUrl) {
	this.connectUrl = connectUrl;
    }

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public String getDatabase() {
	return database;
    }

    public void setDatabase(String database) {
	this.database = database;
    }

    public String getRetention() {
	return retention;
    }

    public void setRetention(String retention) {
	this.retention = retention;
    }

    public boolean isEnableBatch() {
	return enableBatch;
    }

    public void setEnableBatch(boolean enableBatch) {
	this.enableBatch = enableBatch;
    }

    public int getBatchChunkSize() {
	return batchChunkSize;
    }

    public void setBatchChunkSize(int batchChunkSize) {
	this.batchChunkSize = batchChunkSize;
    }

    public int getBatchIntervalMs() {
	return batchIntervalMs;
    }

    public void setBatchIntervalMs(int batchIntervalMs) {
	this.batchIntervalMs = batchIntervalMs;
    }

    public String getLogLevel() {
	return logLevel;
    }

    public void setLogLevel(String logLevel) {
	this.logLevel = logLevel;
    }
}