/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.request;

import java.util.Date;
import java.util.Map;

/**
 * Provides values needed for sending a {@link IBatchCommandInvocationRequest}
 * based on devices that meet given criteria.
 * 
 * @author Derek
 */
public interface IBatchCommandForCriteriaRequest {

    /**
     * Get unique token for batch request.
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
     * If set, only devices with the given specification token will be sent the
     * command.
     * 
     * @return
     */
    public String getSpecificationToken();

    /**
     * If set, only devices in the given group will be sent the command.
     * 
     * @return
     */
    public String getGroupToken();

    /**
     * If set, only devices in groups with the given role will be sent the
     * command.
     * 
     * @return
     */
    public String getGroupsWithRole();

    /**
     * If set, only devices associated with the given site will be sent the
     * command.
     * 
     * @return
     */
    public String getSiteToken();

    /**
     * Get date range start.
     * 
     * @return
     */
    public Date getStartDate();

    /**
     * Get date range end.
     * 
     * @return
     */
    public Date getEndDate();
}