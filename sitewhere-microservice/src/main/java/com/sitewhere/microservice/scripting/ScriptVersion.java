/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.scripting;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sitewhere.rest.model.datatype.JsonDateSerializer;
import com.sitewhere.spi.microservice.scripting.IScriptVersion;

/**
 * Get information about a version of a script.
 * 
 * @author Derek
 */
public class ScriptVersion implements IScriptVersion {

    /** Version id */
    private String versionId;

    /** Comment */
    private String comment;

    /** Created date */
    private Date createdDate;

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptVersion#getVersionId()
     */
    @Override
    public String getVersionId() {
	return versionId;
    }

    public void setVersionId(String versionId) {
	this.versionId = versionId;
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptVersion#getComment()
     */
    @Override
    public String getComment() {
	return comment;
    }

    public void setComment(String comment) {
	this.comment = comment;
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptVersion#getCreatedDate()
     */
    @Override
    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getCreatedDate() {
	return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
	this.createdDate = createdDate;
    }
}