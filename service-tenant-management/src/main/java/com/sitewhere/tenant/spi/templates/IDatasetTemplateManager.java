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
import com.sitewhere.spi.microservice.multitenant.IDatasetTemplate;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Manages the list of available dataset templates that can be used when
 * creating a new tenant.
 * 
 * @author Derek
 */
public interface IDatasetTemplateManager extends ILifecycleComponent {

    /**
     * Get list of dataset templates that can be used to create a new tenant.
     * 
     * @return
     * @throws SiteWhereException
     */
    public List<IDatasetTemplate> getDatasetTemplates() throws SiteWhereException;

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
