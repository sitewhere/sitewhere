/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.configuration;

import com.sitewhere.spring.handler.IConfigurationElements;
import com.sitewhere.spring.handler.IGlobalsParser;
import com.sitewhere.spring.handler.ITenantConfigurationParser;
import com.sitewhere.web.configuration.model.AttributeNode;
import com.sitewhere.web.configuration.model.AttributeType;
import com.sitewhere.web.configuration.model.ConfigurationModel;
import com.sitewhere.web.configuration.model.ElementNode;
import com.sitewhere.web.configuration.model.ElementRole;

/**
 * Configuration model for global elements.
 * 
 * @author Derek
 */
public class GlobalsModel extends ConfigurationModel {

    public GlobalsModel() {
	addElement(createGlobals());
	addElement(createSolrConfigurationElement());
    }

    /**
     * Create the container for global overrides information.
     * 
     * @return
     */
    protected ElementNode createGlobals() {
	ElementNode.Builder builder = new ElementNode.Builder("Global Overrides",
		ITenantConfigurationParser.Elements.Globals.getLocalName(), "cogs", ElementRole.Globals);
	builder.description("Allow tenant-specific changes to global configuration elements.");
	return builder.build();
    }

    /**
     * Create element overriding Solr configuration.
     * 
     * @return
     */
    protected ElementNode createSolrConfigurationElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Override Solr Configuration",
		IGlobalsParser.Elements.SolrConfiguration.getLocalName(), "cogs", ElementRole.Globals_Global);

	builder.namespace(IConfigurationElements.SITEWHERE_COMMUNITY_NS);
	builder.description("Overrides global Solr settings for a tenant.");
	builder.attributeGroup("instance", "Solr Instance Information");
	builder.attribute((new AttributeNode.Builder("Solr server URL", "solrServerUrl", AttributeType.String)
		.description("URL used by Solr client to access server.").group("instance").build()));

	return builder.build();
    }
}