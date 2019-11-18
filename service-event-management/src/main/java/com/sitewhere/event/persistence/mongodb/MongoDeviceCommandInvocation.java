/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.mongodb;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.spi.device.event.CommandInitiator;
import com.sitewhere.spi.device.event.CommandTarget;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;

/**
 * Used to load or save device command invocation data to MongoDB.
 */
public class MongoDeviceCommandInvocation implements MongoConverter<IDeviceCommandInvocation> {

    /** Property for initiator */
    public static final String PROP_INITIATOR = "init";

    /** Property for initiator id */
    public static final String PROP_INITIATOR_ID = "inid";

    /** Property for target */
    public static final String PROP_TARGET = "targ";

    /** Property for target id */
    public static final String PROP_TARGET_ID = "tgid";

    /** Property for command token */
    public static final String PROP_COMMAND_ID = "cmid";

    /** Property for parameter values */
    public static final String PROP_PARAMETER_VALUES = "pmvl";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IDeviceCommandInvocation source) {
	return MongoDeviceCommandInvocation.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IDeviceCommandInvocation convert(Document source) {
	return MongoDeviceCommandInvocation.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IDeviceCommandInvocation source, Document target) {
	MongoDeviceEvent.toDocument(source, target, false);

	target.append(PROP_INITIATOR, source.getInitiator().name());
	target.append(PROP_INITIATOR_ID, source.getInitiatorId());
	target.append(PROP_TARGET, source.getTarget().name());
	target.append(PROP_TARGET_ID, source.getTargetId());
	target.append(PROP_COMMAND_ID, source.getDeviceCommandId());

	Document params = new Document();
	for (String key : source.getParameterValues().keySet()) {
	    params.append(key, source.getParameterValues().get(key));
	}
	target.append(PROP_PARAMETER_VALUES, params);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, DeviceCommandInvocation target) {
	MongoDeviceEvent.fromDocument(source, target, false);

	String initiatorName = (String) source.get(PROP_INITIATOR);
	String initiatorId = (String) source.get(PROP_INITIATOR_ID);
	String targetName = (String) source.get(PROP_TARGET);
	String targetId = (String) source.get(PROP_TARGET_ID);
	UUID commandId = (UUID) source.get(PROP_COMMAND_ID);

	if (initiatorName != null) {
	    target.setInitiator(CommandInitiator.valueOf(initiatorName));
	}
	if (targetName != null) {
	    target.setTarget(CommandTarget.valueOf(targetName));
	}
	target.setInitiatorId(initiatorId);
	target.setTargetId(targetId);
	target.setDeviceCommandId(commandId);

	Map<String, String> params = new HashMap<String, String>();
	Document dbparams = (Document) source.get(PROP_PARAMETER_VALUES);
	if (dbparams != null) {
	    for (String key : dbparams.keySet()) {
		params.put(key, (String) dbparams.get(key));
	    }
	}
	target.setParameterValues(params);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IDeviceCommandInvocation source) {
	Document result = new Document();
	MongoDeviceCommandInvocation.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static DeviceCommandInvocation fromDocument(Document source) {
	DeviceCommandInvocation result = new DeviceCommandInvocation();
	MongoDeviceCommandInvocation.fromDocument(source, result);
	return result;
    }
}