/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.sitewhere.spi.server.debug.ITracer;
import com.sitewhere.spi.server.debug.TracerCategory;

/**
 * Helper class for shortening calls to {@link ITracer} implementation.
 * 
 * @author Derek
 */
public class Tracer {

	/**
	 * Starts tracing.
	 * 
	 * @param category
	 * @param message
	 * @param logger
	 */
	public static void start(TracerCategory category, String message, Logger logger) {
		SiteWhere.getServer().getTracer().start(category, message, logger);
	}

	/**
	 * Stops tracing.
	 * 
	 * @param logger
	 */
	public static void stop(Logger logger) {
		SiteWhere.getServer().getTracer().stop(logger);
	}

	/**
	 * Pushes another level into the trace stack.
	 * 
	 * @param category
	 * @param message
	 * @param logger
	 */
	public static void push(TracerCategory category, String message, Logger logger) {
		SiteWhere.getServer().getTracer().push(category, message, logger);
	}

	/**
	 * Pops back up a level on the trace stack.
	 * 
	 * @param logger
	 */
	public static void pop(Logger logger) {
		SiteWhere.getServer().getTracer().pop(logger);
	}

	/**
	 * Store or log an informational message.
	 * 
	 * @param message
	 * @param logger
	 */
	public static void info(String message, Logger logger) {
		SiteWhere.getServer().getTracer().info(message, logger);
	}

	/**
	 * Store or log an warning message.
	 * 
	 * @param message
	 * @param error
	 * @param logger
	 */
	public static void warn(String message, Throwable error, Logger logger) {
		SiteWhere.getServer().getTracer().warn(message, error, logger);
	}

	/**
	 * Store or log an error message.
	 * 
	 * @param message
	 * @param error
	 * @param logger
	 */
	public static void error(String message, Throwable error, Logger logger) {
		SiteWhere.getServer().getTracer().error(message, error, logger);
	}

	/**
	 * Store timing information.
	 * 
	 * @param message
	 * @param delta
	 * @param unit
	 * @param logger
	 */
	public static void timing(String message, long delta, TimeUnit unit, Logger logger) {
		SiteWhere.getServer().getTracer().timing(message, delta, unit, logger);
	}
}