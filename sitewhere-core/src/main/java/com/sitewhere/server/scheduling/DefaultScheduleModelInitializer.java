/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.scheduling;

import org.apache.log4j.Logger;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.server.scheduling.IScheduleModelInitializer;

/**
 * Used to load default schedule data into the datastore. The server only offers this
 * functionality if no schedules already exist.
 * 
 * @author Derek
 */
public class DefaultScheduleModelInitializer implements IScheduleModelInitializer {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DefaultScheduleModelInitializer.class);

	/** Schedule management implementation */
	private IScheduleManagement scheduleManagement;

	/** Indiates whether model should be initialized if no console is available for input */
	private boolean initializeIfNoConsole = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.scheduling.IScheduleModelInitializer#initialize(com.sitewhere
	 * .spi.scheduling.IScheduleManagement)
	 */
	@Override
	public void initialize(IScheduleManagement scheduleManagement) throws SiteWhereException {
		this.scheduleManagement = scheduleManagement;
	}

	/**
	 * Create schedules included by default.
	 * 
	 * @throws SiteWhereException
	 */
	protected void createDefaultSchedules() throws SiteWhereException {
		LOGGER.info("Creating default schedule data.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.IModelInitializer#isInitializeIfNoConsole()
	 */
	public boolean isInitializeIfNoConsole() {
		return initializeIfNoConsole;
	}

	public void setInitializeIfNoConsole(boolean initializeIfNoConsole) {
		this.initializeIfNoConsole = initializeIfNoConsole;
	}

	public IScheduleManagement getScheduleManagement() {
		return scheduleManagement;
	}

	public void setScheduleManagement(IScheduleManagement scheduleManagement) {
		this.scheduleManagement = scheduleManagement;
	}
}