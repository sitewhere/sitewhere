/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.model;

import com.sitewhere.configuration.ConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;
import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

/**
 * Common connector roles used by microservices.
 * 
 * @author Derek
 */
public enum CommonConnectorRoles implements IConfigurationRoleProvider {

    /** Solr configuration choice. */
    SolrConfigurationChoice(ConfigurationRole.build(CommonConnectorRoleKeys.SolrConfigurationChoice,
	    "Solr Configuration Choice", false, false, false,
	    new IRoleKey[] { CommonConnectorRoleKeys.SolrConfigurationChoiceElement }, new IRoleKey[0])),

    /** Solr configuration choice element */
    SolrConfigurationChoiceElement(ConfigurationRole.build(CommonConnectorRoleKeys.SolrConfigurationChoiceElement,
	    "Solr Configuration", false, false, false, new IRoleKey[0], new IRoleKey[] {
		    CommonConnectorRoleKeys.SolrConfiguration, CommonConnectorRoleKeys.SolrConfigurationReference })),

    /** Custom Solr configuration */
    SolrConfiguration(ConfigurationRole.build(CommonConnectorRoleKeys.SolrConfiguration, "Custom Solr Configuration",
	    false, false, false, new IRoleKey[0], new IRoleKey[0])),

    /** Solr configuration reference */
    SolrConfigurationReference(ConfigurationRole.build(CommonConnectorRoleKeys.SolrConfigurationReference,
	    "Global Solr Configuration Reference", false, false, false, new IRoleKey[0], new IRoleKey[0]));

    private ConfigurationRole role;

    private CommonConnectorRoles(ConfigurationRole role) {
	this.role = role;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider
     * #getRole()
     */
    @Override
    public IConfigurationRole getRole() {
	return role;
    }
}
