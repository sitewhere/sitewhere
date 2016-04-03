/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.groovy.user;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.groovy.GroovyConfiguration;
import com.sitewhere.rest.model.user.request.UserManagementRequestBuilder;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.user.IUserModelInitializer;
import com.sitewhere.spi.user.IUserManagement;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

/**
 * Implementation of {@link IUserModelInitializer} that delegates creation logic to a
 * Groovy script.
 * 
 * @author Derek
 */
public class GroovyUserModelInitializer implements IUserModelInitializer {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(GroovyUserModelInitializer.class);

	/** Injected Groovy configuration */
	private GroovyConfiguration configuration;

	/** Relative path to Groovy script */
	private String scriptPath;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.IModelInitializer#isInitializeIfNoConsole()
	 */
	@Override
	public boolean isInitializeIfNoConsole() throws SiteWhereException {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.user.IUserModelInitializer#initialize(com.sitewhere.spi.
	 * user.IUserManagement)
	 */
	@Override
	public void initialize(IUserManagement userManagement) throws SiteWhereException {
		Binding binding = new Binding();
		binding.setVariable("logger", LOGGER);
		binding.setVariable("userBuilder", new UserManagementRequestBuilder(userManagement));

		try {
			// Use the system account for logging "created by" on created elements.
			SecurityContextHolder.getContext().setAuthentication(SiteWhereServer.getSystemAuthentication());
			getConfiguration().getGroovyScriptEngine().run(getScriptPath(), binding);
		} catch (ResourceException e) {
			throw new SiteWhereException("Unable to access Groovy script. " + e.getMessage(), e);
		} catch (ScriptException e) {
			throw new SiteWhereException("Unable to run Groovy script.", e);
		} finally {
			SecurityContextHolder.getContext().setAuthentication(null);
		}
	}

	public GroovyConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(GroovyConfiguration configuration) {
		this.configuration = configuration;
	}

	public String getScriptPath() {
		return scriptPath;
	}

	public void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}
}