/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.lifecycle.parameters;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponentParameter;

/**
 * Parameter which wraps a String value.
 */
public class StringComponentParameter extends LifecycleComponentParameter<String> {

    /**
     * Create a new builder.
     * 
     * @param parent
     * @param name
     * @return
     */
    public static Builder newBuilder(ILifecycleComponent parent, String name) {
	return new Builder(parent, name);
    }

    /**
     * Builder pattern for creating parameter.
     */
    public static class Builder {

	private StringComponentParameter parameter;

	public Builder(ILifecycleComponent parent, String name) {
	    this.parameter = new StringComponentParameter();
	    parameter.setParent(parent);
	    parameter.setName(name);
	}

	public Builder value(String value) throws SiteWhereException {
	    String resolved = ValueResolver.resolve(value, parameter.getParent());
	    parameter.setValue(resolved);
	    return this;
	}

	public Builder makeRequired() {
	    parameter.setRequired(true);
	    return this;
	}

	public ILifecycleComponentParameter<String> build() {
	    return parameter;
	}
    }
}