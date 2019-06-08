/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.tenant.templates;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.curator.framework.CuratorFramework;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.instance.spi.tenant.templates.ITenantTemplateManager;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.microservice.scripting.ScriptCreateRequest;
import com.sitewhere.microservice.zookeeper.ZkUtils;
import com.sitewhere.rest.model.microservice.scripting.ScriptTemplate;
import com.sitewhere.rest.model.tenant.TenantTemplate;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Manages templates that can be used to create new tenants.
 * 
 * @author Derek
 */
public class TenantTemplateManager extends LifecycleComponent implements ITenantTemplateManager {

    /** Root folder for tenant templates */
    private static final String TENANT_TEMPLATES_ROOT = "/tenant/templates";

    /** Folder that contains default content shared by all tenants */
    private static final String DEFAULT_TENANT_CONTENT_FOLDER = "default";

    /** Folder that contains scripts that should be registered */
    private static final String TEMPLATE_SCRIPTS_FOLDER = "scripts";

    /** File that contains script metadata */
    private static final String SCRIPT_METADATA_FILE = "script-metadata.json";

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
	File root = new File(TENANT_TEMPLATES_ROOT);
	File[] folders = root.listFiles(File::isDirectory);
	for (File folder : folders) {
	    File tfile = new File(folder, MicroserviceTenantEngine.TENANT_TEMPLATE_PATH);
	    if (tfile.exists()) {
		TenantTemplate template = readTenantTemplate(tfile);
		updated.put(template.getId(), template);
	    }
	}
	synchronized (templatesById) {
	    templatesById.clear();
	    templatesById.putAll(updated);
	}

