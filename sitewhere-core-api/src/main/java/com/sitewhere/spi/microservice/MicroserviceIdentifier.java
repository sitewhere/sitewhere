/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice;

/**
 * Provides a list of known identifiers for microservices.
 * 
 * @author Derek
 */
public enum MicroserviceIdentifier implements IFunctionIdentifier {

    AssetManagement("asset-management"),

    BatchOperations("batch-operations"),

    CommandDelivery("command-delivery"),

    DeviceManagement("device-management"),

    DeviceRegistration("device-registration"),

    EventManagement("event-management"),

    EventSearch("event-search"),

    EventSources("event-sources"),

    InboundProcessing("inbound-processing"),

    InstanceManagement("instance-management"),

    LabelGeneration("label-generation"),

    OutboundConnectors("outbound-connectors"),

    PresenceManagement("presence-management"),

    RuleProcessing("rule-processing"),

    ScheduleManagement("schedule-management"),

    StreamingMedia("streaming-media"),

    TenantManagement("tenant-management"),

    UserManagement("user-management"),

    WebRest("web-rest");

    /** Path */
    private String path;

    private MicroserviceIdentifier(String path) {
	this.path = path;
    }

    public static MicroserviceIdentifier getByPath(String path) {
	for (MicroserviceIdentifier value : MicroserviceIdentifier.values()) {
	    if (value.getPath().equals(path)) {
		return value;
	    }
	}
	return null;
    }

    public String getPath() {
	return path;
    }
}