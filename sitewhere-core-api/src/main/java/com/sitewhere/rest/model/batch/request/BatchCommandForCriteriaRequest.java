/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.batch.request;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sitewhere.rest.model.datatype.JsonDateSerializer;
import com.sitewhere.spi.batch.request.IBatchCommandForCriteriaRequest;

/**
 * Model implementation of {@link IBatchCommandForCriteriaRequest}.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class BatchCommandForCriteriaRequest implements IBatchCommandForCriteriaRequest, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = -7139262971572322676L;

    /** Token for operation */
    private String token;

    /** Token for command to execute */
    private String commandToken;

    /** Map of parameter values */
    private Map<String, String> parameterValues = new HashMap<String, String>();

    /** Device type token to limit by */
    private String deviceTypeToken;

    /** Group token to limit by */
    private String groupToken;

    /** Groups with role to limit by */
    private String groupsWithRole;

    /** Area token to limit by */
    private String areaToken;

    /** Start date for create date window */
    private Date startDate;

    /** End date for create date window */
    private Date endDate;

    /*
     * @see
     * com.sitewhere.spi.batch.request.IBatchCommandForCriteriaRequest#getToken()
     */
    @Override
    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

    /*
     * @see com.sitewhere.spi.batch.request.IBatchCommandForCriteriaRequest#
     * getCommandToken()
     */
    @Override
    public String getCommandToken() {
	return commandToken;
    }

    public void setCommandToken(String commandToken) {
	this.commandToken = commandToken;
    }

    /*
     * @see com.sitewhere.spi.batch.request.IBatchCommandForCriteriaRequest#
     * getParameterValues()
     */
    @Override
    public Map<String, String> getParameterValues() {
	return parameterValues;
    }

    public void setParameterValues(Map<String, String> parameterValues) {
	this.parameterValues = parameterValues;
    }

    /*
     * @see com.sitewhere.spi.batch.request.IBatchCommandForCriteriaRequest#
     * getDeviceTypeToken()
     */
    @Override
    public String getDeviceTypeToken() {
	return deviceTypeToken;
    }

    public void setDeviceTypeToken(String deviceTypeToken) {
	this.deviceTypeToken = deviceTypeToken;
    }

    /*
     * @see
     * com.sitewhere.spi.batch.request.IBatchCommandForCriteriaRequest#getGroupToken
     * ()
     */
    @Override
    public String getGroupToken() {
	return groupToken;
    }

    public void setGroupToken(String groupToken) {
	this.groupToken = groupToken;
    }

    /*
     * @see com.sitewhere.spi.batch.request.IBatchCommandForCriteriaRequest#
     * getGroupsWithRole()
     */
    @Override
    public String getGroupsWithRole() {
	return groupsWithRole;
    }

    public void setGroupsWithRole(String groupsWithRole) {
	this.groupsWithRole = groupsWithRole;
    }

    /*
     * @see
     * com.sitewhere.spi.batch.request.IBatchCommandForCriteriaRequest#getAreaToken(
     * )
     */
    @Override
    public String getAreaToken() {
	return areaToken;
    }

    public void setAreaToken(String areaToken) {
	this.areaToken = areaToken;
    }

    /*
     * @see
     * com.sitewhere.spi.batch.request.IBatchCommandForCriteriaRequest#getStartDate(
     * )
     */
    @Override
    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getStartDate() {
	return startDate;
    }

    public void setStartDate(Date startDate) {
	this.startDate = startDate;
    }

    /*
     * @see
     * com.sitewhere.spi.batch.request.IBatchCommandForCriteriaRequest#getEndDate()
     */
    @Override
    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getEndDate() {
	return endDate;
    }

    public void setEndDate(Date endDate) {
	this.endDate = endDate;
    }
}