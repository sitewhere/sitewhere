/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.configuration.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Top-level node for configuration model.
 * 
 * @author Derek
 */
public class ConfigurationModel extends ElementNode {

    /** Map of elements by role */
    private Map<String, List<ElementNode>> elementsByRole = new HashMap<String, List<ElementNode>>();

    public void addElement(ElementNode element) {
	List<ElementNode> elements = getElementsByRole().get(element.getRole());
	if (elements == null) {
	    elements = new ArrayList<ElementNode>();
	    getElementsByRole().put(element.getRole(), elements);
	}
	elements.add(element);
    }

    public Map<String, List<ElementNode>> getElementsByRole() {
	return elementsByRole;
    }

    public void setElementsByRole(Map<String, List<ElementNode>> elementsByRole) {
	this.elementsByRole = elementsByRole;
    }
}