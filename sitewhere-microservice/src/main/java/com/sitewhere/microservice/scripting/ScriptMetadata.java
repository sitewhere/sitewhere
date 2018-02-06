/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.scripting;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.spi.microservice.scripting.IScriptMetadata;
import com.sitewhere.spi.microservice.scripting.IScriptVersion;

/**
 * Contains metadata about a script.
 * 
 * @author Derek
 */
public class ScriptMetadata implements IScriptMetadata {

    /** Script id */
    private String id;

    /** Script name */
    private String name;

    /** Short description */
    private String description;

    /** Type (file suffix) */
    private String type;

    /** Active version identifier */
    private String activeVersion;

    /** List of available versions */
    private List<IScriptVersion> versions = new ArrayList<>();

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptMetadata#getId()
     */
    @Override
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptMetadata#getName()
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
     * com.sitewhere.spi.microservice.scripting.IScriptMetadata#getDescription()
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptMetadata#getType()
     */
    @Override
    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptMetadata#getActiveVersion()
     */
    @Override
    public String getActiveVersion() {
	return activeVersion;
    }

    public void setActiveVersion(String activeVersion) {
	this.activeVersion = activeVersion;
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptMetadata#getVersions()
     */
    @Override
    public List<IScriptVersion> getVersions() {
	return versions;
    }

    public void setVersions(List<ScriptVersion> versions) {
	getVersions().clear();
	getVersions().addAll(versions);
    }
}