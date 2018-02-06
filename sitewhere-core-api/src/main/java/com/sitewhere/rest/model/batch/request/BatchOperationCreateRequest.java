/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.batch.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitewhere.spi.batch.request.IBatchOperationCreateRequest;

/**
 * Holds information needed to create a batch operation.
 * 
 * @author Derek
 */
public class BatchOperationCreateRequest implements IBatchOperationCreateRequest, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = 276630436113821199L;

    /** Unqiue token */
    private String token;

    /** Operation type requested */
    private String operationType;

    /** Operation parameters */
    private Map<String, String> parameters = new HashMap<String, String>();

    /** List of hardware ids of affected devices */
    private List<String> hardwareIds = new ArrayList<String>();

    /** Metadata values */
    private Map<String, String> metadata;

    /*
     * @see com.sitewhere.spi.batch.request.IBatchOperationCreateRequest#getToken()
     */
    @Override
    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

    /*
     * @see
     * com.sitewhere.spi.batch.request.IBatchOperationCreateRequest#getOperationType
     * ()
     */
    @Override
    public String getOperationType() {
	return operationType;
    }

    public void setOperationType(String operationType) {
	this.operationType = operationType;
    }

    /*
     * @see
     * com.sitewhere.spi.batch.request.IBatchOperationCreateRequest#getParameters()
     */
    @Override
    public Map<String, String> getParameters() {
	return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
	this.parameters = parameters;
    }

    /*
     * @see
     * com.sitewhere.spi.batch.request.IBatchOperationCreateRequest#getHardwareIds()
     */
    @Override
    public List<String> getHardwareIds() {
	return hardwareIds;
    }

    public void setHardwareIds(List<String> hardwareIds) {
	this.hardwareIds = hardwareIds;
    }

    /*
     * @see
     * com.sitewhere.spi.batch.request.IBatchOperationCreateRequest#getMetadata()
     */
    @Override
    public Map<String, String> getMetadata() {
	return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }
}