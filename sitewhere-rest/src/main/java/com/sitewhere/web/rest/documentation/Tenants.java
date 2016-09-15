/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.rest.model.command.CommandResponse;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.tenant.request.TenantCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.command.CommandResult;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.web.rest.documentation.ExampleData.Tenant_Default;

/**
 * Example of REST requests for interacting with tenants.
 * 
 * @author Derek
 */
public class Tenants {

    public static class CreateTenantRequest {

	public Object generate() throws SiteWhereException {
	    TenantCreateRequest request = new TenantCreateRequest();
	    request.setId(ExampleData.TENANT_DEFAULT.getId());
	    request.setName(ExampleData.TENANT_DEFAULT.getName());
	    request.setAuthenticationToken(ExampleData.TENANT_DEFAULT.getAuthenticationToken());
	    request.setLogoUrl(ExampleData.TENANT_DEFAULT.getLogoUrl());
	    request.setAuthorizedUserIds(ExampleData.TENANT_DEFAULT.getAuthorizedUserIds());
	    request.setMetadata(null);
	    return request;
	}
    }

    public static class CreateTenantResponse {

	public Object generate() throws SiteWhereException {
	    return ExampleData.TENANT_DEFAULT;
	}
    }

    public static class UpdateTenantRequest {

	public Object generate() throws SiteWhereException {
	    TenantCreateRequest request = new TenantCreateRequest();
	    request.setName(ExampleData.TENANT_DEFAULT.getName() + " Updated");
	    request.setMetadata(null);
	    return request;
	}
    }

    public static class UpdateTenantResponse {

	public Object generate() throws SiteWhereException {
	    Tenant_Default tenant = new Tenant_Default();
	    tenant.setName(ExampleData.TENANT_DEFAULT.getName() + " Updated");
	    return tenant;
	}
    }

    public static class IssueTenantEngineCommandResponse {

	public Object generate() throws SiteWhereException {
	    CommandResponse response = new CommandResponse();
	    response.setResult(CommandResult.Successful);
	    response.setMessage("Tenant engine was stopped.");
	    return response;
	}
    }

    public static class ListTenantsResponse {

	public Object generate() throws SiteWhereException {
	    List<ITenant> list = new ArrayList<ITenant>();
	    list.add(ExampleData.TENANT_DEFAULT);
	    list.add(ExampleData.TENANT_MERCHANT1);
	    return new SearchResults<ITenant>(list, 2);
	}
    }
}