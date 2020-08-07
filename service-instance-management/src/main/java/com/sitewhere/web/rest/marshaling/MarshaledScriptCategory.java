/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.marshaling;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.microservice.scripting.IScriptMetadata;

@JsonInclude(Include.NON_NULL)
public class MarshaledScriptCategory {

    /** Category id */
    private String id;

    /** Category name */
    private String name;

    /** Category description */
    private String description;

    /** List of scripts in category */
    private List<IScriptMetadata> scripts;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public List<IScriptMetadata> getScripts() {
	return scripts;
    }

    public void setScripts(List<IScriptMetadata> scripts) {
	this.scripts = scripts;
    }
}
