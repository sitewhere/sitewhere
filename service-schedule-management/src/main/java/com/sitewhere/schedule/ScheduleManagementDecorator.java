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
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.request.IScheduleCreateRequest;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Wraps an schedule management implementation. Subclasses can implement only
 * the methods they need to override.
 * 
 * @author Derek
 */
public class ScheduleManagementDecorator extends LifecycleComponentDecorator implements IScheduleManagement {

    /** Delegate */
    private IScheduleManagement delegate;

    public ScheduleManagementDecorator(IScheduleManagement delegate) {
	super(delegate);
	this.delegate = delegate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent#setTenant(
     * com.sitewhere .spi.user.ITenant)
     */
    @Override
    public void setTenant(ITenant tenant) {
	delegate.setTenant(tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent#getTenant()
     */
    @Override
    public ITenant getTenant() {
	return delegate.getTenant();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.scheduling.IScheduleManagement#createSchedule(com.
     * sitewhere.spi .scheduling.request.IScheduleCreateRequest)
     */
    @Override
    public ISchedule createSchedule(IScheduleCreateRequest request) throws SiteWhereException {
	return delegate.createSchedule(request);
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
	return delegate.updateSchedule(token, request);
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
	return delegate.getScheduleByToken(token);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.scheduling.IScheduleManagement#listSchedules(com.
     * sitewhere.spi .search.ISearchCriteria)
     */
    @Override
    public ISearchResults<ISchedule> listSchedules(ISearchCriteria criteria) throws SiteWhereException {
	return delegate.listSchedules(criteria);
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
	return delegate.deleteSchedule(token, force);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManagement#createScheduledJob(com.
     * sitewhere .spi.scheduling.request.IScheduledJobCreateRequest)
     */
    @Override
    public IScheduledJob createScheduledJob(IScheduledJobCreateRequest request) throws SiteWhereException {
	return delegate.createScheduledJob(request);
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
	return delegate.updateScheduledJob(token, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManagement#getScheduledJobByToken(
     * java.lang .String)
     */
    @Override
    public IScheduledJob getScheduledJobByToken(String token) throws SiteWhereException {
	return delegate.getScheduledJobByToken(token);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManagement#listScheduledJobs(com.
     * sitewhere .spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IScheduledJob> listScheduledJobs(ISearchCriteria criteria) throws SiteWhereException {
	return delegate.listScheduledJobs(criteria);
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
	return delegate.deleteScheduledJob(token, force);
    }
}