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
import java.util.List;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;

import com.sitewhere.SiteWhere;
import com.sitewhere.Tracer;
import com.sitewhere.common.MarshalUtils;
import com.sitewhere.rest.model.search.tenant.TenantSearchCriteria;
import com.sitewhere.rest.model.tenant.Tenant;
import com.sitewhere.rest.model.tenant.request.TenantCreateRequest;
import com.sitewhere.security.LoginManager;
import com.sitewhere.server.tenant.TenantUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.command.CommandResult;
import com.sitewhere.spi.command.ICommandResponse;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.resource.IResource;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.sitewhere.spi.server.tenant.ISiteWhereTenantEngine;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.configuration.ConfigurationContentParser;
import com.sitewhere.web.configuration.content.ElementContent;
import com.sitewhere.web.rest.RestController;
import com.sitewhere.web.rest.annotations.Concerns;
import com.sitewhere.web.rest.annotations.Concerns.ConcernType;
import com.sitewhere.web.rest.annotations.Documented;
import com.sitewhere.web.rest.annotations.DocumentedController;
import com.sitewhere.web.rest.annotations.Example;
import com.sitewhere.web.rest.annotations.Example.Stage;
import com.sitewhere.web.rest.documentation.Tenants;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for tenant operations.
 * 
 * @author Derek Adams
 */
