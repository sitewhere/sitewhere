/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule;

import com.sitewhere.server.lifecycle.LifecycleComponentDecorator;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.request.IScheduleCreateRequest;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Wraps an schedule management implementation. Subclasses can implement only
 * the methods they need to override.
 * 
 * @author Derek
 */
public class ScheduleManagementDecorator extends LifecycleComponentDecorator<IScheduleManagement>
	implements IScheduleManagement {

    public ScheduleManagementDecorator(IScheduleManagement delegate) {
	super(delegate);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent#
     * setTenantEngine(com.sitewhere.spi.microservice.multitenant.
     * IMicroserviceTenantEngine)
     */
    @Override
    public void setTenantEngine(IMicroserviceTenantEngine tenantEngine) {
	getDelegate().setTenantEngine(tenantEngine);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent#
     * getTenantEngine()
     */
    @Override
    public IMicroserviceTenantEngine getTenantEngine() {
	return getDelegate().getTenantEngine();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.scheduling.IScheduleManagement#createSchedule(com.
     * sitewhere.spi .scheduling.request.IScheduleCreateRequest)
     */
    @Override
    public ISchedule createSchedule(IScheduleCreateRequest request) throws SiteWhereException {
	return getDelegate().createSchedule(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManagement#updateSchedule(java.lang
     * .String, com.sitewhere.spi.scheduling.request.IScheduleCreateRequest)
     */
    @Override
    public ISchedule updateSchedule(String token, IScheduleCreateRequest request) throws SiteWhereException {
	return getDelegate().updateSchedule(token, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManagement#getScheduleByToken(java.
     * lang.String )
     */
    @Override
    public ISchedule getScheduleByToken(String token) throws SiteWhereException {
	return getDelegate().getScheduleByToken(token);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.scheduling.IScheduleManagement#listSchedules(com.
     * sitewhere.spi .search.ISearchCriteria)
     */
    @Override
    public ISearchResults<ISchedule> listSchedules(ISearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listSchedules(criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManagement#deleteSchedule(java.lang
     * .String, boolean)
     */
    @Override
    public ISchedule deleteSchedule(String token, boolean force) throws SiteWhereException {
	return getDelegate().deleteSchedule(token, force);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.scheduling.IScheduleManagement#createScheduledJob(com.
     * sitewhere .spi.scheduling.request.IScheduledJobCreateRequest)
     */
    @Override
    public IScheduledJob createScheduledJob(IScheduledJobCreateRequest request) throws SiteWhereException {
	return getDelegate().createScheduledJob(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManagement#updateScheduledJob(java.
     * lang.String ,
     * com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest)
     */
    @Override
    public IScheduledJob updateScheduledJob(String token, IScheduledJobCreateRequest request)
	    throws SiteWhereException {
	return getDelegate().updateScheduledJob(token, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.scheduling.IScheduleManagement#getScheduledJobByToken(
     * java.lang .String)
     */
    @Override
    public IScheduledJob getScheduledJobByToken(String token) throws SiteWhereException {
	return getDelegate().getScheduledJobByToken(token);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.scheduling.IScheduleManagement#listScheduledJobs(com.
     * sitewhere .spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IScheduledJob> listScheduledJobs(ISearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listScheduledJobs(criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManagement#deleteScheduledJob(java.
     * lang.String , boolean)
     */
    @Override
    public IScheduledJob deleteScheduledJob(String token, boolean force) throws SiteWhereException {
	return getDelegate().deleteScheduledJob(token, force);
    }
}