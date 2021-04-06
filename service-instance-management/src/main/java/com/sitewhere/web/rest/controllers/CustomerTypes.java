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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

/**
 * Controller for customer type operations.
 */
@RestController
@RequestMapping("/api/customertypes")
public class CustomerTypes {

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a customer type.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @PostMapping
    public MarshaledCustomerType createCustomerType(@RequestBody CustomerTypeCreateRequest input)
	    throws SiteWhereException {
	CustomerTypeMarshalHelper helper = new CustomerTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeContainedCustomerTypes(true);
	return helper.convert(getDeviceManagement().createCustomerType(input));
    }

    /**
     * Get information for a given customer type based on token.
     * 
     * @param customerTypeToken
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{customerTypeToken}")
    public MarshaledCustomerType getCustomerTypeByToken(@PathVariable String customerTypeToken)
	    throws SiteWhereException {
	CustomerTypeMarshalHelper helper = new CustomerTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeContainedCustomerTypes(true);
	return helper.convert(assertCustomerType(customerTypeToken));
    }

    /**
     * Update information for a customer type.
     * 
     * @param customerTypeToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PutMapping("/{customerTypeToken}")
    public MarshaledCustomerType updateCustomerType(@PathVariable String customerTypeToken,
	    @RequestBody CustomerTypeCreateRequest request) throws SiteWhereException {
	ICustomerType existing = assertCustomerType(customerTypeToken);
	CustomerTypeMarshalHelper helper = new CustomerTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeContainedCustomerTypes(true);
	ICustomerType updated = getDeviceManagement().updateCustomerType(existing.getId(), request);
	return helper.convert(updated);
    }

    /**
     * Get label for customer type based on a specific generator.
     * 
     * @param customerTypeToken
     * @param generatorId
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{customerTypeToken}/label/{generatorId}")
    public ResponseEntity<?> getCustomerTypeLabel(@PathVariable String customerTypeToken,
	    @PathVariable String generatorId) throws SiteWhereException {
	ICustomerType existing = assertCustomerType(customerTypeToken);
	ILabel label = getLabelGeneration().getCustomerTypeLabel(generatorId, existing.getId());
	if (label == null) {
	    return ResponseEntity.notFound().build();
	}
	return ResponseEntity.ok(label.getContent());
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
    @GetMapping
    public SearchResults<ICustomerType> listCustomerTypes(
	    @RequestParam(defaultValue = "false", required = false) boolean includeContainedCustomerTypes,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	ISearchResults<? extends ICustomerType> matches = getDeviceManagement().listCustomerTypes(criteria);

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
    @DeleteMapping("/{customerTypeToken}")
    public MarshaledCustomerType deleteCustomerType(@PathVariable String customerTypeToken) throws SiteWhereException {
	ICustomerType existing = assertCustomerType(customerTypeToken);
	CustomerTypeMarshalHelper helper = new CustomerTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeContainedCustomerTypes(true);
	ICustomerType deleted = getDeviceManagement().deleteCustomerType(existing.getId());
	return helper.convert(deleted);
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