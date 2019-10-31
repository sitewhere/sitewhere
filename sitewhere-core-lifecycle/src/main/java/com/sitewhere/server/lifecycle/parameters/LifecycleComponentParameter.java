/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.lifecycle.parameters;

import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponentParameter;

/**
 * Parameter associated with a lifecycle component.
 *
 * @param <T>
 */
public class LifecycleComponentParameter<T> implements ILifecycleComponentParameter<T> {

    /** Component name */
    private String name;

    /** Component value */
    private T value;

    /** Required */
    private boolean required;

    /** Parent component */
    private ILifecycleComponent parent;

    /*
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponentParameter#getName()
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
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponentParameter#getValue()
     */
    @Override
    public T getValue() {
	return value;
    }

    public void setValue(T value) {
	this.value = value;
    }

    /*
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponentParameter#isRequired()
     */
    @Override
    public boolean isRequired() {
	return required;
    }

    public void setRequired(boolean required) {
	this.required = required;
    }

    /*
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponentParameter#getParent()
     */
    @Override
    public ILifecycleComponent getParent() {
	return parent;
    }

    public void setParent(ILifecycleComponent parent) {
	this.parent = parent;
    }
}