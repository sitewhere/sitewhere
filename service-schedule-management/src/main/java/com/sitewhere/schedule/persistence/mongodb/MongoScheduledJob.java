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

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoPersistentEntity;
import com.sitewhere.rest.model.scheduling.ScheduledJob;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.ScheduledJobState;
import com.sitewhere.spi.scheduling.ScheduledJobType;

/**
 * Used to load or save scheduled job data to MongoDB.
 * 
 * @author dadams
 */
public class MongoScheduledJob implements MongoConverter<IScheduledJob> {

    /** Property for id */
    public static final String PROP_ID = "_id";

    /** Property for unique token */
    public static final String PROP_TOKEN = "tokn";

    /** Property for schedule token */
    public static final String PROP_SCHEDULE_TOKEN = "sctk";

    /** Property for job type */
    public static final String PROP_JOB_TYPE = "type";

    /** Property for job configuration */
    public static final String PROP_JOB_CONFIGURATION = "conf";

    /** Property for job state */
    public static final String PROP_JOB_STATE = "jbst";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IScheduledJob source) {
	return MongoScheduledJob.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IScheduledJob convert(Document source) {
	return MongoScheduledJob.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IScheduledJob source, Document target) {
	target.append(PROP_ID, source.getId());
	target.append(PROP_TOKEN, source.getToken());
	target.append(PROP_SCHEDULE_TOKEN, source.getScheduleToken());
	target.append(PROP_JOB_TYPE, source.getJobType().name());
	target.append(PROP_JOB_STATE, source.getJobState().name());

	Document config = new Document();
	for (String key : source.getJobConfiguration().keySet()) {
	    config.put(key, source.getJobConfiguration().get(key));
	}
	target.put(PROP_JOB_CONFIGURATION, config);

	MongoPersistentEntity.toDocument(source, target);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, ScheduledJob target) {
	UUID id = (UUID) source.get(PROP_ID);
	String token = (String) source.get(PROP_TOKEN);
	String scheduleToken = (String) source.get(PROP_SCHEDULE_TOKEN);
	String type = (String) source.get(PROP_JOB_TYPE);
	String state = (String) source.get(PROP_JOB_STATE);

	target.setId(id);
	target.setToken(token);
	target.setScheduleToken(scheduleToken);
	target.setJobType(ScheduledJobType.valueOf(type));
	target.setJobState(ScheduledJobState.valueOf(state));

	Document config = (Document) source.get(PROP_JOB_CONFIGURATION);
	if (config != null) {
	    for (String key : config.keySet()) {
		target.getJobConfiguration().put(key, (String) config.get(key));
	    }
	}

	MongoPersistentEntity.fromDocument(source, target);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IScheduledJob source) {
	Document result = new Document();
	MongoScheduledJob.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static ScheduledJob fromDocument(Document source) {
	ScheduledJob result = new ScheduledJob();
	MongoScheduledJob.fromDocument(source, result);
	return result;
    }
}