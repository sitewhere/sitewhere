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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.rest.model.configuration.AttributeGroup;
import com.sitewhere.rest.model.configuration.ConfigurationModel;
import com.sitewhere.rest.model.configuration.ElementRole;
import com.sitewhere.spi.microservice.configuration.model.IAttributeGroup;
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

    // General attribute group.
    public static final IAttributeGroup ATTR_GROUP_GENERAL = new AttributeGroup("genr", "General");

    // Connectivity attribute group.
    public static final IAttributeGroup ATTR_GROUP_CONNECTIVITY = new AttributeGroup("conn", "Connectivity");

    // Authentication attribute group.
    public static final IAttributeGroup ATTR_GROUP_AUTHENTICATION = new AttributeGroup("auth", "Authentication");

    // Performance attribute group.
    public static final IAttributeGroup ATTR_GROUP_PERFORMANCE = new AttributeGroup("perf", "Performance");

    // Batch attribute group.
    public static final IAttributeGroup ATTR_GROUP_BATCH = new AttributeGroup("btch", "Batch Settings");

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(ConfigurationModelProvider.class);

    /** Map of elements by role */
    private Map<String, List<IElementNode>> elementsByRole = new HashMap<String, List<IElementNode>>();

    /** Map of roles by id */
    private Map<String, IConfigurationRole> rolesById = new HashMap<String, IConfigurationRole>();

    /** Other configuration models that provide role/element dependencies */
    private List<IConfigurationModelProvider> dependencies = new ArrayList<IConfigurationModelProvider>();

    /**
     * Create configuration model for a microservice.
     * 
     * @param microservice
     * @param role
     * @param icon
     * @param description
     */
    public ConfigurationModelProvider() {
	initializeDependencies();
	initializeElements();
	initializeRoles();
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

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeDependencies()
     */
    @Override
    public void initializeDependencies() {
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#buildModel()
     */
    @Override
    public IConfigurationModel buildModel() {
	Map<String, IConfigurationRole> usedRoles = findUsedRoles();
	Map<String, List<IElementNode>> usedElements = findUsedElements(usedRoles);

	ConfigurationModel model = new ConfigurationModel();
	model.setDefaultXmlNamespace(getDefaultXmlNamespace());
	model.setRootRoleId(getRootRole().getRole().getKey().getId());
	for (String roleId : usedRoles.keySet()) {
	    IConfigurationRole configRole = usedRoles.get(roleId);
	    model.getRolesById().put(roleId, convert(configRole));
	}
	for (String roleId : usedElements.keySet()) {
	    List<IElementNode> elements = usedElements.get(roleId);
	    model.getElementsByRole().put(roleId, elements);
	}
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("Built model:\n\n" + MarshalUtils.marshalJsonAsPrettyString(model));
	}
	return model;
    }

    /**
     * Recursively add roles based on root role.
     */
    protected Map<String, IConfigurationRole> findUsedRoles() {
	IConfigurationRoleProvider root = getRootRole();
	Map<String, IConfigurationRole> usedRolesById = new HashMap<String, IConfigurationRole>();
	addRoles(root.getRole(), usedRolesById);
	return usedRolesById;
    }

    /**
     * Add roles recursively for a given role.
     * 
     * @param role
     * @param usedRolesById
     */
    protected void addRoles(IConfigurationRole role, Map<String, IConfigurationRole> usedRolesById) {
	String roleId = role.getKey().getId();
	if (!usedRolesById.containsKey(role.getKey().getId())) {
	    IConfigurationRole referenced = getRoleForId(roleId);
	    usedRolesById.put(role.getKey().getId(), referenced);
	}
	for (IRoleKey child : role.getChildren()) {
	    if (!usedRolesById.containsKey(child.getId())) {
		addRoles(getRoleForId(child.getId()), usedRolesById);
	    }
	}
	for (IRoleKey child : role.getSubtypes()) {
	    if (!usedRolesById.containsKey(child.getId())) {
		addRoles(getRoleForId(child.getId()), usedRolesById);
	    }
	}
    }

    /**
     * Get role for role id.
     * 
     * @param roleId
     * @return
     */
    protected IConfigurationRole getRoleForId(String roleId) {
	IConfigurationRole role = getRolesById().get(roleId);
	if (role != null) {
	    return role;
	}
	for (IConfigurationModelProvider model : getDependencies()) {
	    IConfigurationRole nested = model.getRolesById().get(roleId);
	    if (nested != null) {
		return nested;
	    }
	}
	throw new RuntimeException("Model references unknown role: " + roleId);
    }

    /**
     * Convert an {@link IConfigurationRole} to an {@link IElementRole}.
     * 
     * @param role
     * @return
     */
    protected IElementRole convert(IConfigurationRole role) {
	try {
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
	    throw new RuntimeException(t);
	}
    }

    /**
     * Get list of elements associated with role. Recurse into dependencies.
     * 
     * @param roleId
     * @return
     */
    protected List<IElementNode> getElementsForRole(String roleId) {
	List<IElementNode> elements = getElementsByRole().get(roleId);
	if (elements != null) {
	    return elements;
	} else {
	    for (IConfigurationModelProvider model : getDependencies()) {
		elements = model.getElementsByRole().get(roleId);
		if (elements != null) {
		    return elements;
		}
	    }
	    return null;
	}
    }

    /**
     * Find used elements based on used roles.
     * 
     * @param usedRoles
     * @return
     */
    protected Map<String, List<IElementNode>> findUsedElements(Map<String, IConfigurationRole> usedRoles) {
	Map<String, List<IElementNode>> used = new HashMap<>();
	for (String key : usedRoles.keySet()) {
	    IConfigurationRole role = usedRoles.get(key);
	    String roleId = role.getKey().getId();
	    List<IElementNode> elements = getElementsForRole(roleId);
	    if (elements != null) {
		used.put(roleId, elements);
	    }
	}
	return used;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getDependencies()
     */
    @Override
    public List<IConfigurationModelProvider> getDependencies() {
	return dependencies;
    }

    public void setDependencies(List<IConfigurationModelProvider> dependencies) {
	this.dependencies = dependencies;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getRolesById()
     */
    @Override
    public Map<String, IConfigurationRole> getRolesById() {
	return rolesById;
    }

    public void setRolesById(Map<String, IConfigurationRole> rolesById) {
	this.rolesById = rolesById;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getElementsByRole()
     */
    @Override
    public Map<String, List<IElementNode>> getElementsByRole() {
	return elementsByRole;
    }

    public void setElementsByRole(Map<String, List<IElementNode>> elementsByRole) {
	this.elementsByRole = elementsByRole;
    }
}