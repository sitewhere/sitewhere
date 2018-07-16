/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.rest.model.search.tenant.TenantSearchCriteria;
import com.sitewhere.rest.model.tenant.request.TenantCreateRequest;
import com.sitewhere.security.UserContextManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.multitenant.IDatasetTemplate;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantAdministration;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.SiteWhereAuthority;
import com.sitewhere.web.annotation.SiteWhereCrossOrigin;
import com.sitewhere.web.rest.RestControllerBase;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for tenant operations.
 * 
 * @author Derek Adams
 */
@RestController
@SiteWhereCrossOrigin
@RequestMapping(value = "/tenants")
@Api(value = "tenants")
public class Tenants extends RestControllerBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Tenants.class);

    /**
     * Create a new tenant.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Create new tenant")
    public ITenant createTenant(@RequestBody TenantCreateRequest request) throws SiteWhereException {
	checkAuthForAll(SiteWhereAuthority.REST, SiteWhereAuthority.AdminTenants);
	return getTenantManagement().createTenant(request);
    }

    /**
     * Update an existing tenant.
     * 
     * @param tenantToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{tenantToken}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update an existing tenant.")
    public ITenant updateTenant(@ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken,
	    @RequestBody TenantCreateRequest request) throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	checkForAdminOrEditSelf(tenant);
	return getTenantManagement().updateTenant(tenant.getId(), request);
    }

    /**
     * Get a tenant by unique id.
     * 
     * @param tenantToken
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{tenantToken}", method = RequestMethod.GET)
    @ApiOperation(value = "Get tenant by token")
    public ITenant getTenantByToken(@ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken)
	    throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	checkForAdminOrEditSelf(tenant);
	return tenant;
    }

    /**
     * List tenants that match the given criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "List tenants that match criteria")
    public ISearchResults<ITenant> listTenants(
	    @ApiParam(value = "Text search (partial id or name)", required = false) @RequestParam(required = false) String textSearch,
	    @ApiParam(value = "Authorized user id", required = false) @RequestParam(required = false) String authUserId,
	    @ApiParam(value = "Include runtime info", required = false) @RequestParam(required = false, defaultValue = "false") boolean includeRuntimeInfo,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize)
	    throws SiteWhereException {
	checkAuthFor(SiteWhereAuthority.REST, true);

	// Return all tenants if authorized as tenant admin.
	if (checkAuthFor(SiteWhereAuthority.AdminTenants, false)) {
	    TenantSearchCriteria criteria = new TenantSearchCriteria(page, pageSize);
	    criteria.setTextSearch(textSearch);
	    criteria.setUserId(authUserId);
	    criteria.setIncludeRuntimeInfo(includeRuntimeInfo);
	    return getTenantManagement().listTenants(criteria);
	}

	// Only return auth tenants if user has 'admin own tenant'.
	else if (checkAuthFor(SiteWhereAuthority.AdminOwnTenant, false)) {
	    IUser loggedIn = UserContextManager.getCurrentlyLoggedInUser();
	    if (loggedIn != null) {
		TenantSearchCriteria criteria = new TenantSearchCriteria(page, pageSize);
		criteria.setTextSearch(textSearch);
		criteria.setUserId(loggedIn.getUsername());
		criteria.setIncludeRuntimeInfo(includeRuntimeInfo);
		return getTenantManagement().listTenants(criteria);
	    }
	}

	throw operationNotPermitted();
    }

    /**
     * Delete tenant by token.
     * 
     * @param tenantToken
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{tenantToken}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete existing tenant")
    public ITenant deleteTenantById(@ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken)
	    throws SiteWhereException {
	checkAuthForAll(SiteWhereAuthority.REST, SiteWhereAuthority.AdminTenants);
	ITenant tenant = assureTenant(tenantToken);
	checkForAdminOrEditSelf(tenant);
	return getTenantManagement().deleteTenant(tenant.getId());
    }

    /**
     * Lists all available tenant templates.
     * 
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/templates", method = RequestMethod.GET)
    @ApiOperation(value = "List templates available for creating tenants")
    public List<ITenantTemplate> listTenantTemplates() throws SiteWhereException {
	checkAuthFor(SiteWhereAuthority.REST, true);
	if (checkAuthFor(SiteWhereAuthority.AdminTenants, false)
		|| checkAuthFor(SiteWhereAuthority.AdminOwnTenant, false)) {
	}
	return getTenantAdministration().getTenantTemplates();
    }

    /**
     * Lists all available dataset templates.
     * 
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/datasets", method = RequestMethod.GET)
    @ApiOperation(value = "List datasets available for creating tenants")
    public List<IDatasetTemplate> listDatasetTemplates() throws SiteWhereException {
	checkAuthFor(SiteWhereAuthority.REST, true);
	if (checkAuthFor(SiteWhereAuthority.AdminTenants, false)
		|| checkAuthFor(SiteWhereAuthority.AdminOwnTenant, false)) {
	}
	return getTenantAdministration().getDatasetTemplates();
    }

    /**
     * Check for privileges to use REST services + either admin all tenants or admin
     * own tenant on the currently logged in user.
     * 
     * @param tenant
     * @throws SiteWhereException
     */
    public static void checkForAdminOrEditSelf(ITenant tenant) throws SiteWhereException {
	checkAuthFor(SiteWhereAuthority.REST, true);
	if (!checkAuthFor(SiteWhereAuthority.AdminTenants, false)) {
	    checkAuthFor(SiteWhereAuthority.AdminOwnTenant, true);
	    IUser loggedIn = UserContextManager.getCurrentlyLoggedInUser();
	    if ((loggedIn == null) || (!tenant.getAuthorizedUserIds().contains(loggedIn.getUsername()))) {
		throw new SiteWhereSystemException(ErrorCode.OperationNotPermitted, ErrorLevel.ERROR,
			HttpServletResponse.SC_FORBIDDEN);
	    }
	}
    }

    /**
     * Assure that a tenant exists for the given token.
     * 
     * @param tenantToken
     * @return
     * @throws SiteWhereException
     */
    protected ITenant assureTenant(String tenantToken) throws SiteWhereException {
	ITenant tenant = getTenantManagement().getTenantByToken(tenantToken);
	if (tenant == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidTenantToken, ErrorLevel.ERROR);
	}
	return tenant;
    }

    private ITenantManagement getTenantManagement() {
	return getMicroservice().getTenantManagementApiDemux().getApiChannel();
    }

    private ITenantAdministration getTenantAdministration() {
	return getMicroservice().getTenantManagementApiDemux().getApiChannel();
    }
}