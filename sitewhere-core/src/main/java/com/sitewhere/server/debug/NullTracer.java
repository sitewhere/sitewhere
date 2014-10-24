/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.debug;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.sitewhere.spi.server.debug.ITracer;
import com.sitewhere.spi.server.debug.TracerCategory;

/**
 * Implementation of {@link ITracer} that does nothing with the data.
 * 
 * @author Derek
 */
public class NullTracer implements ITracer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.debug.ITracer#start(com.sitewhere.spi.server.debug.
	 * TracerCategory, java.lang.String, org.apache.log4j.Logger)
	 */
	@Override
	public void start(TracerCategory category, String message, Logger logger) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.debug.ITracer#stop(org.apache.log4j.Logger)
	 */
	@Override
	public void stop(Logger logger) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.debug.ITracer#asHtml()
	 */
	@Override
	public String asHtml() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.debug.ITracer#push(com.sitewhere.spi.server.debug.
	 * TracerCategory, java.lang.String, org.apache.log4j.Logger)
	 */
	@Override
	public void push(TracerCategory category, String message, Logger logger) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.debug.ITracer#pop(org.apache.log4j.Logger)
	 */
	@Override
	public void pop(Logger logger) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.debug.ITracer#debug(java.lang.String,
	 * org.apache.log4j.Logger)
	 */
	@Override
	public void debug(String message, Logger logger) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.debug.ITracer#info(java.lang.String,
	 * org.apache.log4j.Logger)
	 */
	@Override
	public void info(String message, Logger logger) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.debug.ITracer#warn(java.lang.String,
	 * java.lang.Throwable, org.apache.log4j.Logger)
	 */
	@Override
	public void warn(String message, Throwable error, Logger logger) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.debug.ITracer#error(java.lang.String,
	 * java.lang.Throwable, org.apache.log4j.Logger)
	 */
	@Override
	public void error(String message, Throwable error, Logger logger) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.debug.ITracer#timing(java.lang.String, long,
	 * java.util.concurrent.TimeUnit, org.apache.log4j.Logger)
	 */
	@Override
	public void timing(String message, long delta, TimeUnit unit, Logger logger) {
	}
}