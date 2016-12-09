/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.common;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sitewhere.rest.model.datatype.JsonDateSerializer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.common.IMetadataProviderEntity;

/**
 * Metadata provider that also contains SiteWhere entity information.
 * 
 * @author Derek Adams
 */
public class MetadataProviderEntity extends MetadataProvider implements IMetadataProviderEntity, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = 1858151633970096161L;

    /** Date entity was created */
    private Date createdDate;

    /** Username for creator */
    private String createdBy;

    /** Date entity was last updated */
    private Date updatedDate;

    /** Username that updated entity */
    private String updatedBy;

    /** Indicates if entity has been deleted */
    private boolean deleted;

    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getCreatedDate() {
	return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
	this.createdDate = createdDate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.common.ISiteWhereEntity#getCreatedBy()
     */
    public String getCreatedBy() {
	return createdBy;
    }

    public void setCreatedBy(String createdBy) {
	this.createdBy = createdBy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.common.ISiteWhereEntity#getUpdatedDate()
     */
    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getUpdatedDate() {
	return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
	this.updatedDate = updatedDate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.common.ISiteWhereEntity#getUpdatedBy()
     */
    public String getUpdatedBy() {
	return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
	this.updatedBy = updatedBy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.common.ISiteWhereEntity#isDeleted()
     */
    public boolean isDeleted() {
	return deleted;
    }

    public void setDeleted(boolean deleted) {
	this.deleted = deleted;
    }

    /**
     * Copy fields from source to target.
     * 
     * @param source
     * @param target
     */
    public static void copy(IMetadataProviderEntity source, MetadataProviderEntity target) throws SiteWhereException {
	target.setCreatedDate(source.getCreatedDate());
	target.setCreatedBy(source.getCreatedBy());
	target.setUpdatedDate(source.getUpdatedDate());
	target.setUpdatedBy(source.getUpdatedBy());
	target.setDeleted(source.isDeleted());
	MetadataProvider.copy(source, target);
    }
}