@Controller
@CrossOrigin
@RequestMapping(value = "/tenants")
@Api(value = "tenants", description = "Operations related to SiteWhere tenants.")
@DocumentedController(name = "Tenants")
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
    @Secured({ SiteWhereRoles.ADMINISTER_TENANTS })
    @Documented(examples = {
	    @Example(stage = Stage.Request, json = Tenants.CreateTenantRequest.class, description = "createTenantRequest.md"),
	    @Example(stage = Stage.Response, json = Tenants.CreateTenantResponse.class, description = "createTenantResponse.md") })
    public ITenant createTenant(@RequestBody TenantCreateRequest request) throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "createTenant", LOGGER);
	try {
	    return SiteWhere.getServer().getTenantManagement().createTenant(request);
	} finally {
	    Tracer.stop(LOGGER);
	}
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
    @Secured({ SiteWhereRoles.ADMINISTER_TENANTS, SiteWhereRoles.ADMINISTER_TENANT_SELF })
    @Documented(examples = {
	    @Example(stage = Stage.Request, json = Tenants.UpdateTenantRequest.class, description = "updateTenantRequest.md"),
	    @Example(stage = Stage.Response, json = Tenants.UpdateTenantResponse.class, description = "updateTenantResponse.md") })
    public ITenant updateTenant(@ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId,
	    @RequestBody TenantCreateRequest request) throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "updateTenant", LOGGER);
	try {
	    assureAuthorizedTenantId(tenantId);
	    return SiteWhere.getServer().getTenantManagement().updateTenant(tenantId, request);
	} finally {
	    Tracer.stop(LOGGER);
	}
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
    @Secured({ SiteWhereRoles.ADMINISTER_TENANTS, SiteWhereRoles.ADMINISTER_TENANT_SELF })
    @Documented(examples = {
	    @Example(stage = Stage.Response, json = Tenants.CreateTenantResponse.class, description = "getTenantByIdResponse.md") })
    public ITenant getTenantById(@ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId,
	    @ApiParam(value = "Include runtime info", required = false) @RequestParam(required = false, defaultValue = "false") boolean includeRuntimeInfo)
	    throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "getTenantById", LOGGER);
	try {
	    ITenant tenant = assureAuthorizedTenantId(tenantId);
	    if (includeRuntimeInfo) {
		ISiteWhereTenantEngine engine = SiteWhere.getServer().getTenantEngine(tenantId);
		if (engine != null) {
		    ((Tenant) tenant).setEngineState(engine.getEngineState());
		}
	    }
	    return tenant;
	} finally {
	    Tracer.stop(LOGGER);
	}
    }

    @RequestMapping(value = "/{tenantId}/engine/{command}", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "Send command to tenant engine")
    @Secured({ SiteWhereRoles.ADMINISTER_TENANTS, SiteWhereRoles.ADMINISTER_TENANT_SELF })
    @Documented(examples = {
	    @Example(stage = Stage.Response, json = Tenants.IssueTenantEngineCommandResponse.class, description = "issueTenantEngineCommandResponse.md") })
    public ICommandResponse issueTenantEngineCommand(
	    @ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId,
	    @ApiParam(value = "Command", required = true) @PathVariable String command) throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "issueTenantEngineCommand", LOGGER);
	try {
	    assureAuthorizedTenantId(tenantId);
	    ISiteWhereTenantEngine engine = SiteWhere.getServer().getTenantEngine(tenantId);
	    if (engine == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidTenantEngineId, ErrorLevel.ERROR);
	    }
	    ICommandResponse response = engine.issueCommand(command, 10);
	    if (response.getResult() == CommandResult.Failed) {
		LOGGER.error("Tenant engine command failed: " + response.getMessage());
	    }
	    return response;
	} finally {
	    Tracer.stop(LOGGER);
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
    @Secured({ SiteWhereRoles.ADMINISTER_TENANTS, SiteWhereRoles.ADMINISTER_TENANT_SELF })
    @Documented
    public String getTenantEngineConfiguration(
	    @ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId) throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "getTenantEngineConfiguration", LOGGER);
	try {
	    assureAuthorizedTenantId(tenantId);
	    IResource configuration = TenantUtils.getActiveTenantConfiguration(tenantId);
	    if (configuration != null) {
		return new String(configuration.getContent());
	    }
	    throw new SiteWhereException("Tenant configuration resource not found.");
	} finally {
	    Tracer.stop(LOGGER);
	}
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
    @Secured({ SiteWhereRoles.ADMINISTER_TENANTS, SiteWhereRoles.ADMINISTER_TENANT_SELF })
    @Documented
    public ElementContent getTenantEngineConfigurationAsJson(
	    @ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId) throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "getTenantEngineConfigurationAsJson", LOGGER);
	try {
	    assureAuthorizedTenantId(tenantId);
	    IResource configuration = TenantUtils.getActiveTenantConfiguration(tenantId);
	    if (configuration != null) {
		return ConfigurationContentParser.parse(configuration.getContent());
	    }
	    throw new SiteWhereException("Tenant configuration resource not found.");
	} finally {
	    Tracer.stop(LOGGER);
	}
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
    @Secured({ SiteWhereRoles.ADMINISTER_TENANTS, SiteWhereRoles.ADMINISTER_TENANT_SELF })
    @Documented
    public ElementContent stageTenantEngineConfiguration(
	    @ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId,
	    HttpServletRequest svtRequest, HttpServletResponse svtResponse) throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "stageTenantEngineConfiguration", LOGGER);
	try {
	    assureAuthorizedTenantId(tenantId);
	    ServletInputStream inData = svtRequest.getInputStream();
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
	} finally {
	    Tracer.stop(LOGGER);
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
    @Secured({ SiteWhereRoles.ADMINISTER_TENANTS, SiteWhereRoles.ADMINISTER_TENANT_SELF })
    @Documented(examples = {
	    @Example(stage = Stage.Response, json = Tenants.CreateTenantResponse.class, description = "getTenantByAuthTokenResponse.md") })
    public ITenant getTenantByAuthToken(
	    @ApiParam(value = "Authentication token", required = true) @PathVariable String authToken)
	    throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "getTenantByAuthToken", LOGGER);
	try {
	    return assureAuthorizedTenant(
		    SiteWhere.getServer().getTenantManagement().getTenantByAuthenticationToken(authToken));
	} finally {
	    Tracer.stop(LOGGER);
	}
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
    @Secured({ SiteWhereRoles.ADMINISTER_TENANTS })
    @Documented(examples = {
	    @Example(stage = Stage.Response, json = Tenants.ListTenantsResponse.class, description = "listTenantsResponse.md") })
    public ISearchResults<ITenant> listTenants(
	    @ApiParam(value = "Text search (partial id or name)", required = false) @RequestParam(required = false) String textSearch,
	    @ApiParam(value = "Authorized user id", required = false) @RequestParam(required = false) String authUserId,
	    @ApiParam(value = "Include runtime info", required = false) @RequestParam(required = false, defaultValue = "false") boolean includeRuntimeInfo,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") @Concerns(values = {
		    ConcernType.Paging }) int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") @Concerns(values = {
		    ConcernType.Paging }) int pageSize)
	    throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "listTenants", LOGGER);
	try {
	    TenantSearchCriteria criteria = new TenantSearchCriteria(page, pageSize);
	    criteria.setTextSearch(textSearch);
	    criteria.setUserId(authUserId);
	    criteria.setIncludeRuntimeInfo(includeRuntimeInfo);
	    return SiteWhere.getServer().getTenantManagement().listTenants(criteria);
	} finally {
	    Tracer.stop(LOGGER);
	}
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
    @Secured({ SiteWhereRoles.ADMINISTER_TENANTS, SiteWhereRoles.ADMINISTER_TENANT_SELF })
    @Documented(examples = {
	    @Example(stage = Stage.Response, json = Tenants.CreateTenantResponse.class, description = "deleteTenantByIdResponse.md") })
    public ITenant deleteTenantById(@ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId,
	    @ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force)
	    throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "deleteTenantById", LOGGER);
	try {
	    assureAuthorizedTenantId(tenantId);
	    return SiteWhere.getServer().getTenantManagement().deleteTenant(tenantId, force);
	} finally {
	    Tracer.stop(LOGGER);
	}
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
    @Secured({ SiteWhereRoles.ADMINISTER_TENANTS })
    public List<ITenant> listTenantsForDevice(
	    @ApiParam(value = "Hardware id", required = true) @PathVariable String hardwareId,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "listTenantsForDevice", LOGGER);
	try {
	    List<ITenant> tenants = SiteWhere.getServer()
		    .getAuthorizedTenants(LoginManager.getCurrentlyLoggedInUser().getUsername(), true);
	    List<ITenant> matches = new ArrayList<ITenant>();
	    for (ITenant tenant : tenants) {
		if (SiteWhere.getServer().getDeviceManagement(tenant).getDeviceByHardwareId(hardwareId) != null) {
		    matches.add(tenant);
		}
	    }
	    return matches;
	} finally {
	    Tracer.stop(LOGGER);
	}
    }
}