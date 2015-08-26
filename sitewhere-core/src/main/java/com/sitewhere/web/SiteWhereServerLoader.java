/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.mule.util.StringMessageUtils;

import com.sitewhere.SiteWhere;
import com.sitewhere.spi.ServerStartupException;
import com.sitewhere.spi.SiteWhereException;

/**
 * Initializes the SiteWhere server.
 * 
 * @author Derek
 */
public class SiteWhereServerLoader extends HttpServlet {

	/** Serial version UUID */
	private static final long serialVersionUID = -8696135593175193509L;

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(SiteWhereServerLoader.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		try {
			SiteWhere.start();
			LOGGER.info("Server started successfully.");
			SiteWhere.getServer().logState();
		} catch (ServerStartupException e) {
			SiteWhere.getServer().setServerStartupError(e);
			List<String> messages = new ArrayList<String>();
			messages.add("!!!! SiteWhere Server Failed to Start !!!!");
			messages.add("");
			messages.add("Component: " + e.getDescription());
			messages.add("Error: " + e.getComponent().getLifecycleError().getMessage());
			String message = StringMessageUtils.getBoilerPlate(messages, '*', 60);
			LOGGER.info("\n" + message + "\n");
		} catch (SiteWhereException e) {
			LOGGER.error("Exception on server startup.", e);
			List<String> messages = new ArrayList<String>();
			messages.add("!!!! SiteWhere Server Failed to Start !!!!");
			messages.add("");
			messages.add("Error: " + e.getMessage());
			String message = StringMessageUtils.getBoilerPlate(messages, '*', 60);
			LOGGER.info("\n" + message + "\n");
		} catch (Throwable e) {
			LOGGER.error("Unhandled exception in server startup.", e);
			List<String> messages = new ArrayList<String>();
			messages.add("!!!! Unhandled Exception !!!!");
			messages.add("");
			messages.add("Error: " + e.getMessage());
			String message = StringMessageUtils.getBoilerPlate(messages, '*', 60);
			LOGGER.info("\n" + message + "\n");
		}
	}
}