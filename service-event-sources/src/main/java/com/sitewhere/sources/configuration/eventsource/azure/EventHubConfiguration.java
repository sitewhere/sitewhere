/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.configuration.eventsource.azure;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.sources.configuration.eventsource.EventSourceConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Configuration for an Azure Event Hub event source.
 */
public class EventHubConfiguration extends EventSourceConfiguration {

    /** Consumer group name */
    private String consumerGroupName;

    /** Namespace name */
    private String namespaceName;

    /** Event Hub name */
    private String eventHubName;

    /** SAS key name */
    private String sasKeyName;

    /** SAS key */
    private String sasKey;

    /** Storage connection string */
    private String storageConnectionString;

    /** Storage container name */
    private String storageContainerName;

    /** Host name prefix */
    private String hostNamePrefix;

    public EventHubConfiguration(ITenantEngineLifecycleComponent component) {
	super(component);
    }

    /*
     * @see
     * com.sitewhere.sources.configuration.eventsource.EventSourceConfiguration#
     * loadFrom(com.fasterxml.jackson.databind.JsonNode)
     */
    @Override
    public void loadFrom(JsonNode json) throws SiteWhereException {
	this.consumerGroupName = configurableString("consumerGroupName", json, "");
	this.namespaceName = configurableString("namespaceName", json, "");
	this.eventHubName = configurableString("eventHubName", json, "");
	this.sasKeyName = configurableString("sasKeyName", json, "");
	this.sasKey = configurableString("sasKey", json, "");
	this.storageConnectionString = configurableString("storageConnectionString", json, "");
	this.storageContainerName = configurableString("storageContainerName", json, "");
	this.hostNamePrefix = configurableString("hostNamePrefix", json, "");
    }

    public String getConsumerGroupName() {
	return consumerGroupName;
    }

    public void setConsumerGroupName(String consumerGroupName) {
	this.consumerGroupName = consumerGroupName;
    }

    public String getNamespaceName() {
	return namespaceName;
    }

    public void setNamespaceName(String namespaceName) {
	this.namespaceName = namespaceName;
    }

    public String getEventHubName() {
	return eventHubName;
    }

    public void setEventHubName(String eventHubName) {
	this.eventHubName = eventHubName;
    }

    public String getSasKeyName() {
	return sasKeyName;
    }

    public void setSasKeyName(String sasKeyName) {
	this.sasKeyName = sasKeyName;
    }

    public String getSasKey() {
	return sasKey;
    }

    public void setSasKey(String sasKey) {
	this.sasKey = sasKey;
    }

    public String getStorageConnectionString() {
	return storageConnectionString;
    }

    public void setStorageConnectionString(String storageConnectionString) {
	this.storageConnectionString = storageConnectionString;
    }

    public String getStorageContainerName() {
	return storageContainerName;
    }

    public void setStorageContainerName(String storageContainerName) {
	this.storageContainerName = storageContainerName;
    }

    public String getHostNamePrefix() {
	return hostNamePrefix;
    }

    public void setHostNamePrefix(String hostNamePrefix) {
	this.hostNamePrefix = hostNamePrefix;
    }
}
