package com.sitewhere.tenant.spi.microservice;

import java.io.File;

import com.sitewhere.microservice.spi.IGlobalMicroservice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.tenant.spi.grpc.ITenantManagementGrpcServer;
import com.sitewhere.tenant.spi.kafka.ITenantBootstrapModelConsumer;
import com.sitewhere.tenant.spi.kafka.ITenantModelProducer;
import com.sitewhere.tenant.spi.templates.ITenantTemplateManager;

/**
 * Microservice that provides tenant management functionality.
 * 
 * @author Derek
 */
public interface ITenantManagementMicroservice extends IGlobalMicroservice {

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
     * Get Kafka consumer that listens for tenant model updates and bootstraps
     * newly added tenants.
     * 
     * @return
     */
    public ITenantBootstrapModelConsumer getTenantBootstrapModelConsumer();
}