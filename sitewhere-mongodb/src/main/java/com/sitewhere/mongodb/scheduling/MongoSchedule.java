/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.scheduling;

import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.scheduling.Schedule;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.TriggerType;

/**
 * Used to load or save schedule data to MongoDB.
 * 
 * @author dadams
 */
public class MongoSchedule implements MongoConverter<ISchedule> {

	/** Property for unique token */
	public static final String PROP_TOKEN = "token";

	/** Property for name */
	public static final String PROP_NAME = "name";

	/** Property for trigger type */
	public static final String PROP_TRIGGER_TYPE = "type";

	/** Property for trigger configuration */
	public static final String PROP_TRIGGER_CONFIGURATION = "config";

	/** Property for start date */
	public static final String PROP_START_DATE = "start";

	/** Property for end date */
	public static final String PROP_END_DATE = "end";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
	 */
	@Override
	public BasicDBObject convert(ISchedule source) {
		return MongoSchedule.toDBObject(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(com.mongodb.DBObject)
	 */
	@Override
	public ISchedule convert(DBObject source) {
		return MongoSchedule.fromDBObject(source);
	}

	/**
	 * Copy information from SPI into Mongo DBObject.
	 * 
	 * @param source
	 * @param target
	 */
	public static void toDBObject(ISchedule source, BasicDBObject target) {
		target.append(PROP_TOKEN, source.getToken());
		target.append(PROP_NAME, source.getName());
		target.append(PROP_START_DATE, source.getStartDate());
		target.append(PROP_END_DATE, source.getEndDate());
		target.append(PROP_TRIGGER_TYPE, source.getTriggerType().name());

		BasicDBObject config = new BasicDBObject();
		for (String key : source.getTriggerConfiguration().keySet()) {
			config.put(key, source.getTriggerConfiguration().get(key));
		}
		target.put(PROP_TRIGGER_CONFIGURATION, config);

		MongoSiteWhereEntity.toDBObject(source, target);
		MongoMetadataProvider.toDBObject(source, target);
	}

	/**
	 * Copy information from Mongo DBObject to model object.
	 * 
	 * @param source
	 * @param target
	 */
	public static void fromDBObject(DBObject source, Schedule target) {
		String token = (String) source.get(PROP_TOKEN);
		String name = (String) source.get(PROP_NAME);
		String type = (String) source.get(PROP_TRIGGER_TYPE);
		Date startDate = (Date) source.get(PROP_START_DATE);
		Date endDate = (Date) source.get(PROP_END_DATE);

		target.setToken(token);
		target.setName(name);
		target.setStartDate(startDate);
		target.setEndDate(endDate);
		target.setTriggerType(TriggerType.valueOf(type));

		DBObject config = (DBObject) source.get(PROP_TRIGGER_CONFIGURATION);
		if (config != null) {
			for (String key : config.keySet()) {
				target.getTriggerConfiguration().put(key, (String) config.get(key));
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
	public static BasicDBObject toDBObject(ISchedule source) {
		BasicDBObject result = new BasicDBObject();
		MongoSchedule.toDBObject(source, result);
		return result;
	}

	/**
	 * Convert a DBObject into the SPI equivalent.
	 * 
	 * @param source
	 * @return
	 */
	public static Schedule fromDBObject(DBObject source) {
		Schedule result = new Schedule();
		MongoSchedule.fromDBObject(source, result);
		return result;
	}
}