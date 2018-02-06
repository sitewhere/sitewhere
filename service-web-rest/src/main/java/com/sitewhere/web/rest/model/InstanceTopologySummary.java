/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Summarizes instance topology for sending via REST.
 * 
 * @author Derek
 */
public class InstanceTopologySummary {

    /** Function indentifier */
    private String identifier;

    /** Display name */
    private String name;

    /** Icon */
    private String icon;

    /** Description */
    private String description;

    /** Scope flag */
    private boolean global;

    /** List of hostnames for entry. */
    private List<String> hostnames = new ArrayList<>();

    public String getIdentifier() {
	return identifier;
    }

    public void setIdentifier(String identifier) {
	this.identifier = identifier;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getIcon() {
	return icon;
    }

    public void setIcon(String icon) {
	this.icon = icon;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public List<String> getHostnames() {
	return hostnames;
    }

    public void setHostnames(List<String> hostnames) {
	this.hostnames = hostnames;
    }

    public boolean isGlobal() {
	return global;
    }

    public void setGlobal(boolean global) {
	this.global = global;
    }
}