/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.web.rest.marshaling;

public class MarshaledScriptTemplate {

    /** Id */
    private String id;

    /** Name */
    private String name;

    /** Description */
    private String description;

    /** Interpreter type */
    private String interpreterType;

    /** Template content */
    private String script;

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

    public String getInterpreterType() {
	return interpreterType;
    }

    public void setInterpreterType(String interpreterType) {
	this.interpreterType = interpreterType;
    }

    public String getScript() {
	return script;
    }

    public void setScript(String script) {
	this.script = script;
    }
}
