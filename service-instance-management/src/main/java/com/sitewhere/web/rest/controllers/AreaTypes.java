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
import com.sitewhere.microservice.api.device.AreaTypeMarshalHelper;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.label.ILabelGeneration;
import com.sitewhere.rest.model.area.request.AreaTypeCreateRequest;
import com.sitewhere.rest.model.device.marshaling.MarshaledAreaType;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Controller for area type operations.
 */
@RestController
@RequestMapping("/api/areatypes")
public class AreaTypes {

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /**
     * Create an area type.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @PostMapping
    public MarshaledAreaType createAreaType(@RequestBody AreaTypeCreateRequest input) throws SiteWhereException {
	AreaTypeMarshalHelper helper = new AreaTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeContainedAreaTypes(true);
	return helper.convert(getDeviceManagement().createAreaType(input));
    }

    /**
     * Get information for a given area type based on token.
     * 
     * @param areaTypeToken
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{areaTypeToken}")
    public MarshaledAreaType getAreaTypeByToken(@PathVariable String areaTypeToken) throws SiteWhereException {
	AreaTypeMarshalHelper helper = new AreaTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeContainedAreaTypes(true);
	return helper.convert(assertAreaType(areaTypeToken));
    }

    /**
     * Update information for an area type.
     * 
     * @param areaTypeToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PutMapping("/{areaTypeToken}")
    public MarshaledAreaType updateAreaType(@PathVariable String areaTypeToken,
	    @RequestBody AreaTypeCreateRequest request) throws SiteWhereException {
	IAreaType existing = assertAreaType(areaTypeToken);
	AreaTypeMarshalHelper helper = new AreaTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeContainedAreaTypes(true);
	return helper.convert(getDeviceManagement().updateAreaType(existing.getId(), request));
    }

    /**
     * Get label for area type based on a specific generator.
     * 
     * @param areaTypeToken
     * @param generatorId
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{areaTypeToken}/label/{generatorId}")
    public ResponseEntity<?> getAreaTypeLabel(@PathVariable String areaTypeToken, @PathVariable String generatorId)
	    throws SiteWhereException {
	IAreaType existing = assertAreaType(areaTypeToken);
	ILabel label = getLabelGeneration().getAreaTypeLabel(generatorId, existing.getId());
	if (label == null) {
	    return ResponseEntity.notFound().build();
	}
	return ResponseEntity.ok(label.getContent());
    }

    /**
     * List area types matching criteria.
     * 
     * @param includeContainedAreaTypes
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GetMapping
    public SearchResults<IAreaType> listAreaTypes(
	    @RequestParam(defaultValue = "false", required = false) boolean includeContainedAreaTypes,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	ISearchResults<? extends IAreaType> matches = getDeviceManagement().listAreaTypes(criteria);

	AreaTypeMarshalHelper helper = new AreaTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeContainedAreaTypes(includeContainedAreaTypes);

	List<IAreaType> results = new ArrayList<IAreaType>();
	for (IAreaType area : matches.getResults()) {
	    results.add(helper.convert(area));
	}
	return new SearchResults<IAreaType>(results, matches.getNumResults());
    }

    /**
     * Delete information for an area type based on token.
     * 
     * @param areaTypeToken
     * @return
     * @throws SiteWhereException
     */
    @DeleteMapping("/{areaTypeToken}")
    public IAreaType deleteAreaType(@PathVariable String areaTypeToken) throws SiteWhereException {
	IAreaType existing = assertAreaType(areaTypeToken);
	return getDeviceManagement().deleteAreaType(existing.getId());
    }

    /**
     * Get area type associated with token or throw an exception if invalid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected IAreaType assertAreaType(String token) throws SiteWhereException {
	IAreaType type = getDeviceManagement().getAreaTypeByToken(token);
	if (type == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAreaTypeToken, ErrorLevel.ERROR);
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