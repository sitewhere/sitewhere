/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.configuration;

import com.sitewhere.configuration.model.CommonDatastoreProvider;
import com.sitewhere.configuration.model.ConfigurationModelProvider;
import com.sitewhere.configuration.parser.IAssetManagementParser;
import com.sitewhere.rest.model.configuration.AttributeNode;
import com.sitewhere.rest.model.configuration.ElementNode;
import com.sitewhere.spi.microservice.configuration.model.AttributeType;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;

/**
 * Configuration model provider for asset management microservice.
 * 
 * @author Derek
 */
public class AssetManagementModelProvider extends ConfigurationModelProvider {

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getDefaultXmlNamespace()
     */
    @Override
    public String getDefaultXmlNamespace() {
	return "http://sitewhere.io/schema/sitewhere/microservice/asset-management";
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getRootRole()
     */
    @Override
    public IConfigurationRoleProvider getRootRole() {
	return AssetManagementRoles.AssetManagement;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeElements()
     */
    @Override
    public void initializeElements() {
	addElement(createAssetManagement());
	addElement(createAssetModules());
	addElement(createWso2IdentityAssetModuleElement());
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeRoles()
     */
    @Override
    public void initializeRoles() {
	for (AssetManagementRoles role : AssetManagementRoles.values()) {
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
     * Create the container for asset management configuration.
     * 
     * @return
     */
    protected ElementNode createAssetManagement() {
	ElementNode.Builder builder = new ElementNode.Builder("Asset Management", IAssetManagementParser.ROOT, "tag",
		AssetManagementRoleKeys.AssetManagement, this);
	builder.description("Configure asset management features such as persistence and external modules.");
	return builder.build();
    }

    /**
     * Create the container for asset management configuration.
     * 
     * @return
     */
    protected ElementNode createAssetModules() {
	ElementNode.Builder builder = new ElementNode.Builder("Asset Modules",
		IAssetManagementParser.Elements.AssetModules.getLocalName(), "tag",
		AssetManagementRoleKeys.AssetModules, this);
	builder.description("Configure asset modules.");
	return builder.build();
    }

    /**
     * Add common asset module attributes.
     * 
     * @param builder
     */
    protected void addCommonAssetModuleAttributes(ElementNode.Builder builder) {
	builder.attribute((new AttributeNode.Builder("Module id", "moduleId", AttributeType.String)
		.description("Unique id used to reference the asset module").build()));
    }

    /**
     * Create element configuration for WSO2 Identity asset module.
     * 
     * @return
     */
    protected ElementNode createWso2IdentityAssetModuleElement() {
	ElementNode.Builder builder = new ElementNode.Builder("WSO2 Identity Asset Module",
		com.sitewhere.configuration.parser.IAssetManagementParser.IAssetModulesParser.Elements.Wso2IdentityAssetModule
			.getLocalName(),
		"users", AssetManagementRoleKeys.AssetModule, this);

	builder.description("Asset module that interacts with a WSO2 Identity Server instance "
		+ "to provide a list of person assets.");
	addCommonAssetModuleAttributes(builder);

	builder.attribute((new AttributeNode.Builder("SCIM users URL", "scimUsersUrl", AttributeType.String)
		.description("SCIM URL for accessing the list of users.")
		.defaultValue("https://localhost:9443/wso2/scim/Users").build()));
	builder.attribute((new AttributeNode.Builder("Username", "username", AttributeType.String)
		.description("Basic authentication username for web service calls.").defaultValue("admin").build()));
	builder.attribute((new AttributeNode.Builder("Password", "password", AttributeType.String)
		.description("Basic authentication password for web service calls.").defaultValue("admin").build()));
	builder.attribute(
		(new AttributeNode.Builder("Ignore bad certificate", "ignoreBadCertificate", AttributeType.Boolean)
			.description("Indicates whether an invalid SSL certificate on the server should be ignored. "
				+ "Do not enable in production systems.")
			.defaultValue("false").build()));

	return builder.build();
    }
}