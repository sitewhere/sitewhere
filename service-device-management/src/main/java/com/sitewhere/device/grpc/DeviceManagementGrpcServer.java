package com.sitewhere.device.grpc;

import com.sitewhere.device.spi.grpc.IDeviceManagementGrpcServer;
import com.sitewhere.device.spi.microservice.IDeviceManagementMicroservice;
import com.sitewhere.microservice.grpc.MultitenantGrpcServer;

/**
 * Hosts a GRPC server that handles device management requests.
 * 
 * @author Derek
 */
public class DeviceManagementGrpcServer extends MultitenantGrpcServer implements IDeviceManagementGrpcServer {

    public DeviceManagementGrpcServer(IDeviceManagementMicroservice microservice) {
	super(microservice, new DeviceManagementRouter(microservice));
    }
}