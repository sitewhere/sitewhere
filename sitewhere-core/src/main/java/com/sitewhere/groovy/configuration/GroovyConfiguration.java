package com.sitewhere.groovy.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.groovy.GlobalResourceConnector;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.groovy.IGroovyConfiguration;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import groovy.util.GroovyScriptEngine;

/**
 * Provides common Groovy configuration for core server components.
 * 
 * @author Derek
 */
public class GroovyConfiguration extends LifecycleComponent implements IGroovyConfiguration {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Used to connect Groovy engine to SiteWhere resource manager */
    private GlobalResourceConnector resourceConnector;

    /** Groovy script engine */
    private GroovyScriptEngine groovyScriptEngine;

    /** Field for setting GSE verbose flag */
    private boolean verbose = false;

    /** Field for setting GSE debug flag */
    private boolean debug = false;

    public GroovyConfiguration() {
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
	resourceConnector = new GlobalResourceConnector();
	groovyScriptEngine = new GroovyScriptEngine(resourceConnector);

	groovyScriptEngine.getConfig().setVerbose(isVerbose());
	groovyScriptEngine.getConfig().setDebug(isDebug());
	LOGGER.info(
		"Global Groovy script engine configured with (verbose:" + isVerbose() + ") (debug:" + isDebug() + ").");
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