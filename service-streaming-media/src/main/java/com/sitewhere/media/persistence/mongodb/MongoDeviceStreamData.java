/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.media.persistence.mongodb;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.rest.model.device.streaming.DeviceStreamData;
import com.sitewhere.spi.device.streaming.IDeviceStreamData;

/**
 * Handles loading/saving {@link DeviceStreamData} objects to a MongoDB
 * datastore.
 */
public class MongoDeviceStreamData implements MongoConverter<IDeviceStreamData> {

    /** Property for parent stream id */
    public static final String PROP_STREAM_ID = "stid";

    /** Property for sequence number */
    public static final String PROP_SEQUENCE_NUMBER = "seqn";

    /** Property for binary data */
    public static final String PROP_DATA = "data";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IDeviceStreamData source) {
	return MongoDeviceStreamData.toDocument(source, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IDeviceStreamData convert(Document source) {
	return MongoDeviceStreamData.fromDocument(source, false);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     * @param isNested
     */
    public static void toDocument(IDeviceStreamData source, Document target, boolean isNested) {
	// MongoDeviceEvent.toDocument(source, target, isNested);

	target.append(PROP_STREAM_ID, source.getStreamId());
	target.append(PROP_SEQUENCE_NUMBER, source.getSequenceNumber());
	target.append(PROP_DATA, source.getData());
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     * @param isNested
     */
    public static void fromDocument(Document source, DeviceStreamData target, boolean isNested) {
	// MongoDeviceEvent.fromDocument(source, target, isNested);

	String streamId = (String) source.get(PROP_STREAM_ID);
	Long sequenceNumber = (Long) source.get(PROP_SEQUENCE_NUMBER);
	byte[] data = (byte[]) source.get(PROP_DATA);

	target.setStreamId(streamId);
	target.setSequenceNumber(sequenceNumber);
	target.setData(data);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @param isNested
     * @return
     */
    public static Document toDocument(IDeviceStreamData source, boolean isNested) {
	Document result = new Document();
	MongoDeviceStreamData.toDocument(source, result, isNested);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @param isNested
     * @return
     */
    public static DeviceStreamData fromDocument(Document source, boolean isNested) {
	DeviceStreamData result = new DeviceStreamData();
	MongoDeviceStreamData.fromDocument(source, result, isNested);
	return result;
    }
}