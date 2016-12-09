/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.device;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.rest.model.device.event.DeviceStreamData;
import com.sitewhere.spi.device.event.IDeviceStreamData;

/**
 * Handles loading/saving {@link DeviceStreamData} objects to a MongoDB
 * datastore.
 * 
 * @author Derek
 */
public class MongoDeviceStreamData implements MongoConverter<IDeviceStreamData> {

    /** Property for parent stream id */
    public static final String PROP_STREAM_ID = "streamId";

    /** Property for sequence number */
    public static final String PROP_SEQUENCE_NUMBER = "sequenceNumber";

    /** Property for binary data */
    public static final String PROP_DATA = "data";

    /**
     * @param source
     * @return
     */
    @Override
    public BasicDBObject convert(IDeviceStreamData source) {
	return MongoDeviceStreamData.toDBObject(source, false);
    }

    /**
     * @param source
     * @return
     */
    @Override
    public IDeviceStreamData convert(DBObject source) {
	return MongoDeviceStreamData.fromDBObject(source, false);
    }

    /**
     * Copy information from SPI into Mongo DBObject.
     * 
     * @param source
     * @param target
     * @param isNested
     */
    public static void toDBObject(IDeviceStreamData source, BasicDBObject target, boolean isNested) {
	MongoDeviceEvent.toDBObject(source, target, isNested);

	target.append(PROP_STREAM_ID, source.getStreamId());
	target.append(PROP_SEQUENCE_NUMBER, source.getSequenceNumber());
	target.append(PROP_DATA, source.getData());
    }

    /**
     * Copy information from Mongo DBObject to model object.
     * 
     * @param source
     * @param target
     * @param isNested
     */
    public static void fromDBObject(DBObject source, DeviceStreamData target, boolean isNested) {
	MongoDeviceEvent.fromDBObject(source, target, isNested);

	String streamId = (String) source.get(PROP_STREAM_ID);
	Long sequenceNumber = (Long) source.get(PROP_SEQUENCE_NUMBER);
	byte[] data = (byte[]) source.get(PROP_DATA);

	target.setStreamId(streamId);
	target.setSequenceNumber(sequenceNumber);
	target.setData(data);
    }

    /**
     * Convert SPI object to Mongo DBObject.
     * 
     * @param source
     * @param isNested
     * @return
     */
    public static BasicDBObject toDBObject(IDeviceStreamData source, boolean isNested) {
	BasicDBObject result = new BasicDBObject();
	MongoDeviceStreamData.toDBObject(source, result, isNested);
	return result;
    }

    /**
     * Convert a DBObject into the SPI equivalent.
     * 
     * @param source
     * @param isNested
     * @return
     */
    public static DeviceStreamData fromDBObject(DBObject source, boolean isNested) {
	DeviceStreamData result = new DeviceStreamData();
	MongoDeviceStreamData.fromDBObject(source, result, isNested);
	return result;
    }
}