/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.microservice.configuration.model.IElementNode;

/**
 * Contains a map of all model elements grouped by role.
 * 
 * @author Derek
 */
public class ConfigurationModel extends ElementNode implements IConfigurationModel {

    /** Default XML namespace */
    private String defaultXmlNamespace;

    /** Map of elements by role */
    private Map<String, List<IElementNode>> elementsByRole = new HashMap<String, List<IElementNode>>();

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IConfigurationModel#
     * addElement(com.sitewhere.spi.microservice.configuration.model.IElementNode)
     */
    @Override
    public void addElement(IElementNode element) {
	List<IElementNode> elements = getElementsByRole().get(element.getRole());
	if (elements == null) {
	    elements = new ArrayList<IElementNode>();
	    getElementsByRole().put(element.getRole(), elements);
	}
	elements.add(element);
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IConfigurationModel#
     * getDefaultXmlNamespace()
     */
    @Override
    public String getDefaultXmlNamespace() {
	return defaultXmlNamespace;
    }

    public void setDefaultXmlNamespace(String defaultXmlNamespace) {
	this.defaultXmlNamespace = defaultXmlNamespace;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IConfigurationModel#
     * getElementsByRole()
     */
    @Override
    public Map<String, List<IElementNode>> getElementsByRole() {
	return elementsByRole;
    }

    public void setElementsByRole(Map<String, List<IElementNode>> elementsByRole) {
	this.elementsByRole = elementsByRole;
    }
}