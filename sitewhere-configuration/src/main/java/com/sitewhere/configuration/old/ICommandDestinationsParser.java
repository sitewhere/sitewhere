/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.old;

/**
 * Enumerates elements used by command destinations parser.
 * 
 * @author Derek
 */
public interface ICommandDestinationsParser {

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Command destination */
	CommandDestination("command-destination"),

	/** MQTT command destination */
	MqttCommandDestination("mqtt-command-destination"),

	/** CoAP command destination */
	CoapCommandDestination("coap-command-destination"),

	/** Twilio command destination */
	TwilioCommandDestination("twilio-command-destination");

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
     * Enumeration of binary command encoders.
     * 
     * @author Derek
     */
    public static enum BinaryCommandEncoders {

	/** Encodes commands with standard SiteWhere GPB naming convention */
	ProtobufEncoder("protobuf-command-encoder"),

	/**
	 * Encodes commands with hybrid protobuf / Java serialization approach
	 */
	JavaHybridProtobufEncoder("java-protobuf-hybrid-encoder"),

	/** Encodes commands using a JSON representation */
	JsonCommandEncoder("json-command-encoder"),

	/** Uses a Groovy script to encode commands */
	GroovyCommandEncoder("groovy-command-encoder"),

	/** Reference to externally defined event decoder */
	CommandEncoder("command-encoder");

	/** Event code */
	private String localName;

	private BinaryCommandEncoders(String localName) {
	    this.localName = localName;
	}

	public static BinaryCommandEncoders getByLocalName(String localName) {
	    for (BinaryCommandEncoders value : BinaryCommandEncoders.values()) {
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
     * Enumeration of String command encoders.
     * 
     * @author Derek
     */
    public static enum StringCommandEncoders {

	/** Reference to externally defined event decoder */
	GroovyStringCommandEncoder("groovy-string-command-encoder");

	/** Event code */
	private String localName;

	private StringCommandEncoders(String localName) {
	    this.localName = localName;
	}

	public static StringCommandEncoders getByLocalName(String localName) {
	    for (StringCommandEncoders value : StringCommandEncoders.values()) {
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