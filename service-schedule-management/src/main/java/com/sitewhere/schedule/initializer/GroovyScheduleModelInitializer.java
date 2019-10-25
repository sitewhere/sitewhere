/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule.initializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.rest.model.scheduling.request.scripting.ScheduleManagementRequestBuilder;
import com.sitewhere.schedule.spi.initializer.IScheduleModelInitializer;
import com.sitewhere.server.ModelInitializer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.groovy.IGroovyConfiguration;
import com.sitewhere.spi.microservice.scripting.ScriptType;
import com.sitewhere.spi.scheduling.IScheduleManagement;

import groovy.lang.Binding;

/**
 * Implementation of {@link IScheduleModelInitializer} that delegates creation
 * logic to a Groovy script.
 */
public class GroovyScheduleModelInitializer extends ModelInitializer implements IScheduleModelInitializer {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(GroovyScheduleModelInitializer.class);

    /** Tenant Groovy configuration */
    private IGroovyConfiguration groovyConfiguration;

    /** Relative path to Groovy script */
    private String scriptPath;

    public GroovyScheduleModelInitializer(IGroovyConfiguration groovyConfiguration, String scriptPath) {
	this.groovyConfiguration = groovyConfiguration;
	this.scriptPath = scriptPath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.scheduling.IScheduleModelInitializer#initialize(
     * com.sitewhere.spi.scheduling.IScheduleManagement)
     */
    @Override
    public void initialize(IScheduleManagement scheduleManagement) throws SiteWhereException {
	// Skip if not enabled.
	if (!isEnabled()) {
	    return;
	}

	Binding binding = new Binding();
	binding.setVariable("logger", LOGGER);
	binding.setVariable("scheduleBuilder", new ScheduleManagementRequestBuilder(scheduleManagement));

	try {
	    getGroovyConfiguration().run(ScriptType.Initializer, getScriptPath(), binding);
	} catch (SiteWhereException e) {
	    throw new SiteWhereException("Unable to run schedule model initializer.", e);
	}
    }

    public IGroovyConfiguration getGroovyConfiguration() {
	return groovyConfiguration;
    }

    public void setGroovyConfiguration(IGroovyConfiguration groovyConfiguration) {
	this.groovyConfiguration = groovyConfiguration;
    }

    public String getScriptPath() {
	return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
	this.scriptPath = scriptPath;
    }
}