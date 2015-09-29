/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import org.apache.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.SiteWhere;
import com.sitewhere.Tracer;
import com.sitewhere.core.user.SitewhereRoles;
import com.sitewhere.rest.model.search.user.TenantSearchCriteria;
import com.sitewhere.rest.model.user.Tenant;
import com.sitewhere.rest.model.user.request.TenantCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.command.ICommandResponse;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.ISiteWhereTenantEngine;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.sitewhere.spi.system.IVersion;
import com.sitewhere.spi.user.ITenant;
import com.sitewhere.web.rest.annotations.Concerns;
import com.sitewhere.web.rest.annotations.Concerns.ConcernType;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for tenant operations.
 * 
 * @author Derek Adams
 */
@Controller
@RequestMapping(value = "/tenants")
@Api(value = "tenants", description = "Operations related to SiteWhere tenants.")
public class TenantsController extends SiteWhereController {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(TenantsController.class);

	/**
	 * Create a new tenant.
	 * 
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create a new tenant")
	@Secured({ SitewhereRoles.ROLE_ADMINISTER_USERS })
	public ITenant createTenant(@RequestBody TenantCreateRequest request) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "createTenant", LOGGER);
		try {
			return SiteWhere.getServer().getUserManagement().createTenant(request);
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
	@Secured({ SitewhereRoles.ROLE_ADMINISTER_USERS })
	public ITenant updateTenant(
			@ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId,
			@RequestBody TenantCreateRequest request) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "updateTenant", LOGGER);
		try {
			return SiteWhere.getServer().getUserManagement().updateTenant(tenantId, request);
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
	@ApiOperation(value = "Find tenant by unique id")
	@Secured({ SitewhereRoles.ROLE_ADMINISTER_USERS })
	public ITenant getTenantById(
			@ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId,
			@ApiParam(value = "Include runtime info", required = false) @RequestParam(required = false, defaultValue = "false") boolean includeRuntimeInfo)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "getTenantById", LOGGER);
		try {
			ITenant tenant = SiteWhere.getServer().getUserManagement().getTenantById(tenantId);
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
	@ApiOperation(value = "Send a command to a tenant engine")
	@Secured({ SitewhereRoles.ROLE_ADMINISTER_USERS })
	public ICommandResponse issueTenantEngineCommand(
			@ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId,
			@ApiParam(value = "Command", required = true) @PathVariable String command)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "issueTenantEngineCommand", LOGGER);
		try {
			ISiteWhereTenantEngine engine = SiteWhere.getServer().getTenantEngine(tenantId);
			if (engine == null) {
				throw new SiteWhereSystemException(ErrorCode.InvalidTenantEngineId, ErrorLevel.ERROR);
			}
			return engine.issueCommand(command, 10);
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
	@ApiOperation(value = "Get current configuration for a tenant engine")
	@Secured({ SitewhereRoles.ROLE_ADMINISTER_USERS })
	public String getTenantEngineConfiguration(
			@ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "getTenantEngineConfiguration", LOGGER);
		try {
			ISiteWhereTenantEngine engine = SiteWhere.getServer().getTenantEngine(tenantId);
			if (engine == null) {
				throw new SiteWhereSystemException(ErrorCode.InvalidTenantEngineId, ErrorLevel.ERROR);
			}
			IVersion version = SiteWhere.getServer().getVersion();
			return engine.getConfigurationResolver().getTenantConfiguration(engine.getTenant(), version);
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
	@ApiOperation(value = "Find tenant by authentication token")
	@Secured({ SitewhereRoles.ROLE_ADMINISTER_USERS })
	public ITenant getTenantByAuthToken(
			@ApiParam(value = "Authentication token", required = true) @PathVariable String authToken)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "getTenantByAuthToken", LOGGER);
		try {
			return SiteWhere.getServer().getUserManagement().getTenantByAuthenticationToken(authToken);
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
	@ApiOperation(value = "Find tenants that match criteria")
	@Secured({ SitewhereRoles.ROLE_ADMINISTER_USERS })
	public ISearchResults<ITenant> listTenants(
			@ApiParam(value = "Authorized user id", required = false) @RequestParam(required = false) String authUserId,
			@ApiParam(value = "Include runtime info", required = false) @RequestParam(required = false, defaultValue = "false") boolean includeRuntimeInfo,
			@ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") @Concerns(values = { ConcernType.Paging }) int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") @Concerns(values = { ConcernType.Paging }) int pageSize)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "listTenants", LOGGER);
		try {
			TenantSearchCriteria criteria = new TenantSearchCriteria(page, pageSize);
			criteria.setUserId(authUserId);
			criteria.setIncludeRuntimeInfo(includeRuntimeInfo);
			return SiteWhere.getServer().getUserManagement().listTenants(criteria);
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
	@ApiOperation(value = "Delete a tenant by id")
	@Secured({ SitewhereRoles.ROLE_ADMINISTER_USERS })
	public ITenant deleteTenantById(
			@ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId,
			@ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "deleteTenantById", LOGGER);
		try {
			return SiteWhere.getServer().getUserManagement().deleteTenant(tenantId, force);
		} finally {
			Tracer.stop(LOGGER);
		}
	}
}