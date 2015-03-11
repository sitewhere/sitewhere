/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.spi.device.batch.OperationType;
import com.sitewhere.spi.device.request.IBatchOperationCreateRequest;

/**
 * Holds information needed to create a batch operation.
 * 
 * @author Derek
 */
public class BatchOperationCreateRequest extends MetadataProvider implements IBatchOperationCreateRequest,
		Serializable {

	/** Serialization version identifier */
	private static final long serialVersionUID = 276630436113821199L;

	/** Unqiue token */
	private String token;

	/** Operation type requested */
	private OperationType operationType;

	/** Operation parameters */
	private Map<String, String> parameters = new HashMap<String, String>();

	/** List of hardware ids of affected devices */
	private List<String> hardwareIds = new ArrayList<String>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.request.IBatchOperationCreateRequest#getToken()
	 */
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.request.IBatchOperationCreateRequest#getOperationType()
	 */
	public OperationType getOperationType() {
		return operationType;
	}

	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.request.IBatchOperationCreateRequest#getParameters()
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.request.IBatchOperationCreateRequest#getHardwareIds()
	 */
	public List<String> getHardwareIds() {
		return hardwareIds;
	}

	public void setHardwareIds(List<String> hardwareIds) {
		this.hardwareIds = hardwareIds;
	}
}