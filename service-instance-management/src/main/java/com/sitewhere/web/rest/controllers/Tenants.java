/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.web.rest.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.security.SiteWhereAuthentication;
import com.sitewhere.microservice.security.UserContext;
import com.sitewhere.microservice.tenant.MarshaledTenantConfigurationTemplate;
import com.sitewhere.microservice.tenant.MarshaledTenantDatasetTemplate;
import com.sitewhere.rest.model.search.Pager;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.tenant.TenantSearchCriteria;
import com.sitewhere.rest.model.tenant.request.TenantCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.tenant.ITenantManagement;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.tenant.ITenant;

import io.sitewhere.k8s.crd.tenant.configuration.TenantConfigurationTemplate;
import io.sitewhere.k8s.crd.tenant.configuration.TenantConfigurationTemplateList;
import io.sitewhere.k8s.crd.tenant.dataset.TenantDatasetTemplate;
import io.sitewhere.k8s.crd.tenant.dataset.TenantDatasetTemplateList;
import io.swagger.annotations.Api;

/**
 * Controller for tenant operations.
 */
@Path("/api/tenants")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "tenants")
@Tag(name = "Tenants", description = "Tenants define separate configurable runtimes which run in a SiteWhere instance.")
@SecurityRequirements({ @SecurityRequirement(name = "jwtAuth", scopes = {}),
	@SecurityRequirement(name = "tenantIdHeader", scopes = {}),
	@SecurityRequirement(name = "tenantAuthHeader", scopes = {}) })
public class Tenants {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Tenants.class);

    @Inject
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a new tenant.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Operation(summary = "Create new tenant", description = "Create new tenant")
    public Response createTenant(@RequestBody TenantCreateRequest request) throws SiteWhereException {
	return Response.ok(getTenantManagement().createTenant(request)).build();
    }

    /**
     * Update an existing tenant.
     * 
     * @param tenantToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PUT
    @Path("/{tenantToken}")
    @Operation(summary = "Update an existing tenant", description = "Update an existing tenant")
    public Response updateTenant(
	    @Parameter(description = "Tenant token", required = true) @PathParam("tenantToken") String tenantToken,
	    @RequestBody TenantCreateRequest request) throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	SiteWhereAuthentication user = UserContext.getCurrentUser();
	if (!tenant.getAuthorizedUserIds().contains(user.getUsername())) {
	    throw new SiteWhereSystemException(ErrorCode.NotAuthorizedForTenant, ErrorLevel.ERROR);
	}
	return Response.ok(getTenantManagement().updateTenant(tenantToken, request)).build();
    }

    /**
     * Get a tenant by unique id.
     * 
     * @param tenantToken
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{tenantToken}")
    @Operation(summary = "Get tenant by token", description = "Get tenant by token")
    public Response getTenantByToken(
	    @Parameter(description = "Tenant token", required = true) @PathParam("tenantToken") String tenantToken)
	    throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	SiteWhereAuthentication user = UserContext.getCurrentUser();
	if (!tenant.getAuthorizedUserIds().contains(user.getUsername())) {
	    throw new SiteWhereSystemException(ErrorCode.NotAuthorizedForTenant, ErrorLevel.ERROR);
	}
	return Response.ok(tenant).build();
    }

    /**
     * List tenants that match the given criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Operation(summary = "List tenants that match criteria", description = "List tenants that match criteria")
    public Response listTenants(
	    @Parameter(description = "Text search (partial id or name)", required = false) @QueryParam("textSearch") String textSearch,
	    @Parameter(description = "Authorized user id", required = false) @QueryParam("authUserId") String authUserId,
	    @Parameter(description = "Include runtime info", required = false) @QueryParam("includeRuntimeInfo") @DefaultValue("true") boolean includeRuntimeInfo,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {

	TenantSearchCriteria criteria = new TenantSearchCriteria(page, pageSize);
	criteria.setTextSearch(textSearch);
	criteria.setUserId(authUserId);
	criteria.setIncludeRuntimeInfo(includeRuntimeInfo);
	ISearchResults<ITenant> tenants = getTenantManagement().listTenants(new TenantSearchCriteria(1, 0));

	SiteWhereAuthentication user = UserContext.getCurrentUser();
	Pager<ITenant> authorized = new Pager<ITenant>(criteria);
	for (ITenant tenant : tenants.getResults()) {
	    if (tenant.getAuthorizedUserIds().contains(user.getUsername())) {
		authorized.process(tenant);
	    }
	}
	SearchResults<ITenant> matches = new SearchResults<>(authorized.getResults(), authorized.getTotal());
	return Response.ok(matches).build();
    }

    /**
     * Delete tenant by token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @DELETE
    @Path("/{token}")
    @Operation(summary = "Delete existing tenant", description = "Delete existing tenant")
    public Response deleteTenant(
	    @Parameter(description = "Tenant token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	ITenant tenant = assureTenant(token);
	SiteWhereAuthentication user = UserContext.getCurrentUser();
	if (!tenant.getAuthorizedUserIds().contains(user.getUsername())) {
	    throw new SiteWhereSystemException(ErrorCode.NotAuthorizedForTenant, ErrorLevel.ERROR);
	}
	return Response.ok(getTenantManagement().deleteTenant(token)).build();
    }

    /**
     * Lists all available tenant templates.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/templates/configuration")
    @Operation(summary = "List templates available for creating tenants", description = "List templates available for creating tenants")
    public Response listTenantConfigurationTemplates() throws SiteWhereException {
	TenantConfigurationTemplateList list = getMicroservice().getSiteWhereKubernetesClient()
		.getTenantConfigurationTemplates().list();
	List<MarshaledTenantConfigurationTemplate> templates = new ArrayList<>();
	for (TenantConfigurationTemplate template : list.getItems()) {
	    MarshaledTenantConfigurationTemplate marshaled = new MarshaledTenantConfigurationTemplate();
	    marshaled.setId(template.getMetadata().getName());
	    marshaled.setName(template.getSpec().getName());
	    marshaled.setDescription(template.getSpec().getDescription());
	    templates.add(marshaled);
	}

	return Response.ok(templates).build();
    }

    /**
     * Lists all available dataset templates.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/templates/dataset")
    @Operation(summary = "List datasets available for creating tenants", description = "List datasets available for creating tenants")
    public Response listTenantDatasetTemplates() throws SiteWhereException {
	TenantDatasetTemplateList list = getMicroservice().getSiteWhereKubernetesClient().getTenantDatasetTemplates()
		.list();
	List<MarshaledTenantDatasetTemplate> templates = new ArrayList<>();
	for (TenantDatasetTemplate template : list.getItems()) {
	    MarshaledTenantDatasetTemplate marshaled = new MarshaledTenantDatasetTemplate();
	    marshaled.setId(template.getMetadata().getName());
	    marshaled.setName(template.getSpec().getName());
	    marshaled.setDescription(template.getSpec().getDescription());
	    templates.add(marshaled);
	}

	return Response.ok(templates).build();
    }

    /**
     * Assure that a tenant exists for the given token.
     * 
     * @param tenantToken
     * @return
     * @throws SiteWhereException
     */
    protected ITenant assureTenant(String tenantToken) throws SiteWhereException {
	ITenant tenant = getTenantManagement().getTenant(tenantToken);
	if (tenant == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidTenantToken, ErrorLevel.ERROR);
	}
	return tenant;
    }

    protected ITenantManagement getTenantManagement() {
	return getMicroservice().getTenantManagement();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}