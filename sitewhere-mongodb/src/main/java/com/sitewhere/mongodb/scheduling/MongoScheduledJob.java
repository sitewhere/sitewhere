/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.scheduling;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
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

    /** Property for unique token */
    public static final String PROP_TOKEN = "token";

    /** Property for schedule token */
    public static final String PROP_SCHEDULE_TOKEN = "schedule";

    /** Property for job type */
    public static final String PROP_JOB_TYPE = "type";

    /** Property for job configuration */
    public static final String PROP_JOB_CONFIGURATION = "config";

    /** Property for job state */
    public static final String PROP_JOB_STATE = "state";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public BasicDBObject convert(IScheduledJob source) {
	return MongoScheduledJob.toDBObject(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(com.mongodb.DBObject)
     */
    @Override
    public IScheduledJob convert(DBObject source) {
	return MongoScheduledJob.fromDBObject(source);
    }

    /**
     * Copy information from SPI into Mongo DBObject.
     * 
     * @param source
     * @param target
     */
    public static void toDBObject(IScheduledJob source, BasicDBObject target) {
	target.append(PROP_TOKEN, source.getToken());
	target.append(PROP_SCHEDULE_TOKEN, source.getScheduleToken());
	target.append(PROP_JOB_TYPE, source.getJobType().name());
	target.append(PROP_JOB_STATE, source.getJobState().name());

	BasicDBObject config = new BasicDBObject();
	for (String key : source.getJobConfiguration().keySet()) {
	    config.put(key, source.getJobConfiguration().get(key));
	}
	target.put(PROP_JOB_CONFIGURATION, config);

	MongoSiteWhereEntity.toDBObject(source, target);
	MongoMetadataProvider.toDBObject(source, target);
    }

    /**
     * Copy information from Mongo DBObject to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDBObject(DBObject source, ScheduledJob target) {
	String token = (String) source.get(PROP_TOKEN);
	String scheduleToken = (String) source.get(PROP_SCHEDULE_TOKEN);
	String type = (String) source.get(PROP_JOB_TYPE);
	String state = (String) source.get(PROP_JOB_STATE);

	target.setToken(token);
	target.setScheduleToken(scheduleToken);
	target.setJobType(ScheduledJobType.valueOf(type));
	target.setJobState(ScheduledJobState.valueOf(state));

	DBObject config = (DBObject) source.get(PROP_JOB_CONFIGURATION);
	if (config != null) {
	    for (String key : config.keySet()) {
		target.getJobConfiguration().put(key, (String) config.get(key));
	    }
	}

	MongoSiteWhereEntity.fromDBObject(source, target);
	MongoMetadataProvider.fromDBObject(source, target);
    }

    /**
     * Convert SPI object to Mongo DBObject.
     * 
     * @param source
     * @return
     */
    public static BasicDBObject toDBObject(IScheduledJob source) {
	BasicDBObject result = new BasicDBObject();
	MongoScheduledJob.toDBObject(source, result);
	return result;
    }

    /**
     * Convert a DBObject into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static ScheduledJob fromDBObject(DBObject source) {
	ScheduledJob result = new ScheduledJob();
	MongoScheduledJob.fromDBObject(source, result);
	return result;
    }
}