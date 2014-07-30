/*
 * ITracer.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server.debug;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * Pluggable debug facility that allows hierarchical data execution information to be
 * stored for later analysis.
 * 
 * @author Derek
 */
public interface ITracer {

	/**
	 * Pushes another level into the trace stack.
	 * 
	 * @param category
	 * @param message
	 * @param logger
	 */
	public void push(TracerCategory category, String message, Logger logger);

	/**
	 * Pops back up a level on the trace stack.
	 * 
	 * @param logger
	 */
	public void pop(Logger logger);

	/**
	 * Store or log an informational message.
	 * 
	 * @param message
	 * @param logger
	 */
	public void info(String message, Logger logger);

	/**
	 * Store or log an warning message.
	 * 
	 * @param message
	 * @param error
	 * @param logger
	 */
	public void warn(String message, Throwable error, Logger logger);

	/**
	 * Store or log an error message.
	 * 
	 * @param message
	 * @param error
	 * @param logger
	 */
	public void error(String message, Throwable error, Logger logger);

	/**
	 * Store timing information.
	 * 
	 * @param message
	 * @param delta
	 * @param unit
	 * @param logger
	 */
	public void timing(String message, long delta, TimeUnit unit, Logger logger);
}