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
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.api.device.CustomerTypeMarshalHelper;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.label.ILabelGeneration;
import com.sitewhere.rest.model.customer.request.CustomerTypeCreateRequest;
import com.sitewhere.rest.model.device.marshaling.MarshaledCustomerType;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.customer.ICustomerType;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.search.ISearchResults;

import io.swagger.annotations.Api;

/**
 * Controller for customer type operations.
 */
@Path("/api/customertypes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "customertypes")
@Tag(name = "Customer Types", description = "Customer types define common characteristics for related customers.")
@SecurityRequirements({ @SecurityRequirement(name = "jwtAuth", scopes = {}),
	@SecurityRequirement(name = "tenantIdHeader", scopes = {}),
	@SecurityRequirement(name = "tenantAuthHeader", scopes = {}) })
public class CustomerTypes {

    @Inject
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a customer type.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Operation(summary = "Create new customer type", description = "Create new customer type")
    public Response createCustomerType(@RequestBody CustomerTypeCreateRequest input) throws SiteWhereException {
	CustomerTypeMarshalHelper helper = new CustomerTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeContainedCustomerTypes(true);
	MarshaledCustomerType marshaled = helper.convert(getDeviceManagement().createCustomerType(input));
	return Response.ok(marshaled).build();
    }

    /**
     * Get information for a given customer type based on token.
     * 
     * @param customerTypeToken
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{customerTypeToken}")
    @Operation(summary = "Get customer type by token", description = "Get customer type by token")
    public Response getCustomerTypeByToken(
	    @Parameter(description = "Token that identifies customer type", required = true) @PathParam("customerTypeToken") String customerTypeToken)
	    throws SiteWhereException {
	CustomerTypeMarshalHelper helper = new CustomerTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeContainedCustomerTypes(true);
	MarshaledCustomerType marshaled = helper.convert(assertCustomerType(customerTypeToken));
	return Response.ok(marshaled).build();
    }

    /**
     * Update information for a customer type.
     * 
     * @param customerTypeToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PUT
    @Path("/{customerTypeToken}")
    @Operation(summary = "Update existing customer type", description = "Update existing customer type")
    public Response updateCustomerType(
	    @Parameter(description = "Token that identifies customer type", required = true) @PathParam("customerTypeToken") String customerTypeToken,
	    @RequestBody CustomerTypeCreateRequest request) throws SiteWhereException {
	ICustomerType existing = assertCustomerType(customerTypeToken);
	CustomerTypeMarshalHelper helper = new CustomerTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeContainedCustomerTypes(true);
	ICustomerType updated = getDeviceManagement().updateCustomerType(existing.getId(), request);
	MarshaledCustomerType marshaled = helper.convert(updated);
	return Response.ok(marshaled).build();
    }

    /**
     * Get label for customer type based on a specific generator.
     * 
     * @param customerTypeToken
     * @param generatorId
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{customerTypeToken}/label/{generatorId}")
    @Produces("image/png")
    @Operation(summary = "Get label for customer type", description = "Get label for customer type")
    public Response getCustomerTypeLabel(
	    @Parameter(description = "Token that identifies customer type", required = true) @PathParam("customerTypeToken") String customerTypeToken,
	    @Parameter(description = "Generator id", required = true) @PathParam("generatorId") String generatorId)
	    throws SiteWhereException {
	ICustomerType existing = assertCustomerType(customerTypeToken);
	ILabel label = getLabelGeneration().getCustomerTypeLabel(generatorId, existing.getId());
	if (label == null) {
	    return Response.status(Status.NOT_FOUND).build();
	}
	return Response.ok(label.getContent()).build();
    }

    /**
     * List customer types matching criteria.
     * 
     * @param includeContainedCustomerTypes
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Operation(summary = "List customer types matching criteria", description = "List customer types matching criteria")
    public Response listCustomerTypes(
	    @Parameter(description = "Include contained customer types", required = false) @QueryParam("includeContainedCustomerTypes") @DefaultValue("false") boolean includeContainedCustomerTypes,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	ISearchResults<? extends ICustomerType> matches = getDeviceManagement().listCustomerTypes(criteria);

	CustomerTypeMarshalHelper helper = new CustomerTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeContainedCustomerTypes(includeContainedCustomerTypes);

	List<ICustomerType> results = new ArrayList<ICustomerType>();
	for (ICustomerType customerType : matches.getResults()) {
	    results.add(helper.convert(customerType));
	}
	return Response.ok(new SearchResults<ICustomerType>(results, matches.getNumResults())).build();
    }

    /**
     * Delete information for a customer type based on token.
     * 
     * @param customerTypeToken
     * @return
     * @throws SiteWhereException
     */
    @DELETE
    @Path("/{customerTypeToken}")
    @Operation(summary = "Delete customer type by token", description = "Delete customer type by token")
    public Response deleteCustomerType(
	    @Parameter(description = "Token that identifies customer type", required = true) @PathParam("customerTypeToken") String customerTypeToken)
	    throws SiteWhereException {
	ICustomerType existing = assertCustomerType(customerTypeToken);
	CustomerTypeMarshalHelper helper = new CustomerTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeContainedCustomerTypes(true);
	ICustomerType deleted = getDeviceManagement().deleteCustomerType(existing.getId());
	MarshaledCustomerType marshaled = helper.convert(deleted);
	return Response.ok(marshaled).build();
    }

    /**
     * Get customer type associated with token or throw an exception if invalid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected ICustomerType assertCustomerType(String token) throws SiteWhereException {
	ICustomerType type = getDeviceManagement().getCustomerTypeByToken(token);
	if (type == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidCustomerTypeToken, ErrorLevel.ERROR);
	}
	return type;
    }

    protected IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagement();
    }

    protected ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiChannel();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}
