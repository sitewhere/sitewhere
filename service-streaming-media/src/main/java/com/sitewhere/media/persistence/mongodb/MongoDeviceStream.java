/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.media.persistence.mongodb;

import java.util.UUID;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoPersistentEntity;
import com.sitewhere.rest.model.device.streaming.DeviceStream;
import com.sitewhere.spi.device.streaming.IDeviceStream;

/**
 * Handles loading/saving {@link DeviceStream} objects to a MongoDB datastore.
 */
public class MongoDeviceStream implements MongoConverter<IDeviceStream> {

    /** Property for parent assignment id */
    public static final String PROP_ASSIGNMENT_ID = "asid";

    /** Property for content type */
    public static final String PROP_CONTENT_TYPE = "ctyp";

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
	target.append(PROP_ASSIGNMENT_ID, source.getAssignmentId());
	target.append(PROP_CONTENT_TYPE, source.getContentType());

	MongoPersistentEntity.toDocument(source, target);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, DeviceStream target) {
	UUID assignmentId = (UUID) source.get(PROP_ASSIGNMENT_ID);
	String contentType = (String) source.get(PROP_CONTENT_TYPE);

	target.setAssignmentId(assignmentId);
	target.setContentType(contentType);

	MongoPersistentEntity.fromDocument(source, target);
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