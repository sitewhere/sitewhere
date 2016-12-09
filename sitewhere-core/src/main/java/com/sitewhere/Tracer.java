/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.server.debug.NullTracer;
import com.sitewhere.spi.server.debug.ITracer;
import com.sitewhere.spi.server.debug.TracerCategory;

/**
 * Helper class for shortening calls to {@link ITracer} implementation.
 * 
 * @author Derek
 */
public class Tracer {

    /** Private logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Fallback tracer implementation */
    private static ITracer FALLBACK = new NullTracer();

    /**
     * Starts tracing.
     * 
     * @param category
     * @param message
     * @param logger
     */
    public static void start(TracerCategory category, String message, Logger logger) {
	getTracer().start(category, message, logger);
    }

    /**
     * Stops tracing.
     * 
     * @param logger
     */
    public static void stop(Logger logger) {
	getTracer().stop(logger);
    }

    /**
     * Set enablement of tracer implementation.
     * 
     * @param enabled
     */
    public static void setEnabled(boolean enabled) {
	getTracer().setEnabled(enabled);
    }

    /**
     * Get an HTML representation of the tracer output.
     * 
     * @return
     * @throws UnsupportedOperationException
     */
    public static String asHtml() throws UnsupportedOperationException {
	return getTracer().asHtml();
    }

    /**
     * Pushes another level into the trace stack.
     * 
     * @param category
     * @param message
     * @param logger
     */
    public static void push(TracerCategory category, String message, Logger logger) {
	getTracer().push(category, message, logger);
    }

    /**
     * Pops back up a level on the trace stack.
     * 
     * @param logger
     */
    public static void pop(Logger logger) {
	getTracer().pop(logger);
    }

    /**
     * Store or log a debug message.
     * 
     * @param message
     * @param logger
     */
    public static void debug(String message, Logger logger) {
	getTracer().debug(message, logger);
    }

    /**
     * Store or log an informational message.
     * 
     * @param message
     * @param logger
     */
    public static void info(String message, Logger logger) {
	getTracer().info(message, logger);
    }

    /**
     * Store or log an warning message.
     * 
     * @param message
     * @param error
     * @param logger
     */
    public static void warn(String message, Throwable error, Logger logger) {
	getTracer().warn(message, error, logger);
    }

    /**
     * Store or log an error message.
     * 
     * @param message
     * @param error
     * @param logger
     */
    public static void error(String message, Throwable error, Logger logger) {
	getTracer().error(message, error, logger);
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
	getTracer().timing(message, delta, unit, logger);
    }

    /**
     * Gets tracer while detecting server problems.
     * 
     * @return
     */
    protected static ITracer getTracer() {
	if (!SiteWhere.isServerAvailable()) {
	    LOGGER.debug("Tracer called, but server does not exist. Check log file for errors on startup.");
	    return FALLBACK;
	}
	if (SiteWhere.getServer().getTracer() == null) {
	    LOGGER.warn("Tracer not initialized. Check log file for errors on startup.");
	    return FALLBACK;
	}
	return SiteWhere.getServer().getTracer();
    }
}