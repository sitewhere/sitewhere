/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.parser;

/**
 * Enumerates elements used by common datastore parser.
 * 
 * @author Derek
 */
public interface IDatastoreCommonParser {

    public static enum Elements {

	/** Device management datastore */
	DeviceManagementDatastore("device-management-datastore"),

	/** Event management datastore */
	EventManagementDatastore("event-management-datastore");

	/** Event code */
	private String localName;

	private Elements(String localName) {
	    this.localName = localName;
	}

	public static Elements getByLocalName(String localName) {
	    for (Elements value : Elements.values()) {
		if (value.getLocalName().equals(localName)) {
		    return value;
		}
	    }
	    return null;
	}

	public String getLocalName() {
	    return localName;
	}

	public void setLocalName(String localName) {
	    this.localName = localName;
	}
    }

    public static enum DeviceManagementDatastoreElements {

	/** MongoDB datastore */
	MongoDBDatastore("mongodb-datastore"),

	/** MongoDB datastore reference */
	MongoDBReference("mongodb-datastore-reference");

	/** Event code */
	private String localName;

	private DeviceManagementDatastoreElements(String localName) {
	    this.localName = localName;
	}

	public static DeviceManagementDatastoreElements getByLocalName(String localName) {
	    for (DeviceManagementDatastoreElements value : DeviceManagementDatastoreElements.values()) {
		if (value.getLocalName().equals(localName)) {
		    return value;
		}
	    }
	    return null;
	}

	public String getLocalName() {
	    return localName;
	}

	public void setLocalName(String localName) {
	    this.localName = localName;
	}
    }

    public static enum EventManagementDatastoreElements {

	/** MongoDB datastore */
	MongoDBDatastore("mongodb-datastore"),

	/** MongoDB datastore reference */
	MongoDBReference("mongodb-datastore-reference"),

	/** InfluxDB datastore */
	InfluxDBDatastore("influxdb-datastore"),

	/** InfluxDB datastore reference */
	InfluxDBReference("influxdb-datastore-reference"),

	/** Cassandra datastore */
	CassandraDatastore("cassandra-datastore"),

	/** Cassandra datastore reference */
	CassandraReference("cassandra-datastore-reference");

	/** Event code */
	private String localName;

	private EventManagementDatastoreElements(String localName) {
	    this.localName = localName;
	}

	public static EventManagementDatastoreElements getByLocalName(String localName) {
	    for (EventManagementDatastoreElements value : EventManagementDatastoreElements.values()) {
		if (value.getLocalName().equals(localName)) {
		    return value;
		}
	    }
	    return null;
	}

	public String getLocalName() {
	    return localName;
	}

	public void setLocalName(String localName) {
	    this.localName = localName;
	}
    }
}