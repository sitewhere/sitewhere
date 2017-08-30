/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicemanagement.mongodb;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.device.streaming.DeviceStream;
import com.sitewhere.spi.device.streaming.IDeviceStream;

/**
 * Handles loading/saving {@link DeviceStream} objects to a MongoDB datastore.
 * 
 * @author Derek
 */
public class MongoDeviceStream implements MongoConverter<IDeviceStream> {

    /** Property for parent assignment token */
    public static final String PROP_ASSIGNMENT_TOKEN = "assignmentToken";

    /** Property for stream id */
    public static final String PROP_STREAM_ID = "streamId";

    /** Property for content type */
    public static final String PROP_CONTENT_TYPE = "contentType";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IDeviceStream source) {
	return MongoDeviceStream.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IDeviceStream convert(Document source) {
	return MongoDeviceStream.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IDeviceStream source, Document target) {
	target.append(PROP_ASSIGNMENT_TOKEN, source.getAssignmentToken());
	target.append(PROP_STREAM_ID, source.getStreamId());
	target.append(PROP_CONTENT_TYPE, source.getContentType());

	MongoSiteWhereEntity.toDocument(source, target);
	MongoMetadataProvider.toDocument(source, target);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, DeviceStream target) {
	String assignmentToken = (String) source.get(PROP_ASSIGNMENT_TOKEN);
	String streamId = (String) source.get(PROP_STREAM_ID);
	String contentType = (String) source.get(PROP_CONTENT_TYPE);

	target.setAssignmentToken(assignmentToken);
	target.setStreamId(streamId);
	target.setContentType(contentType);

	MongoSiteWhereEntity.fromDocument(source, target);
	MongoMetadataProvider.fromDocument(source, target);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IDeviceStream source) {
	Document result = new Document();
	MongoDeviceStream.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static DeviceStream fromDocument(Document source) {
	DeviceStream result = new DeviceStream();
	MongoDeviceStream.fromDocument(source, result);
	return result;
    }
}