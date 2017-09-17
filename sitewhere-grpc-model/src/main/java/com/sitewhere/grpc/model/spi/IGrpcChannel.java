package com.sitewhere.grpc.model.spi;

import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

import io.grpc.ManagedChannel;

/**
 * Connected channel to a remote GRPC server.
 * 
 * @author Derek
 *
 * @param <B>
 * @param <A>
 */
public interface IGrpcChannel<B, A> extends ILifecycleComponent {

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