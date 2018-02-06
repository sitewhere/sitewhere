/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rules.configuration;

import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

public enum RuleProcessingRoleKeys implements IRoleKey {

    /** Rule processing */
    RuleProcessing("rule_prc"),

    /** Zone test processor */
    ZoneTestProcessor("zon_tst_prc"),

    /** Zone test processor element */
    ZoneTestElement("zon_tst_elm");

    private String id;

    private RuleProcessingRoleKeys(String id) {
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
