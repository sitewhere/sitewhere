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

    AssetManagement("asset-management", "com.sitewhere.grpc.service.UserManagement"),

    BatchOperations("batch-operations", "com.sitewhere.grpc.service.BatchManagement"),

    CommandDelivery("command-delivery", null),

    DeviceManagement("device-management", "com.sitewhere.grpc.service.DeviceManagement"),

    DeviceRegistration("device-registration", null),

    EventManagement("event-management", "com.sitewhere.grpc.service.DeviceEventManagement"),

    EventSearch("event-search", null),

    EventSources("event-sources", null),

    InboundProcessing("inbound-processing", null),

    InstanceManagement("instance-management", null),

    LabelGeneration("label-generation", "com.sitewhere.grpc.service.LabelGeneration"),

    OutboundConnectors("outbound-connectors", null),

    DeviceState("device-state", "com.sitewhere.grpc.service.DeviceState"),

    RuleProcessing("rule-processing", null),

    ScheduleManagement("schedule-management", "com.sitewhere.grpc.service.ScheduleManagement"),

    StreamingMedia("streaming-media", null),

    TenantManagement("tenant-management", "com.sitewhere.grpc.service.TenantManagement"),

    UserManagement("user-management", "com.sitewhere.grpc.service.UserManagement"),

    WebRest("web-rest", null);

    /** Path */
    private String path;

    /** Service name */
    private String grpcServiceName;

    private MicroserviceIdentifier(String path, String grpcServiceName) {
	this.path = path;
	this.grpcServiceName = grpcServiceName;
    }

    public static MicroserviceIdentifier getByPath(String path) {
	for (MicroserviceIdentifier value : MicroserviceIdentifier.values()) {
	    if (value.getPath().equals(path)) {
		return value;
	    }
	}
	return null;
    }

    /*
     * @see com.sitewhere.spi.microservice.IFunctionIdentifier#getPath()
     */
    @Override
    public String getPath() {
	return path;
    }

    /*
     * @see com.sitewhere.spi.microservice.IFunctionIdentifier#getShortName()
     */
    @Override
    public String getShortName() {
	return name();
    }

    /*
     * @see com.sitewhere.spi.microservice.IFunctionIdentifier#getGrpcServiceName()
     */
    @Override
    public String getGrpcServiceName() {
	return grpcServiceName;
    }
}