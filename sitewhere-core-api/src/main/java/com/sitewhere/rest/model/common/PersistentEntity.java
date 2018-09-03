/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.common;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sitewhere.rest.model.datatype.JsonDateSerializer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.common.IPersistentEntity;

/**
 * Base class for persistent entities that have a unique UUID along with a
 * user-definable token. The entities also track create/update information.
 * 
 * @author Derek Adams
 */
public class PersistentEntity extends MetadataProvider implements IPersistentEntity {

    /** Serialization version identifier */
    private static final long serialVersionUID = 1858151633970096161L;

    /** Unique device specification id */
    private UUID id;

    /** Unique token */
    private String token;

    /** Date entity was created */
    private Date createdDate;

    /** Username for creator */
    private String createdBy;

    /** Date entity was last updated */
    private Date updatedDate;

    /** Username that updated entity */
    private String updatedBy;

    /*
     * @see com.sitewhere.spi.common.IPersistentEntity#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.common.IPersistentEntity#getToken()
     */
    @Override
    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

    /*
     * @see com.sitewhere.spi.common.IPersistentEntity#getCreatedDate()
     */
    @Override
    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getCreatedDate() {
	return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
	this.createdDate = createdDate;
    }

    /*
     * @see com.sitewhere.spi.common.IPersistentEntity#getCreatedBy()
     */
    @Override
    public String getCreatedBy() {
	return createdBy;
    }

    public void setCreatedBy(String createdBy) {
	this.createdBy = createdBy;
    }

    /*
     * @see com.sitewhere.spi.common.IPersistentEntity#getUpdatedDate()
     */
    @Override
    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getUpdatedDate() {
	return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
	this.updatedDate = updatedDate;
    }

    /*
     * @see com.sitewhere.spi.common.IPersistentEntity#getUpdatedBy()
     */
    @Override
    public String getUpdatedBy() {
	return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
	this.updatedBy = updatedBy;
    }

    /**
     * Copy fields from source to target.
     * 
     * @param source
     * @param target
     */
    public static void copy(IPersistentEntity source, PersistentEntity target) throws SiteWhereException {
	target.setCreatedDate(source.getCreatedDate());
	target.setCreatedBy(source.getCreatedBy());
	target.setUpdatedDate(source.getUpdatedDate());
	target.setUpdatedBy(source.getUpdatedBy());
	MetadataProvider.copy(source, target);
    }
}