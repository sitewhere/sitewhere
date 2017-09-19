package com.sitewhere.grpc.model.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.model.spi.IGrpcChannel;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * Management wrapper for a GRPC channel.
 * 
 * @author Derek
 *
 * @param <B>
 * @param <A>
 */
public abstract class GrpcChannel<B, A> extends LifecycleComponent implements IGrpcChannel<B, A> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Remote host */
    private String hostname;

    /** Remote port */
    private int port;

    /** GRPC managed channe */
    private ManagedChannel channel;

    /** Blocking stub */
    private B blockingStub;

    /** Asynchronous stub */
    private A asyncStub;

    public GrpcChannel(String hostname, int port) {
	this.hostname = hostname;
	this.port = port;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.channel = ManagedChannelBuilder.forAddress(getHostname(), getPort()).usePlaintext(true).build();
	this.blockingStub = createBlockingStub();
	this.asyncStub = createAsyncStub();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getChannel() != null) {
	    getChannel().shutdown();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.spi.IGrpcChannel#getChannel()
     */
    public ManagedChannel getChannel() {
	return channel;
    }

    public void setChannel(ManagedChannel channel) {
	this.channel = channel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.spi.IGrpcChannel#getBlockingStub()
     */
    public B getBlockingStub() {
	return blockingStub;
    }

    public void setBlockingStub(B blockingStub) {
	this.blockingStub = blockingStub;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.spi.IGrpcChannel#getAsyncStub()
     */
    public A getAsyncStub() {
	return asyncStub;
    }

    public void setAsyncStub(A asyncStub) {
	this.asyncStub = asyncStub;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.spi.IGrpcChannel#getBlockingStub()
     */
    @Override
    public abstract B createBlockingStub();

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.spi.IGrpcChannel#getAsyncStub()
     */
    @Override
    public abstract A createAsyncStub();

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    public String getHostname() {
	return hostname;
    }

    public void setHostname(String hostname) {
	this.hostname = hostname;
    }

    public int getPort() {
	return port;
    }

    public void setPort(int port) {
	this.port = port;
    }
}