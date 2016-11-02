/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.scheduling;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoTimeoutException;
import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.mongodb.IScheduleManagementMongoClient;
import com.sitewhere.mongodb.MongoPersistence;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.scheduling.Schedule;
import com.sitewhere.rest.model.scheduling.ScheduledJob;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
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
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Schedule management implementation that uses MongoDB for persistence.
 * 
 * @author dadams
 */
public class MongoScheduleManagement extends TenantLifecycleComponent implements IScheduleManagement {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Injected with global SiteWhere Mongo client */
    private IScheduleManagementMongoClient mongoClient;

    public MongoScheduleManagement() {
	super(LifecycleComponentType.DataStore);
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
	// Ensure that collection indexes exist.
	ensureIndexes();
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
     * Ensure that expected collection indexes exist.
     * 
     * @throws SiteWhereException
     */
    protected void ensureIndexes() throws SiteWhereException {
	getMongoClient().getSchedulesCollection(getTenant()).createIndex(new BasicDBObject(MongoSchedule.PROP_TOKEN, 1),
		new BasicDBObject("unique", true));
	getMongoClient().getScheduledJobsCollection(getTenant())
		.createIndex(new BasicDBObject(MongoScheduledJob.PROP_TOKEN, 1), new BasicDBObject("unique", true));
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
	Schedule schedule = SiteWherePersistence.scheduleCreateLogic(request, uuid);

	DBCollection schedules = getMongoClient().getSchedulesCollection(getTenant());
	DBObject created = MongoSchedule.toDBObject(schedule);
	MongoPersistence.insert(schedules, created, ErrorCode.DuplicateScheduleToken);

	return MongoSchedule.fromDBObject(created);
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
	DBObject match = assertSchedule(token);
	Schedule schedule = MongoSchedule.fromDBObject(match);

	// Use common update logic so that backend implemetations act the same
	// way.
	SiteWherePersistence.scheduleUpdateLogic(schedule, request);
	DBObject updated = MongoSchedule.toDBObject(schedule);

	BasicDBObject query = new BasicDBObject(MongoSchedule.PROP_TOKEN, token);
	DBCollection schedules = getMongoClient().getSchedulesCollection(getTenant());
	MongoPersistence.update(schedules, query, updated);

	return MongoSchedule.fromDBObject(updated);
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
	DBObject schedule = getScheduleDBObjectByToken(token);
	if (schedule != null) {
	    return MongoSchedule.fromDBObject(schedule);
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.scheduling.IScheduleManagement#listSchedules(com.
     * sitewhere.spi .search.ISearchCriteria)
     */
    @Override
    public ISearchResults<ISchedule> listSchedules(ISearchCriteria criteria) throws SiteWhereException {
	DBCollection schedules = getMongoClient().getSchedulesCollection(getTenant());
	DBObject dbCriteria = new BasicDBObject();
	MongoSiteWhereEntity.setDeleted(dbCriteria, false);
	BasicDBObject sort = new BasicDBObject(MongoSiteWhereEntity.PROP_CREATED_DATE, -1);
	return MongoPersistence.search(ISchedule.class, schedules, dbCriteria, sort, criteria);
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
	DBObject existing = assertSchedule(token);
	DBCollection schedules = getMongoClient().getSchedulesCollection(getTenant());
	if (force) {
	    MongoPersistence.delete(schedules, existing);
	    return MongoSchedule.fromDBObject(existing);
	} else {
	    MongoSiteWhereEntity.setDeleted(existing, true);
	    BasicDBObject query = new BasicDBObject(MongoSchedule.PROP_TOKEN, token);
	    MongoPersistence.update(schedules, query, existing);
	    return MongoSchedule.fromDBObject(existing);
	}
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
	String uuid = null;
	if (request.getToken() != null) {
	    uuid = request.getToken();
	} else {
	    uuid = UUID.randomUUID().toString();
	}

	// Use common logic so all backend implementations work the same.
	ScheduledJob job = SiteWherePersistence.scheduledJobCreateLogic(request, uuid);

	DBCollection jobs = getMongoClient().getScheduledJobsCollection(getTenant());
	DBObject created = MongoScheduledJob.toDBObject(job);
	MongoPersistence.insert(jobs, created, ErrorCode.DuplicateScheduledJobToken);

	return MongoScheduledJob.fromDBObject(created);
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
	DBObject match = assertScheduledJob(token);
	ScheduledJob job = MongoScheduledJob.fromDBObject(match);

	// Use common update logic so that backend implemetations act the same
	// way.
	SiteWherePersistence.scheduledJobUpdateLogic(job, request);
	DBObject updated = MongoScheduledJob.toDBObject(job);

	BasicDBObject query = new BasicDBObject(MongoScheduledJob.PROP_TOKEN, token);
	DBCollection jobs = getMongoClient().getScheduledJobsCollection(getTenant());
	MongoPersistence.update(jobs, query, updated);

	return MongoScheduledJob.fromDBObject(updated);
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
	DBObject job = getScheduledJobDBObjectByToken(token);
	if (job != null) {
	    return MongoScheduledJob.fromDBObject(job);
	}
	return null;
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
	DBCollection jobs = getMongoClient().getScheduledJobsCollection(getTenant());
	DBObject dbCriteria = new BasicDBObject();
	MongoSiteWhereEntity.setDeleted(dbCriteria, false);
	BasicDBObject sort = new BasicDBObject(MongoSiteWhereEntity.PROP_CREATED_DATE, -1);
	return MongoPersistence.search(IScheduledJob.class, jobs, dbCriteria, sort, criteria);
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
	DBObject existing = assertScheduledJob(token);
	DBCollection jobs = getMongoClient().getScheduledJobsCollection(getTenant());
	if (force) {
	    MongoPersistence.delete(jobs, existing);
	    return MongoScheduledJob.fromDBObject(existing);
	} else {
	    MongoSiteWhereEntity.setDeleted(existing, true);
	    BasicDBObject query = new BasicDBObject(MongoScheduledJob.PROP_TOKEN, token);
	    MongoPersistence.update(jobs, query, existing);
	    return MongoScheduledJob.fromDBObject(existing);
	}
    }

    /**
     * Return the {@link DBObject} for the schedule with the given token. Throws
     * an exception if the token is not valid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected DBObject assertSchedule(String token) throws SiteWhereException {
	DBObject match = getScheduleDBObjectByToken(token);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidScheduleToken, ErrorLevel.ERROR);
	}
	return match;
    }

    /**
     * Returns the {@link DBObject} for the schedule with the given token.
     * Returns null if not found.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected DBObject getScheduleDBObjectByToken(String token) throws SiteWhereException {
	try {
	    DBCollection collection = getMongoClient().getSchedulesCollection(getTenant());
	    BasicDBObject query = new BasicDBObject(MongoSchedule.PROP_TOKEN, token);
	    DBObject result = collection.findOne(query);
	    return result;
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
    }

    /**
     * Return the {@link DBObject} for the scheduled job with the given token.
     * Throws an exception if the token is not valid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected DBObject assertScheduledJob(String token) throws SiteWhereException {
	DBObject match = getScheduledJobDBObjectByToken(token);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidScheduledJobToken, ErrorLevel.ERROR);
	}
	return match;
    }

    /**
     * Returns the {@link DBObject} for the scheduled job with the given token.
     * Returns null if not found.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected DBObject getScheduledJobDBObjectByToken(String token) throws SiteWhereException {
	try {
	    DBCollection collection = getMongoClient().getScheduledJobsCollection(getTenant());
	    BasicDBObject query = new BasicDBObject(MongoSchedule.PROP_TOKEN, token);
	    DBObject result = collection.findOne(query);
	    return result;
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
    }

    public IScheduleManagementMongoClient getMongoClient() {
	return mongoClient;
    }

    public void setMongoClient(IScheduleManagementMongoClient mongoClient) {
	this.mongoClient = mongoClient;
    }
}