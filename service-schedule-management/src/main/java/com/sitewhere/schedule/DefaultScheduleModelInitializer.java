/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.schedule.spi.initializer.IScheduleModelInitializer;
import com.sitewhere.server.ModelInitializer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.IScheduleManagement;

/**
 * Used to load default schedule data into the datastore. The server only offers
 * this functionality if no schedules already exist.
 * 
 * @author Derek
 */
public class DefaultScheduleModelInitializer extends ModelInitializer implements IScheduleModelInitializer {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(DefaultScheduleModelInitializer.class);

    /** Schedule management implementation */
    private IScheduleManagement scheduleManagement;

    /**
     * Indiates whether model should be initialized if no console is available for
     * input
     */
    private boolean initializeIfNoConsole = false;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.scheduling.IScheduleModelInitializer#initialize(
     * com.sitewhere .spi.scheduling.IScheduleManagement)
     */
    @Override
    public void initialize(IScheduleManagement scheduleManagement) throws SiteWhereException {
	this.scheduleManagement = scheduleManagement;

	// Skip if not enabled.
	if (!isEnabled()) {
	    return;
	}

	createDefaultSchedules();
    }

    /**
     * Create schedules included by default.
     * 
     * @throws SiteWhereException
     */
    protected void createDefaultSchedules() throws SiteWhereException {
	LOGGER.info("Creating default schedule data.");
	getScheduleManagement().createSchedule(ScheduleHelper.createSimpleSchedule(
		"95ff6a81-3d92-4b10-b8af-957c172ad97b", "Every thirty seconds", null, null, new Long(30 * 1000), 0));
	getScheduleManagement().createSchedule(ScheduleHelper.createSimpleSchedule(
		"20f5e855-d8a2-431d-a68b-61f4549dbb80", "Every minute", null, null, new Long(60 * 1000), 0));
	getScheduleManagement().createSchedule(ScheduleHelper.createSimpleSchedule(
		"2c82d6d5-6a0a-48be-99ab-7451a69e3ba7", "Every 10 minutes", null, null, new Long(10 * 60 * 1000), 0));
	getScheduleManagement().createSchedule(ScheduleHelper.createCronSchedule("ee23196c-5bc3-4685-8c9d-6dcbb3062ec2",
		"On the half hour", null, null, "0 0/30 * 1/1 * ? *"));
	getScheduleManagement().createSchedule(ScheduleHelper.createCronSchedule("de305d54-75b4-431b-adb2-eb6b9e546014",
		"On the hour", null, null, "0 0 0/1 1/1 * ? *"));
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