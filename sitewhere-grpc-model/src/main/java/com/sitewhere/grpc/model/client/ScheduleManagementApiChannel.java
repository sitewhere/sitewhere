/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.model.client;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.model.spi.ApiNotAvailableException;
import com.sitewhere.grpc.model.spi.client.IScheduleManagementApiChannel;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.request.IScheduleCreateRequest;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleConstraints;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Supports SiteWhere schedule management APIs on top of a
 * {@link ScheduleManagementGrpcChannel}.
 * 
 * @author Derek
 */
public class ScheduleManagementApiChannel extends ApiChannel<ScheduleManagementGrpcChannel>
	implements IScheduleManagementApiChannel {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Asset management GRPC channel */
    private ScheduleManagementGrpcChannel grpcChannel;

    public ScheduleManagementApiChannel(ScheduleManagementGrpcChannel grpcChannel) {
	this.grpcChannel = grpcChannel;
    }

    @Override
    public ISchedule createSchedule(IScheduleCreateRequest request) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISchedule updateSchedule(String token, IScheduleCreateRequest request) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISchedule getScheduleByToken(String token) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISearchResults<ISchedule> listSchedules(ISearchCriteria criteria) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISchedule deleteSchedule(String token, boolean force) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IScheduledJob createScheduledJob(IScheduledJobCreateRequest request) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IScheduledJob updateScheduledJob(String token, IScheduledJobCreateRequest request)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IScheduledJob getScheduledJobByToken(String token) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISearchResults<IScheduledJob> listScheduledJobs(ISearchCriteria criteria) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IScheduledJob deleteScheduledJob(String token, boolean force) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.ApiChannel#getGrpcChannel()
     */
    @Override
    public ScheduleManagementGrpcChannel getGrpcChannel() {
	return grpcChannel;
    }

    public void setGrpcChannel(ScheduleManagementGrpcChannel grpcChannel) {
	this.grpcChannel = grpcChannel;
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
}