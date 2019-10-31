/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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

import com.sitewhere.device.marshaling.CustomerTypeMarshalHelper;
import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.rest.model.customer.request.CustomerTypeCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.customer.ICustomerType;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.label.ILabelGeneration;
import com.sitewhere.spi.search.ISearchResults;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * Controller for customer type operations.
 * 
 * @author Derek Adams
 */
@Path("/customertypes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "customertypes")
public class CustomerTypes {

    @Inject
    private IInstanceManagementMicroservice<?> microservice;

    /**
     * Create a customer type.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @POST
    @ApiOperation(value = "Create new customer type")
    public Response createCustomerType(@RequestBody CustomerTypeCreateRequest input) throws SiteWhereException {
	return Response.ok(getDeviceManagement().createCustomerType(input)).build();
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
    @ApiOperation(value = "Get customer type by token")
    public Response getCustomerTypeByToken(
	    @ApiParam(value = "Token that identifies customer type", required = true) @PathParam("customerTypeToken") String customerTypeToken)
	    throws SiteWhereException {
	return Response.ok(assertCustomerType(customerTypeToken)).build();
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
    @ApiOperation(value = "Update existing customer type")
    public Response updateCustomerType(
	    @ApiParam(value = "Token that identifies customer type", required = true) @PathParam("customerTypeToken") String customerTypeToken,
	    @RequestBody CustomerTypeCreateRequest request) throws SiteWhereException {
	ICustomerType existing = assertCustomerType(customerTypeToken);
	return Response.ok(getDeviceManagement().updateCustomerType(existing.getId(), request)).build();
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
    @ApiOperation(value = "Get label for customer type")
    public Response getCustomerTypeLabel(
	    @ApiParam(value = "Token that identifies customer type", required = true) @PathParam("customerTypeToken") String customerTypeToken,
	    @ApiParam(value = "Generator id", required = true) @PathParam("generatorId") String generatorId)
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
    @ApiOperation(value = "List customer types matching criteria")
    public Response listCustomerTypes(
	    @ApiParam(value = "Include contained customer types", required = false) @QueryParam("includeContainedCustomerTypes") @DefaultValue("false") boolean includeContainedCustomerTypes,
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	ISearchResults<ICustomerType> matches = getDeviceManagement().listCustomerTypes(criteria);

	CustomerTypeMarshalHelper helper = new CustomerTypeMarshalHelper(getCachedDeviceManagement());
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
    @ApiOperation(value = "Delete customer type by token")
    public Response deleteCustomerType(
	    @ApiParam(value = "Token that identifies customer type", required = true) @PathParam("customerTypeToken") String customerTypeToken)
	    throws SiteWhereException {
	ICustomerType existing = assertCustomerType(customerTypeToken);
	return Response.ok(getDeviceManagement().deleteCustomerType(existing.getId())).build();
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
	return getMicroservice().getDeviceManagementApiChannel();
    }

    protected IDeviceManagement getCachedDeviceManagement() {
	return getMicroservice().getCachedDeviceManagement();
    }

    protected ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiChannel();
    }

    protected IInstanceManagementMicroservice<?> getMicroservice() {
	return microservice;
    }
}
