/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.tenant.spi.microservice;

import java.io.File;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IGlobalMicroservice;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.tenant.spi.grpc.ITenantManagementGrpcServer;
import com.sitewhere.tenant.spi.kafka.ITenantBootstrapModelConsumer;
import com.sitewhere.tenant.spi.kafka.ITenantModelProducer;
import com.sitewhere.tenant.spi.templates.IDatasetTemplateManager;
import com.sitewhere.tenant.spi.templates.ITenantTemplateManager;

/**
 * Microservice that provides tenant management functionality.
 * 
 * @author Derek
 */
public interface ITenantManagementMicroservice<T extends IFunctionIdentifier> extends IGlobalMicroservice<T> {

    /**
     * Get tenant templates file handle on filesystem.
     * 
     * @return
     * @throws SiteWhereException
     */
    public File getTenantTemplatesRoot() throws SiteWhereException;

    /**
     * Get tenant template manager.
     * 
     * @return
     */
    public ITenantTemplateManager getTenantTemplateManager();

    /**
     * Get dataset templates file handle on filesystem.
     * 
     * @return
     * @throws SiteWhereException
     */
    public File getDatasetTemplatesRoot() throws SiteWhereException;

    /**
     * Get tenant template manager.
     * 
     * @return
     */
    public IDatasetTemplateManager getDatasetTemplateManager();

    /**
     * Get GRPC server for tenant managment APIS.
     * 
     * @return
     */
    public ITenantManagementGrpcServer getTenantManagementGrpcServer();

    /**
     * Get tenant management persistence API.
     * 
     * @return
     */
    public ITenantManagement getTenantManagement();

    /**
     * Get Kafka producer for tenant model updates.
     * 
     * @return
     */
    public ITenantModelProducer getTenantModelProducer();

    /**
     * Get Kafka consumer that listens for tenant model updates and bootstraps newly
     * added tenants.
     * 
     * @return
     */
    public ITenantBootstrapModelConsumer getTenantBootstrapModelConsumer();
}