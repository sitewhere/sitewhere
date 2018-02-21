/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.groovy;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.control.CompilationFailedException;

import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.groovy.IGroovyConfiguration;
import com.sitewhere.spi.microservice.scripting.IScriptMetadata;
import com.sitewhere.spi.microservice.scripting.IScriptSynchronizer;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

/**
 * Provides common Groovy configuration for core server components.
 * 
 * @author Derek
 */
public class GroovyConfiguration extends LifecycleComponent implements IGroovyConfiguration {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(GroovyConfiguration.class);

    /** Synchronizer for loading Zk scripts to filesystem */
    private IScriptSynchronizer scriptSynchronizer;

    /** Groovy script engine */
    private GroovyScriptEngine groovyScriptEngine;

    /** Field for setting GSE verbose flag */
    private boolean verbose = false;

    /** Field for setting GSE debug flag */
    private boolean debug = false;

    public GroovyConfiguration(IScriptSynchronizer scriptSynchronizer) {
	super(LifecycleComponentType.Other);
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
	    groovyScriptEngine = new GroovyScriptEngine(
		    new URL[] { getScriptSynchronizer().getFileSystemRoot().toURI().toURL() });
	    groovyScriptEngine.getConfig().setVerbose(isVerbose());
	    groovyScriptEngine.getConfig().setDebug(isDebug());
	} catch (MalformedURLException e) {
	    throw new SiteWhereException("Unable to create Groovy script engine.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.groovy.IGroovyConfiguration#run(java.lang.
     * String, groovy.lang.Binding)
     */
    @Override
    public Object run(IScriptMetadata script, Binding binding) throws SiteWhereException {
	String scriptPath = script.getId() + "." + script.getType();
	return run(scriptPath, binding);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.groovy.IGroovyConfiguration#run(java.lang.
     * String, groovy.lang.Binding)
     */
    @Override
    public Object run(String scriptPath, Binding binding) throws SiteWhereException {
	try {
	    return getGroovyScriptEngine().run(scriptPath, binding);
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
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
	return LOGGER;
    }

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