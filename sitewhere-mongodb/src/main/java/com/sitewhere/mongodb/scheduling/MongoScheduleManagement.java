/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.scheduling;

import java.util.UUID;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoTimeoutException;
import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.mongodb.IScheduleManagementMongoClient;
import com.sitewhere.mongodb.MongoPersistence;
import com.sitewhere.rest.model.scheduling.Schedule;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.scheduling.request.IScheduleCreateRequest;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Schedule management implementation that uses MongoDB for persistence.
 * 
 * @author dadams
 */
public class MongoScheduleManagement extends TenantLifecycleComponent implements IScheduleManagement {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(MongoScheduleManagement.class);

	/** Injected with global SiteWhere Mongo client */
	private IScheduleManagementMongoClient mongoClient;

	public MongoScheduleManagement() {
		super(LifecycleComponentType.DataStore);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		// Ensure that collection indexes exist.
		ensureIndexes();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
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
		getMongoClient().getSchedulesCollection(getTenant()).createIndex(
				new BasicDBObject(MongoSchedule.PROP_TOKEN, 1), new BasicDBObject("unique", true));
		getMongoClient().getScheduledJobsCollection(getTenant()).createIndex(
				new BasicDBObject(MongoScheduledJob.PROP_TOKEN, 1), new BasicDBObject("unique", true));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.scheduling.IScheduleManagement#createSchedule(com.sitewhere.spi
	 * .scheduling.request.IScheduleCreateRequest)
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
		MongoPersistence.insert(schedules, created);

		return MongoSchedule.fromDBObject(created);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.scheduling.IScheduleManagement#updateSchedule(java.lang.String,
	 * com.sitewhere.spi.scheduling.request.IScheduleCreateRequest)
	 */
	@Override
	public ISchedule updateSchedule(String token, IScheduleCreateRequest request) throws SiteWhereException {
		DBObject match = assertSchedule(token);
		Schedule schedule = MongoSchedule.fromDBObject(match);

		// Use common update logic so that backend implemetations act the same way.
		SiteWherePersistence.scheduleUpdateLogic(schedule, request);
		DBObject updated = MongoSchedule.toDBObject(schedule);

		BasicDBObject query = new BasicDBObject(MongoSchedule.PROP_TOKEN, token);
		DBCollection schedules = getMongoClient().getSchedulesCollection(getTenant());
		MongoPersistence.update(schedules, query, updated);

		return MongoSchedule.fromDBObject(updated);
	}

	/**
	 * Return the {@link DBObject} for the schedule with the given token. Throws an
	 * exception if the token is not valid.
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
	 * Returns the {@link DBObject} for the schedule with the given token. Returns null if
	 * not found.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject getScheduleDBObjectByToken(String token) throws SiteWhereException {
		try {
			DBCollection ops = getMongoClient().getSchedulesCollection(getTenant());
			BasicDBObject query = new BasicDBObject(MongoSchedule.PROP_TOKEN, token);
			DBObject result = ops.findOne(query);
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