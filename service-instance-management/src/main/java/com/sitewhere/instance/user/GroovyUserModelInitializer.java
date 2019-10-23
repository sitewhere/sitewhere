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
import com.sitewhere.microservice.groovy.GroovyConfiguration;
import com.sitewhere.rest.model.user.request.UserManagementRequestBuilder;
import com.sitewhere.server.ModelInitializer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.user.IUserManagement;

import groovy.lang.Binding;

/**
 * Implementation of {@link IUserModelInitializer} that delegates creation logic
 * to a Groovy script.
 */
public class GroovyUserModelInitializer extends ModelInitializer implements IUserModelInitializer {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(GroovyUserModelInitializer.class);

    /** Groovy configuration */
    private GroovyConfiguration groovyConfiguration;

    /** Relative path to Groovy script */
    private String scriptPath;

    public GroovyUserModelInitializer(GroovyConfiguration groovyConfiguration, String scriptPath) {
	this.groovyConfiguration = groovyConfiguration;
	this.scriptPath = scriptPath;
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
	    getGroovyConfiguration().run(getScriptPath(), binding);
	} catch (SiteWhereException e) {
	    throw new SiteWhereException("Unable to run user model initializer.", e);
	}
    }

    public GroovyConfiguration getGroovyConfiguration() {
	return groovyConfiguration;
    }

    public void setGroovyConfiguration(GroovyConfiguration groovyConfiguration) {
	this.groovyConfiguration = groovyConfiguration;
    }

    public String getScriptPath() {
	return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
	this.scriptPath = scriptPath;
    }
}