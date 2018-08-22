/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.tenant.configuration;

import com.sitewhere.configuration.model.CommonDatastoreProvider;
import com.sitewhere.configuration.model.ConfigurationModelProvider;
import com.sitewhere.configuration.parser.ITenantManagementParser;
import com.sitewhere.rest.model.configuration.ElementNode;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;

/**
 * Configuration model for tenant management microservice.
 * 
 * @author Derek
 */
public class TenantManagementModelProvider extends ConfigurationModelProvider {

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getDefaultXmlNamespace()
     */
    @Override
    public String getDefaultXmlNamespace() {
	return "http://sitewhere.io/schema/sitewhere/microservice/tenant-management";
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getRootRole()
     */
    @Override
    public IConfigurationRoleProvider getRootRole() {
	return TenantManagementRoles.TenantManagement;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeElements()
     */
    @Override
    public void initializeElements() {
	addElement(createTenantManagementElement());
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeRoles()
     */
    @Override
    public void initializeRoles() {
	for (TenantManagementRoles role : TenantManagementRoles.values()) {
	    getRolesById().put(role.getRole().getKey().getId(), role.getRole());
	}
    }

    /*
     * @see com.sitewhere.configuration.model.ConfigurationModelProvider#
     * initializeDependencies()
     */
    @Override
    public void initializeDependencies() {
	getDependencies().add(new CommonDatastoreProvider());
    }

    /**
     * Create tenant management element.
     * 
     * @return
     */
    protected ElementNode createTenantManagementElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Tenant Management API", ITenantManagementParser.ROOT,
		"layer-group", TenantManagementRoleKeys.TenantManagement, this);

	builder.description("Handles tenant model operations including persistence.");

	return builder.build();
    }
}