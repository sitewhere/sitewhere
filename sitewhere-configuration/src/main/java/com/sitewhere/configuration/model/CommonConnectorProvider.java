/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.model;

import com.sitewhere.configuration.CommonConnectorModel;
import com.sitewhere.configuration.parser.IConnectorCommonParser;
import com.sitewhere.rest.model.configuration.AttributeNode;
import com.sitewhere.rest.model.configuration.ElementNode;
import com.sitewhere.spi.microservice.configuration.model.AttributeType;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;

/**
 * Provides common connector configuration model roles and elements.
 * 
 * @author Derek
 */
public class CommonConnectorProvider extends ConfigurationModelProvider {

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getDefaultXmlNamespace()
     */
    @Override
    public String getDefaultXmlNamespace() {
	return "http://sitewhere.io/schema/sitewhere/microservice/common/connector";
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getRootRole()
     */
    @Override
    public IConfigurationRoleProvider getRootRole() {
	return CommonConnectorRoles.SolrConfigurationChoice;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeElements()
     */
    @Override
    public void initializeElements() {
	addElement(createSolrConfigurationChoiceElement());
	addElement(createSolrConfigurationElement());
	addElement(createSolrConfigurationReferenceElement());
    }

    /**
     * Create element that offers choice between Solr configuration options.
     * 
     * @return
     */
    protected ElementNode createSolrConfigurationChoiceElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		CommonConnectorRoles.SolrConfigurationChoice.getRole().getName(),
		IConnectorCommonParser.Solr.SolrConfigurationChoice.getLocalName(), "sign-out",
		CommonConnectorRoleKeys.SolrConfigurationChoice, this);

	builder.description("Specifies configuration choices for connecting with Apache Solr.");

	return builder.build();
    }

    /**
     * Create element for custom Solr configuration.
     * 
     * @return
     */
    protected ElementNode createSolrConfigurationElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		CommonConnectorRoles.SolrConfiguration.getRole().getName(),
		IConnectorCommonParser.SolrConnectorElements.SolrConfiguration.getLocalName(), "sign-out",
		CommonConnectorRoleKeys.SolrConfiguration, this);

	builder.description("Supports defining a custom Solr configuration.");
	CommonConnectorModel.addSolrConnectivityAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	return builder.build();
    }

    /**
     * Create reference to global Solr configuration.
     * 
     * @return
     */
    protected ElementNode createSolrConfigurationReferenceElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		CommonConnectorRoles.SolrConfigurationReference.getRole().getName(),
		IConnectorCommonParser.SolrConnectorElements.SolrConfigurationReference.getLocalName(), "sign-out",
		CommonConnectorRoleKeys.SolrConfigurationReference, this);

	builder.description("Use a globally-defined Solr configuration.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	builder.attribute((new AttributeNode.Builder("Configuration Id", "id", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY).description("Unique id for global configuration")
			.makeRequired().build()));

	return builder.build();
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeRoles()
     */
    @Override
    public void initializeRoles() {
	for (CommonConnectorRoles role : CommonConnectorRoles.values()) {
	    getRolesById().put(role.getRole().getKey().getId(), role.getRole());
	}
    }
}
