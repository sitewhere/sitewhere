package com.sitewhere.web.configuration;

import com.sitewhere.spring.handler.ISearchProvidersParser;
import com.sitewhere.spring.handler.ITenantConfigurationParser;
import com.sitewhere.web.configuration.model.AttributeNode;
import com.sitewhere.web.configuration.model.AttributeType;
import com.sitewhere.web.configuration.model.ConfigurationModel;
import com.sitewhere.web.configuration.model.ElementNode;
import com.sitewhere.web.configuration.model.ElementRole;

/**
 * Configuration model for search provider elements.
 * 
 * @author Derek
 */
public class SearchProvidersModel extends ConfigurationModel {

    public SearchProvidersModel() {
	addElement(createSearchProviders());
	addElement(createSolrSearchProvider());
    }

    /**
     * Create the container for asset management configuration.
     * 
     * @return
     */
    protected ElementNode createSearchProviders() {
	ElementNode.Builder builder = new ElementNode.Builder("Search Providers",
		ITenantConfigurationParser.Elements.SearchProviders.getLocalName(), "search",
		ElementRole.SearchProviders);
	builder.description("Configure search providers.");
	return builder.build();
    }

    /**
     * Create element configuration for Solr search provider.
     * 
     * @return
     */
    protected ElementNode createSolrSearchProvider() {
	ElementNode.Builder builder = new ElementNode.Builder("Solr Search Provider",
		ISearchProvidersParser.Elements.SolrSearchProvider.getLocalName(), "search",
		ElementRole.SearchProviders_SearchProvider);

	builder.description("Provider that delegates search tasks to a linked Solr instance.");
	builder.attribute((new AttributeNode.Builder("Id", "id", AttributeType.String)
		.description("Unique id for search provider.").defaultValue("solr").makeIndex().build()));
	builder.attribute((new AttributeNode.Builder("Name", "name", AttributeType.String)
		.description("Name shown for search provider.").defaultValue(" Apache Solr").build()));

	return builder.build();
    }
}