package com.sitewhere.instance.spi.microservice;

import com.sitewhere.grpc.model.spi.client.ITenantManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.IUserManagementApiChannel;
import com.sitewhere.instance.spi.templates.IInstanceTemplateManager;
import com.sitewhere.microservice.spi.IMicroservice;

/**
 * API for instance management microservice.
 * 
 * @author Derek
 */
public interface IInstanceManagementMicroservice extends IMicroservice {

    /**
     * Get instance template manager instance.
     * 
     * @return
     */
    public IInstanceTemplateManager getInstanceTemplateManager();

    /**
     * Get the user management API channel.
     * 
     * @return
     */
    public IUserManagementApiChannel getUserManagementApiChannel();

    /**
     * Get the tenant management API channel.
     * 
     * @return
     */
    public ITenantManagementApiChannel getTenantManagementApiChannel();
}
