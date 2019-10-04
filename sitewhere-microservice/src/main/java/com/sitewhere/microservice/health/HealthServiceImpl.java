/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.health;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.stub.StreamObserver;

/**
 * gRPC Health Service Protocol Implementation
 * 
 * @author Jorge Villaverde
 */
public class HealthServiceImpl extends HealthGrpc.HealthImplBase {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(HealthServiceImpl.class);

    private HealthCheckResponse.ServingStatus servingStatus = HealthCheckResponse.ServingStatus.SERVING;

    public void setServingStatus(HealthCheckResponse.ServingStatus servingStatus) {
	this.servingStatus = servingStatus;
    }

    /*
     * @see io.grpc.health.v1.HealthGrpc.HealthImplBase#check(io.grpc.health.v1.
     * HealthCheckRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void check(HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
	LOGGER.trace("Health check called");

	HealthCheckResponse response = HealthCheckResponse.newBuilder().setStatus(servingStatus).build();
	responseObserver.onNext(response);
	responseObserver.onCompleted();
    }

    /*
     * @see io.grpc.health.v1.HealthGrpc.HealthImplBase#watch(io.grpc.health.v1.
     * HealthCheckRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void watch(HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
	LOGGER.trace("Health watch called");

	HealthCheckResponse response = HealthCheckResponse.newBuilder().setStatus(servingStatus).build();
	responseObserver.onNext(response);
	responseObserver.onCompleted();
    }
}
