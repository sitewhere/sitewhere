/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.tenant;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.instance.spi.tenant.ITenantModelInitializer;
import com.sitewhere.microservice.model.ModelInitializer;
import com.sitewhere.microservice.tenant.TenantManagementRequestBuilder;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.groovy.IGroovyConfiguration;
import com.sitewhere.spi.microservice.groovy.IGroovyVariables;
import com.sitewhere.spi.microservice.scripting.ScriptType;
import com.sitewhere.spi.microservice.tenant.ITenantManagement;

import groovy.lang.Binding;

/**
 * Implementation of {@link ITenantModelInitializer} that delegates creation
 * logic to a Groovy script.
 */
public class GroovyTenantModelInitializer extends ModelInitializer implements ITenantModelInitializer {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(GroovyTenantModelInitializer.class);

    /** Groovy configuration */
    private IGroovyConfiguration groovyConfiguration;

    /** Script name */
    private String scriptName;

    public GroovyTenantModelInitializer(IGroovyConfiguration groovyConfiguration, String scriptName) {
	this.groovyConfiguration = groovyConfiguration;
	this.scriptName = scriptName;
    }

    @Override
    public void initialize(ITenantManagement tenantManagement) throws SiteWhereException {
	// Skip if not enabled.
	if (!isEnabled()) {
	    return;
	}

	Binding binding = new Binding();
	binding.setVariable(IGroovyVariables.VAR_LOGGER, LOGGER);
	binding.setVariable(IGroovyVariables.VAR_TENANT_MANAGEMENT_BUILDER,
		new TenantManagementRequestBuilder(tenantManagement));

	try {
	    getGroovyConfiguration().run(ScriptType.Initializer, getScriptName(), binding);
	} catch (SiteWhereException e) {
	    throw new SiteWhereException("Unable to run tenant model initializer.", e);
	}
    }

    public IGroovyConfiguration getGroovyConfiguration() {
	return groovyConfiguration;
    }

    public void setGroovyConfiguration(IGroovyConfiguration groovyConfiguration) {
	this.groovyConfiguration = groovyConfiguration;
    }

    public String getScriptName() {
	return scriptName;
    }

    public void setScriptName(String scriptName) {
	this.scriptName = scriptName;
    }
}