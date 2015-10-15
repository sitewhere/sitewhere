/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.scheduling;

import org.apache.log4j.Logger;

import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.scheduling.IScheduleManager;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IScheduleManager} that uses Quartz to handle schedule
 * management.
 * 
 * @author Derek
 */
public class QuartzScheduleManager extends TenantLifecycleComponent implements IScheduleManager {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(QuartzScheduleManager.class);

	/** Schedule management implementation */
	private IScheduleManagement scheduleManagement;

	public QuartzScheduleManager(IScheduleManagement scheduleManagement) {
		super(LifecycleComponentType.ScheduleManager);
		this.scheduleManagement = scheduleManagement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	public IScheduleManagement getScheduleManagement() {
		return scheduleManagement;
	}

	public void setScheduleManagement(IScheduleManagement scheduleManagement) {
		this.scheduleManagement = scheduleManagement;
	}
}