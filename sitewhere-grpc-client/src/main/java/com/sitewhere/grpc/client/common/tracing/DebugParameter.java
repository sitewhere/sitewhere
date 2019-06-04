/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.common.tracing;

/**
 * Used to provide context for debugging parameters to GRPC calls.
 * 
 * @author Derek
 */
public class DebugParameter {

    /** Parameter name */
    private String name;

    /** Parameter content */
    private Object content;

    public DebugParameter(String name, Object content) {
	this.name = name;
	this.content = content;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Object getContent() {
	return content;
    }

    public void setContent(Object content) {
	this.content = content;
    }

    public static DebugParameter create(String name, Object content) {
	return new DebugParameter(name, content);
    }
}