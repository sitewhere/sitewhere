/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.eventmanagement.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceMeasurements;

/**
 * Used to load or save device measurements data to MongoDB.
 * 
 * @author dadams
 */
public class MongoDeviceMeasurements implements MongoConverter<IDeviceMeasurements> {

    /** Element that holds measurements */
    public static final String PROP_MEASUREMENTS = "measurements";

    /** Attribute name for measurement name */
    public static final String PROP_NAME = "name";

    /** Attribute name for measurement value */
    public static final String PROP_VALUE = "value";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.dao.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IDeviceMeasurements source) {
	return MongoDeviceMeasurements.toDocument(source, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public DeviceMeasurements convert(Document source) {
	return MongoDeviceMeasurements.fromDocument(source, false);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IDeviceMeasurements source, Document target, boolean isNested) {
	MongoDeviceEvent.toDocument(source, target, isNested);

	// Save arbitrary measurements.
	List<Document> props = new ArrayList<Document>();
	for (String key : source.getMeasurements().keySet()) {
	    Document prop = new Document();
	    prop.put(PROP_NAME, key);
	    prop.put(PROP_VALUE, source.getMeasurement(key));
	    props.add(prop);
	}
	target.append(PROP_MEASUREMENTS, props);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     * @param isNested
     */
    @SuppressWarnings("unchecked")
    public static void fromDocument(Document source, DeviceMeasurements target, boolean isNested) {
	MongoDeviceEvent.fromDocument(source, target, isNested);

	// Load arbitrary measurements.
	List<Document> props = (List<Document>) source.get(PROP_MEASUREMENTS);
	if (props != null) {
	    for (Document prop : props) {
		String name = (String) prop.get(PROP_NAME);
		Double value = (Double) prop.get(PROP_VALUE);
		target.addOrReplaceMeasurement(name, value);
	    }
	}
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @param isNested
     * @return
     */
    public static Document toDocument(IDeviceMeasurements source, boolean isNested) {
	Document result = new Document();
	MongoDeviceMeasurements.toDocument(source, result, isNested);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @param isNested
     * @return
     */
    public static DeviceMeasurements fromDocument(Document source, boolean isNested) {
	DeviceMeasurements result = new DeviceMeasurements();
	MongoDeviceMeasurements.fromDocument(source, result, isNested);
	return result;
    }
}