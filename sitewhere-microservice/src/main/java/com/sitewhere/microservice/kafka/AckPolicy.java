/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.kafka;

/**
 * Indicates level of acknowledgement producer expects from Kafka.
 */
public enum AckPolicy {

    /** No acknowledgement required */
    FireAndForget("0"),

    /** Acknowledgement from leader only */
    Leader("1"),

    /** Acknowledgement from all replicas */
    All("all");

    /** Config passed to Kafka */
    private String config;

    private AckPolicy(String config) {
	this.config = config;
    }

    public String getConfig() {
	return config;
    }
}
