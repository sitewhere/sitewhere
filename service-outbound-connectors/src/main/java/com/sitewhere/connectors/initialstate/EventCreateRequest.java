/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.initialstate;

/**
 * Object used for JSON marshaling of bucket request data.
 */
public class EventCreateRequest {

    /** Stream key */
    private String key;

    /** Value */
    private String value;

    /** Seconds since epoch */
    private double epoch;

    /** ISO-8601 data */
    private String iso8601;

    public String getKey() {
	return key;
    }

    public void setKey(String key) {
	this.key = key;
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    public double getEpoch() {
	return epoch;
    }

    public void setEpoch(double epoch) {
	this.epoch = epoch;
    }

    public String getIso8601() {
	return iso8601;
    }

    public void setIso8601(String iso8601) {
	this.iso8601 = iso8601;
    }
}