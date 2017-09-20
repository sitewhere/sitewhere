package com.sitewhere.user.spi.microservice;

import com.sitewhere.microservice.spi.IGlobalMicroservice;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.user.spi.grpc.IUserManagementGrpcServer;

/**
 * Microservice that provides user management functionality.
 * 
 * @author Derek
 */
public interface IUserManagementMicroservice extends IGlobalMicroservice {

    /**
     * Get GRPC server for user managment APIS.
     * 
     * @return
     */
    public IUserManagementGrpcServer getUserManagementGrpcServer();

    /**
     * Get user management persistence API.
     * 
     * @return
     */
    public IUserManagement getUserManagement();
}