package com.sitewhere.tenant.spi.microservice;

import com.sitewhere.microservice.spi.IGlobalMicroservice;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.tenant.spi.grpc.ITenantManagementGrpcServer;
import com.sitewhere.tenant.spi.kafka.ITenantModelProducer;

/**
 * Microservice that provides tenant management functionality.
 * 
 * @author Derek
 */
public interface ITenantManagementMicroservice extends IGlobalMicroservice {

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
}