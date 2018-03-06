/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.batch.request;

import java.util.List;
import java.util.Map;

/**
 * Parameters for a batch operation that executes a command for all devices in
 * the batch.
 * 
 * @author Derek
 */
public interface IBatchCommandInvocationRequest {

    /** Batch operation parameter name for command token */
    public static final String PARAM_COMMAND_TOKEN = "commandToken";

    /** Metadata property on batch element that holds invocation event id */
    public static final String META_INVOCATION_EVENT_ID = "invocation";

    /**
     * Get unique token for request.
     * 
     * @return
     */
    public String getToken();

    /**
     * Get token for command to be executed.
     * 
     * @return
     */
    public String getCommandToken();

    /**
     * Get the list of parameter names mapped to values.
     * 
     * @return
     */
    public Map<String, String> getParameterValues();

    /**
     * Get the list of targeted device tokens.
     * 
     * @return
     */
    public List<String> getDeviceTokens();
}