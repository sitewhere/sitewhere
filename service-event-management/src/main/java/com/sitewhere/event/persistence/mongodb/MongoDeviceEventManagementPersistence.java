/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.mongodb;

import org.bson.Document;

import com.mongodb.MongoCommandException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoCollection;
import com.sitewhere.mongodb.MongoPersistence;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;

public class MongoDeviceEventManagementPersistence extends MongoPersistence {

    /**
     * Insert an event, taking into account whether the device management
     * implementation in configured for bulk operations.
     * 
     * @param collection
     * @param object
     * @param bulk
     * @param buffer
     * @throws SiteWhereException
     */
    public static void insertEvent(MongoCollection<Document> collection, Document object, boolean bulk,
	    IDeviceEventBuffer buffer) throws SiteWhereException {
	try {
	    if (bulk) {
		buffer.add(object);
	    } else {
		collection.insertOne(object);
	    }
	} catch (MongoCommandException e) {
	    throw new SiteWhereException("Error during MongoDB insert.", e);
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
    }

    /**
     * Marshal an {@link IDeviceEvent} into a {@link Document}.
     * 
     * @param event
     * @return
     * @throws SiteWhereException
     */
    public static Document marshalEvent(IDeviceEvent event) throws SiteWhereException {
	switch (event.getEventType()) {
	case Measurement: {
	    return MongoDeviceMeasurement.toDocument((IDeviceMeasurement) event, false);
	}
	case Location: {
	    return MongoDeviceLocation.toDocument((IDeviceLocation) event, false);
	}
	case Alert: {
	    return MongoDeviceAlert.toDocument((IDeviceAlert) event, false);
	}
	case CommandInvocation: {
	    return MongoDeviceCommandInvocation.toDocument((IDeviceCommandInvocation) event);
	}
	case CommandResponse: {
	    return MongoDeviceCommandResponse.toDocument((IDeviceCommandResponse) event);
	}
	case StateChange: {
	    return MongoDeviceStateChange.toDocument((IDeviceStateChange) event);
	}
	default: {
	    throw new SiteWhereException("Event type not handled: " + event.getEventType());
	}
	}
    }

    /**
     * Given a {@link Document} that contains event information, unmarhal it to the
     * correct type.
     * 
     * @param found
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceEvent unmarshalEvent(Document found) throws SiteWhereException {
	String type = (String) found.get(MongoDeviceEvent.PROP_EVENT_TYPE);
	if (type == null) {
	    throw new SiteWhereException("Event matched but did not contain event type field.");
	}
	DeviceEventType eventType = DeviceEventType.valueOf(type);
	if (eventType == null) {
	    throw new SiteWhereException("Event type not recognized: " + type);
	}

	switch (eventType) {
	case Measurement: {
	    return MongoDeviceMeasurement.fromDocument(found, false);
	}
	case Location: {
	    return MongoDeviceLocation.fromDocument(found, false);
	}
	case Alert: {
	    return MongoDeviceAlert.fromDocument(found, false);
	}
	case CommandInvocation: {
	    return MongoDeviceCommandInvocation.fromDocument(found);
	}
	case CommandResponse: {
	    return MongoDeviceCommandResponse.fromDocument(found);
	}
	case StateChange: {
	    return MongoDeviceStateChange.fromDocument(found);
	}
	default: {
	    throw new SiteWhereException("Event type not handled: " + type);
	}
	}
    }
}
