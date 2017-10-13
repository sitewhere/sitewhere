package com.sitewhere.microservice.spi.grpc;

import com.sitewhere.microservice.spi.IMicroservice;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

import io.grpc.BindableService;

/**
 * Wraps a GRPC server so that its lifecycle can be managed within a
 * microservice.
 * 
 * @author Derek
 */
public interface IGrpcServer extends ILifecycleComponent {

    /**
     * Get the parent {@link IMicroservice}.
     * 
     * @return
     */
    public IMicroservice getMicroservice();

    /**
     * Get the wrapped {@link BindableService}.
     * 
     * @return
     */
    public BindableService getServiceImplementation();
}