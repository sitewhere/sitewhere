/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spring.handler;

import org.w3c.dom.Element;

/**
 * Utility methods for SiteWhere DOM functionality.
 * 
 * @author Derek
 */
public class SiteWhereDomUtils {

    /**
     * Indicates if an element is from a SiteWhere namespace.
     * 
     * @param child
     * @return
     */
    public static boolean hasSiteWhereNamespace(Element child) {
	if (IConfigurationElements.SITEWHERE_COMMUNITY_NS.equals(child.getNamespaceURI())
		|| (IConfigurationElements.SITEWHERE_CE_TENANT_NS.equals(child.getNamespaceURI()))) {
	    return true;
	}
	return false;
    }
}