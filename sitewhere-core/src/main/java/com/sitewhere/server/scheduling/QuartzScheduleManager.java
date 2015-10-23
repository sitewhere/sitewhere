/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.scheduling;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.simpl.RAMJobStore;
import org.quartz.simpl.SimpleThreadPool;

import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.scheduling.IScheduleManager;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.user.ITenant;

/**
 * Implementation of {@link IScheduleManager} that uses Quartz to handle schedule
 * management.
 * 
 * @author Derek
 */
public class QuartzScheduleManager extends TenantLifecycleComponent implements IScheduleManager {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(QuartzScheduleManager.class);

	/** Instance id common to all schedulers */
	private static final String INSTANCE_ID = "sitewhere";

	/** Default number of threads used to process scheduled tasks */
	private static final int DEFAULT_THREAD_COUNT = 5;

	/** Schedule management implementation */
	private IScheduleManagement scheduleManagement;

	/** Number of threads used for processing */
	private int numProcessingThreads = DEFAULT_THREAD_COUNT;

	public QuartzScheduleManager(IScheduleManagement scheduleManagement) {
		super(LifecycleComponentType.ScheduleManager);
		this.scheduleManagement = scheduleManagement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.server.lifecycle.TenantLifecycleComponent#setTenant(com.sitewhere
	 * .spi.user.ITenant)
	 */
	@Override
	public void setTenant(ITenant tenant) {
		super.setTenant(tenant);
		try {
			DirectSchedulerFactory.getInstance().createScheduler(getTenant().getId(), INSTANCE_ID,
					new SimpleThreadPool(getNumProcessingThreads(), Thread.NORM_PRIORITY), new RAMJobStore());
		} catch (SchedulerException e) {
			throw new RuntimeException("Unable to create Quartz scheduler for schedule manager.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		try {
			getScheduler().start();
		} catch (SchedulerException e) {
			throw new SiteWhereException("Unable to start scheduler instance.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		try {
			getScheduler().shutdown();
		} catch (SchedulerException e) {
			throw new SiteWhereException("Unable to start scheduler instance.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.scheduling.IScheduleManager#scheduleJob(com.sitewhere.spi.scheduling
	 * .IScheduledJob)
	 */
	@Override
	public void scheduleJob(IScheduledJob job) throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.scheduling.IScheduleManager#unscheduleJob(com.sitewhere.spi.
	 * scheduling.IScheduledJob)
	 */
	@Override
	public void unscheduleJob(IScheduledJob job) throws SiteWhereException {
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

	/**
	 * Get scheduler instance for this tenant.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public Scheduler getScheduler() throws SiteWhereException {
		try {
			return DirectSchedulerFactory.getInstance().getScheduler(getTenant().getId());
		} catch (SchedulerException e) {
			throw new SiteWhereException("Unable to get scheduler instance.", e);
		}
	}

	public IScheduleManagement getScheduleManagement() {
		return scheduleManagement;
	}

	public void setScheduleManagement(IScheduleManagement scheduleManagement) {
		this.scheduleManagement = scheduleManagement;
	}

	public int getNumProcessingThreads() {
		return numProcessingThreads;
	}

	public void setNumProcessingThreads(int numProcessingThreads) {
		this.numProcessingThreads = numProcessingThreads;
	}
}