/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.tenant.kafka;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;
import com.sitewhere.tenant.TenantManagementDecorator;
import com.sitewhere.tenant.spi.kafka.ITenantModelProducer;

/**
 * Decorator for tenant management APIs that triggers Kafka tenant model update
 * messages as the result of model changes.
 * 
 * @author Derek
 */
public class TenantManagementKafkaTriggers extends TenantManagementDecorator {

    /** Kafka producer for tenant model updated */
    private ITenantModelProducer tenantModelProducer;

    public TenantManagementKafkaTriggers(ITenantManagement delegate, ITenantModelProducer tenantModelProducer) {
	super(delegate);
	this.tenantModelProducer = tenantModelProducer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.tenant.TenantManagementDecorator#createTenant(com.sitewhere
     * .spi.tenant.request.ITenantCreateRequest)
     */
    @Override
    public ITenant createTenant(ITenantCreateRequest request) throws SiteWhereException {
	ITenant added = super.createTenant(request);
	getTenantModelProducer().onTenantAdded(added);
	return added;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.tenant.TenantManagementDecorator#updateTenant(java.
     * lang. String, com.sitewhere.spi.tenant.request.ITenantCreateRequest)
     */
    @Override
    public ITenant updateTenant(String id, ITenantCreateRequest request) throws SiteWhereException {
	ITenant updated = super.updateTenant(id, request);
	getTenantModelProducer().onTenantUpdated(updated);
	return updated;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.tenant.TenantManagementDecorator#deleteTenant(java.
     * lang.String, boolean)
     */
    @Override
    public ITenant deleteTenant(String tenantId, boolean force) throws SiteWhereException {
	ITenant deleted = super.deleteTenant(tenantId, force);
	getTenantModelProducer().onTenantDeleted(deleted);
	return deleted;
    }

    public ITenantModelProducer getTenantModelProducer() {
	return tenantModelProducer;
    }

    public void setTenantModelProducer(ITenantModelProducer tenantModelProducer) {
	this.tenantModelProducer = tenantModelProducer;
    }
}