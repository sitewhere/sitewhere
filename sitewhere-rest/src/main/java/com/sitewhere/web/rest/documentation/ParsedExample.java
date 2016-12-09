/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import com.sitewhere.web.rest.annotations.Example.Stage;

/**
 * Parsed example code.
 * 
 * @author Derek
 */
public class ParsedExample {

    /** Description */
    private String description;

    /** Formatted JSON */
    private String json;

    /** Stage example applies to */
    private Stage stage;

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getJson() {
	return json;
    }

    public void setJson(String json) {
	this.json = json;
    }

    public Stage getStage() {
	return stage;
    }

    public void setStage(Stage stage) {
	this.stage = stage;
    }
}