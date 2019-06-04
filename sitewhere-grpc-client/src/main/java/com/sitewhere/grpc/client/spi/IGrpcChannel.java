/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.spi;

import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

import io.grpc.ManagedChannel;

/**
 * Connected channel to a remote GRPC server.
 * 
 * @author Derek
 *
 * @param <B>
 * @param <A>
 */
public interface IGrpcChannel<B, A> extends ITenantEngineLifecycleComponent {

    /**
     * Get managed channel.
     * 
     * @return
     */
    public ManagedChannel getChannel();

    /**
     * Create blocking version of stub.
     * 
     * @return
     */
    public B createBlockingStub();

    /**
     * Get blocking version of stub.
     * 
     * @return
     */
    public B getBlockingStub();

    /**
     * Create asynchronous version of stub.
     * 
     * @return
     */
    public A createAsyncStub();

    /**
     * Get asynchronous version of stub.
     * 
     * @return
     */
    public A getAsyncStub();
}