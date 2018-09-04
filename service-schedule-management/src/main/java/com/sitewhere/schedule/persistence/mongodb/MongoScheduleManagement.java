/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule.persistence.mongodb;

import java.util.UUID;

import org.bson.Document;

import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.sitewhere.mongodb.IMongoConverterLookup;
import com.sitewhere.mongodb.MongoPersistence;
import com.sitewhere.mongodb.common.MongoPersistentEntity;
import com.sitewhere.rest.model.scheduling.Schedule;
import com.sitewhere.rest.model.scheduling.ScheduledJob;
import com.sitewhere.schedule.persistence.ScheduleManagementPersistence;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
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
public class MongoScheduleManagement extends TenantEngineLifecycleComponent implements IScheduleManagement {

    /** Converter lookup */
    private static IMongoConverterLookup LOOKUP = new MongoConverters();

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

    /**
     * Ensure that expected collection indexes exist.
     * 
     * @throws SiteWhereException
     */
    protected void ensureIndexes() throws SiteWhereException {
	getMongoClient().getSchedulesCollection().createIndex(new Document(MongoSchedule.PROP_TOKEN, 1),
		new IndexOptions().unique(true));
	getMongoClient().getScheduledJobsCollection().createIndex(new Document(MongoScheduledJob.PROP_TOKEN, 1),
		new IndexOptions().unique(true));
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

	MongoCollection<Document> schedules = getMongoClient().getSchedulesCollection();
	Document created = MongoSchedule.toDocument(schedule);
	MongoPersistence.insert(schedules, created, ErrorCode.DuplicateScheduleToken);

	return MongoSchedule.fromDocument(created);
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
	Document match = assertSchedule(token);
	Schedule schedule = MongoSchedule.fromDocument(match);

	// Use common update logic.
	ScheduleManagementPersistence.scheduleUpdateLogic(schedule, request);
	Document updated = MongoSchedule.toDocument(schedule);

	Document query = new Document(MongoSchedule.PROP_TOKEN, token);
	MongoCollection<Document> schedules = getMongoClient().getSchedulesCollection();
	MongoPersistence.update(schedules, query, updated);

	return MongoSchedule.fromDocument(updated);
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
	Document schedule = getScheduleDocumentByToken(token);
	if (schedule != null) {
	    return MongoSchedule.fromDocument(schedule);
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
	MongoCollection<Document> schedules = getMongoClient().getSchedulesCollection();
	Document dbCriteria = new Document();
	Document sort = new Document(MongoPersistentEntity.PROP_CREATED_DATE, -1);
	return MongoPersistence.search(ISchedule.class, schedules, dbCriteria, sort, criteria, LOOKUP);
    }

    /*
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManagement#deleteSchedule(java.lang.
     * String)
     */
    @Override
    public ISchedule deleteSchedule(String token) throws SiteWhereException {
	Document existing = assertSchedule(token);
	MongoCollection<Document> schedules = getMongoClient().getSchedulesCollection();
	MongoPersistence.delete(schedules, existing);
	return MongoSchedule.fromDocument(existing);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.scheduling.IScheduleManagement#createScheduledJob(com.
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
	ScheduledJob job = ScheduleManagementPersistence.scheduledJobCreateLogic(request, uuid);

	MongoCollection<Document> jobs = getMongoClient().getScheduledJobsCollection();
	Document created = MongoScheduledJob.toDocument(job);
	MongoPersistence.insert(jobs, created, ErrorCode.DuplicateScheduledJobToken);

	return MongoScheduledJob.fromDocument(created);
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
	Document match = assertScheduledJob(token);
	ScheduledJob job = MongoScheduledJob.fromDocument(match);

	// Use common update logic.
	ScheduleManagementPersistence.scheduledJobUpdateLogic(job, request);
	Document updated = MongoScheduledJob.toDocument(job);

	Document query = new Document(MongoScheduledJob.PROP_TOKEN, token);
	MongoCollection<Document> jobs = getMongoClient().getScheduledJobsCollection();
	MongoPersistence.update(jobs, query, updated);

	return MongoScheduledJob.fromDocument(updated);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.scheduling.IScheduleManagement#getScheduledJobByToken(
     * java.lang .String)
     */
    @Override
    public IScheduledJob getScheduledJobByToken(String token) throws SiteWhereException {
	Document job = getScheduledJobDocumentByToken(token);
	if (job != null) {
	    return MongoScheduledJob.fromDocument(job);
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.scheduling.IScheduleManagement#listScheduledJobs(com.
     * sitewhere .spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IScheduledJob> listScheduledJobs(ISearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> jobs = getMongoClient().getScheduledJobsCollection();
	Document dbCriteria = new Document();
	Document sort = new Document(MongoPersistentEntity.PROP_CREATED_DATE, -1);
	return MongoPersistence.search(IScheduledJob.class, jobs, dbCriteria, sort, criteria, LOOKUP);
    }

    /*
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManagement#deleteScheduledJob(java.lang
     * .String)
     */
    @Override
    public IScheduledJob deleteScheduledJob(String token) throws SiteWhereException {
	Document existing = assertScheduledJob(token);
	MongoCollection<Document> jobs = getMongoClient().getScheduledJobsCollection();
	MongoPersistence.delete(jobs, existing);
	return MongoScheduledJob.fromDocument(existing);
    }

    /**
     * Return the {@link Document} for the schedule with the given token. Throws an
     * exception if the token is not valid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document assertSchedule(String token) throws SiteWhereException {
	Document match = getScheduleDocumentByToken(token);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidScheduleToken, ErrorLevel.ERROR);
	}
	return match;
    }

    /**
     * Returns the {@link Document} for the schedule with the given token. Returns
     * null if not found.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getScheduleDocumentByToken(String token) throws SiteWhereException {
	try {
	    MongoCollection<Document> collection = getMongoClient().getSchedulesCollection();
	    Document query = new Document(MongoSchedule.PROP_TOKEN, token);
	    return collection.find(query).first();
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
    }

    /**
     * Return the {@link Document} for the scheduled job with the given token.
     * Throws an exception if the token is not valid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document assertScheduledJob(String token) throws SiteWhereException {
	Document match = getScheduledJobDocumentByToken(token);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidScheduledJobToken, ErrorLevel.ERROR);
	}
	return match;
    }

    /**
     * Returns the {@link Document} for the scheduled job with the given token.
     * Returns null if not found.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getScheduledJobDocumentByToken(String token) throws SiteWhereException {
	try {
	    MongoCollection<Document> collection = getMongoClient().getScheduledJobsCollection();
	    Document query = new Document(MongoSchedule.PROP_TOKEN, token);
	    return collection.find(query).first();
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