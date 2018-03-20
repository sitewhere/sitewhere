/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.instance.cassandra;

/**
 * Common configuration settings for a Cassandra client.
 * 
 * @author Derek
 */
public class CassandraConfiguration {

    /** Default value for contact points */
    private static final String DEFAULT_CONTACT_POINTS = "cassandra";

    /** Default value for keyspace */
    private static final String DEFAULT_KEYSPACE = "sitewhere";

    /** Comma delimited list of contact points for cassandra cluster */
    private String contactPoints = DEFAULT_CONTACT_POINTS;

    /** Keyspace being accessed */
    private String keyspace = DEFAULT_KEYSPACE;

    public String getContactPoints() {
	return contactPoints;
    }

    public void setContactPoints(String contactPoints) {
	this.contactPoints = contactPoints;
    }

    public String getKeyspace() {
	return keyspace;
    }

    public void setKeyspace(String keyspace) {
	this.keyspace = keyspace;
    }
}