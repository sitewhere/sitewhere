/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.groovy;

import java.io.IOException;
import java.net.URL;

import org.codehaus.groovy.control.CompilationFailedException;

import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.groovy.IGroovyConfiguration;
import com.sitewhere.spi.microservice.scripting.IScriptContext;
import com.sitewhere.spi.microservice.scripting.IScriptMetadata;
import com.sitewhere.spi.microservice.scripting.IScriptSynchronizer;
import com.sitewhere.spi.microservice.scripting.ScriptType;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

/**
 * Provides common Groovy configuration for core server components.
 */
public class GroovyConfiguration extends LifecycleComponent implements IGroovyConfiguration {

    /** Script context */
    private IScriptContext scriptContext;

    /** Synchronizes scripts to the filesystem */
    private IScriptSynchronizer scriptSynchronizer;

    /** Groovy script engine */
    private GroovyScriptEngine groovyScriptEngine;

    /** Field for setting GSE verbose flag */
    private boolean verbose = false;

    /** Field for setting GSE debug flag */
    private boolean debug = false;

    public GroovyConfiguration(IScriptContext scriptContext, IScriptSynchronizer scriptSynchronizer) {
	super(LifecycleComponentType.Other);
	this.scriptContext = scriptContext;
	this.scriptSynchronizer = scriptSynchronizer;
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
	try {
	    URL root = new URL(
		    String.format("file://%s", getMicroservice().getInstanceSettings().getFileSystemStorageRoot()));
	    getLogger().debug(String.format("Root path URL for script resolution is %s.", root.toString()));
	    groovyScriptEngine = new GroovyScriptEngine(new URL[] { root });
	    groovyScriptEngine.getConfig().setVerbose(isVerbose());
	    groovyScriptEngine.getConfig().setDebug(isDebug());
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to create Groovy script engine.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.groovy.IGroovyConfiguration#run(com.sitewhere.
     * spi.microservice.scripting.IScriptMetadata, groovy.lang.Binding)
     */
    @Override
    public Object run(IScriptMetadata script, Binding binding) throws SiteWhereException {
	String name = script.getId() + "." + script.getType();
	return run(ScriptType.ManagedScript, name, binding);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.groovy.IGroovyConfiguration#run(com.sitewhere.
     * spi.microservice.scripting.ScriptType, java.lang.String, groovy.lang.Binding)
     */
    @Override
    public Object run(ScriptType type, String name, Binding binding) throws SiteWhereException {
	try {
	    String resolved = getScriptSynchronizer().resolve(getScriptContext(), type, name).toString();
	    return getGroovyScriptEngine().run(resolved, binding);
	} catch (ResourceException e) {
	    throw new SiteWhereException("Unable to access Groovy script.", e);
	} catch (ScriptException e) {
	    throw new SiteWhereException("Unable to run Groovy script.", e);
	} catch (CompilationFailedException e) {
	    throw new SiteWhereException("Error compiling Groovy script.", e);
	} catch (Throwable e) {
	    throw new SiteWhereException("Unhandled exception in Groovy script.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.groovy.IGroovyConfiguration#getScriptContext()
     */
    @Override
    public IScriptContext getScriptContext() {
	return scriptContext;
    }

    public void setScriptContext(IScriptContext scriptContext) {
	this.scriptContext = scriptContext;
    }

    /*
     * @see com.sitewhere.spi.microservice.groovy.IGroovyConfiguration#
     * getScriptSynchronizer()
     */
    @Override
    public IScriptSynchronizer getScriptSynchronizer() {
	return scriptSynchronizer;
    }

    public void setScriptSynchronizer(IScriptSynchronizer scriptSynchronizer) {
	this.scriptSynchronizer = scriptSynchronizer;
    }

    protected GroovyScriptEngine getGroovyScriptEngine() {
	return groovyScriptEngine;
    }

    protected void setGroovyScriptEngine(GroovyScriptEngine groovyScriptEngine) {
	this.groovyScriptEngine = groovyScriptEngine;
    }

    public boolean isVerbose() {
	return verbose;
    }

    public void setVerbose(boolean verbose) {
	this.verbose = verbose;
    }

    public boolean isDebug() {
	return debug;
    }

    public void setDebug(boolean debug) {
	this.debug = debug;
    }
}