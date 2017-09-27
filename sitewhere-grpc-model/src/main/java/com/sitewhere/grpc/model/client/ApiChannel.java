package com.sitewhere.grpc.model.client;

import java.util.concurrent.TimeUnit;

import com.sitewhere.grpc.model.spi.ApiNotAvailableException;
import com.sitewhere.grpc.model.spi.IApiChannel;
import com.sitewhere.server.lifecycle.LifecycleComponent;

import io.grpc.ConnectivityState;

/**
 * Base class for channels that uses SiteWhere APIs to communicate with GRPC
 * services.
 * 
 * @author Derek
 *
 * @param <T>
 */
public abstract class ApiChannel<T extends GrpcChannel<?, ?>> extends LifecycleComponent implements IApiChannel {

    /** Interval at which GRPC connection will be checked */
    private static final long CONNECTION_CHECK_INTERVAL = 2 * 1000;

    /**
     * Get underlying GRPC channel.
     * 
     * @return
     */
    public abstract T getGrpcChannel();

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.spi.IApiChannel#waitForApiAvailable()
     */
    @Override
    public void waitForApiAvailable() throws ApiNotAvailableException {
	waitForApiAvailable(60, TimeUnit.SECONDS);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.spi.IApiChannel#waitForApiAvailable(long,
     * java.util.concurrent.TimeUnit)
     */
    @Override
    public void waitForApiAvailable(long duration, TimeUnit unit) throws ApiNotAvailableException {
	long deadline = System.currentTimeMillis() + unit.toMillis(duration);
	while ((System.currentTimeMillis() - deadline) < 0) {
	    try {
		ConnectivityState state = getGrpcChannel().getChannel().getState(true);
		if (ConnectivityState.READY != state) {
		    getLogger().info("Waiting for GRPC service to become available. (status:" + state.name() + ")");
		    Thread.sleep(CONNECTION_CHECK_INTERVAL);
		} else {
		    return;
		}
	    } catch (Exception e) {
		throw new ApiNotAvailableException("Unhandled exception waiting for API to become available.", e);
	    }
	}
	throw new ApiNotAvailableException("API not available within timeout period.");
    }
}