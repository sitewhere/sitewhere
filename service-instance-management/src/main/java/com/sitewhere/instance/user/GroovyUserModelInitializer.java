/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.user;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.instance.spi.user.IUserModelInitializer;
import com.sitewhere.microservice.api.user.IUserManagement;
import com.sitewhere.microservice.api.user.UserManagementRequestBuilder;
import com.sitewhere.microservice.model.ModelInitializer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.groovy.IGroovyConfiguration;
import com.sitewhere.spi.microservice.scripting.ScriptType;

import groovy.lang.Binding;

/**
 * Implementation of {@link IUserModelInitializer} that delegates creation logic
 * to a Groovy script.
 */
public class GroovyUserModelInitializer extends ModelInitializer implements IUserModelInitializer {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(GroovyUserModelInitializer.class);

    /** Groovy configuration */
    private IGroovyConfiguration groovyConfiguration;

    /** Relative path to Groovy script */
    private String scriptName;

    public GroovyUserModelInitializer(IGroovyConfiguration groovyConfiguration, String scriptName) {
	this.groovyConfiguration = groovyConfiguration;
	this.scriptName = scriptName;
    }

    /*
     * @see com.sitewhere.instance.spi.user.IUserModelInitializer#initialize(com.
     * sitewhere.spi.user.IUserManagement)
     */
    @Override
    public void initialize(IUserManagement userManagement) throws SiteWhereException {
	// Skip if not enabled.
	if (!isEnabled()) {
	    return;
	}

	Binding binding = new Binding();
	binding.setVariable("logger", LOGGER);
	binding.setVariable("userBuilder", new UserManagementRequestBuilder(userManagement));

	try {
	    getGroovyConfiguration().run(ScriptType.Initializer, getScriptName(), binding);
	} catch (SiteWhereException e) {
	    throw new SiteWhereException("Unable to run user model initializer.", e);
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