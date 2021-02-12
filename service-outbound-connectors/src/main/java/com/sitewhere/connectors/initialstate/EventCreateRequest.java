/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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