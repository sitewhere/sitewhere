package com.sitewhere.instance.spi.templates;

import java.util.Map;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Manages a list of templates used to create SiteWhere instances.
 * 
 * @author Derek
 */
public interface IInstanceTemplateManager extends ILifecycleComponent {

    /**
     * Get map of instance templates indexed by template id.
     * 
     * @return
     * @throws SiteWhereException
     */
    public Map<String, IInstanceTemplate> getInstanceTemplates() throws SiteWhereException;
}