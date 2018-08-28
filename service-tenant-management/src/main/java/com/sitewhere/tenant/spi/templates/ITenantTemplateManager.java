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

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.tenant.ITenant;

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
     * Initialize tenant Zookeeper information based on tenant template.
     * 
     * @param curator
     * @param tenant
     * @throws SiteWhereException
     */
    public void initializeTenantZkFromTemplateContents(CuratorFramework curator, ITenant tenant)
	    throws SiteWhereException;
}