/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.persistence;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;

/**
 * Common methods needed by device service provider implementations.
 * 
 * @author Derek
 */
public class Persistence {

    /**
     * Initialize entity fields. TODO: Use credentials for subject.
     * 
     * @param createdBy
     * @param entity
     * @throws SiteWhereException
     */
    public static void initializeEntityMetadata(MetadataProviderEntity entity) throws SiteWhereException {
	entity.setCreatedDate(new Date());
	entity.setCreatedBy(null);
	entity.setDeleted(false);
    }

    /**
     * Set updated fields. TODO: Use credentials for subject.
     * 
     * @param updatedBy
     * @param entity
     * @throws SiteWhereException
     */
    public static void setUpdatedEntityMetadata(MetadataProviderEntity entity) throws SiteWhereException {
	entity.setUpdatedDate(new Date());
	entity.setUpdatedBy(null);
    }

    /**
     * Requires that a String field be a non null, non space-filled value.
     * 
     * @param field
     * @throws SiteWhereException
     */
    protected static void require(String field) throws SiteWhereException {
	if (StringUtils.isBlank(field)) {
	    throw new SiteWhereSystemException(ErrorCode.IncompleteData, ErrorLevel.ERROR);
	}
    }

    /**
     * Require that a non-String field be non-null.
     * 
     * @param field
     * @throws SiteWhereException
     */
    protected static void requireNotNull(Object field) throws SiteWhereException {
	if (field == null) {
	    throw new SiteWhereSystemException(ErrorCode.IncompleteData, ErrorLevel.ERROR);
	}
    }

    /**
     * Requires that a String field be a non null, non space-filled value.
     * 
     * @param field
     * @throws SiteWhereException
     */
    protected static void requireFormat(String field, String regex, ErrorCode ifFails) throws SiteWhereException {
	require(field);
	if (!field.matches(regex)) {
	    throw new SiteWhereSystemException(ifFails, ErrorLevel.ERROR);
	}
    }

    /**
     * Detect whether the request has an updated value.
     * 
     * @param request
     * @param target
     * @return
     */
    protected static boolean isUpdated(Object request, Object target) {
	return (request != null) && (!request.equals(target));
    }
}