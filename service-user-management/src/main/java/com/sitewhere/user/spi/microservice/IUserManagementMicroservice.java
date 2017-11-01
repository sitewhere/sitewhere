/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.user.spi.microservice;

import com.sitewhere.spi.microservice.IGlobalMicroservice;
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