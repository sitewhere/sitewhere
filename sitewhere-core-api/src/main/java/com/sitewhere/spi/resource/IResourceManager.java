package com.sitewhere.spi.resource;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.resource.request.IResourceCreateRequest;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Interface for component that manages system resources.
 * 
 * @author Derek
 */
public interface IResourceManager extends ILifecycleComponent {

    /**
     * Create one or more global resources.
     * 
     * @param request
     * @param mode
     * @return
     * @throws SiteWhereException
     */
    public IMultiResourceCreateResponse createGlobalResources(List<IResourceCreateRequest> request,
	    ResourceCreateMode mode) throws SiteWhereException;

    /**
     * Get a global resource registered for the given path.
     * 
     * @param path
     * @return
     * @throws SiteWhereException
     */
    public IResource getGlobalResource(String path) throws SiteWhereException;

    /**
     * Get list of all global resources.
     * 
     * @return
     * @throws SiteWhereException
     */
    public List<IResource> getGlobalResources() throws SiteWhereException;

    /**
     * Delete global resource at the given path.
     * 
     * @param path
     * @return
     * @throws SiteWhereException
     */
    public IResource deleteGlobalResource(String path) throws SiteWhereException;

    /**
     * Copies all global resources with the given prefix to the given tenant.
     * 
     * @param globalPath
     * @param tenantId
     * @throws SiteWhereException
     */
    public void copyGlobalResourcesToTenant(String prefix, String tenantId) throws SiteWhereException;

    /**
     * Create one or more tenant resources.
     * 
     * @param tenantId
     * @param request
     * @param mode
     * @return
     * @throws SiteWhereException
     */
    public IMultiResourceCreateResponse createTenantResources(String tenantId, List<IResourceCreateRequest> request,
	    ResourceCreateMode mode) throws SiteWhereException;

    /**
     * Get a tenant resource registered for the given path.
     * 
     * @param tenantId
     * @param path
     * @return
     * @throws SiteWhereException
     */
    public IResource getTenantResource(String tenantId, String path) throws SiteWhereException;

    /**
     * Get list of all resources for the given tenant.
     * 
     * @param tenantId
     * @return
     * @throws SiteWhereException
     */
    public List<IResource> getTenantResources(String tenantId) throws SiteWhereException;

    /**
     * Delete tenant resource at the given path.
     * 
     * @param tenantId
     * @param path
     * @return
     * @throws SiteWhereException
     */
    public IResource deleteTenantResource(String tenantId, String path) throws SiteWhereException;
}