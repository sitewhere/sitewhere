/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration;

import com.sitewhere.configuration.model.AttributeNode;
import com.sitewhere.configuration.model.ConfigurationModel;
import com.sitewhere.configuration.model.ElementNode;
import com.sitewhere.configuration.model.ElementRoles;
import com.sitewhere.configuration.old.ITenantConfigurationParser;
import com.sitewhere.spi.microservice.configuration.model.AttributeType;

/**
 * Configuration model for asset management elements.
 * 
 * @author Derek
 */
public class AssetManagementModel extends ConfigurationModel {

    public AssetManagementModel() {
	addElement(createAssetManagement());
	addElement(createWso2IdentityAssetModuleElement());
    }

    /**
     * Create the container for asset management configuration.
     * 
     * @return
     */
    protected ElementNode createAssetManagement() {
	ElementNode.Builder builder = new ElementNode.Builder("Asset Management",
		ITenantConfigurationParser.Elements.AssetManagement.getLocalName(), "tag", ElementRoles.AssetManagment);
	builder.description("Configure asset management features.");
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
		"users", ElementRoles.AssetManagment_AssetModule);

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

    /**
     * Add common filesystem asset module attributes.
     * 
     * @param builder
     */
    protected void addCommonFilesystemAssetModuleAttributes(ElementNode.Builder builder) {
	builder.attribute((new AttributeNode.Builder("Module name", "moduleName", AttributeType.String)
		.description("Name shown for module in user interface.").build()));
	builder.attribute((new AttributeNode.Builder("Filename", "filename", AttributeType.String)
		.description("Name of XML file found in the configuration assets folder.").build()));
    }
}