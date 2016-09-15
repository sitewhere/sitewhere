/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.request;

import java.util.List;
import java.util.Map;

import com.sitewhere.spi.device.batch.IBatchOperation;
import com.sitewhere.spi.device.batch.OperationType;

/**
 * Provides information needex to create an {@link IBatchOperation}.
 * 
 * @author Derek
 */
public interface IBatchOperationCreateRequest {

    /** Metadata property on events that holds batch id that generated event */
    public static final String META_BATCH_OPERATION_ID = "batch";

    /**
     * Get the unique token.
     * 
     * @return
     */
    public String getToken();

    /**
     * Get operation to be performed.
     * 
     * @return
     */
    public OperationType getOperationType();

    /**
     * Get operation parameters.
     * 
     * @return
     */
    public Map<String, String> getParameters();

    /**
     * Get list of hardware ids for devices to be operated on.
     * 
     * @return
     */
    public List<String> getHardwareIds();

    /**
     * Get metadata entries.
     * 
     * @return
     */
    public Map<String, String> getMetadata();
}