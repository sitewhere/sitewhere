/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.scripting;

import com.sitewhere.spi.microservice.scripting.IScriptCreateRequest;

/**
 * Information required to create a new script.
 * 
 * @author Derek
 */
public class ScriptCreateRequest implements IScriptCreateRequest {

    /** Script id */
    private String id;

    /** Script name */
    private String name;

    /** Short description */
    private String description;

    /** Type (file suffix) */
    private String type;

    /** Script content (base 64 encoded) */
    private String content;

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptCreateRequest#getId()
     */
    @Override
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptCreateRequest#getName()
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
     * com.sitewhere.spi.microservice.scripting.IScriptCreateRequest#getDescription(
     * )
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptCreateRequest#getType()
     */
    @Override
    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptCreateRequest#getContent()
     */
    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}