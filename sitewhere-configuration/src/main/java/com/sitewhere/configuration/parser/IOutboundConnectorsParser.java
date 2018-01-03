/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.parser;

/**
 * Enumerates elements used by outbound processing parser.
 * 
 * @author Derek
 */
public interface IOutboundConnectorsParser {

    // Root element name.
    public static final String ROOT = "outbound-connectors";

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Reference to custom inbound connector */
	OutboundConnector("outbound-connector"),

	/** Tests location values against zones */
	ZoneTestEventProcessor("zone-test-event-processor"),

	/** Sends outbound events to an MQTT topic */
	MqttConnector("mqtt-connector"),

	/** Sends outbound events to a RabbitMQ exchange */
	RabbitMqConnector("rabbit-mq-connector"),

	/** Sends outbound events over Hazelcast topics */
	HazelcastConnector("hazelcast-connector"),

	/** Indexes outbound events in Apache Solr */
	SolrConnector("solr-connector"),

	/** Sends outbound events to an Azure EventHub */
	AzureEventHubConnector("azure-eventhub-connector"),

	/** Sends outbound events to an Amazon SQS queue */
	AmazonSqsConnector("amazon-sqs-connector"),

	/** Sends outbound events to InitialState.com */
	InitialStateConnector("initial-state-connector"),

	/** Sends outbound events to dweet.io */
	DweetIoConnector("dweet-io-connector"),

	/** Outbound connector that delegates to a Groovy script */
	GroovyConnector("groovy-connector");

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

    /**
     * Expected filter elements.
     * 
     * @author Derek
     */
    public static enum Filters {

	/** Include or exclude events for a site */
	SiteFilter("site-filter"),

	/** Include or exclude events for a specification */
	SpecificationFilter("specification-filter"),

	/** Include or exclude events based on running a script */
	GroovyFilter("groovy-filter");

	/** Event code */
	private String localName;

	private Filters(String localName) {
	    this.localName = localName;
	}

	public static Filters getByLocalName(String localName) {
	    for (Filters value : Filters.values()) {
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

    /**
     * Expected multicaster elements.
     * 
     * @author Derek
     */
    public static enum Multicasters {

	/** Multicasts to all devices with a given specification */
	AllWithSpecificationMulticaster("all-with-specification-multicaster");

	/** Event code */
	private String localName;

	private Multicasters(String localName) {
	    this.localName = localName;
	}

	public static Multicasters getByLocalName(String localName) {
	    for (Multicasters value : Multicasters.values()) {
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

    /**
     * Expected multicaster elements.
     * 
     * @author Derek
     */
    public static enum RouteBuilders {

	/** Uses Groovy script to build routes */
	GroovyRouteBuilder("groovy-route-builder");

	/** Event code */
	private String localName;

	private RouteBuilders(String localName) {
	    this.localName = localName;
	}

	public static RouteBuilders getByLocalName(String localName) {
	    for (RouteBuilders value : RouteBuilders.values()) {
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