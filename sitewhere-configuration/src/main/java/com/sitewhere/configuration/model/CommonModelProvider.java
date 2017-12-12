/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.model;

import com.sitewhere.rest.model.configuration.AttributeNode;
import com.sitewhere.rest.model.configuration.ElementNode;
import com.sitewhere.spi.microservice.configuration.model.AttributeType;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;

/**
 * Provides common shared configuration model roles and elements.
 * 
 * @author Derek
 */
public class CommonModelProvider extends ConfigurationModelProvider {

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getDefaultXmlNamespace()
     */
    @Override
    public String getDefaultXmlNamespace() {
	return "http://sitewhere.io/schema/sitewhere/microservice/common";
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getRootRole()
     */
    @Override
    public IConfigurationRoleProvider getRootRole() {
	return CommonModelRoles.SiteWhereCommon;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeElements()
     */
    @Override
    public void initializeElements() {
	addElement(createMongoDbDefaultDatastoreElement());
	addElement(createMongoDbAlternateDatastoreElement());
    }

    /**
     * Create MongoDB default datastore element.
     * 
     * @return
     */
    protected ElementNode createMongoDbDefaultDatastoreElement() {
	ElementNode.Builder builder = new ElementNode.Builder("MongoDB Default Datastore", "default-mongodb-datastore",
		"database", CommonModelRoleKeys.Datastore);

	builder.description("Use the default MongoDB datastore configuration specified for the instance.");

	return builder.build();
    }

    /**
     * Create MongoDB alternate datastore element.
     * 
     * @return
     */
    protected ElementNode createMongoDbAlternateDatastoreElement() {
	ElementNode.Builder builder = new ElementNode.Builder("MongoDB Alternate Datastore",
		"alternate-mongodb-datastore", "database", CommonModelRoleKeys.Datastore);

	builder.description("Use an alternate MongoDB datastore configuration specified for the instance.");

	builder.attribute((new AttributeNode.Builder("Configuration Id", "id", AttributeType.String)
		.description("Unique id for alternate datastore configuration").build()));

	return builder.build();
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeRoles()
     */
    @Override
    public void initializeRoles() {
	for (CommonModelRoles role : CommonModelRoles.values()) {
	    getRolesById().put(role.getRole().getKey().getId(), role.getRole());
	}
    }
}