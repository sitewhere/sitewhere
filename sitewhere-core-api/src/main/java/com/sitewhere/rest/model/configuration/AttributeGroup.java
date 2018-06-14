/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.configuration;

import com.sitewhere.spi.microservice.configuration.model.IAttributeGroup;

public class AttributeGroup implements IAttributeGroup {

    /** Group id */
    private String id;

    /** Group name */
    private String name;

    public AttributeGroup(String id, String name) {
	this.id = id;
	this.name = name;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IAttributeGroup#getId()
     */
    @Override
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IAttributeGroup#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }
}
