/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule;

import java.util.HashMap;
import java.util.Map;

import com.sitewhere.rest.model.device.request.BatchCommandForCriteriaRequest;
import com.sitewhere.spi.scheduling.JobConstants;

public class BatchCommandInvocationJobParser {

    /**
     * Parse configuration data.
     * 
     * @param data
     */
    public static BatchCommandForCriteriaRequest parse(Map<String, String> data) {

	String specificationToken = null;
	String siteToken = null;
	String groupToken = null;
	String groupRole = null;
	String commandToken = null;
	Map<String, String> parameters = new HashMap<String, String>();

	for (String key : data.keySet()) {
	    String value = data.get(key);
	    if (JobConstants.BatchCommandInvocation.SPECIFICATION_TOKEN.equals(key)) {
		specificationToken = value;
	    } else if (JobConstants.BatchCommandInvocation.SITE_TOKEN.equals(key)) {
		siteToken = value;
	    } else if (JobConstants.BatchCommandInvocation.GROUP_TOKEN.equals(key)) {
		groupToken = value;
	    } else if (JobConstants.BatchCommandInvocation.GROUP_ROLE.equals(key)) {
		groupRole = value;
	    } else if (JobConstants.CommandInvocation.COMMAND_TOKEN.equals(key)) {
		commandToken = value;
	    } else if (key.startsWith(JobConstants.CommandInvocation.PARAMETER_PREFIX)) {
		String paramKey = key.substring(JobConstants.CommandInvocation.PARAMETER_PREFIX.length());
		parameters.put(paramKey, value);
	    }
	}

	BatchCommandForCriteriaRequest request = new BatchCommandForCriteriaRequest();
	request.setCommandToken(commandToken);
	request.setParameterValues(parameters);
	request.setSpecificationToken(specificationToken);
	request.setSiteToken(siteToken);
	request.setGroupToken(groupToken);
	request.setGroupsWithRole(groupRole);
	return request;
    }
}
