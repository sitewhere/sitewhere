/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.scripting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	// Initialize scripts templates.
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
	return new byte[0];
    }

    protected Map<String, IScriptTemplate> getScriptTemplatesById() {
	return scriptTemplatesById;
    }
}