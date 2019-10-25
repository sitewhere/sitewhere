/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.groovy;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.sitewhere.groovy.IGroovyVariables;
import com.sitewhere.microservice.security.SystemUserCallable;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.groovy.IGroovyComponent;
import com.sitewhere.spi.microservice.scripting.IScriptMetadata;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import groovy.lang.Binding;

/**
 * Component that leverages a Groovy script.
 * 
 * @author Derek
 */
public class GroovyComponent extends TenantEngineLifecycleComponent implements IGroovyComponent {

    /** Unique script id to execute */
    private String scriptId;

    /** Number of threads used for processing */
    private int numThreads = 1;

    /** Script metadata */
    private IScriptMetadata scriptMetadata;

    /** Executor for multithreading */
    private ExecutorService executor;

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

	if (getScriptId() == null) {
	    throw new SiteWhereException("Script id was not initialized properly.");
	}
	// this.scriptMetadata = ((IConfigurableMicroservice<?>)
	// getMicroservice()).getScriptManagement()
	// .getScriptMetadata(getMicroservice().getIdentifier(),
	// getTenantEngine().getTenant().getToken(),
	// getScriptId());
	// if (getScriptMetadata() == null) {
	// throw new SiteWhereException("Script '" + getScriptId() + "' was not
	// found.");
	// }
	//
	// getLogger().info(String.format("Groovy component will use version %s of
	// script '%s'",
	// getScriptMetadata().getActiveVersion(), getScriptMetadata().getName()));
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	// Create thread pool for processing requests.
	this.executor = Executors.newFixedThreadPool(getNumThreads());
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.groovy.IGroovyComponent#createBindingFor(com.
     * sitewhere.spi.server.lifecycle.ILifecycleComponent)
     */
    @Override
    public Binding createBindingFor(ILifecycleComponent component) throws SiteWhereException {
	Binding binding = new Binding();
	if (component instanceof ITenantEngineLifecycleComponent) {
	    binding.setVariable(IGroovyVariables.VAR_TENANT,
		    ((ITenantEngineLifecycleComponent) component).getTenantEngine().getTenant());
	}
	binding.setVariable(IGroovyVariables.VAR_LOGGER, getLogger());
	return binding;
    }

    /*
     * @see com.sitewhere.spi.microservice.groovy.IGroovyComponent#run(groovy.lang.
     * Binding)
     */
    @Override
    public Object run(Binding binding) throws SiteWhereException {
	Future<Object> result = executor.submit(
		new SystemUserCallable<Object>(getTenantEngine().getMicroservice(), getTenantEngine().getTenant()) {

		    /*
		     * @see com.sitewhere.microservice.security.SystemUserCallable#runAsSystemUser()
		     */
		    @Override
		    public Object runAsSystemUser() throws SiteWhereException {
			return getTenantEngine().getGroovyConfiguration().run(getScriptMetadata(), binding);
		    }
		});
	try {
	    return result.get();
	} catch (InterruptedException e) {
	    throw new SiteWhereException("Script execution interrupted.", e);
	} catch (ExecutionException e) {
	    throw new SiteWhereException(e.getCause());
	}
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.stop(monitor);

	if (executor != null) {
	    executor.shutdownNow();
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

    /*
     * @see com.sitewhere.spi.microservice.groovy.IGroovyComponent#getNumThreads()
     */
    @Override
    public int getNumThreads() {
	return numThreads;
    }

    public void setNumThreads(int numThreads) {
	this.numThreads = numThreads;
    }

    public IScriptMetadata getScriptMetadata() {
	return scriptMetadata;
    }

    public void setScriptMetadata(IScriptMetadata scriptMetadata) {
	this.scriptMetadata = scriptMetadata;
    }
}