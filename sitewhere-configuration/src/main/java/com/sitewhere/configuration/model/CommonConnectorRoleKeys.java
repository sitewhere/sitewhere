/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.model;

import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

public enum CommonConnectorRoleKeys implements IRoleKey {

    /** Solr configuration choice */
    SolrConfigurationChoice("solr_config_choice"),

    /** Solr configuration choice element */
    SolrConfigurationChoiceElement("solr_config_choice_elm"),

    /** Solr configuration */
    SolrConfiguration("solr_config"),

    /** Solr configuration reference */
    SolrConfigurationReference("solr_config_ref");

    private String id;

    private CommonConnectorRoleKeys(String id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IRoleKey#getId()
     */
    @Override
    public String getId() {
	return id;
    }
}