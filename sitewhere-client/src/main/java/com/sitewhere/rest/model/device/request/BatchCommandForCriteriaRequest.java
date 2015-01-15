/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.sitewhere.spi.device.request.IBatchCommandForCriteriaRequest;

/**
 * Model implementation of {@link IBatchCommandForCriteriaRequest}.
 * 
 * @author Derek
 */
public class BatchCommandForCriteriaRequest implements IBatchCommandForCriteriaRequest {

	/** Token for operation */
	private String token;

	/** Token for command to execute */
	private String commandToken;

	/** Map of parameter values */
	private Map<String, String> parameterValues = new HashMap<String, String>();

	/** Specification token to limit by */
	private String specificationToken;

	/** Indicates whether to exclude assigned devices */
	private boolean excludeAssigned;

	/** Group token to limit by */
	private String groupToken;

	/** Groups with role to limit by */
	private String groupsWithRole;

	/** Start date for create date window */
	private Date startDate;

	/** End date for create date window */
	private Date endDate;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getCommandToken() {
		return commandToken;
	}

	public void setCommandToken(String commandToken) {
		this.commandToken = commandToken;
	}

	public Map<String, String> getParameterValues() {
		return parameterValues;
	}

	public void setParameterValues(Map<String, String> parameterValues) {
		this.parameterValues = parameterValues;
	}

	public String getSpecificationToken() {
		return specificationToken;
	}

	public void setSpecificationToken(String specificationToken) {
		this.specificationToken = specificationToken;
	}

	public boolean isExcludeAssigned() {
		return excludeAssigned;
	}

	public void setExcludeAssigned(boolean excludeAssigned) {
		this.excludeAssigned = excludeAssigned;
	}

	public String getGroupToken() {
		return groupToken;
	}

	public void setGroupToken(String groupToken) {
		this.groupToken = groupToken;
	}

	public String getGroupsWithRole() {
		return groupsWithRole;
	}

	public void setGroupsWithRole(String groupsWithRole) {
		this.groupsWithRole = groupsWithRole;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}