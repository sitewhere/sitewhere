package com.sitewhere.groovy.configuration;

import java.beans.Introspector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.groovy.TenantResourceConnector;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
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

    /** Interval at which classloader cache is cleaned */
    private static final int CACHE_CLEAN_INTERVAL = 30 * 1000;

    /** Used to connect Groovy engine to SiteWhere resource manager */
    private TenantResourceConnector resourceConnector;

    /** Groovy script engine */
    private GroovyScriptEngine groovyScriptEngine;

    /** Field for setting GSE verbose flag */
    private boolean verbose = false;

    /** Field for setting GSE debug flag */
    private boolean debug = false;

    /** Executor for cleanup thread */
    private ExecutorService executor;

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
	resourceConnector = new TenantResourceConnector(getTenant().getId());
	groovyScriptEngine = new GroovyScriptEngine(resourceConnector);

	groovyScriptEngine.getConfig().setVerbose(isVerbose());
	groovyScriptEngine.getConfig().setDebug(isDebug());
	LOGGER.info(
		"Tenant Groovy script engine configured with (verbose:" + isVerbose() + ") (debug:" + isDebug() + ").");

	this.executor = Executors.newSingleThreadExecutor();
	executor.execute(new CacheCleaner());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (executor != null) {
	    executor.shutdownNow();
	}
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

    /**
     * Cleans classloader cache at an interval.
     * 
     * @author Derek
     */
    private class CacheCleaner implements Runnable {

	@Override
	public void run() {
	    while (true) {
		getGroovyScriptEngine().getGroovyClassLoader().clearCache();
		Introspector.flushCaches();
		try {
		    Thread.sleep(CACHE_CLEAN_INTERVAL);
		} catch (InterruptedException e) {
		    LOGGER.warn("Groovy cache cleaner thread stopping.");
		    return;
		}
	    }
	}
    }
}