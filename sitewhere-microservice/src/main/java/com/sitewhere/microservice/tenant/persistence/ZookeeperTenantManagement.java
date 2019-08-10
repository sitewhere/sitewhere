/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.tenant.persistence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.rest.model.search.Pager;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.tenant.TenantSearchCriteria;
import com.sitewhere.rest.model.tenant.Tenant;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.tenant.ITenantSearchCriteria;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;

/**
 * Tenant management implementation which stores data in Zookeeper.
 */
public class ZookeeperTenantManagement extends LifecycleComponent implements ITenantManagement {

    /** JSON data for tenant model */
    private static final String TENANT_JSON = "tenant.json";

    public ZookeeperTenantManagement() {
	super(LifecycleComponentType.DataStore);
    }

    /*
     * @see
     * com.sitewhere.spi.tenant.ITenantManagement#createTenant(com.sitewhere.spi.
     * tenant.request.ITenantCreateRequest)
     */
    @Override
    public ITenant createTenant(ITenantCreateRequest request) throws SiteWhereException {
	Tenant tenant = TenantManagementPersistenceLogic.tenantCreateLogic(request);

	CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	String tenantPath = getTenantModelPath(tenant.getId());
	try {
	    Stat existing = curator.checkExists().forPath(tenantPath);
	    if (existing == null) {
		getLogger().debug("Zk node for tenant '" + request.getToken() + "' not found. Creating...");
		return createZookeeperTenant(tenant);
	    } else {
		throw new SiteWhereSystemException(ErrorCode.DuplicateUser, ErrorLevel.ERROR);
	    }
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to create tenant.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.tenant.ITenantManagement#updateTenant(java.util.UUID,
     * com.sitewhere.spi.tenant.request.ITenantCreateRequest)
     */
    @Override
    public ITenant updateTenant(UUID id, ITenantCreateRequest request) throws SiteWhereException {
	ITenant existing = getTenant(id);
	if (existing == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidTenantId, ErrorLevel.ERROR);
	}

	Tenant updated = TenantUtils.copy(existing);
	TenantManagementPersistenceLogic.tenantUpdateLogic(request, updated);
	return updateZookeeperTenant(updated);
    }

    /*
     * @see com.sitewhere.spi.tenant.ITenantManagement#getTenant(java.util.UUID)
     */
    @Override
    public ITenant getTenant(UUID id) throws SiteWhereException {
	getLogger().info(String.format("Getting tenant by UUID %s", id.toString()));
	String tenantPath = getTenantModelPath(id);
	CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	try {
	    Stat existing = curator.checkExists().forPath(tenantPath);
	    if (existing != null) {
		byte[] content = curator.getData().forPath(tenantPath);
		return MarshalUtils.unmarshalJson(content, Tenant.class);
	    }
	    return null;
	} catch (Exception e) {
	    throw new SiteWhereException(String.format("Unable to get tenant at path '%s'.", tenantPath));
	}
    }

    /*
     * @see
     * com.sitewhere.spi.tenant.ITenantManagement#getTenantByToken(java.lang.String)
     */
    @Override
    public ITenant getTenantByToken(String token) throws SiteWhereException {
	getLogger().info(String.format("Getting tenant by token %s", token));
	ISearchResults<ITenant> all = listTenants(new TenantSearchCriteria(1, 0));
	for (ITenant tenant : all.getResults()) {
	    if (tenant.getToken().equals(token)) {
		return tenant;
	    }
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.tenant.ITenantManagement#listTenants(com.sitewhere.spi.
     * search.tenant.ITenantSearchCriteria)
     */
    @Override
    public ISearchResults<ITenant> listTenants(ITenantSearchCriteria criteria) throws SiteWhereException {
	getLogger().info("Listing all tenants.");
	try {
	    CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	    List<String> children = curator.getChildren()
		    .forPath(getConfigurableMicroservice().getInstanceTenantsConfigurationPath());
	    List<ITenant> tenants = new ArrayList<>();
	    for (String child : children) {
		try {
		    tenants.add(getTenant(UUID.fromString(child)));
		} catch (SiteWhereException e) {
		    getLogger().info("Unable to retrieve tenant for list. Data may be corrupt.", e);
		}
	    }
	    tenants.sort(new Comparator<ITenant>() {

		@Override
		public int compare(ITenant o1, ITenant o2) {
		    return o1.getName().compareTo(o2.getName());
		}
	    });
	    Pager<ITenant> pager = new Pager<ITenant>(criteria);
	    for (ITenant tenant : tenants) {
		pager.process(tenant);
	    }
	    return new SearchResults<ITenant>(pager.getResults(), pager.getTotal());
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to retrieve tenant list from Zookeeper.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.tenant.ITenantManagement#deleteTenant(java.util.UUID)
     */
    @Override
    public ITenant deleteTenant(UUID tenantId) throws SiteWhereException {
	try {
	    ITenant tenant = getTenant(tenantId);
	    CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	    String path = getConfigurableMicroservice().getInstanceTenantConfigurationPath(tenantId);
	    curator.delete().deletingChildrenIfNeeded().forPath(path);
	    return tenant;
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to delete tenant from Zookeeper.", e);
	}
    }

    /**
     * Get path to model JSON for tenant.
     * 
     * @param tenantId
     * @return
     * @throws SiteWhereException
     */
    protected String getTenantModelPath(UUID tenantId) throws SiteWhereException {
	return getConfigurableMicroservice().getInstanceTenantConfigurationPath(tenantId) + "/" + TENANT_JSON;
    }

    /**
     * Store a tenant in Zookeeper.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    protected ITenant createZookeeperTenant(ITenant tenant) throws SiteWhereException {
	CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	String tenantPath = getTenantModelPath(tenant.getId());
	try {
	    curator.create().creatingParentsIfNeeded().forPath(tenantPath,
		    MarshalUtils.marshalJsonAsPrettyString(tenant).getBytes());
	    return tenant;
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to store tenant information in Zookeeper.", e);
	}
    }

    /**
     * Update a tenant in Zookeeper.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    protected ITenant updateZookeeperTenant(ITenant tenant) throws SiteWhereException {
	CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	String tenantPath = getTenantModelPath(tenant.getId());
	try {
	    if (curator.checkExists().forPath(tenantPath) != null) {
		curator.setData().forPath(tenantPath, MarshalUtils.marshalJsonAsPrettyString(tenant).getBytes());
	    } else {
		throw new SiteWhereSystemException(ErrorCode.InvalidTenantId, ErrorLevel.ERROR);
	    }
	    return tenant;
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to store tenant information in Zookeeper.", e);
	}
    }

    protected IConfigurableMicroservice<?> getConfigurableMicroservice() {
	return (IConfigurableMicroservice<?>) getMicroservice();
    }
}
