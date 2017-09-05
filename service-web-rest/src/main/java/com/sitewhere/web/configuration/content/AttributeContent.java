/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.configuration.content;

/**
 * Contains information about an XML attribute.
 * 
 * @author Derek
 */
public class AttributeContent extends XmlContent {

    /** Attribute value */
    private String value;

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }
}