/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.scheduling;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.request.IScheduleCreateRequest;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Management interface for persistent scheduling implementations.
 */
public interface IScheduleManagement extends ITenantEngineLifecycleComponent {

    /**
     * Create a new schedule.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public ISchedule createSchedule(IScheduleCreateRequest request) throws SiteWhereException;

    /**
     * Update an existing schedule.
     * 
     * @param token
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public ISchedule updateSchedule(String token, IScheduleCreateRequest request) throws SiteWhereException;

    /**
     * Get a schedule by unique token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public ISchedule getScheduleByToken(String token) throws SiteWhereException;

    /**
     * List schedules that match the given criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<ISchedule> listSchedules(ISearchCriteria criteria) throws SiteWhereException;

    /**
     * Delete an existing schedule.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public ISchedule deleteSchedule(String token) throws SiteWhereException;

    /**
     * Create a new scheduled job.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IScheduledJob createScheduledJob(IScheduledJobCreateRequest request) throws SiteWhereException;

    /**
     * Update an existing scheduled job.
     * 
     * @param token
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IScheduledJob updateScheduledJob(String token, IScheduledJobCreateRequest request) throws SiteWhereException;

    /**
     * Get a scheduled job by unique token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public IScheduledJob getScheduledJobByToken(String token) throws SiteWhereException;

    /**
     * List scheduled jobs that match the given criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IScheduledJob> listScheduledJobs(ISearchCriteria criteria) throws SiteWhereException;

    /**
     * Delete an existing scheduled job.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public IScheduledJob deleteScheduledJob(String token) throws SiteWhereException;
}