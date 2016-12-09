/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.configuration;

import com.sitewhere.spring.handler.AssetManagementParser;
import com.sitewhere.spring.handler.TenantConfigurationParser;
import com.sitewhere.web.configuration.model.AttributeNode;
import com.sitewhere.web.configuration.model.AttributeType;
import com.sitewhere.web.configuration.model.ConfigurationModel;
import com.sitewhere.web.configuration.model.ElementNode;
import com.sitewhere.web.configuration.model.ElementRole;

/**
 * Configuration model for asset management elements.
 * 
 * @author Derek
 */
public class AssetManagementModel extends ConfigurationModel {

    public AssetManagementModel() {
	addElement(createAssetManagement());
	addElement(createWso2IdentityAssetModuleElement());
	addElement(createXmlDeviceAssetModuleElement());
	addElement(createXmlHardwareAssetModuleElement());
	addElement(createXmlPersonAssetModuleElement());
	addElement(createXmlLocationAssetModuleElement());
    }

    /**
     * Create the container for asset management configuration.
     * 
     * @return
     */
    protected ElementNode createAssetManagement() {
	ElementNode.Builder builder = new ElementNode.Builder("Asset Management",
		TenantConfigurationParser.Elements.AssetManagement.getLocalName(), "tag", ElementRole.AssetManagment);
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
		AssetManagementParser.Elements.Wso2IdentityAssetModule.getLocalName(), "users",
		ElementRole.AssetManagment_AssetModule);

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

    /**
     * Create element configuration for XML device asset module.
     * 
     * @return
     */
    protected ElementNode createXmlDeviceAssetModuleElement() {
	ElementNode.Builder builder = new ElementNode.Builder("XML Device Asset Module",
		AssetManagementParser.Elements.FilesystemDeviceAssetModule.getLocalName(), "tablet",
		ElementRole.AssetManagment_AssetModule);

	builder.description("List of device assets stored in XML format on the local filesystem.");
	addCommonAssetModuleAttributes(builder);
	addCommonFilesystemAssetModuleAttributes(builder);

	return builder.build();
    }

    /**
     * Create element configuration for XML hardware asset module.
     * 
     * @return
     */
    protected ElementNode createXmlHardwareAssetModuleElement() {
	ElementNode.Builder builder = new ElementNode.Builder("XML Hardware Asset Module",
		AssetManagementParser.Elements.FilesystemHardwareAssetModule.getLocalName(), "laptop",
		ElementRole.AssetManagment_AssetModule);

	builder.description("List of hardware assets stored in XML format on the local filesystem.");
	addCommonAssetModuleAttributes(builder);
	addCommonFilesystemAssetModuleAttributes(builder);

	return builder.build();
    }

    /**
     * Create element configuration for XML person asset module.
     * 
     * @return
     */
    protected ElementNode createXmlPersonAssetModuleElement() {
	ElementNode.Builder builder = new ElementNode.Builder("XML Person Asset Module",
		AssetManagementParser.Elements.FilesystemPersonAssetModule.getLocalName(), "users",
		ElementRole.AssetManagment_AssetModule);

	builder.description("List of person assets stored in XML format on the local filesystem.");
	addCommonAssetModuleAttributes(builder);
	addCommonFilesystemAssetModuleAttributes(builder);

	return builder.build();
    }

    /**
     * Create element configuration for XML location asset module.
     * 
     * @return
     */
    protected ElementNode createXmlLocationAssetModuleElement() {
	ElementNode.Builder builder = new ElementNode.Builder("XML Location Asset Module",
		AssetManagementParser.Elements.FilesystemLocationAssetModule.getLocalName(), "map-pin",
		ElementRole.AssetManagment_AssetModule);

	builder.description("List of location assets stored in XML format on the local filesystem.");
	addCommonAssetModuleAttributes(builder);
	addCommonFilesystemAssetModuleAttributes(builder);

	return builder.build();
    }
}