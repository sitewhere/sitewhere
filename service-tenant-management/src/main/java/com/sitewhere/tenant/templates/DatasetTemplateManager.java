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

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.microservice.zookeeper.ZkUtils;
import com.sitewhere.rest.model.tenant.DatasetTemplate;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IDatasetTemplate;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.tenant.spi.microservice.ITenantManagementMicroservice;
import com.sitewhere.tenant.spi.templates.IDatasetTemplateManager;

/**
 * Manages templates that can be used to initialize data for new tenants.
 * 
 * @author Derek
 */
public class DatasetTemplateManager extends LifecycleComponent implements IDatasetTemplateManager {

    /** Map of templates by template id */
    private Map<String, IDatasetTemplate> templatesById = new HashMap<String, IDatasetTemplate>();

    public DatasetTemplateManager() {
	super(LifecycleComponentType.DatasetTemplateManager);
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
	Map<String, IDatasetTemplate> updated = new HashMap<String, IDatasetTemplate>();

	// Loop through tenant folders and pull templates.
	File root = ((ITenantManagementMicroservice<?>) getMicroservice()).getDatasetTemplatesRoot();
	File[] folders = root.listFiles(File::isDirectory);
	for (File folder : folders) {
	    File tfile = new File(folder, MicroserviceTenantEngine.DATASET_TEMPLATE_PATH);
	    if (tfile.exists()) {
		InputStream input;
		try {
		    input = new FileInputStream(tfile);
		    byte[] content = IOUtils.toByteArray(input);
		    DatasetTemplate template = MarshalUtils.unmarshalJson(content, DatasetTemplate.class);
		    updated.put(template.getId(), template);
		} catch (IOException e) {
		    getLogger().warn("Unable to unmarshal template.", e);
		}
	    }
	}
	synchronized (templatesById) {
	    templatesById.clear();
	    templatesById.putAll(updated);
	}

	getLogger().info("Dataset template manager found the following templates:");
	for (IDatasetTemplate template : getDatasetTemplates()) {
	    getLogger().info("[" + template.getId() + "] " + template.getName());
	}
    }

    /*
     * @see com.sitewhere.tenant.spi.templates.IDatasetTemplateManager#
     * getDatasetTemplates()
     */
    @Override
    public List<IDatasetTemplate> getDatasetTemplates() throws SiteWhereException {
	List<IDatasetTemplate> list = new ArrayList<>();
	list.addAll(getTemplatesById().values());

	// Sort by template name.
	list.sort(new Comparator<IDatasetTemplate>() {

	    @Override
	    public int compare(IDatasetTemplate o1, IDatasetTemplate o2) {
		return o1.getName().compareTo(o2.getName());
	    }
	});
	return list;
    }

    /*
     * @see com.sitewhere.tenant.spi.templates.IDatasetTemplateManager#
     * copyTemplateContentsToZk(java.lang.String,
     * org.apache.curator.framework.CuratorFramework, java.lang.String)
     */
    @Override
    public void copyTemplateContentsToZk(String templateId, CuratorFramework curator, String tenantPath)
	    throws SiteWhereException {
	IDatasetTemplate template = getTemplatesById().get(templateId);
	if (template == null) {
	    throw new SiteWhereException("Dataset template not found: " + templateId);
	}

	File root = ((ITenantManagementMicroservice<?>) getMicroservice()).getDatasetTemplatesRoot();

	// Copy template contents on top of default.
	File templateFolder = new File(root, templateId);
	if (!templateFolder.exists()) {
	    throw new SiteWhereException(
		    "Dataset template folder not found at '" + templateFolder.getAbsolutePath() + "'.");
	}
	ZkUtils.copyFolderRecursivelytoZk(curator, tenantPath, templateFolder, templateFolder);
    }

    protected Map<String, IDatasetTemplate> getTemplatesById() {
	return templatesById;
    }

    protected void setTemplatesById(Map<String, IDatasetTemplate> templatesById) {
	this.templatesById = templatesById;
    }
}
