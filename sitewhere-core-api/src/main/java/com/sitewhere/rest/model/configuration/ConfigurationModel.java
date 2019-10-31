/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.microservice.configuration.model.IElementNode;
import com.sitewhere.spi.microservice.configuration.model.IElementRole;
import com.sitewhere.spi.microservice.state.IMicroserviceDetails;

/**
 * Contains information about model used to configure a microservice.
 */
public class ConfigurationModel implements IConfigurationModel {

    /** Microservice details */
    private IMicroserviceDetails microserviceDetails;

    /** Default XML namespace */
    private String defaultXmlNamespace;

    /** Id of root role */
    private String rootRoleId;

    /** Map of elements by role */
    private Map<String, List<IElementNode>> elementsByRole = new HashMap<String, List<IElementNode>>();

    /** Map of element roles by id */
    private Map<String, IElementRole> rolesById = new HashMap<String, IElementRole>();

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IConfigurationModel#
     * getMicroserviceDetails()
     */
    @Override
    public IMicroserviceDetails getMicroserviceDetails() {
	return microserviceDetails;
    }

    public void setMicroserviceDetails(IMicroserviceDetails microserviceDetails) {
	this.microserviceDetails = microserviceDetails;
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
     * getRootRoleId()
     */
    @Override
    public String getRootRoleId() {
	return rootRoleId;
    }

    public void setRootRoleId(String rootRoleId) {
	this.rootRoleId = rootRoleId;
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

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IConfigurationModel#
     * getRolesById()
     */
    @Override
    public Map<String, IElementRole> getRolesById() {
	return rolesById;
    }

    public void setRolesById(Map<String, IElementRole> rolesById) {
	this.rolesById = rolesById;
    }
}