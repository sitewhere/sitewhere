/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration;

import com.sitewhere.spi.microservice.configuration.model.IConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

/**
 * Used to indicate role of an element.
 */
public class ConfigurationRole implements IConfigurationRole {

    /** Role key */
    private IRoleKey key;

    /** Role name */
    private String name;

    /** Indicates if role is optional */
    private boolean optional;

    /** Indicates if multiple elements in role are allowed */
    private boolean multiple;

    /** Indicates if elements in role can be reordered */
    private boolean reorderable;

    /** Indicates if element is permanent */
    private boolean permanent;

    /** Child roles in the order they should appear */
    private IRoleKey[] children;

    /** Subtypes that specialize the given role */
    private IRoleKey[] subtypes;

    public static ConfigurationRole build(IRoleKey key, String name, boolean optional, boolean multiple,
	    boolean reorderable) {
	return build(key, name, optional, multiple, reorderable, new IRoleKey[0]);
    }

    public static ConfigurationRole build(IRoleKey key, String name, boolean optional, boolean multiple,
	    boolean reorderable, IRoleKey[] children) {
	return build(key, name, optional, multiple, reorderable, children, new IRoleKey[0]);
    }

    public static ConfigurationRole build(IRoleKey key, String name, boolean optional, boolean multiple,
	    boolean reorderable, IRoleKey[] children, IRoleKey[] subtypes) {
	return build(key, name, optional, multiple, reorderable, children, subtypes, false);
    }

    public static ConfigurationRole build(IRoleKey key, String name, boolean optional, boolean multiple,
	    boolean reorderable, IRoleKey[] children, IRoleKey[] subtypes, boolean permanent) {
	ConfigurationRole role = new ConfigurationRole();
	role.key = key;
	role.name = name;
	role.optional = optional;
	role.multiple = multiple;
	role.reorderable = reorderable;
	role.children = children;
	role.subtypes = subtypes;
	role.permanent = permanent;
	return role;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IConfigurationRole#getKey(
     * )
     */
    @Override
    public IRoleKey getKey() {
	return key;
    }

    public void setKey(IRoleKey key) {
	this.key = key;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IConfigurationRole#getName
     * ()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IConfigurationRole#
     * isOptional()
     */
    @Override
    public boolean isOptional() {
	return optional;
    }

    public void setOptional(boolean optional) {
	this.optional = optional;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IConfigurationRole#
     * isMultiple()
     */
    @Override
    public boolean isMultiple() {
	return multiple;
    }

    public void setMultiple(boolean multiple) {
	this.multiple = multiple;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IConfigurationRole#
     * isReorderable()
     */
    @Override
    public boolean isReorderable() {
	return reorderable;
    }

    public void setReorderable(boolean reorderable) {
	this.reorderable = reorderable;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IConfigurationRole#
     * isPermanent()
     */
    @Override
    public boolean isPermanent() {
	return permanent;
    }

    public void setPermanent(boolean permanent) {
	this.permanent = permanent;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IConfigurationRole#
     * getChildren()
     */
    @Override
    public IRoleKey[] getChildren() {
	return children;
    }

    public void setChildren(IRoleKey[] children) {
	this.children = children;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IConfigurationRole#
     * getSubtypes()
     */
    @Override
    public IRoleKey[] getSubtypes() {
	return subtypes;
    }

    public void setSubtypes(IRoleKey[] subtypes) {
	this.subtypes = subtypes;
    }
}