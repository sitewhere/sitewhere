package com.sitewhere.groovy.configuration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.server.resource.SiteWhereHomeResourceManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.groovy.ITenantGroovyConfiguration;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import groovy.util.GroovyScriptEngine;

/**
 * Provides common Groovy configuration for tenant components.
 * 
 * @author Derek
 */
public class TenantGroovyConfiguration extends TenantLifecycleComponent implements ITenantGroovyConfiguration {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Groovy script engine */
    private GroovyScriptEngine groovyScriptEngine;

    /** Field for setting GSE verbose flag */
    private boolean verbose = false;

    /** Field for setting GSE debug flag */
    private boolean debug = false;

    public TenantGroovyConfiguration() {
	super(LifecycleComponentType.Other);
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
	File root = SiteWhereHomeResourceManager.calculateConfigurationPath();
	File groovy = new File(root, "tenants/" + getTenant().getId() + "/scripts/groovy");
	if (!groovy.exists()) {
	    getLogger().warn("Tenant Groovy scripts folder did not exist. Creating.");
	    groovy.mkdirs();
	}

	URL[] roots = null;
	try {
	    roots = new URL[] { groovy.toURI().toURL() };
	} catch (MalformedURLException e) {
	    throw new SiteWhereException("Invalid Groovy script root.", e);
	}

	groovyScriptEngine = new GroovyScriptEngine(roots);

	groovyScriptEngine.getConfig().setVerbose(isVerbose());
	groovyScriptEngine.getConfig().setDebug(isDebug());
	LOGGER.info(
		"Tenant Groovy script engine configured with (verbose:" + isVerbose() + ") (debug:" + isDebug() + ").");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.groovy.ITenantGroovyConfiguration#
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