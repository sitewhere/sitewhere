package com.sitewhere.web.spi.microservice;

import com.sitewhere.grpc.model.client.UserManagementGrpcChannel;
import com.sitewhere.microservice.spi.IGlobalMicroservice;
import com.sitewhere.spi.user.IUserManagement;

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
    public IUserManagement getUserManagement();
}