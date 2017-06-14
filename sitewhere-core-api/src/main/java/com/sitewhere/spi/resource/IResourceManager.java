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
     * Get root names for tenant template resource trees.
     * 
     * @return
     * @throws SiteWhereException
     */
    public List<String> getTenantTemplateRoots() throws SiteWhereException;

    /**
     * Gets resources associated with the given tenant template.
     * 
     * @param templateId
     * @return
     * @throws SiteWhereException
     */
    public List<IResource> getTenantTemplateResources(String templateId) throws SiteWhereException;

    /**
     * Delete global resource at the given path.
     * 
     * @param path
     * @return
     * @throws SiteWhereException
     */
    public IResource deleteGlobalResource(String path) throws SiteWhereException;

    /**
     * Copies resources from a tenant template into a tenant with the given id.
     * 
     * @param prefix
     * @param tenantId
     * @param mode
     * @return
     * @throws SiteWhereException
     */
    public IMultiResourceCreateResponse copyTemplateResourcesToTenant(String templateId, String tenantId,
	    ResourceCreateMode mode) throws SiteWhereException;

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

    /**
     * Delete all resources associated with the given tenant.
     * 
     * @param tenantId
     * @throws SiteWhereException
     */
    public void deleteTenantResources(String tenantId) throws SiteWhereException;
}