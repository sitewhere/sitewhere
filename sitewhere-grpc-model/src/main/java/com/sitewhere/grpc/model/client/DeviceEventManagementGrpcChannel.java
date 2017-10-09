package com.sitewhere.grpc.model.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.service.DeviceEventManagementGrpc;
import com.sitewhere.grpc.service.DeviceEventManagementGrpc.DeviceEventManagementBlockingStub;
import com.sitewhere.grpc.service.DeviceEventManagementGrpc.DeviceEventManagementStub;

/**
 * Channel that allows for communication with a remote device event management
 * GRPC server.
 * 
 * @author Derek
 */
public class DeviceEventManagementGrpcChannel
	extends GrpcChannel<DeviceEventManagementBlockingStub, DeviceEventManagementStub> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public DeviceEventManagementGrpcChannel(String host, int port) {
	super(host, port);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createBlockingStub()
     */
    @Override
    public DeviceEventManagementBlockingStub createBlockingStub() {
	return DeviceEventManagementGrpc.newBlockingStub(getChannel());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createAsyncStub()
     */
    @Override
    public DeviceEventManagementStub createAsyncStub() {
	return DeviceEventManagementGrpc.newStub(getChannel());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}