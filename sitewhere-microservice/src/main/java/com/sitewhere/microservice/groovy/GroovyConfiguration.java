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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.groovy.IScriptSynchronizer;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import groovy.util.GroovyScriptEngine;

/**
 * Provides common Groovy configuration for core server components.
 * 
 * @author Derek
 */
public class GroovyConfiguration extends LifecycleComponent {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

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
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.groovy.IGroovyConfiguration#
     * getGroovyScriptEngine()
     */
    public GroovyScriptEngine getGroovyScriptEngine() {
	return groovyScriptEngine;
    }

    public void setGroovyScriptEngine(GroovyScriptEngine groovyScriptEngine) {
	this.groovyScriptEngine = groovyScriptEngine;
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

    public IScriptSynchronizer getScriptSynchronizer() {
	return scriptSynchronizer;
    }

    public void setScriptSynchronizer(IScriptSynchronizer scriptSynchronizer) {
	this.scriptSynchronizer = scriptSynchronizer;
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