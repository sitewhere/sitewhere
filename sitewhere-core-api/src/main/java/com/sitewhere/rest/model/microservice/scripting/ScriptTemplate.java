/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.microservice.scripting;

import com.sitewhere.spi.microservice.scripting.IScriptTemplate;

/**
 * Model for script template which is used to load JSON templates.
 */
public class ScriptTemplate implements IScriptTemplate {

    /** Script id */
    private String id;

    /** Script name */
    private String name;

    /** Description */
    private String description;

    /** Type (file extension) */
    private String type;

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptTemplate#getId()
     */
    @Override
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptTemplate#getName()
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
     * com.sitewhere.spi.microservice.scripting.IScriptTemplate#getDescription()
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptTemplate#getType()
     */
    @Override
    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }
}
