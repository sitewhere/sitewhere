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
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.DeviceGroupElementMarshalHelper;
import com.sitewhere.microservice.api.device.DeviceGroupMarshalHelper;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.label.ILabelGeneration;
import com.sitewhere.rest.model.device.request.DeviceGroupCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupElementCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Controller for device group operations.
 */
@RestController
@RequestMapping("/api/devicegroups")
public class DeviceGroups {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(DeviceGroups.class);

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a device group.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PostMapping
    public IDeviceGroup createDeviceGroup(@RequestBody DeviceGroupCreateRequest request) throws SiteWhereException {
	return getDeviceManagement().createDeviceGroup(request);
    }

    /**
     * Get device group by unique token.
     * 
     * @param groupToken
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{groupToken}")
    public IDeviceGroup getDeviceGroupByToken(@PathVariable String groupToken) throws SiteWhereException {
	return assureDeviceGroup(groupToken);
    }

    /**
     * Update an existing device group.
     * 
     * @param groupToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PutMapping("/{groupToken}")
    public IDeviceGroup updateDeviceGroup(@PathVariable String groupToken,
	    @RequestBody DeviceGroupCreateRequest request) throws SiteWhereException {
	IDeviceGroup group = assureDeviceGroup(groupToken);
	return getDeviceManagement().updateDeviceGroup(group.getId(), request);
    }

    /**
     * Get label for device group based on a specific generator.
     * 
     * @param groupToken
     * @param generatorId
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{groupToken}/label/{generatorId}")
    public ResponseEntity<?> getDeviceGroupLabel(@PathVariable String groupToken, @PathVariable String generatorId)
	    throws SiteWhereException {
	IDeviceGroup group = assureDeviceGroup(groupToken);
	ILabel label = getLabelGeneration().getDeviceGroupLabel(generatorId, group.getId());
	if (label == null) {
	    return ResponseEntity.notFound().build();
	}
	return ResponseEntity.ok(label.getContent());
    }

    /**
     * Delete an existing device group.
     * 
     * @param groupToken
     * @return
     * @throws SiteWhereException
     */
    @DeleteMapping("/{groupToken}")
    public IDeviceGroup deleteDeviceGroup(@PathVariable String groupToken) throws SiteWhereException {
	IDeviceGroup group = assureDeviceGroup(groupToken);
	return getDeviceManagement().deleteDeviceGroup(group.getId());
    }

