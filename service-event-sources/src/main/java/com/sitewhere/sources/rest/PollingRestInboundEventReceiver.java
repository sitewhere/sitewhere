/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.sources.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.sources.PollingInboundEventReceiver;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.scripting.IScriptMetadata;

/**
 * Performs polling on a REST endpoint at a given interval.
 */
public class PollingRestInboundEventReceiver extends PollingInboundEventReceiver<byte[]> {

    /** Static logger instance */
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
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
	// this.scriptMetadata = ((IConfigurableMicroservice<?>)
	// getMicroservice()).getScriptManagement()
	// .getScriptMetadata(getMicroservice().getIdentifier(),
	// getTenantEngine().getTenant().getId(),
	// getScriptId());
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
    public void doPoll() throws SiteWhereException {
	// Binding binding = new Binding();
	// List<byte[]> payloads = new ArrayList<byte[]>();
	// binding.setVariable(IScriptVariables.VAR_REST_CLIENT, rest);
	// binding.setVariable(IScriptVariables.VAR_EVENT_PAYLOADS, payloads);
	// binding.setVariable(IScriptVariables.VAR_LOGGER, LOGGER);
	//
	// try {
	// getTenantEngine().getGroovyConfiguration().run(getScriptMetadata(), binding);
	// payloads = (List<byte[]>)
	// binding.getVariable(IGroovyVariables.VAR_EVENT_PAYLOADS);
	//
	// // Process each payload individually.
	// for (byte[] payload : payloads) {
	// onEventPayloadReceived(payload, null);
	// }
	// } catch (SiteWhereException e) {
	// throw e;
	// } catch (Exception e) {
	// throw new SiteWhereException("Unhandled exception in Groovy script.", e);
	// }
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