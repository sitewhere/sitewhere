/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.schedule;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.simpl.RAMJobStore;
import org.quartz.simpl.SimpleThreadPool;

import com.sitewhere.microservice.api.schedule.IScheduleManagement;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.schedule.jobs.QuartzBuilder;
import com.sitewhere.schedule.spi.IScheduleManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Implementation of {@link IScheduleManager} that uses Quartz to handle
 * schedule management.
 */
public class QuartzScheduleManager extends TenantEngineLifecycleComponent implements IScheduleManager {

    /** Instance id common to all schedulers */
    private static final String INSTANCE_ID = "sitewhere";

    /** Default number of threads used to process scheduled tasks */
    private static final int DEFAULT_THREAD_COUNT = 5;

    /** Schedule management implementation */
    private IScheduleManagement scheduleManagement;

    /** Number of threads used for processing */
    private int numProcessingThreads = DEFAULT_THREAD_COUNT;

    /** Cache schedules by token */
    private Map<UUID, ISchedule> schedulesById = new HashMap<UUID, ISchedule>();

    public QuartzScheduleManager(IScheduleManagement scheduleManagement) {
	super(LifecycleComponentType.ScheduleManager);
	this.scheduleManagement = scheduleManagement;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent#setTenantEngine
     * (com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine)
     */
    @Override
    public void setTenantEngine(IMicroserviceTenantEngine<?> tenantEngine) {
	super.setTenantEngine(tenantEngine);
	try {
	    DirectSchedulerFactory.getInstance().createScheduler(
		    tenantEngine.getTenantResource().getMetadata().getName(), INSTANCE_ID,
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
	Map<UUID, ISchedule> updated = new HashMap<UUID, ISchedule>();
	ISearchResults<? extends ISchedule> schedules = getScheduleManagement().listSchedules(SearchCriteria.ALL);
	for (ISchedule schedule : schedules.getResults()) {
	    updated.put(schedule.getId(), schedule);
	}
	this.schedulesById = updated;
	getLogger().info("Updated cache with " + getSchedulesById().size() + " schedules.");
    }

    /**
     * Schedule all jobs registered in the system.
     * 
     * @throws SiteWhereException
     */
    protected void scheduleJobs() throws SiteWhereException {
	ISearchResults<? extends IScheduledJob> jobs = getScheduleManagement().listScheduledJobs(SearchCriteria.ALL);
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
     * @see com.sitewhere.spi.scheduling.IScheduleManager#scheduleJob(com.sitewhere.
     * spi.scheduling .IScheduledJob)
     */
    @Override
    public void scheduleJob(IScheduledJob job) throws SiteWhereException {
	JobDetail detail = QuartzBuilder.buildJobDetail(job);
	ISchedule schedule = getSchedulesById().get(job.getScheduleId());
	if (schedule == null) {
	    throw new SiteWhereException(
		    String.format("Job references unknown schedule: %s", job.getScheduleId().toString()));
	}

	getLogger().info("Scheduling job " + job.getToken() + " for '" + schedule.getName() + "'.");
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

    /**
     * Get scheduler instance for this tenant.
     * 
     * @return
     * @throws SiteWhereException
     */
    public Scheduler getScheduler() throws SiteWhereException {
	try {
	    return DirectSchedulerFactory.getInstance()
		    .getScheduler(getTenantEngine().getTenantResource().getMetadata().getName());
	} catch (SchedulerException e) {
	    throw new SiteWhereException("Unable to get scheduler instance.", e);
	}
    }

    protected IScheduleManagement getScheduleManagement() {
	return scheduleManagement;
    }

    protected int getNumProcessingThreads() {
	return numProcessingThreads;
    }

    protected Map<UUID, ISchedule> getSchedulesById() {
	return schedulesById;
    }
}