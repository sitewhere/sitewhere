/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.tenant.templates;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.microservice.zookeeper.ZkUtils;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.configuration.IDefaultResourcePaths;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.tenant.spi.microservice.ITenantManagementMicroservice;
import com.sitewhere.tenant.spi.templates.ITenantTemplate;
import com.sitewhere.tenant.spi.templates.ITenantTemplateManager;

/**
 * Manages templates that can be used to create new tenants.
 * 
 * @author Derek
 */
public class TenantTemplateManager extends LifecycleComponent implements ITenantTemplateManager {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Injected handle to microservice */
    @Autowired
    private ITenantManagementMicroservice microservice;

    /** Map of templates by template id */
    private Map<String, ITenantTemplate> templatesById = new HashMap<String, ITenantTemplate>();

    public TenantTemplateManager() {
	super(LifecycleComponentType.TenantTemplateManager);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	Map<String, ITenantTemplate> updated = new HashMap<String, ITenantTemplate>();

	// Loop through tenant folders and pull templates.
	File root = getMicroservice().getTenantTemplatesRoot();
	File[] folders = root.listFiles(File::isDirectory);
	for (File folder : folders) {
	    File tfile = new File(folder, IDefaultResourcePaths.TEMPLATE_JSON_FILE_NAME);
	    if (tfile.exists()) {
		InputStream input;
		try {
		    input = new FileInputStream(tfile);
		    byte[] content = IOUtils.toByteArray(input);
		    TenantTemplate template = MarshalUtils.unmarshalJson(content, TenantTemplate.class);
		    updated.put(template.getId(), template);
		} catch (IOException e) {
		    LOGGER.warn("Unable to unmarshal template.", e);
		}
	    }
	}
	synchronized (templatesById) {
	    templatesById.clear();
	    templatesById.putAll(updated);
	}

	LOGGER.info("Template manager found the following templates:");
	for (ITenantTemplate template : getTenantTemplates()) {
	    LOGGER.info("[" + template.getId() + "] " + template.getName());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.tenant.ITenantTemplateManager#getTenantTemplates
     * ()
     */
    @Override
    public List<ITenantTemplate> getTenantTemplates() throws SiteWhereException {
	List<ITenantTemplate> list = new ArrayList<ITenantTemplate>();
	list.addAll(getTemplatesById().values());

	// Sort by template name.
	list.sort(new Comparator<ITenantTemplate>() {

	    @Override
	    public int compare(ITenantTemplate o1, ITenantTemplate o2) {
		return o1.getName().compareTo(o2.getName());
	    }
	});
	return list;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.tenant.spi.templates.ITenantTemplateManager#
     * copyTemplateContentsToZk(java.lang.String,
     * org.apache.curator.framework.CuratorFramework, java.lang.String)
     */
    @Override
    public void copyTemplateContentsToZk(String templateId, CuratorFramework curator, String tenantPath)
	    throws SiteWhereException {
	ITenantTemplate template = getTemplatesById().get(templateId);
	if (template == null) {
	    throw new SiteWhereException("Tenant template not found: " + templateId);
	}
	File root = getMicroservice().getTenantTemplatesRoot();
	File templateFolder = new File(root, templateId);
	if (!templateFolder.exists()) {
	    throw new SiteWhereException("Template folder not found at '" + templateFolder.getAbsolutePath() + "'.");
	}
	ZkUtils.copyFolderRecursivelytoZk(curator, tenantPath, templateFolder, templateFolder);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    public Map<String, ITenantTemplate> getTemplatesById() {
	return templatesById;
    }

    public void setTemplatesById(Map<String, ITenantTemplate> templatesById) {
	this.templatesById = templatesById;
    }

    public ITenantManagementMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(ITenantManagementMicroservice microservice) {
	this.microservice = microservice;
    }
}