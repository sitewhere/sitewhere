/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.batch.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitewhere.rest.model.common.request.PersistentEntityCreateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationCreateRequest;

/**
 * Holds information needed to create a batch operation.
 * 
 * @author Derek
 */
public class BatchOperationCreateRequest extends PersistentEntityCreateRequest implements IBatchOperationCreateRequest {

    /** Serialization version identifier */
    private static final long serialVersionUID = 276630436113821199L;

    /** Operation type requested */
    private String operationType;

    /** Operation parameters */
    private Map<String, String> parameters = new HashMap<String, String>();

    /** List of tokens for affected devices */
    private List<String> deviceTokens = new ArrayList<>();

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
     * com.sitewhere.spi.batch.request.IBatchOperationCreateRequest#getDeviceTokens(
     * )
     */
    @Override
    public List<String> getDeviceTokens() {
	return deviceTokens;
    }

    public void setDeviceTokens(List<String> deviceTokens) {
	this.deviceTokens = deviceTokens;
    }
}