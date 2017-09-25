package com.sitewhere.web.spi.microservice;

import com.sitewhere.grpc.model.client.UserManagementGrpcChannel;
import com.sitewhere.grpc.model.spi.client.IUserManagementApiChannel;
import com.sitewhere.microservice.spi.IGlobalMicroservice;

/**
 * Microservice that provides web/REST functionality.
 * 
 * @author Derek
 */
public interface IWebRestMicroservice extends IGlobalMicroservice {

    /**
     * Allows access to user management services via a GRPC channel.
     * 
     * @return
     */
    public UserManagementGrpcChannel getUserManagementGrpcChannel();

    /**
     * User management API access via the GRPC channel.
     * 
     * @return
     */
    public IUserManagementApiChannel getUserManagementApiChannel();
}