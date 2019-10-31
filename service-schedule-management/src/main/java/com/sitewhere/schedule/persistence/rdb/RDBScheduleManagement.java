/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule.persistence.rdb;


import com.sitewhere.rdb.RDBTenantComponent;
import com.sitewhere.rdb.entities.AreaType;
import com.sitewhere.rdb.entities.DeviceCommand;
import com.sitewhere.rdb.multitenancy.MultiTenantContext;
import com.sitewhere.rest.model.scheduling.Schedule;
import com.sitewhere.rest.model.scheduling.ScheduledJob;
import com.sitewhere.schedule.persistence.ScheduleManagementPersistence;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.request.IScheduleCreateRequest;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Schedule management implementation that uses Relational database for persistence.
 *
 * @author Luciano Baez
 */
public class RDBScheduleManagement extends RDBTenantComponent<ScheduleManagementRDBClient> implements IScheduleManagement {

    public RDBScheduleManagement() {
        super(LifecycleComponentType.DataStore);
    }

    @Override
    public ScheduleManagementRDBClient getRDBClient() throws SiteWhereException {
        String tenantId = this.getTenantEngine().getTenant().getId().toString();
        MultiTenantContext.setTenantId(tenantId);
        return dbClient;
    }

    /** Injected with global SiteWhere relational database client */
    private ScheduleManagementRDBClient dbClient;

    public ScheduleManagementRDBClient getDbClient() {
        return dbClient;
    }

    public void setDbClient(ScheduleManagementRDBClient dbClient) {
        this.dbClient = dbClient;
    }


    /*
     * (non-Javadoc)
     *
     * @see com.sitewhere.spi.scheduling.IScheduleManagement#createSchedule(com.
     * sitewhere.spi .scheduling.request.IScheduleCreateRequest)
     */
    @Override
    public ISchedule createSchedule(IScheduleCreateRequest request) throws SiteWhereException {
        String uuid = null;
        if (request.getToken() != null) {
            uuid = request.getToken();
        } else {
            uuid = UUID.randomUUID().toString();
        }
        // Use common logic so all backend implementations work the same.
        Schedule schedule = ScheduleManagementPersistence.scheduleCreateLogic(request, uuid);

        com.sitewhere.rdb.entities.Schedule created = new com.sitewhere.rdb.entities.Schedule();
        BeanUtils.copyProperties(schedule, created);

        created = getRDBClient().getDbManager().getScheduleRepository().save(created);
        return created;
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
        Optional<com.sitewhere.rdb.entities.Schedule> opt = getRDBClient().getDbManager().getScheduleRepository().findByToken(token);
        com.sitewhere.rdb.entities.Schedule updated = null;
        if (opt.isPresent()) {
            updated = opt.get();
            Schedule schedule = new Schedule();
            schedule.setId(updated.getId());
            ScheduleManagementPersistence.scheduleUpdateLogic(schedule, request);
            BeanUtils.copyProperties(schedule, updated);
            updated = getRDBClient().getDbManager().getScheduleRepository().save(updated);
        }
        return updated;
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
        Optional<com.sitewhere.rdb.entities.Schedule> opt = getRDBClient().getDbManager().getScheduleRepository().findByToken(token);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public ISearchResults<ISchedule> listSchedules(ISearchCriteria criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.ASC,"createdDate");
        Specification<com.sitewhere.rdb.entities.Schedule> specification = new Specification<com.sitewhere.rdb.entities.Schedule>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.Schedule> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return null;
            }
        };

        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.Schedule> result = getRDBClient().getDbManager().getScheduleRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result, result.size());
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.Schedule> page = getRDBClient().getDbManager().getScheduleRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent(), page.getTotalElements());
        }
    }

    @Override
    public ISchedule deleteSchedule(String token) throws SiteWhereException {

        Optional<com.sitewhere.rdb.entities.Schedule> opt = getRDBClient().getDbManager().getScheduleRepository().findByToken(token);
        if(!opt.isPresent()) {
            throw new SiteWhereSystemException(ErrorCode.InvalidScheduleToken, ErrorLevel.ERROR);
        }

        com.sitewhere.rdb.entities.Schedule deleted = opt.get();
        getRDBClient().getDbManager().getScheduleRepository().delete(deleted);
        return deleted;
    }

    @Override
    public IScheduledJob createScheduledJob(IScheduledJobCreateRequest request) throws SiteWhereException {
        String uuid = null;
        if (request.getToken() != null) {
            uuid = request.getToken();
        } else {
            uuid = UUID.randomUUID().toString();
        }

        // Use common logic so all backend implementations work the same.
        ScheduledJob job = ScheduleManagementPersistence.scheduledJobCreateLogic(request, uuid);
        com.sitewhere.rdb.entities.ScheduledJob created = new com.sitewhere.rdb.entities.ScheduledJob();
        BeanUtils.copyProperties(job, created);

        created = getRDBClient().getDbManager().getScheduledJobRepository().save(created);
        return created;
    }

    @Override
    public IScheduledJob updateScheduledJob(String token, IScheduledJobCreateRequest request) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.ScheduledJob> opt = getRDBClient().getDbManager().getScheduledJobRepository().findByToken(token);
        com.sitewhere.rdb.entities.ScheduledJob updated = null;
        if (opt.isPresent()) {
            updated = opt.get();
            ScheduledJob scheduledJob = new ScheduledJob();
            scheduledJob.setId(updated.getId());
            ScheduleManagementPersistence.scheduledJobUpdateLogic(scheduledJob, request);
            BeanUtils.copyProperties(scheduledJob, updated);
            updated = getRDBClient().getDbManager().getScheduledJobRepository().save(updated);
        }
        return updated;
    }

    @Override
    public IScheduledJob getScheduledJobByToken(String token) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.ScheduledJob> opt = getRDBClient().getDbManager().getScheduledJobRepository().findByToken(token);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public ISearchResults<IScheduledJob> listScheduledJobs(ISearchCriteria criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.ASC,"createdDate");
        Specification<com.sitewhere.rdb.entities.ScheduledJob> specification = new Specification<com.sitewhere.rdb.entities.ScheduledJob>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.ScheduledJob> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return null;
            }
        };

        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.ScheduledJob> result = getRDBClient().getDbManager().getScheduledJobRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result, result.size());
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.ScheduledJob> page = getRDBClient().getDbManager().getScheduledJobRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent(), page.getTotalElements());
        }
    }

    @Override
    public IScheduledJob deleteScheduledJob(String token) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.ScheduledJob> opt = getRDBClient().getDbManager().getScheduledJobRepository().findByToken(token);
        if(!opt.isPresent()) {
            throw new SiteWhereSystemException(ErrorCode.InvalidScheduledJobToken, ErrorLevel.ERROR);
        }
        com.sitewhere.rdb.entities.ScheduledJob deleted = opt.get();
        getRDBClient().getDbManager().getScheduledJobRepository().delete(deleted);
        return deleted;
    }
}
