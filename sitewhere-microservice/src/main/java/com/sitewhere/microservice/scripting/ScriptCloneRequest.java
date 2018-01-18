/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.scripting;

import com.sitewhere.spi.microservice.scripting.IScriptCloneRequest;

/**
 * Information required to clone a script.
 * 
 * @author Derek
 */
public class ScriptCloneRequest implements IScriptCloneRequest {

    /** Comment */
    private String comment;

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptCloneRequest#getComment()
     */
    @Override
    public String getComment() {
	return comment;
    }

    public void setComment(String comment) {
	this.comment = comment;
    }
}