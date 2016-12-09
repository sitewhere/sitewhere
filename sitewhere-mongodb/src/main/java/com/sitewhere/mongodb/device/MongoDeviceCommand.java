/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.device;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.device.command.CommandParameter;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.ParameterType;

/**
 * Used to load or save device command data to MongoDB.
 * 
 * @author dadams
 */
public class MongoDeviceCommand implements MongoConverter<IDeviceCommand> {

    /** Property for token */
    public static final String PROP_TOKEN = "token";

    /** Property for specification token */
    public static final String PROP_SPEC_TOKEN = "specificationToken";

    /** Property for command namespace */
    public static final String PROP_NAMESPACE = "namespace";

    /** Property for command name */
    public static final String PROP_NAME = "name";

    /** Property for command description */
    public static final String PROP_DESCRIPTION = "description";

    /** Property for command parameters list */
    public static final String PROP_PARAMETERS = "parameters";

    /** Property for command parameter name */
    public static final String PROP_PARAM_NAME = "name";

    /** Property for command parameter type */
    public static final String PROP_PARAM_TYPE = "type";

    /** Property for command parameter required indicator */
    public static final String PROP_PARAM_REQUIRED = "required";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public BasicDBObject convert(IDeviceCommand source) {
	return MongoDeviceCommand.toDBObject(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(com.mongodb.DBObject)
     */
    @Override
    public IDeviceCommand convert(DBObject source) {
	return MongoDeviceCommand.fromDBObject(source);
    }

    /**
     * Copy information from SPI into Mongo DBObject.
     * 
     * @param source
     * @param target
     */
    public static void toDBObject(IDeviceCommand source, BasicDBObject target) {
	target.append(PROP_TOKEN, source.getToken());
	target.append(PROP_SPEC_TOKEN, source.getSpecificationToken());
	target.append(PROP_NAMESPACE, source.getNamespace());
	target.append(PROP_NAME, source.getName());
	target.append(PROP_DESCRIPTION, source.getDescription());

	// Create parameters list.
	List<DBObject> params = new ArrayList<DBObject>();
	for (ICommandParameter parameter : source.getParameters()) {
	    BasicDBObject dbparam = new BasicDBObject();
	    dbparam.append(PROP_PARAM_NAME, parameter.getName());
	    dbparam.append(PROP_PARAM_TYPE, parameter.getType().name());
	    dbparam.append(PROP_PARAM_REQUIRED, parameter.isRequired());
	    params.add(dbparam);
	}
	target.append(PROP_PARAMETERS, params);

	MongoSiteWhereEntity.toDBObject(source, target);
	MongoMetadataProvider.toDBObject(source, target);
    }

    /**
     * Copy information from Mongo DBObject to model object.
     * 
     * @param source
     * @param target
     */
    @SuppressWarnings("unchecked")
    public static void fromDBObject(DBObject source, DeviceCommand target) {
	String token = (String) source.get(PROP_TOKEN);
	String specToken = (String) source.get(PROP_SPEC_TOKEN);
	String namespace = (String) source.get(PROP_NAMESPACE);
	String name = (String) source.get(PROP_NAME);
	String desc = (String) source.get(PROP_DESCRIPTION);

	target.setToken(token);
	target.setSpecificationToken(specToken);
	target.setNamespace(namespace);
	target.setName(name);
	target.setDescription(desc);

	List<DBObject> params = (List<DBObject>) source.get(PROP_PARAMETERS);
	if (params != null) {
	    for (DBObject param : params) {
		String pname = (String) param.get(PROP_PARAM_NAME);
		String ptype = (String) param.get(PROP_PARAM_TYPE);
		Boolean prequired = (Boolean) param.get(PROP_PARAM_REQUIRED);
		CommandParameter parameter = new CommandParameter();
		parameter.setName(pname);
		if (ptype != null) {
		    parameter.setType(ParameterType.valueOf(ptype));
		}
		if (prequired != null) {
		    parameter.setRequired(prequired);
		}
		target.getParameters().add(parameter);
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
    public static BasicDBObject toDBObject(IDeviceCommand source) {
	BasicDBObject result = new BasicDBObject();
	MongoDeviceCommand.toDBObject(source, result);
	return result;
    }

    /**
     * Convert a DBObject into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static DeviceCommand fromDBObject(DBObject source) {
	DeviceCommand result = new DeviceCommand();
	MongoDeviceCommand.fromDBObject(source, result);
	return result;
    }
}