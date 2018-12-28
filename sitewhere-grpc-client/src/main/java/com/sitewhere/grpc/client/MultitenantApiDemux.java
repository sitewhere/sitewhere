/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.grpc.client.spi.multitenant.IMultitenantApiChannel;
import com.sitewhere.grpc.client.spi.multitenant.IMultitenantApiDemux;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Extends {@link ApiDemux} with functionality required to support multitenant
 * APIs.
 * 
 * @author Derek
 *
 * @param <T>
 */
public abstract class MultitenantApiDemux<T extends IMultitenantApiChannel<?>> extends ApiDemux<T>
	implements IMultitenantApiDemux<T> {

    /** Time in milliseconds before tenant availability indicator is stale */
    private static final int TENANT_AVAILABLE_STALE_PERIOD = 5 * 1000;

    /** Keeps track of last time tenant engine was known to be available */
    private Map<String, Map<UUID, Long>> tenantAccessByChannel = new ConcurrentHashMap<>();

    public MultitenantApiDemux(boolean cacheEnabled) {
	super(cacheEnabled);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.ApiDemux#isApiChannelMatch(com.sitewhere.spi.tenant
     * .ITenant, com.sitewhere.grpc.client.spi.IApiChannel)
     */
    @Override
    protected boolean isApiChannelMatch(ITenant tenant, T channel) throws SiteWhereException {
	if (tenant == null) {
	    return true;
	}
	Map<UUID, Long> lastAvailableByTenantId = getTenantAccessByChannel().get(channel.getHostname());
	if (lastAvailableByTenantId == null) {
	    lastAvailableByTenantId = new ConcurrentHashMap<UUID, Long>();
	    getTenantAccessByChannel().put(channel.getHostname(), lastAvailableByTenantId);
	}
	UUID tenantId = tenant.getId();
	Long lastAvailable = lastAvailableByTenantId.get(tenantId);
	if (lastAvailable != null) {
	    long stale = lastAvailable + TENANT_AVAILABLE_STALE_PERIOD;
	    if (stale - System.currentTimeMillis() > 0) {
		return true;
	    }
	}
	getLogger().debug("Using remote call for tenant engine availability.");
	boolean available = channel.checkTenantEngineAvailable();
	if (available) {
	    lastAvailableByTenantId.put(tenantId, new Long(System.currentTimeMillis()));
	} else {
	    lastAvailableByTenantId.remove(tenantId);
	}
	return available;
    }

    /*
     * @see com.sitewhere.grpc.client.spi.multitenant.IMultitenantApiDemux#
     * waitForCorrespondingTenantEngineAvailable(com.sitewhere.spi.microservice.
     * multitenant.IMicroserviceTenantEngine)
     */
    @Override
    public void waitForCorrespondingTenantEngineAvailable(IMicroserviceTenantEngine engine) throws SiteWhereException {
	Authentication system = engine.getMicroservice().getSystemUser().getAuthenticationForTenant(engine.getTenant());
	SecurityContextHolder.getContext().setAuthentication(system);
	waitForApiChannelAvailable(engine.getTenant());
    }

    protected Map<String, Map<UUID, Long>> getTenantAccessByChannel() {
	return tenantAccessByChannel;
    }

    protected void setTenantAccessByChannel(Map<String, Map<UUID, Long>> tenantAccessByChannel) {
	this.tenantAccessByChannel = tenantAccessByChannel;
    }
}