	getLogger().info("Tenant template manager found the following templates:");
	for (ITenantTemplate template : getTenantTemplates()) {
	    getLogger().info("[" + template.getId() + "] " + template.getName());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.tenant.ITenantTemplateManager#getTenantTemplates ()
     */
    @Override
    public List<ITenantTemplate> getTenantTemplates() throws SiteWhereException {
	List<ITenantTemplate> list = new ArrayList<>();
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
     * @see com.sitewhere.tenant.spi.templates.ITenantTemplateManager#
     * initializeTenantZkFromTemplateContents(org.apache.curator.framework.
     * CuratorFramework, com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public void initializeTenantZkFromTemplateContents(CuratorFramework curator, ITenant tenant)
	    throws SiteWhereException {
	// Resolve tenant template based on template id.
	ITenantTemplate template = getTemplatesById().get(tenant.getTenantTemplateId());
	if (template == null) {
	    throw new SiteWhereException("Tenant template not found: " + tenant.getTenantTemplateId());
	}

	File root = new File(TENANT_TEMPLATES_ROOT);

	// Copy default content and register default scripts.
	addDefaultContent(curator, tenant, root);

	// Copy tenant template configuration files and register scripts.
	copyTemplateContent(curator, tenant, template, root);
	addTemplateScripts(tenant, template, root);
    }

    /**
     * Copy content from 'default' folder as baseline (also register any default
     * scripts).
     * 
     * @param curator
     * @param tenantPath
     * @param root
     * @throws SiteWhereException
     */
    protected void addDefaultContent(CuratorFramework curator, ITenant tenant, File root) throws SiteWhereException {
	String tenantPath = getInstanceManagementMicroservice().getInstanceTenantConfigurationPath(tenant.getId());

	// Copy default content shared by all tenants.
	File defaultFolder = new File(root, DEFAULT_TENANT_CONTENT_FOLDER);
	if (!defaultFolder.exists()) {
	    throw new SiteWhereException("Default folder not found at '" + defaultFolder.getAbsolutePath() + "'.");
	}
	ZkUtils.copyFolderRecursivelytoZk(curator, tenantPath, defaultFolder, defaultFolder,
		Collections.singletonList("scripts"));

	// Add any default scripts.
	addScriptsForFolder(tenant, defaultFolder);
    }

    /**
     * Copy template content on top of baseline structure added from 'default'
     * folder.
     * 
     * @param curator
     * @param template
     * @param tenantPath
     * @param root
     * @throws SiteWhereException
     */
    protected void copyTemplateContent(CuratorFramework curator, ITenant tenant, ITenantTemplate template, File root)
	    throws SiteWhereException {
	String tenantPath = getInstanceManagementMicroservice().getInstanceTenantConfigurationPath(tenant.getId());
	File templateFolder = getTenantTemplateFolder(template, root);
	ZkUtils.copyFolderRecursivelytoZk(curator, tenantPath, templateFolder, templateFolder,
		Collections.singletonList("scripts"));
    }

    /**
     * Registers all scripts contained in the 'scripts' subfolder of the tenant
     * template.
     * 
     * @param tenant
     * @param template
     * @param root
     * @throws SiteWhereException
     */
    protected void addTemplateScripts(ITenant tenant, ITenantTemplate template, File root) throws SiteWhereException {
	File templateFolder = getTenantTemplateFolder(template, root);
	addScriptsForFolder(tenant, templateFolder);
    }

    /**
     * Add scripts contained in the given folder.
     * 
     * @param tenant
     * @param folder
     * @throws SiteWhereException
     */
    protected void addScriptsForFolder(ITenant tenant, File folder) throws SiteWhereException {
	File scriptsFolder = new File(folder, TEMPLATE_SCRIPTS_FOLDER);
	if (scriptsFolder.exists()) {
	    File[] contents = scriptsFolder.listFiles();
	    for (File file : contents) {
		if (file.isDirectory()) {
		    String identifierPath = file.getName();
		    MicroserviceIdentifier identifier = MicroserviceIdentifier.getByPath(identifierPath);
		    if (identifier == null) {
			getLogger()
				.warn("Unknown microservice specified for tenant template script: " + identifierPath);
			continue;
		    }
		    addScriptsForIdentifier(tenant, identifier, file);
		}
	    }
	}
    }

    /**
     * Based on script template data contained in the subfolder, call the script
     * management API and add the script.
     * 
     * @param tenant
     * @param identifier
     * @param identifierRoot
     * @throws SiteWhereException
     */
    protected void addScriptsForIdentifier(ITenant tenant, MicroserviceIdentifier identifier, File identifierRoot)
	    throws SiteWhereException {
	File[] scriptFolders = identifierRoot.listFiles();
	for (File scriptFolder : scriptFolders) {
	    if (scriptFolder.isDirectory()) {
		File metadata = new File(scriptFolder, SCRIPT_METADATA_FILE);
		if (!metadata.exists()) {
		    throw new SiteWhereException(
			    "Script metadata folder does not contain '" + SCRIPT_METADATA_FILE + "'.");
		}
		ScriptTemplate template = readScriptTemplate(metadata);
		String contentFilename = template.getId() + "." + template.getType();
		File contentFile = new File(scriptFolder, contentFilename);
		if (!contentFile.exists()) {
		    throw new SiteWhereException("Script content not found at: " + contentFilename);
		}
		byte[] content = readScriptContent(contentFile);
		ScriptCreateRequest request = new ScriptCreateRequest();
		request.setId(template.getId());
		request.setName(template.getName());
		request.setDescription(template.getDescription());
		request.setType(template.getType());
		request.setContent(Base64.getEncoder().encodeToString(content));
		((IConfigurableMicroservice<?>) getMicroservice()).getScriptManagement().createScript(identifier,
			tenant.getId(), request);
	    }
	}
    }

    /**
     * Read tenant template from metadata file.
     * 
     * @param file
     * @return
     * @throws SiteWhereException
     */
    protected TenantTemplate readTenantTemplate(File file) throws SiteWhereException {
	InputStream input;
	try {
	    input = new FileInputStream(file);
	    byte[] content = IOUtils.toByteArray(input);
	    TenantTemplate template = MarshalUtils.unmarshalJson(content, TenantTemplate.class);
	    return template;
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to unmarshal tenant template.", e);
	}
    }

    /**
     * Read script template from metadata file.
     * 
     * @param file
     * @return
     * @throws SiteWhereException
     */
    protected ScriptTemplate readScriptTemplate(File file) throws SiteWhereException {
	InputStream input;
	try {
	    input = new FileInputStream(file);
	    byte[] content = IOUtils.toByteArray(input);
	    ScriptTemplate template = MarshalUtils.unmarshalJson(content, ScriptTemplate.class);
	    return template;
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to unmarshal script template.", e);
	}
    }

    /**
     * Read script content and return as byte[].
     * 
     * @param file
     * @return
     * @throws SiteWhereException
     */
    protected byte[] readScriptContent(File file) throws SiteWhereException {
	InputStream input;
	try {
	    input = new FileInputStream(file);
	    return IOUtils.toByteArray(input);
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to read script content.", e);
	}
    }

    /**
     * Get tenant template folder.
     * 
     * @param template
     * @param root
     * @return
     * @throws SiteWhereException
     */
    protected File getTenantTemplateFolder(ITenantTemplate template, File root) throws SiteWhereException {
	File templateFolder = new File(root, template.getId());
	if (!templateFolder.exists()) {
	    throw new SiteWhereException(
		    "Tenant template folder not found at '" + templateFolder.getAbsolutePath() + "'.");
	}
	return templateFolder;
    }

    /**
     * Get instance management microservice.
     * 
     * @return
     */
    protected IInstanceManagementMicroservice<?> getInstanceManagementMicroservice() {
	return (IInstanceManagementMicroservice<?>) getMicroservice();
    }

    protected Map<String, ITenantTemplate> getTemplatesById() {
	return templatesById;
    }

    protected void setTemplatesById(Map<String, ITenantTemplate> templatesById) {
	this.templatesById = templatesById;
    }
}