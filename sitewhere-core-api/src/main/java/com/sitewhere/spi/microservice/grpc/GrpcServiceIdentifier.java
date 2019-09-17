/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.grpc;

/**
 * Enumerates gRPC services offered by microservices.
 */
public enum GrpcServiceIdentifier implements IGrpcServiceIdentifier {

    MicroserviceManagement("com.sitewhere.grpc.service.MicroserviceManagement"),

    AssetManagement("com.sitewhere.grpc.service.AssetManagement"),

    BatchOperations("com.sitewhere.grpc.service.BatchManagement"),

    DeviceManagement("com.sitewhere.grpc.service.DeviceManagement"),

    EventManagement("com.sitewhere.grpc.service.DeviceEventManagement"),

    InstanceManagement("com.sitewhere.grpc.service.InstanceManagement"),

    LabelGeneration("com.sitewhere.grpc.service.LabelGeneration"),

    DeviceState("com.sitewhere.grpc.service.DeviceState"),

    UserManagement("com.sitewhere.grpc.service.UserManagement"),

    TenantManagement("com.sitewhere.grpc.service.TenantManagement"),

    ScheduleManagement("com.sitewhere.grpc.service.ScheduleManagement");

    /** Service name */
    private String grpcServiceName;

    private GrpcServiceIdentifier(String grpcServiceName) {
	this.grpcServiceName = grpcServiceName;
    }

    public static GrpcServiceIdentifier getByServiceName(String grpcServiceName) {
	for (GrpcServiceIdentifier value : GrpcServiceIdentifier.values()) {
	    if (value.getGrpcServiceName().equals(grpcServiceName)) {
		return value;
	    }
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier#getGrpcServiceName
     * ()
     */
    @Override
    public String getGrpcServiceName() {
	return grpcServiceName;
    }
}
