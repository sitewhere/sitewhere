package com.sitewhere.device.grpc;

import com.sitewhere.device.spi.grpc.IDeviceManagementGrpcServer;
import com.sitewhere.microservice.grpc.ManagedGrpcServer;
import com.sitewhere.microservice.spi.IMicroservice;
import com.sitewhere.spi.device.IDeviceManagement;

/**
 * Hosts a GRPC server that handles device management requests.
 * 
 * @author Derek
 */
public class DeviceManagementGrpcServer extends ManagedGrpcServer implements IDeviceManagementGrpcServer {

    public DeviceManagementGrpcServer(IMicroservice microservice, IDeviceManagement deviceManagement) {
	super(microservice, new DeviceManagementImpl(deviceManagement));
    }
}