package com.sitewhere.tenant.spi.templates;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Manages the list of available tenant templates that can be used when creating
 * a new tenant.
 * 
 * @author Derek
 */
public interface ITenantTemplateManager extends ILifecycleComponent {

    /**
     * Get list of templates that can be used to create a new tenant.
     * 
     * @return
     * @throws SiteWhereException
     */
    public List<ITenantTemplate> getTenantTemplates() throws SiteWhereException;

    /**
     * Copy template contents to the given Zk path (to bootstrap a tenant).
     * 
     * @param templateId
     * @param curator
     * @param tenantPath
     * @throws SiteWhereException
     */
    public void copyTemplateContentsToZk(String templateId, CuratorFramework curator, String tenantPath)
	    throws SiteWhereException;
}