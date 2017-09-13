package com.sitewhere.instance.spi.templates;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Manages a list of templates used to create SiteWhere instances.
 * 
 * @author Derek
 */
public interface IInstanceTemplateManager extends ILifecycleComponent {

    /**
     * Get list of templates that can be used to create a new SiteWhere
     * instance.
     * 
     * @return
     * @throws SiteWhereException
     */
    public List<IInstanceTemplate> getInstanceTemplates() throws SiteWhereException;
}