/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.scripting;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.rest.model.microservice.scripting.ScriptTemplate;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.scripting.IScriptTemplate;
import com.sitewhere.spi.microservice.scripting.IScriptTemplateManager;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Manages the list of script templates for a microservice.
 * 
 * @author Derek
 */
public class ScriptTemplateManager extends LifecycleComponent implements IScriptTemplateManager {

    /** Name of file where script templates are stored */
    private static final String SCRIPT_TEMPLATE_FILENAME = "script-template.json";

    /** Map of script templates by template id */
    private Map<String, IScriptTemplate> scriptTemplatesById = new HashMap<String, IScriptTemplate>();

    public ScriptTemplateManager() {
	super(LifecycleComponentType.ScriptTemplateManager);
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
	Map<String, IScriptTemplate> updated = new HashMap<>();

	// Loop through tenant folders and pull templates.
	File root = getMicroservice().getScriptTemplatesRoot();
	if (root.exists()) {
	    File[] folders = root.listFiles(File::isDirectory);
	    for (File folder : folders) {
		File tfile = new File(folder, SCRIPT_TEMPLATE_FILENAME);
		if (tfile.exists()) {
		    InputStream input;
		    try {
			input = new FileInputStream(tfile);
			byte[] content = IOUtils.toByteArray(input);
			ScriptTemplate template = MarshalUtils.unmarshalJson(content, ScriptTemplate.class);
			updated.put(template.getId(), template);
		    } catch (IOException e) {
			getLogger().warn("Unable to unmarshal template.", e);
		    }
		}
	    }
	    synchronized (scriptTemplatesById) {
		scriptTemplatesById.clear();
		scriptTemplatesById.putAll(updated);
	    }

	    getLogger().info("Script template manager found the following templates:");
	    for (IScriptTemplate template : getScriptTemplates()) {
		getLogger().info("[" + template.getId() + "] " + template.getName());
	    }
	} else {
	    getLogger().info("Microservice does not contain any script templates.");
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptTemplateManager#
     * getScriptTemplates()
     */
    @Override
    public List<IScriptTemplate> getScriptTemplates() throws SiteWhereException {
	List<IScriptTemplate> templates = new ArrayList<IScriptTemplate>(getScriptTemplatesById().values());
	Collections.sort(templates, new Comparator<IScriptTemplate>() {

	    @Override
	    public int compare(IScriptTemplate o1, IScriptTemplate o2) {
		return o1.getName().compareTo(o2.getName());
	    }
	});
	return templates;
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptTemplateManager#
     * getScriptTemplateContent(java.lang.String)
     */
    @Override
    public byte[] getScriptTemplateContent(String id) throws SiteWhereException {
	IScriptTemplate template = getScriptTemplatesById().get(id);
	if (template == null) {
	    throw new SiteWhereException("Invalid script template id.");
	}
	File root = getMicroservice().getScriptTemplatesRoot();
	if (!root.exists()) {
	    throw new SiteWhereException("Templates folder does not exist.");
	} else {
	    File tfolder = new File(root, template.getId());
	    if (!tfolder.exists()) {
		throw new SiteWhereException("Template subfolder does not exist.");
	    }
	    File tfile = new File(tfolder, template.getId() + "." + template.getType());
	    if (!tfile.exists()) {
		throw new SiteWhereException("Template file does not exist: " + tfile.getAbsolutePath());
	    }
	    try (FileInputStream input = new FileInputStream(tfile)) {
		return IOUtils.toByteArray(input);
	    } catch (IOException e) {
		throw new SiteWhereException("Unable to get script template content.", e);
	    }
	}
    }

    protected Map<String, IScriptTemplate> getScriptTemplatesById() {
	return scriptTemplatesById;
    }
}