    /**
     * List device groups that match criteria.
     * 
     * @param role
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GetMapping
    public SearchResults<IDeviceGroup> listDeviceGroups(@RequestParam(required = false) String role,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	ISearchResults<? extends IDeviceGroup> results;
	if (role == null) {
	    results = getDeviceManagement().listDeviceGroups(criteria);
	} else {
	    results = getDeviceManagement().listDeviceGroupsWithRole(role, criteria);
	}
	DeviceGroupMarshalHelper helper = new DeviceGroupMarshalHelper();
	List<IDeviceGroup> groupsConv = new ArrayList<IDeviceGroup>();
	for (IDeviceGroup group : results.getResults()) {
	    groupsConv.add(helper.convert(group));
	}
	return new SearchResults<IDeviceGroup>(groupsConv, results.getNumResults());
    }

    /**
     * List elements from a device group that meet the given criteria.
     * 
     * @param groupToken
     * @param includeDetails
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{groupToken}/elements")
    public SearchResults<IDeviceGroupElement> listDeviceGroupElements(@PathVariable String groupToken,
	    @RequestParam(defaultValue = "false", required = false) boolean includeDetails,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {
	DeviceGroupElementMarshalHelper helper = new DeviceGroupElementMarshalHelper(getDeviceManagement())
		.setIncludeDetails(includeDetails);
	SearchCriteria criteria = new SearchCriteria(page, pageSize);

	IDeviceGroup group = assureDeviceGroup(groupToken);
	ISearchResults<? extends IDeviceGroupElement> results = getDeviceManagement()
		.listDeviceGroupElements(group.getId(), criteria);
	List<IDeviceGroupElement> elmConv = new ArrayList<IDeviceGroupElement>();
	for (IDeviceGroupElement elm : results.getResults()) {
	    elmConv.add(helper.convert(elm, getAssetManagement()));
	}
	return new SearchResults<IDeviceGroupElement>(elmConv, results.getNumResults());
    }

    /**
     * Add a list of device group elements to an existing group.
     * 
     * @param groupToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PostMapping("/{groupToken}/elements")
    @SuppressWarnings("unchecked")
    public SearchResults<IDeviceGroupElement> addDeviceGroupElements(@PathVariable String groupToken,
	    @RequestBody List<DeviceGroupElementCreateRequest> request) throws SiteWhereException {
	DeviceGroupElementMarshalHelper helper = new DeviceGroupElementMarshalHelper(getDeviceManagement())
		.setIncludeDetails(false);
	List<IDeviceGroupElementCreateRequest> elements = (List<IDeviceGroupElementCreateRequest>) (List<? extends IDeviceGroupElementCreateRequest>) request;

	// Validate the list of new elements.
	validateDeviceGroupElements(request, getDeviceManagement());

	IDeviceGroup group = assureDeviceGroup(groupToken);
	List<? extends IDeviceGroupElement> results = getDeviceManagement().addDeviceGroupElements(group.getId(),
		elements, true);
	List<IDeviceGroupElement> converted = new ArrayList<IDeviceGroupElement>();
	for (IDeviceGroupElement elm : results) {
	    converted.add(helper.convert(elm, getAssetManagement()));
	}
	return new SearchResults<IDeviceGroupElement>(converted);
    }

    /**
     * Validate new elements to assure they reference real objects.
     * 
     * @param elements
     * @param deviceManagement
     * @throws SiteWhereException
     */
    protected void validateDeviceGroupElements(List<DeviceGroupElementCreateRequest> elements,
	    IDeviceManagement deviceManagement) throws SiteWhereException {
	for (DeviceGroupElementCreateRequest request : elements) {
	    if (request.getDeviceToken() != null) {
		IDevice device = deviceManagement.getDeviceByToken(request.getDeviceToken());
		if (device == null) {
		    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceToken, ErrorLevel.ERROR);
		}
	    }
	    if (request.getNestedGroupToken() != null) {
		IDeviceGroup group = deviceManagement.getDeviceGroupByToken(request.getNestedGroupToken());
		if (group == null) {
		    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceGroupToken, ErrorLevel.ERROR);
		}
	    }
	}
    }

    /**
     * Delete a single device group element.
     * 
     * @param groupToken
     * @param elementId
     * @return
     * @throws SiteWhereException
     */
    @DeleteMapping("/{groupToken}/elements/{elementId}")
    public SearchResults<IDeviceGroupElement> deleteDeviceGroupElement(@PathVariable String groupToken,
	    @PathVariable UUID elementId) throws SiteWhereException {
	List<UUID> elements = new ArrayList<>();
	elements.add(elementId);
	return deleteDeviceGroupElements(groupToken, elements);
    }

    /**
     * Delete a list of elements from an existing device group.
     * 
     * @param groupToken
     * @param elementIds
     * @return
     * @throws SiteWhereException
     */
    @DeleteMapping("/{groupToken}/elements")
    public SearchResults<IDeviceGroupElement> deleteDeviceGroupElements(@PathVariable String groupToken,
	    @RequestBody List<UUID> elementIds) throws SiteWhereException {
	DeviceGroupElementMarshalHelper helper = new DeviceGroupElementMarshalHelper(getDeviceManagement())
		.setIncludeDetails(false);

	List<? extends IDeviceGroupElement> results = getDeviceManagement().removeDeviceGroupElements(elementIds);
	List<IDeviceGroupElement> converted = new ArrayList<IDeviceGroupElement>();
	for (IDeviceGroupElement elm : results) {
	    converted.add(helper.convert(elm, getAssetManagement()));
	}
	return new SearchResults<IDeviceGroupElement>(converted);
    }

    /**
     * Assure that a device group exists for the given token.
     * 
     * @param groupToken
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceGroup assureDeviceGroup(String groupToken) throws SiteWhereException {
	IDeviceGroup group = getDeviceManagement().getDeviceGroupByToken(groupToken);
	if (group == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceGroupToken, ErrorLevel.ERROR);
	}
	return group;
    }

    protected IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagement();
    }

    protected IAssetManagement getAssetManagement() {
	return getMicroservice().getAssetManagement();
    }

    protected ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiChannel();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}