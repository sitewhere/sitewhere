/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.old;

/**
 * Enumerates elements used by inbound processing chain parser.
 * 
 * @author Derek
 */
public interface IInboundProcessingChainParser {

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Reference to custom inbound event processor */
	InboundEventProcessor("inbound-event-processor"),

	/** Event storage processor */
	EventStorageProcessor("event-storage-processor"),

	/** Registration processor */
	RegistrationProcessor("registration-processor"),

	/** Device stream processor */
	DeviceStreamProcessor("device-stream-processor"),

	/** Hazelcast queue processor */
	HazelcastQueueProcessor("hazelcast-queue-processor");

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
}