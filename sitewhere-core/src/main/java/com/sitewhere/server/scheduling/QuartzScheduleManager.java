/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.scheduling;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.simpl.RAMJobStore;
import org.quartz.simpl.SimpleThreadPool;

import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.server.scheduling.jobs.QuartzBuilder;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.scheduling.IScheduleManager;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Implementation of {@link IScheduleManager} that uses Quartz to handle
 * schedule management.
 * 
 * @author Derek
 */
public class QuartzScheduleManager extends TenantLifecycleComponent implements IScheduleManager {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Instance id common to all schedulers */
    private static final String INSTANCE_ID = "sitewhere";

    /** Default number of threads used to process scheduled tasks */
    private static final int DEFAULT_THREAD_COUNT = 5;

    /** Schedule management implementation */
    private IScheduleManagement scheduleManagement;

    /** Number of threads used for processing */
    private int numProcessingThreads = DEFAULT_THREAD_COUNT;

    /** Cache schedules by token */
    private Map<String, ISchedule> schedulesByToken = new HashMap<String, ISchedule>();

    public QuartzScheduleManager(IScheduleManagement scheduleManagement) {
	super(LifecycleComponentType.ScheduleManager);
	this.scheduleManagement = scheduleManagement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.TenantLifecycleComponent#setTenant(com.
     * sitewhere .spi.user.ITenant)
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
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	try {
	    getScheduler().start();
	    cacheSchedules();
	    scheduleJobs();
	} catch (SchedulerException e) {
	    throw new SiteWhereException("Unable to start scheduler instance.", e);
	}
    }

    /**
     * Cache the list of schedules by unique token.
     * 
     * @throws SiteWhereException
     */
    protected void cacheSchedules() throws SiteWhereException {
	Map<String, ISchedule> updated = new HashMap<String, ISchedule>();
	ISearchResults<ISchedule> schedules = getScheduleManagement().listSchedules(new SearchCriteria(1, 0));
	for (ISchedule schedule : schedules.getResults()) {
	    updated.put(schedule.getToken(), schedule);
	}
	this.schedulesByToken = updated;
	LOGGER.info("Updated cache with " + getSchedulesByToken().size() + " schedules.");
    }

    /**
     * Schedule all jobs registered in the system.
     * 
     * @throws SiteWhereException
     */
    protected void scheduleJobs() throws SiteWhereException {
	ISearchResults<IScheduledJob> jobs = getScheduleManagement().listScheduledJobs(new SearchCriteria(1, 0));
	for (IScheduledJob job : jobs.getResults()) {
	    scheduleJob(job);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
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
     * com.sitewhere.spi.scheduling.IScheduleManager#scheduleAdded(com.sitewhere
     * .spi. scheduling.ISchedule)
     */
    @Override
    public void scheduleAdded(ISchedule schedule) throws SiteWhereException {
	cacheSchedules();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.scheduling.IScheduleManager#scheduleRemoved(com.
     * sitewhere.spi .scheduling.ISchedule)
     */
    @Override
    public void scheduleRemoved(ISchedule schedule) throws SiteWhereException {
	cacheSchedules();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManager#scheduleJob(com.sitewhere.
     * spi.scheduling .IScheduledJob)
     */
    @Override
    public void scheduleJob(IScheduledJob job) throws SiteWhereException {
	JobDetail detail = QuartzBuilder.buildJobDetail(job);
	ISchedule schedule = getSchedulesByToken().get(job.getScheduleToken());
	if (schedule == null) {
	    throw new SiteWhereException("Job references unknown schedule: " + job.getScheduleToken());
	}

	LOGGER.info("Scheduling job " + job.getToken() + " for '" + schedule.getName() + "'.");
	Trigger trigger = QuartzBuilder.buildTrigger(job, schedule);
	try {
	    getScheduler().scheduleJob(detail, trigger);
	} catch (SchedulerException e) {
	    throw new SiteWhereException("Unable to schedule job.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManager#unscheduleJob(com.sitewhere
     * .spi. scheduling.IScheduledJob)
     */
    @Override
    public void unscheduleJob(IScheduledJob job) throws SiteWhereException {
	try {
	    getScheduler().unscheduleJob(new TriggerKey(job.getToken()));
	} catch (SchedulerException e) {
	    throw new SiteWhereException("Unable to unschedule job.", e);
	}
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

    public Map<String, ISchedule> getSchedulesByToken() {
	return schedulesByToken;
    }

    public void setSchedulesByToken(Map<String, ISchedule> schedulesByToken) {
	this.schedulesByToken = schedulesByToken;
    }
}