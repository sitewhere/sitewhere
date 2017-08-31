package com.sitewhere.tenant.spi.templates;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.tenant.ITenantTemplate;

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
}