/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.tenant.spi.templates;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;

import com.sitewhere.microservice.spi.multitenant.ITenantTemplate;
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