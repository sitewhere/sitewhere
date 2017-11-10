package com.sitewhere.batch.spi.microservice;

import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;

/**
 * Microservice that provides batch operations functionality.
 * 
 * @author Derek
 */
public interface IBatchOperationsMicroservice extends IMultitenantMicroservice<IBatchOperationsTenantEngine> {
}