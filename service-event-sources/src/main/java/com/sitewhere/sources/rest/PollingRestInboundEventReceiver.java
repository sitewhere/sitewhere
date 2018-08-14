/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.rest;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.groovy.IGroovyVariables;
import com.sitewhere.sources.PollingInboundEventReceiver;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.microservice.scripting.IScriptMetadata;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

import groovy.lang.Binding;

/**
 * Performs polling on a REST endpoint at a given interval.
 * 
 * @author Derek
 */
public class PollingRestInboundEventReceiver extends PollingInboundEventReceiver<byte[]> {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(PollingRestInboundEventReceiver.class);

    /** Script metadata */
    private IScriptMetadata scriptMetadata;

    /** Unique script id to execute */
    private String scriptId;

    /** Base URL used for REST calls */
    private String baseUrl;

    /** Username used for REST calls */
    private String username;

    /** Password used for REST calls */
    private String password;

    /** Helper class for REST operations */
    private RestHelper rest;

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
	this.scriptMetadata = ((IConfigurableMicroservice<?>) getMicroservice()).getScriptManagement()
		.getScriptMetadata(getMicroservice().getIdentifier(), getTenantEngine().getTenant().getId(),
			getScriptId());
	if (getScriptMetadata() == null) {
	    throw new SiteWhereException("Script '" + getScriptId() + "' was not found.");
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.communication.PollingInboundEventReceiver#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.rest = new RestHelper(getBaseUrl(), getUsername(), getPassword());
	super.start(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.communication.PollingInboundEventReceiver#doPoll()
     */
    @Override
    @SuppressWarnings("unchecked")
    public void doPoll() throws SiteWhereException {
	Binding binding = new Binding();
	List<byte[]> payloads = new ArrayList<byte[]>();
	binding.setVariable(IGroovyVariables.VAR_REST_CLIENT, rest);
	binding.setVariable(IGroovyVariables.VAR_EVENT_PAYLOADS, payloads);
	binding.setVariable(IGroovyVariables.VAR_LOGGER, LOGGER);

	try {
	    getTenantEngine().getGroovyConfiguration().run(getScriptMetadata(), binding);
	    payloads = (List<byte[]>) binding.getVariable(IGroovyVariables.VAR_EVENT_PAYLOADS);

	    // Process each payload individually.
	    for (byte[] payload : payloads) {
		onEventPayloadReceived(payload, null);
	    }
	} catch (SiteWhereException e) {
	    throw e;
	} catch (Exception e) {
	    throw new SiteWhereException("Unhandled exception in Groovy script.", e);
	}
    }

    protected IScriptMetadata getScriptMetadata() {
	return scriptMetadata;
    }

    public String getScriptId() {
	return scriptId;
    }

    public void setScriptId(String scriptId) {
	this.scriptId = scriptId;
    }

    public String getBaseUrl() {
	return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
	this.baseUrl = baseUrl;
    }

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }
}