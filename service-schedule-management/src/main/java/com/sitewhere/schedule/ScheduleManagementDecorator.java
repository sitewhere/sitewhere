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

import java.util.UUID;

import com.sitewhere.microservice.api.schedule.IScheduleManagement;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponentDecorator;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.request.IScheduleCreateRequest;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Wraps an schedule management implementation. Subclasses can implement only
 * the methods they need to override.
 */
public class ScheduleManagementDecorator extends TenantEngineLifecycleComponentDecorator<IScheduleManagement>
	implements IScheduleManagement {

    public ScheduleManagementDecorator(IScheduleManagement delegate) {
	super(delegate);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.schedule.IScheduleManagement#createSchedule(
     * com.sitewhere.spi.scheduling.request.IScheduleCreateRequest)
     */
    @Override
    public ISchedule createSchedule(IScheduleCreateRequest request) throws SiteWhereException {
	return getDelegate().createSchedule(request);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.schedule.IScheduleManagement#updateSchedule(
     * java.util.UUID, com.sitewhere.spi.scheduling.request.IScheduleCreateRequest)
     */
    @Override
    public ISchedule updateSchedule(UUID scheduleId, IScheduleCreateRequest request) throws SiteWhereException {
	return getDelegate().updateSchedule(scheduleId, request);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.schedule.IScheduleManagement#getSchedule(java.
     * util.UUID)
     */
    @Override
    public ISchedule getSchedule(UUID scheduleId) throws SiteWhereException {
	return getDelegate().getSchedule(scheduleId);
    }

    /*
     * @see com.sitewhere.microservice.api.schedule.IScheduleManagement#
     * getScheduleByToken(java.lang.String)
     */
    @Override
    public ISchedule getScheduleByToken(String token) throws SiteWhereException {
	return getDelegate().getScheduleByToken(token);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.schedule.IScheduleManagement#listSchedules(com
     * .sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<? extends ISchedule> listSchedules(ISearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listSchedules(criteria);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.schedule.IScheduleManagement#deleteSchedule(
     * java.util.UUID)
     */
    @Override
    public ISchedule deleteSchedule(UUID scheduleId) throws SiteWhereException {
	return getDelegate().deleteSchedule(scheduleId);
    }

    /*
     * @see com.sitewhere.microservice.api.schedule.IScheduleManagement#
     * createScheduledJob(com.sitewhere.spi.scheduling.request.
     * IScheduledJobCreateRequest)
     */
    @Override
    public IScheduledJob createScheduledJob(IScheduledJobCreateRequest request) throws SiteWhereException {
	return getDelegate().createScheduledJob(request);
    }

    /*
     * @see com.sitewhere.microservice.api.schedule.IScheduleManagement#
     * updateScheduledJob(java.util.UUID,
     * com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest)
     */
    @Override
    public IScheduledJob updateScheduledJob(UUID scheduledJobId, IScheduledJobCreateRequest request)
	    throws SiteWhereException {
	return getDelegate().updateScheduledJob(scheduledJobId, request);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.schedule.IScheduleManagement#getScheduledJob(
     * java.util.UUID)
     */
    @Override
    public IScheduledJob getScheduledJob(UUID scheduledJobId) throws SiteWhereException {
	return getDelegate().getScheduledJob(scheduledJobId);
    }

    /*
     * @see com.sitewhere.microservice.api.schedule.IScheduleManagement#
     * getScheduledJobByToken(java.lang.String)
     */
    @Override
    public IScheduledJob getScheduledJobByToken(String token) throws SiteWhereException {
	return getDelegate().getScheduledJobByToken(token);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.schedule.IScheduleManagement#listScheduledJobs
     * (com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<? extends IScheduledJob> listScheduledJobs(ISearchCriteria criteria)
	    throws SiteWhereException {
	return getDelegate().listScheduledJobs(criteria);
    }

    /*
     * @see com.sitewhere.microservice.api.schedule.IScheduleManagement#
     * deleteScheduledJob(java.util.UUID)
     */
    @Override
    public IScheduledJob deleteScheduledJob(UUID scheduledJobId) throws SiteWhereException {
	return getDelegate().deleteScheduledJob(scheduledJobId);
    }
}