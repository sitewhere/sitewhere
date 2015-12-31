/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.configuration;

import com.sitewhere.spring.handler.GlobalsParser;
import com.sitewhere.spring.handler.IConfigurationElements;
import com.sitewhere.spring.handler.TenantConfigurationParser;
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
		addElement(createGroovyConfigurationElement());
	}

	/**
	 * Create the container for global overrides information.
	 * 
	 * @return
	 */
	protected ElementNode createGlobals() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Global Overrides",
						TenantConfigurationParser.Elements.Globals.getLocalName(), "cogs",
						ElementRole.Globals);
		builder.description("Allow tenant-specific changes to global configuration elements.");
		return builder.build();
	}

	/**
	 * Create element overriding Solr configuration.
	 * 
	 * @return
	 */
	protected ElementNode createSolrConfigurationElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Override Solr Configuration",
						GlobalsParser.Elements.SolrConfiguration.getLocalName(), "cogs",
						ElementRole.Globals_Global);

		builder.namespace(IConfigurationElements.SITEWHERE_COMMUNITY_NS);
		builder.description("Overrides global Solr settings for a tenant.");
		builder.attribute((new AttributeNode.Builder("Solr server URL", "solrServerUrl",
				AttributeType.String).setDescription("URL used by Solr client to access server.").build()));

		return builder.build();
	}

	/**
	 * Create element overriding Groovy configuration.
	 * 
	 * @return
	 */
	protected ElementNode createGroovyConfigurationElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Override Groovy Configuration",
						GlobalsParser.Elements.GroovyConfiguration.getLocalName(), "cogs",
						ElementRole.Globals_Global);

		builder.namespace(IConfigurationElements.SITEWHERE_COMMUNITY_NS);
		builder.description("Overrides global Groovy settings for a tenant.");
		builder.attribute((new AttributeNode.Builder("Enable debugging", "debug", AttributeType.Boolean).setDescription("Turns on Groovy script engine debugging if true.").build()));
		builder.attribute((new AttributeNode.Builder("Enable verbose mode", "verbose",
				AttributeType.Boolean).setDescription("Turns on Groovy script engine verbose flag if true.").build()));
		builder.attribute((new AttributeNode.Builder("External script root", "externalScriptRoot",
				AttributeType.String).setDescription("Sets script root to an external URL rathen than using the default filesystem path.").build()));

		return builder.build();
	}
}