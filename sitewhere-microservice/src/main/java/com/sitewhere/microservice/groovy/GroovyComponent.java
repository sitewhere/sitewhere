/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.groovy;

import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.groovy.IGroovyComponent;
import com.sitewhere.spi.microservice.scripting.IScriptMetadata;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import groovy.lang.Binding;

/**
 * Component that leverages a Groovy script.
 * 
 * @author Derek
 */
public abstract class GroovyComponent extends TenantEngineLifecycleComponent implements IGroovyComponent {

    /** Unique script id to execute */
    private String scriptId;

    /** Script metadata */
    private IScriptMetadata scriptMetadata;

    public GroovyComponent() {
    }

    public GroovyComponent(LifecycleComponentType type) {
	super(type);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);
	this.scriptMetadata = getTenantEngine().getMicroservice().getScriptManagement()
		.getScriptMetadata(getTenantEngine().getTenant().getId(), getScriptId());
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.groovy.IGroovyComponent#run(com.sitewhere.spi.
     * microservice.scripting.IScriptMetadata, groovy.lang.Binding)
     */
    @Override
    public Object run(Binding binding) throws SiteWhereException {
	if (getScriptMetadata() != null) {
	    return getTenantEngine().getGroovyConfiguration().run(getScriptMetadata(), binding);
	} else {
	    throw new SiteWhereException("Unable to run Groovy script. Initialization failed.");
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.groovy.IGroovyComponent#getScriptId()
     */
    @Override
    public String getScriptId() {
	return scriptId;
    }

    public void setScriptId(String scriptId) {
	this.scriptId = scriptId;
    }

    public IScriptMetadata getScriptMetadata() {
	return scriptMetadata;
    }

    public void setScriptMetadata(IScriptMetadata scriptMetadata) {
	this.scriptMetadata = scriptMetadata;
    }
}