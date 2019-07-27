package com.sitewhere.grpc.client.instance;

import com.sitewhere.grpc.client.GrpcChannel;
import com.sitewhere.grpc.service.InstanceManagementGrpc;
import com.sitewhere.grpc.service.InstanceManagementGrpc.InstanceManagementBlockingStub;
import com.sitewhere.grpc.service.InstanceManagementGrpc.InstanceManagementStub;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;

/**
 * Channel that allows for communication with a remote instance management GRPC
 * server.
 */
public class InstanceManagementGrpcChannel extends GrpcChannel<InstanceManagementBlockingStub, InstanceManagementStub> {

    public InstanceManagementGrpcChannel(IInstanceSettings settings, IFunctionIdentifier identifier,
	    IGrpcServiceIdentifier grpcServiceIdentifier, int port) {
	super(settings, identifier, grpcServiceIdentifier, port);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createBlockingStub()
     */
    @Override
    public InstanceManagementBlockingStub createBlockingStub() {
	return InstanceManagementGrpc.newBlockingStub(getChannel());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createAsyncStub()
     */
    @Override
    public InstanceManagementStub createAsyncStub() {
	return InstanceManagementGrpc.newStub(getChannel());
    }
}
