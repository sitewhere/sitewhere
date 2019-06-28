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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.device.marshaling.CustomerTypeMarshalHelper;
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
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.annotation.SiteWhereCrossOrigin;
import com.sitewhere.web.rest.RestControllerBase;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for customer type operations.
 * 
 * @author Derek Adams
 */
@RestController
@SiteWhereCrossOrigin
@RequestMapping(value = "/customertypes")
@Api(value = "customertypes")
public class CustomerTypes extends RestControllerBase {

    /**
     * Create a customer type.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Create new customer type")
    @Secured({ SiteWhereRoles.REST })
    public ICustomerType createCustomerType(@RequestBody CustomerTypeCreateRequest input) throws SiteWhereException {
	return getDeviceManagement().createCustomerType(input);
    }

    /**
     * Get information for a given customer type based on token.
     * 
     * @param customerTypeToken
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{customerTypeToken:.+}", method = RequestMethod.GET)
    @ApiOperation(value = "Get customer type by token")
    @Secured({ SiteWhereRoles.REST })
    public ICustomerType getCustomerTypeByToken(
	    @ApiParam(value = "Token that identifies customer type", required = true) @PathVariable String customerTypeToken)
	    throws SiteWhereException {
	return assertCustomerType(customerTypeToken);
    }

    /**
     * Update information for a customer type.
     * 
     * @param customerTypeToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{customerTypeToken:.+}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update existing customer type")
    @Secured({ SiteWhereRoles.REST })
    public ICustomerType updateCustomerType(
	    @ApiParam(value = "Token that identifies customer type", required = true) @PathVariable String customerTypeToken,
	    @RequestBody CustomerTypeCreateRequest request) throws SiteWhereException {
	ICustomerType existing = assertCustomerType(customerTypeToken);
	return getDeviceManagement().updateCustomerType(existing.getId(), request);
    }

    /**
     * Get label for customer type based on a specific generator.
     * 
     * @param customerTypeToken
     * @param generatorId
     * @param servletRequest
     * @param response
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{customerTypeToken}/label/{generatorId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get label for customer type")
    public ResponseEntity<byte[]> getCustomerTypeLabel(
	    @ApiParam(value = "Token that identifies customer type", required = true) @PathVariable String customerTypeToken,
	    @ApiParam(value = "Generator id", required = true) @PathVariable String generatorId,
	    HttpServletRequest servletRequest, HttpServletResponse response) throws SiteWhereException {
	ICustomerType existing = assertCustomerType(customerTypeToken);
	ILabel label = getLabelGeneration().getCustomerTypeLabel(generatorId, existing.getId());

	final HttpHeaders headers = new HttpHeaders();
	headers.setContentType(MediaType.IMAGE_PNG);
	return new ResponseEntity<byte[]>(label.getContent(), headers, HttpStatus.OK);
    }

    /**
     * List customer types matching criteria.
     * 
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "List customer types matching criteria")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<ICustomerType> listCustomerTypes(
	    @ApiParam(value = "Include contained customer types", required = false) @RequestParam(defaultValue = "false") boolean includeContainedCustomerTypes,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	ISearchResults<ICustomerType> matches = getDeviceManagement().listCustomerTypes(criteria);

	CustomerTypeMarshalHelper helper = new CustomerTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeContainedCustomerTypes(includeContainedCustomerTypes);

	List<ICustomerType> results = new ArrayList<ICustomerType>();
	for (ICustomerType customerType : matches.getResults()) {
	    results.add(helper.convert(customerType));
	}
	return new SearchResults<ICustomerType>(results, matches.getNumResults());
    }

    /**
     * Delete information for a customer type based on token.
     * 
     * @param customerTypeToken
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{customerTypeToken:.+}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete customer type by token")
    @Secured({ SiteWhereRoles.REST })
    public ICustomerType deleteCustomerType(
	    @ApiParam(value = "Token that identifies customer type", required = true) @PathVariable String customerTypeToken)
	    throws SiteWhereException {
	ICustomerType existing = assertCustomerType(customerTypeToken);
	return getDeviceManagement().deleteCustomerType(existing.getId());
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

    private IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagementApiChannel();
    }

    private ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiChannel();
    }
}
