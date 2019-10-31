/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.configuration;

import com.sitewhere.spi.microservice.configuration.model.IAttributeChoice;

/**
 * Choice for an enumerated value for an attribute.
 */
public class AttributeChoice implements IAttributeChoice {

    /** Name */
    private String name;

    /** Value */
    private String value;

    public AttributeChoice() {
    }

    public AttributeChoice(String name, String value) {
	this.name = name;
	this.value = value;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IAttributeChoice#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IAttributeChoice#getValue(
     * )
     */
    @Override
    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }
}