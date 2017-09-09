package com.sitewhere.microservice.spi.grpc;

import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

import io.grpc.ServerServiceDefinition;

/**
 * Wraps a GRPC server so that its lifecycle can be managed within a
 * microservice.
 * 
 * @author Derek
 */
public interface IManagedGrpcServer extends ILifecycleComponent {

    /**
     * Get {@link ServerServiceDefinition} for building server.
     * 
     * @return
     */
    public ServerServiceDefinition getServerServiceDefinition();
}