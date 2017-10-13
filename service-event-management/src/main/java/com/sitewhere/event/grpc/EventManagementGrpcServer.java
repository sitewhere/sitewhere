package com.sitewhere.event.grpc;

import com.sitewhere.event.spi.grpc.IEventManagementGrpcServer;
import com.sitewhere.microservice.grpc.GrpcServer;
import com.sitewhere.microservice.spi.IMicroservice;
import com.sitewhere.spi.device.event.IDeviceEventManagement;

/**
 * Hosts a GRPC server that handles device event management requests.
 * 
 * @author Derek
 */
public class EventManagementGrpcServer extends GrpcServer implements IEventManagementGrpcServer {

    public EventManagementGrpcServer(IMicroservice microservice, IDeviceEventManagement deviceEventManagement) {
	super(microservice, new EventManagementImpl(deviceEventManagement));
    }
}