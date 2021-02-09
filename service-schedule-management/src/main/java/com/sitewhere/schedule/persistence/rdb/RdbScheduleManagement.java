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
package com.sitewhere.schedule.persistence.rdb;

import java.util.List;
import java.util.UUID;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.sitewhere.microservice.api.schedule.IScheduleManagement;
import com.sitewhere.rdb.RdbTenantComponent;
import com.sitewhere.rdb.spi.IRdbEntityManagerProvider;
import com.sitewhere.rdb.spi.IRdbQueryProvider;
import com.sitewhere.rest.model.scheduling.Schedule;
import com.sitewhere.rest.model.scheduling.ScheduledJob;
import com.sitewhere.schedule.persistence.ScheduleManagementPersistence;
import com.sitewhere.schedule.persistence.rdb.entity.Queries;
import com.sitewhere.schedule.persistence.rdb.entity.RdbSchedule;
import com.sitewhere.schedule.persistence.rdb.entity.RdbScheduledJob;
import com.sitewhere.schedule.spi.microservice.IScheduleManagementTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.request.IScheduleCreateRequest;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Implementation of {@link IScheduleManagement} that stores data in RDB.
 */
public class RdbScheduleManagement extends RdbTenantComponent implements IScheduleManagement {

    /*
     * @see
     * com.sitewhere.microservice.api.schedule.IScheduleManagement#createSchedule(
     * com.sitewhere.spi.scheduling.request.IScheduleCreateRequest)
     */
    @Override
    public RdbSchedule createSchedule(IScheduleCreateRequest request) throws SiteWhereException {
	// Use common logic so all backend implementations work the same.
	Schedule schedule = ScheduleManagementPersistence.scheduleCreateLogic(request);
	RdbSchedule created = new RdbSchedule();
	RdbSchedule.copy(schedule, created);
	return getEntityManagerProvider().persist(created);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.schedule.IScheduleManagement#updateSchedule(
     * java.util.UUID, com.sitewhere.spi.scheduling.request.IScheduleCreateRequest)
     */
    @Override
    public RdbSchedule updateSchedule(UUID scheduleId, IScheduleCreateRequest request) throws SiteWhereException {
	RdbSchedule existing = getEntityManagerProvider().findById(scheduleId, RdbSchedule.class);
	if (existing != null) {
	    Schedule updates = new Schedule();

	    // Use common update logic.
	    ScheduleManagementPersistence.scheduleUpdateLogic(updates, request);
	    RdbSchedule.copy(updates, existing);
	    return getEntityManagerProvider().merge(existing);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.microservice.api.schedule.IScheduleManagement#getSchedule(java.
     * util.UUID)
     */
    @Override
    public RdbSchedule getSchedule(UUID scheduleId) throws SiteWhereException {
	return getEntityManagerProvider().findById(scheduleId, RdbSchedule.class);
    }

    /*
     * @see com.sitewhere.microservice.api.schedule.IScheduleManagement#
     * getScheduleByToken(java.lang.String)
     */
    @Override
    public RdbSchedule getScheduleByToken(String token) throws SiteWhereException {
	Query query = getEntityManagerProvider().query(Queries.QUERY_SCHEDULE_BY_TOKEN);
	query.setParameter("token", token);
	return getEntityManagerProvider().findOne(query, RdbSchedule.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.schedule.IScheduleManagement#listSchedules(com
     * .sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<? extends ISchedule> listSchedules(ISearchCriteria criteria) throws SiteWhereException {
	return getEntityManagerProvider().findWithCriteria(criteria, new IRdbQueryProvider<RdbSchedule>() {

	    /*
	     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
	     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
	     */
	    @Override
	    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbSchedule> root)
		    throws SiteWhereException {
		// No search options available.
	    }

	    /*
	     * @see
	     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
	     * CriteriaBuilder, javax.persistence.criteria.Root,
	     * javax.persistence.criteria.CriteriaQuery)
	     */
	    @Override
	    public CriteriaQuery<RdbSchedule> addSort(CriteriaBuilder cb, Root<RdbSchedule> root,
		    CriteriaQuery<RdbSchedule> query) {
		return query.orderBy(cb.asc(root.get("name")));
	    }
	}, RdbSchedule.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.schedule.IScheduleManagement#deleteSchedule(
     * java.util.UUID)
     */
    @Override
    public ISchedule deleteSchedule(UUID scheduleId) throws SiteWhereException {
	return getEntityManagerProvider().remove(scheduleId, RdbSchedule.class);
    }

    /*
     * @see com.sitewhere.microservice.api.schedule.IScheduleManagement#
     * createScheduledJob(com.sitewhere.spi.scheduling.request.
     * IScheduledJobCreateRequest)
     */
    @Override
    public RdbScheduledJob createScheduledJob(IScheduledJobCreateRequest request) throws SiteWhereException {
	ISchedule schedule = getScheduleByToken(request.getScheduleToken());
	if (schedule == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidScheduleToken, ErrorLevel.ERROR);
	}

	// Use common logic so all backend implementations work the same.
	ScheduledJob job = ScheduleManagementPersistence.scheduledJobCreateLogic(schedule, request);
	RdbScheduledJob created = new RdbScheduledJob();
	RdbScheduledJob.copy(job, created);
	return getEntityManagerProvider().persist(created);
    }

    /*
     * @see com.sitewhere.microservice.api.schedule.IScheduleManagement#
     * updateScheduledJob(java.util.UUID,
     * com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest)
     */
    @Override
    public RdbScheduledJob updateScheduledJob(UUID scheduledJobId, IScheduledJobCreateRequest request)
	    throws SiteWhereException {
	RdbScheduledJob existing = getEntityManagerProvider().findById(scheduledJobId, RdbScheduledJob.class);
	if (existing != null) {
	    ScheduledJob updates = new ScheduledJob();

	    // Use common update logic.
	    ScheduleManagementPersistence.scheduledJobUpdateLogic(updates, request);
	    RdbScheduledJob.copy(updates, existing);
	    return getEntityManagerProvider().merge(existing);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.microservice.api.schedule.IScheduleManagement#getScheduledJob(
     * java.util.UUID)
     */
    @Override
    public RdbScheduledJob getScheduledJob(UUID scheduledJobId) throws SiteWhereException {
	return getEntityManagerProvider().findById(scheduledJobId, RdbScheduledJob.class);
    }

    /*
     * @see com.sitewhere.microservice.api.schedule.IScheduleManagement#
     * getScheduledJobByToken(java.lang.String)
     */
    @Override
    public RdbScheduledJob getScheduledJobByToken(String token) throws SiteWhereException {
	Query query = getEntityManagerProvider().query(Queries.QUERY_SCHEDULED_JOB_BY_TOKEN);
	query.setParameter("token", token);
	return getEntityManagerProvider().findOne(query, RdbScheduledJob.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.schedule.IScheduleManagement#listScheduledJobs
     * (com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<? extends IScheduledJob> listScheduledJobs(ISearchCriteria criteria)
	    throws SiteWhereException {
	return getEntityManagerProvider().findWithCriteria(criteria, new IRdbQueryProvider<RdbScheduledJob>() {

	    /*
	     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
	     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
	     */
	    @Override
	    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbScheduledJob> root)
		    throws SiteWhereException {
		// No search options available.
	    }

	    /*
	     * @see
	     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
	     * CriteriaBuilder, javax.persistence.criteria.Root,
	     * javax.persistence.criteria.CriteriaQuery)
	     */
	    @Override
	    public CriteriaQuery<RdbScheduledJob> addSort(CriteriaBuilder cb, Root<RdbScheduledJob> root,
		    CriteriaQuery<RdbScheduledJob> query) {
		return query.orderBy(cb.desc(root.get("createdDate")));
	    }
	}, RdbScheduledJob.class);
    }

    /*
     * @see com.sitewhere.microservice.api.schedule.IScheduleManagement#
     * deleteScheduledJob(java.util.UUID)
     */
    @Override
    public RdbScheduledJob deleteScheduledJob(UUID scheduledJobId) throws SiteWhereException {
	return getEntityManagerProvider().remove(scheduledJobId, RdbScheduledJob.class);
    }

    /*
     * @see com.sitewhere.rdb.spi.IRdbTenantComponent#getEntityManagerProvider()
     */
    @Override
    public IRdbEntityManagerProvider getEntityManagerProvider() {
	return ((IScheduleManagementTenantEngine) getTenantEngine()).getRdbEntityManagerProvider();
    }
}
