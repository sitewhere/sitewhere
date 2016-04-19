/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.ISiteWhereApplication;
import com.sitewhere.spi.server.ISiteWhereServer;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Main class for accessing core SiteWhere functionality.
 * 
 * @author Derek
 */
public class SiteWhere {

	/** Singleton server instance */
	private static ISiteWhereServer SERVER;

	/**
	 * Called once to bootstrap the SiteWhere server.
	 * 
	 * @throws SiteWhereException
	 */
	public static void start(ISiteWhereApplication application) throws SiteWhereException {
		Class<? extends ISiteWhereServer> clazz = application.getServerClass();
		try {
			SERVER = clazz.newInstance();
			SERVER.initialize();
			SERVER.lifecycleStart();

			// Handle errors that prevent server startup.
			if (SERVER.getLifecycleStatus() == LifecycleStatus.Error) {
				throw SERVER.getLifecycleError();
			}
		} catch (InstantiationException e) {
			throw new SiteWhereException("Unable to create SiteWhere server instance.", e);
		} catch (IllegalAccessException e) {
			throw new SiteWhereException("Unable to access SiteWhere server class.", e);
		}
	}

	/**
	 * Called to shut down the SiteWhere server.
	 * 
	 * @throws SiteWhereException
	 */
	public static void stop() throws SiteWhereException {
		getServer().lifecycleStop();

		// Handle errors that prevent server shutdown.
		if (SERVER.getLifecycleStatus() == LifecycleStatus.Error) {
			throw SERVER.getLifecycleError();
		}
	}

	/**
	 * Get the singleton SiteWhere server instance.
	 * 
	 * @return
	 */
	public static ISiteWhereServer getServer() {
		if (SERVER == null) {
			throw new RuntimeException("SiteWhere server has not been initialized.");
		}
		return SERVER;
	}
}