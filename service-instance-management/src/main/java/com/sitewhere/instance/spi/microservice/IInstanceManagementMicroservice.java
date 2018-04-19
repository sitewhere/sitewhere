/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.spi.microservice;

import com.sitewhere.grpc.client.spi.provider.ITenantManagementDemuxProvider;
import com.sitewhere.grpc.client.spi.provider.IUserManagementDemuxProvider;
import com.sitewhere.instance.spi.templates.IInstanceTemplateManager;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IGlobalMicroservice;
import com.sitewhere.spi.microservice.scripting.IScriptSynchronizer;

/**
 * API for instance management microservice.
 * 
 * @author Derek
 */
public interface IInstanceManagementMicroservice<T extends IFunctionIdentifier>
	extends IGlobalMicroservice<T>, IUserManagementDemuxProvider<T>, ITenantManagementDemuxProvider<T> {

    /**
     * Get instance template manager instance.
     * 
     * @return
     */
    public IInstanceTemplateManager getInstanceTemplateManager();

    /**
     * Get instance script synchronizer.
     * 
     * @return
     */
    public IScriptSynchronizer getInstanceScriptSynchronizer();
}
