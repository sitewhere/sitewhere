/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.configuration;

import java.util.List;

import com.sitewhere.spi.microservice.configuration.model.IElementRole;

/**
 * Model object for a configuration element role.
 */
public class ElementRole implements IElementRole {

    /** Display name */
    private String name;

    /** Optional flag */
    private boolean optional;

    /** Multiple allowed */
    private boolean multiple;

    /** Elements reorderable */
    private boolean reorderable;

    /** Can not be deleted */
    private boolean permanent;

    /** Child roles */
    private List<String> childRoles;

    /** Subtypes for replacement */
    private List<String> subtypeRoles;

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IElementRole#getName()
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
     * com.sitewhere.spi.microservice.configuration.model.IElementRole#isOptional()
     */
    @Override
    public boolean isOptional() {
	return optional;
    }

    public void setOptional(boolean optional) {
	this.optional = optional;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IElementRole#isMultiple()
     */
    @Override
    public boolean isMultiple() {
	return multiple;
    }

    public void setMultiple(boolean multiple) {
	this.multiple = multiple;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IElementRole#isReorderable
     * ()
     */
    @Override
    public boolean isReorderable() {
	return reorderable;
    }

    public void setReorderable(boolean reorderable) {
	this.reorderable = reorderable;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IElementRole#isPermanent()
     */
    @Override
    public boolean isPermanent() {
	return permanent;
    }

    public void setPermanent(boolean permanent) {
	this.permanent = permanent;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IElementRole#getChildRoles
     * ()
     */
    @Override
    public List<String> getChildRoles() {
	return childRoles;
    }

    public void setChildRoles(List<String> childRoles) {
	this.childRoles = childRoles;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IElementRole#
     * getSubtypeRoles()
     */
    @Override
    public List<String> getSubtypeRoles() {
	return subtypeRoles;
    }

    public void setSubtypeRoles(List<String> subtypeRoles) {
	this.subtypeRoles = subtypeRoles;
    }
}