/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.rest.model.search.tenant.TenantSearchCriteria;
import com.sitewhere.rest.model.tenant.Tenant;
import com.sitewhere.rest.model.tenant.request.TenantCreateRequest;
import com.sitewhere.security.LoginManager;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.server.tenant.TenantUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.command.CommandResult;
import com.sitewhere.spi.command.ICommandResponse;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.monitoring.IProgressMessage;
import com.sitewhere.spi.resource.IResource;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.tenant.ISiteWhereTenantEngine;
import com.sitewhere.spi.server.tenant.ITenantTemplate;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.SiteWhereAuthority;
import com.sitewhere.web.SiteWhere;
import com.sitewhere.web.configuration.ConfigurationContentParser;
import com.sitewhere.web.configuration.TenantConfigurationModel;
import com.sitewhere.web.configuration.content.ElementContent;
import com.sitewhere.web.configuration.model.ElementRole;
import com.sitewhere.web.rest.RestController;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for tenant operations.
 * 
 * @author Derek Adams
 */
@Controller
@CrossOrigin(exposedHeaders = { "X-SiteWhere-Error", "X-SiteWhere-Error-Code" })
@RequestMapping(value = "/tenants")
@Api(value = "tenants", description = "Operations related to SiteWhere tenants.")
public class TenantsController extends RestController {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /**
     * Create a new tenant.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "Create new tenant")
    public ITenant createTenant(@RequestBody TenantCreateRequest request, HttpServletRequest servletRequest,
	    HttpServletResponse servletResponse) throws SiteWhereException {
	checkAuthForAll(servletRequest, servletResponse, SiteWhereAuthority.REST, SiteWhereAuthority.AdminTenants);
	return SiteWhere.getServer().getTenantManagement().createTenant(request);
    }

    /**
     * Update an existing tenant.
     * 
     * @param tenantId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{tenantId}", method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation(value = "Update an existing tenant.")
    public ITenant updateTenant(@ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId,
	    @RequestBody TenantCreateRequest request, HttpServletRequest servletRequest,
	    HttpServletResponse servletResponse) throws SiteWhereException {
	ITenant tenant = assureTenant(tenantId);
	checkForAdminOrEditSelf(servletRequest, servletResponse, tenant);
	return SiteWhere.getServer().getTenantManagement().updateTenant(tenantId, request);
    }

    /**
     * Get a tenant by unique id.
     * 
     * @param tenantId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{tenantId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get tenant by unique id")
    public ITenant getTenantById(@ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId,
	    @ApiParam(value = "Include runtime info", required = false) @RequestParam(required = false, defaultValue = "false") boolean includeRuntimeInfo,
	    HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws SiteWhereException {
	ITenant tenant = assureTenant(tenantId);
	checkForAdminOrEditSelf(servletRequest, servletResponse, tenant);
	if (includeRuntimeInfo) {
	    ISiteWhereTenantEngine engine = SiteWhere.getServer().getTenantEngine(tenantId);
	    if (engine != null) {
		((Tenant) tenant).setEngineState(engine.getEngineState());
	    }
	}
	return tenant;
    }

    @RequestMapping(value = "/{tenantId}/engine/{command}", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "Send command to tenant engine")
    public ICommandResponse issueTenantEngineCommand(
	    @ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId,
	    @ApiParam(value = "Command", required = true) @PathVariable String command,
	    HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws SiteWhereException {
	try {
	    // Verify authorization and engine exists.
	    ITenant tenant = assureTenant(tenantId);
	    checkForAdminOrEditSelf(servletRequest, servletResponse, tenant);
	    ISiteWhereTenantEngine engine = SiteWhere.getServer().getTenantEngine(tenantId);
	    if (engine == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidTenantEngineId, ErrorLevel.ERROR);
	    }

	    // Required to allow partial response processing in browser.
	    servletResponse.setContentType(MediaType.TEXT_HTML_VALUE);

	    // Issue command and monitor progress.
	    ICommandResponse response = engine.issueCommand(command,
		    new LifecycleProgressMonitor(new LifecycleProgressContext(1, "Issue tenant engine command")) {

			@Override
			public void reportProgress(IProgressMessage message) throws SiteWhereException {
			    try {
				servletResponse.getOutputStream().println(MarshalUtils.marshalJsonAsString(message));
				servletResponse.getOutputStream().flush();
			    } catch (IOException e) {
				LOGGER.warn("Unable to write progress to stream.", e);
			    }
			}
		    });
	    if (response.getResult() == CommandResult.Failed) {
		LOGGER.error("Tenant engine command failed: " + response.getMessage());
	    }
	    return response;
	} finally {
	    try {
		servletResponse.getOutputStream().flush();
	    } catch (IOException e) {
		LOGGER.warn("Unable to close output stream.");
	    }
	}
    }

    /**
     * Get the current configuration for a tenant engine.
     * 
     * @param tenantId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{tenantId}/engine/configuration", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get tenant engine configuration")
    public String getTenantEngineConfiguration(
	    @ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId,
	    HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws SiteWhereException {
	ITenant tenant = assureTenant(tenantId);
	checkForAdminOrEditSelf(servletRequest, servletResponse, tenant);
	IResource configuration = TenantUtils.getActiveTenantConfiguration(tenantId);
	if (configuration != null) {
	    return new String(configuration.getContent());
	}
	throw new SiteWhereException("Tenant configuration resource not found.");
    }

    /**
     * Get the current configuration for a tenant engine formatted as JSON.
     * 
     * @param tenantId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{tenantId}/engine/configuration/json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get tenant engine configuration as JSON")
    public ElementContent getTenantEngineConfigurationAsJson(
	    @ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId,
	    HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws SiteWhereException {
	ITenant tenant = assureTenant(tenantId);
	checkForAdminOrEditSelf(servletRequest, servletResponse, tenant);
	IResource configuration = TenantUtils.getActiveTenantConfiguration(tenantId);
	if (configuration != null) {
	    return ConfigurationContentParser.parse(configuration.getContent());
	}
	throw new SiteWhereException("Tenant configuration resource not found.");
    }

    /**
     * Stages a new tenant configuration based on a JSON representation of the
     * configuration. Returns the XML configuration that was staged.
     * 
     * @param tenantId
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{tenantId}/engine/configuration/json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "Stage tenant engine configuration from JSON")
    public ElementContent stageTenantEngineConfiguration(
	    @ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId,
	    HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws SiteWhereException {
	try {
	    ITenant tenant = assureTenant(tenantId);
	    checkForAdminOrEditSelf(servletRequest, servletResponse, tenant);
	    ServletInputStream inData = servletRequest.getInputStream();
	    ByteArrayOutputStream byteData = new ByteArrayOutputStream();
	    int data;
	    while ((data = inData.read()) != -1) {
		byteData.write(data);
	    }
	    byteData.close();
	    ElementContent content = MarshalUtils.unmarshalJson(byteData.toByteArray(), ElementContent.class);
	    Document document = ConfigurationContentParser.buildXml(content);
	    String xml = ConfigurationContentParser.format(document);
	    TenantUtils.stageTenantConfiguration(tenantId, xml);
	    return content;
	} catch (IOException e) {
	    throw new SiteWhereException("Error staging tenant configuration.", e);
	}
    }

    /**
     * Get a tenant by unique authentication token.
     * 
     * @param authToken
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/authtoken/{authToken}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get tenant by authentication token")
    public ITenant getTenantByAuthToken(
	    @ApiParam(value = "Authentication token", required = true) @PathVariable String authToken,
	    HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws SiteWhereException {
	ITenant tenant = SiteWhere.getServer().getTenantManagement().getTenantByAuthenticationToken(authToken);
	if (tenant != null) {
	    checkForAdminOrEditSelf(servletRequest, servletResponse, tenant);
	    return tenant;
	}
	return null;
    }

    /**
     * List tenants that match the given criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "List tenants that match criteria")
    public ISearchResults<ITenant> listTenants(
	    @ApiParam(value = "Text search (partial id or name)", required = false) @RequestParam(required = false) String textSearch,
	    @ApiParam(value = "Authorized user id", required = false) @RequestParam(required = false) String authUserId,
	    @ApiParam(value = "Include runtime info", required = false) @RequestParam(required = false, defaultValue = "false") boolean includeRuntimeInfo,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws SiteWhereException {
	checkAuthFor(servletRequest, servletResponse, SiteWhereAuthority.REST, true);

	// Return all tenants if authorized as tenant admin.
	if (checkAuthFor(servletRequest, servletResponse, SiteWhereAuthority.AdminTenants, false)) {
	    TenantSearchCriteria criteria = new TenantSearchCriteria(page, pageSize);
	    criteria.setTextSearch(textSearch);
	    criteria.setUserId(authUserId);
	    criteria.setIncludeRuntimeInfo(includeRuntimeInfo);
	    return SiteWhere.getServer().getTenantManagement().listTenants(criteria);
	}

	// Only return auth tenants if user has 'admin own tenant'.
	else if (checkAuthFor(servletRequest, servletResponse, SiteWhereAuthority.AdminOwnTenant, false)) {
	    IUser loggedIn = LoginManager.getCurrentlyLoggedInUser();
	    if (loggedIn != null) {
		TenantSearchCriteria criteria = new TenantSearchCriteria(page, pageSize);
		criteria.setTextSearch(textSearch);
		criteria.setUserId(loggedIn.getUsername());
		criteria.setIncludeRuntimeInfo(includeRuntimeInfo);
		return SiteWhere.getServer().getTenantManagement().listTenants(criteria);
	    }
	}

	throw new SiteWhereSystemException(ErrorCode.OperationNotPermitted, ErrorLevel.ERROR,
		HttpServletResponse.SC_FORBIDDEN);
    }

    /**
     * Delete tenant by unique tenant id.
     * 
     * @param tenantId
     * @param force
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{tenantId}", method = RequestMethod.DELETE)
    @ResponseBody
    @ApiOperation(value = "Delete existing tenant")
    public ITenant deleteTenantById(@ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId,
	    @ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force,
	    HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws SiteWhereException {
	checkAuthForAll(servletRequest, servletResponse, SiteWhereAuthority.REST, SiteWhereAuthority.AdminTenants);
	ITenant tenant = assureTenant(tenantId);
	checkForAdminOrEditSelf(servletRequest, servletResponse, tenant);
	return SiteWhere.getServer().getTenantManagement().deleteTenant(tenantId, force);
    }

    /**
     * Lists all tenants that contain a device with the given hardware id.
     * 
     * @param hardwareId
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/device/{hardwareId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "List tenants that contain a device")
    public List<ITenant> listTenantsForDevice(
	    @ApiParam(value = "Hardware id", required = true) @PathVariable String hardwareId,
	    HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws SiteWhereException {
	checkAuthForAll(servletRequest, servletResponse, SiteWhereAuthority.REST, SiteWhereAuthority.AdminTenants);
	List<ITenant> tenants = SiteWhere.getServer()
		.getAuthorizedTenants(LoginManager.getCurrentlyLoggedInUser().getUsername(), true);
	List<ITenant> matches = new ArrayList<ITenant>();
	for (ITenant tenant : tenants) {
	    if (SiteWhere.getServer().getDeviceManagement(tenant).getDeviceByHardwareId(hardwareId) != null) {
		matches.add(tenant);
	    }
	}
	return matches;
    }

    /**
     * Get tenant configuration model as a JSON object.
     * 
     * @param servletRequest
     * @param servletResponse
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/configuration/model", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get hierarchical model for tenant configuration")
    public TenantConfigurationModel getTenantConfigurationModel(HttpServletRequest servletRequest,
	    HttpServletResponse servletResponse) throws SiteWhereException {
	checkAuthFor(servletRequest, servletResponse, SiteWhereAuthority.REST, true);
	return new TenantConfigurationModel();
    }

    /**
     * Get tenant configuration roles.
     * 
     * @param servletRequest
     * @param servletResponse
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/configuration/roles", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get role information for tenant configuration")
    public Map<String, ElementRole> getTenantConfigurationRoles(HttpServletRequest servletRequest,
	    HttpServletResponse servletResponse) throws SiteWhereException {
	checkAuthFor(servletRequest, servletResponse, SiteWhereAuthority.REST, true);
	ElementRole[] roles = ElementRole.values();
	Map<String, ElementRole> rolesById = new HashMap<String, ElementRole>();
	for (ElementRole role : roles) {
	    rolesById.put(role.name(), role);
	}
	return rolesById;
    }

    /**
     * Lists all available tenant templates.
     * 
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/templates", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "List templates available for creating tenants")
    public List<ITenantTemplate> listTenantTemplates(HttpServletRequest servletRequest,
	    HttpServletResponse servletResponse) throws SiteWhereException {
	checkAuthFor(servletRequest, servletResponse, SiteWhereAuthority.REST, true);
	if (checkAuthFor(servletRequest, servletResponse, SiteWhereAuthority.AdminTenants, false)
		|| checkAuthFor(servletRequest, servletResponse, SiteWhereAuthority.AdminOwnTenant, false)) {
	}
	// return
	// SiteWhere.getServer().getTenantTemplateManager().getTenantTemplates();
	return null; // TODO: Figure out access to tenant templates.
    }

    /**
     * Assure that a tenant exists for the given id.
     * 
     * @param tenantId
     * @return
     * @throws SiteWhereException
     */
    protected ITenant assureTenant(String tenantId) throws SiteWhereException {
	ITenant tenant = SiteWhere.getServer().getTenantManagement().getTenantById(tenantId);
	if (tenant == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidTenantId, ErrorLevel.ERROR);
	}
	return tenant;
    }

    /**
     * Check for privileges to use REST services + either admin all tenants or
     * admin own tenant on the currently logged in user.
     * 
     * @param servletRequest
     * @param servletResponse
     * @param tenant
     * @throws SiteWhereException
     */
    public static void checkForAdminOrEditSelf(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
	    ITenant tenant) throws SiteWhereException {
	checkAuthFor(servletRequest, servletResponse, SiteWhereAuthority.REST, true);
	if (!checkAuthFor(servletRequest, servletResponse, SiteWhereAuthority.AdminTenants, false)) {
	    checkAuthFor(servletRequest, servletResponse, SiteWhereAuthority.AdminOwnTenant, true);
	    IUser loggedIn = LoginManager.getCurrentlyLoggedInUser();
	    if ((loggedIn == null) || (!tenant.getAuthorizedUserIds().contains(loggedIn.getUsername()))) {
		throw new SiteWhereSystemException(ErrorCode.OperationNotPermitted, ErrorLevel.ERROR,
			HttpServletResponse.SC_FORBIDDEN);
	    }
	}
    }
}