/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;

import com.sitewhere.SiteWhere;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Handles server shutdown logic when servlet context is destroyed.
 * 
 * @author Derek
 */
public class ShutdownListener implements ServletContextListener {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
     * ServletContextEvent )
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
	// TODO: What monitors shutdown?
	LifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
		new LifecycleProgressContext(1, "Stopping SiteWhere Server"));
	SiteWhere.getServer().lifecycleStop(monitor);

	// Verify shutdown was successful.
	if (SiteWhere.getServer().getLifecycleStatus() == LifecycleStatus.Stopped) {
	    LOGGER.info("Server shut down successfully.");
	}

	// Shut down Log4J manually.
	LoggerContext context = (LoggerContext) LogManager.getContext();
	Configurator.shutdown(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.servlet.ServletContextListener#contextInitialized(javax.servlet.
     * ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
    }
}