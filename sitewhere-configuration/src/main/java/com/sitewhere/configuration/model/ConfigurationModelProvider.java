/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.rest.model.configuration.ConfigurationModel;
import com.sitewhere.rest.model.configuration.ElementRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModelProvider;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;
import com.sitewhere.spi.microservice.configuration.model.IElementNode;
import com.sitewhere.spi.microservice.configuration.model.IElementRole;
import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

/**
 * Implementation of {@link IConfigurationModelProvider} that can use other
 * configuration models to resolve model dependencies.
 * 
 * @author Derek
 */
public abstract class ConfigurationModelProvider implements IConfigurationModelProvider {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Map of elements by role */
    private Map<String, List<IElementNode>> elementsByRole = new HashMap<String, List<IElementNode>>();

    /** Map of element roles by id */
    private Map<String, IElementRole> rolesById = new HashMap<String, IElementRole>();

    /** Other configuration models that provide role/element dependencies */
    private List<IConfigurationModel> dependencies = new ArrayList<IConfigurationModel>();

    /**
     * Create configuration model for a microservice.
     * 
     * @param microservice
     * @param role
     * @param icon
     * @param description
     */
    public ConfigurationModelProvider() {
	addElements();
	addRoles();
    }

    /**
     * Add an element to the model.
     * 
     * @param element
     */
    protected void addElement(IElementNode element) {
	List<IElementNode> elements = getElementsByRole().get(element.getRole());
	if (elements == null) {
	    elements = new ArrayList<IElementNode>();
	    getElementsByRole().put(element.getRole(), elements);
	}
	elements.add(element);
    }

    /**
     * Add elements contained in model.
     */
    public abstract void addElements();

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#buildModel()
     */
    @Override
    public IConfigurationModel buildModel() {
	addRoles();
	return new ConfigurationModel();
    }

    /**
     * Recursively add roles based on root role.
     */
    protected void addRoles() {
	IConfigurationRoleProvider root = getRootRole();
	if (root != null) {
	    addRoles(root);
	}
    }

    /**
     * Add roles recursively for a given role.
     * 
     * @param current
     */
    @SuppressWarnings("unused")
    protected void addRoles(IConfigurationRoleProvider provider) {
	IConfigurationRole role = provider.getRole();
	if (!getRolesById().containsKey(role.getKey().getId())) {
	    getRolesById().put(role.getKey().getId(), convert(role));
	}
	for (IRoleKey child : role.getChildren()) {
	    // addRoles(child);
	}
	for (IRoleKey child : role.getSubtypes()) {
	    // addRoles(child);
	}
    }

    /**
     * Convert an {@link IConfigurationRole} to an {@link IElementRole}.
     * 
     * @param role
     * @return
     */
    protected IElementRole convert(IConfigurationRole role) {
	try {
	    LOGGER.info("About to convert \n\n" + MarshalUtils.marshalJsonAsPrettyString(role));
	    ElementRole converted = new ElementRole();
	    converted.setName(role.getName());
	    converted.setOptional(role.isOptional());
	    converted.setMultiple(role.isMultiple());
	    converted.setReorderable(role.isReorderable());
	    converted.setPermanent(role.isPermanent());
	    if ((role.getChildren() != null) && (role.getChildren().length > 0)) {
		converted.setChildRoles(new ArrayList<String>());
		for (IRoleKey child : role.getChildren()) {
		    converted.getChildRoles().add(child.getId());
		}
	    }
	    if ((role.getSubtypes() != null) && (role.getSubtypes().length > 0)) {
		converted.setSubtypeRoles(new ArrayList<String>());
		for (IRoleKey child : role.getSubtypes()) {
		    converted.getSubtypeRoles().add(child.getId());
		}
	    }
	    return converted;
	} catch (Throwable t) {
	    LOGGER.error("Unable to convert role.", t);
	    throw new RuntimeException(t);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getDependencies()
     */
    @Override
    public List<IConfigurationModel> getDependencies() {
	return dependencies;
    }

    public void setDependencies(List<IConfigurationModel> dependencies) {
	this.dependencies = dependencies;
    }

    public Map<String, List<IElementNode>> getElementsByRole() {
	return elementsByRole;
    }

    public void setElementsByRole(Map<String, List<IElementNode>> elementsByRole) {
	this.elementsByRole = elementsByRole;
    }

    public Map<String, IElementRole> getRolesById() {
	return rolesById;
    }

    public void setRolesById(Map<String, IElementRole> rolesById) {
	this.rolesById = rolesById;
    }
}