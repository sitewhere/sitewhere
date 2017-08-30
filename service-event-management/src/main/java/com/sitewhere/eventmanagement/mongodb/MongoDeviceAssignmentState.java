/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.eventmanagement.mongodb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.rest.model.device.DeviceAssignmentState;
import com.sitewhere.spi.device.IDeviceAssignmentState;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceMeasurement;

/**
 * Used to load or save assignment state to MongoDB.
 * 
 * @author dadams
 */
public class MongoDeviceAssignmentState implements MongoConverter<IDeviceAssignmentState> {

    /** Property for last interaction date */
    public static final String PROP_LAST_INTERACTION_DATE = "lastInteractionDate";

    /** Property for presence missing date */
    public static final String PROP_PRESENCE_MISSING_DATE = "presenceMissingDate";

    /** Property for last location */
    public static final String PROP_LAST_LOCATION = "lastLocation";

    /** Property for latest measurements */
    public static final String PROP_LATEST_MEASUREMENTS = "latestMeasurements";

    /** Property for latest measurements */
    public static final String PROP_LATEST_ALERTS = "latestAlerts";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IDeviceAssignmentState source) {
	return MongoDeviceAssignmentState.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IDeviceAssignmentState convert(Document source) {
	return MongoDeviceAssignmentState.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IDeviceAssignmentState source, Document target) {
	if (source.getLastInteractionDate() != null) {
	    target.append(PROP_LAST_INTERACTION_DATE, source.getLastInteractionDate());
	}

	if (source.getPresenceMissingDate() != null) {
	    target.append(PROP_PRESENCE_MISSING_DATE, source.getPresenceMissingDate());
	}

	if (source.getLastLocation() != null) {
	    target.append(PROP_LAST_LOCATION, MongoDeviceLocation.toDocument(source.getLastLocation(), true));
	}

	if (!source.getLatestMeasurements().isEmpty()) {
	    List<Document> measurements = new ArrayList<Document>();
	    for (IDeviceMeasurement sm : source.getLatestMeasurements()) {
		measurements.add(MongoDeviceMeasurement.toDocument(sm, true));
	    }
	    target.append(PROP_LATEST_MEASUREMENTS, measurements);
	}

	if (!source.getLatestAlerts().isEmpty()) {
	    List<Document> alerts = new ArrayList<Document>();
	    for (IDeviceAlert sa : source.getLatestAlerts()) {
		alerts.add(MongoDeviceAlert.toDocument(sa, true));
	    }
	    target.append(PROP_LATEST_ALERTS, alerts);
	}
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    @SuppressWarnings("unchecked")
    public static void fromDocument(Document source, DeviceAssignmentState target) {
	target.setLastInteractionDate((Date) source.get(PROP_LAST_INTERACTION_DATE));
	target.setPresenceMissingDate((Date) source.get(PROP_PRESENCE_MISSING_DATE));
	Document lastLocation = (Document) source.get(PROP_LAST_LOCATION);
	if (lastLocation != null) {
	    target.setLastLocation(MongoDeviceLocation.fromDocument(lastLocation, true));
	}
	List<Document> latestMeasurements = (List<Document>) source.get(PROP_LATEST_MEASUREMENTS);
	if (latestMeasurements != null) {
	    for (Document sm : latestMeasurements) {
		target.getLatestMeasurements().add(MongoDeviceMeasurement.fromDocument(sm, true));
	    }
	}
	List<Document> latestAlerts = (List<Document>) source.get(PROP_LATEST_ALERTS);
	if (latestAlerts != null) {
	    for (Document sa : latestAlerts) {
		target.getLatestAlerts().add(MongoDeviceAlert.fromDocument(sa, true));
	    }
	}
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IDeviceAssignmentState source) {
	Document result = new Document();
	MongoDeviceAssignmentState.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static DeviceAssignmentState fromDocument(Document source) {
	DeviceAssignmentState result = new DeviceAssignmentState();
	MongoDeviceAssignmentState.fromDocument(source, result);
	return result;
    }
}