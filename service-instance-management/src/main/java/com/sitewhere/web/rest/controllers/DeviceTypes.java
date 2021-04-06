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
import com.sitewhere.microservice.api.device.DeviceTypeMarshalHelper;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.label.ILabelGeneration;
import com.sitewhere.rest.model.device.DeviceType;
import com.sitewhere.rest.model.device.request.DeviceCommandCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceStatusCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceTypeCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDeviceStatus;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Controller for device specification operations.
 */
@RestController
@RequestMapping("/api/devicetypes")
public class DeviceTypes {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(DeviceTypes.class);

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a device type.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PostMapping
    public DeviceType createDeviceType(@RequestBody DeviceTypeCreateRequest request) throws SiteWhereException {
	IDeviceType result = getDeviceManagement().createDeviceType(request);
	DeviceTypeMarshalHelper helper = new DeviceTypeMarshalHelper(getDeviceManagement());
	return helper.convert(result);
    }

    /**
     * Get a device type by unique token.
     * 
     * @param token
     * @param includeAsset
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{token}")
    public DeviceType getDeviceTypeByToken(@PathVariable String token,
	    @RequestParam(defaultValue = "true", required = false) boolean includeAsset) throws SiteWhereException {
	IDeviceType result = assertDeviceTypeByToken(token);
	DeviceTypeMarshalHelper helper = new DeviceTypeMarshalHelper(getDeviceManagement());
	return helper.convert(result);
    }

    /**
     * Update an existing device type.
     * 
     * @param token
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PutMapping("/{token}")
    public DeviceType updateDeviceType(@PathVariable String token, @RequestBody DeviceTypeCreateRequest request)
	    throws SiteWhereException {
	IDeviceType deviceType = assertDeviceTypeByToken(token);
	IDeviceType result = getDeviceManagement().updateDeviceType(deviceType.getId(), request);
	DeviceTypeMarshalHelper helper = new DeviceTypeMarshalHelper(getDeviceManagement());
	return helper.convert(result);
    }

    /**
     * Get label for device type based on a specific generator.
     * 
     * @param token
     * @param generatorId
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{token}/label/{generatorId}")
    public ResponseEntity<?> getDeviceTypeLabel(@PathVariable String token, @PathVariable String generatorId)
	    throws SiteWhereException {
	IDeviceType deviceType = assertDeviceTypeByToken(token);
	ILabel label = getLabelGeneration().getDeviceTypeLabel(generatorId, deviceType.getId());
	if (label == null) {
	    return ResponseEntity.notFound().build();
	}
	return ResponseEntity.ok(label.getContent());
    }

    /**
     * List device types that meet the given criteria.
     * 
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GetMapping
    public SearchResults<IDeviceType> listDeviceTypes(@RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	ISearchResults<? extends IDeviceType> results = getDeviceManagement().listDeviceTypes(criteria);
	DeviceTypeMarshalHelper helper = new DeviceTypeMarshalHelper(getDeviceManagement());
	List<IDeviceType> typesConv = new ArrayList<IDeviceType>();
	for (IDeviceType type : results.getResults()) {
	    typesConv.add(helper.convert(type));
	}
	return new SearchResults<IDeviceType>(typesConv, results.getNumResults());
    }

    /**
     * Delete an existing device type.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @DeleteMapping("/{token}")
    public DeviceType deleteDeviceType(@PathVariable String token) throws SiteWhereException {
	IDeviceType existing = assertDeviceTypeByToken(token);
	IDeviceType result = getDeviceManagement().deleteDeviceType(existing.getId());
	DeviceTypeMarshalHelper helper = new DeviceTypeMarshalHelper(getDeviceManagement());
	return helper.convert(result);
    }

    /**
     * Create a device command.
     * 
     * @param token
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PostMapping("/{token}/commands")
    public IDeviceCommand createDeviceCommand(@PathVariable String token,
	    @RequestBody DeviceCommandCreateRequest request) throws SiteWhereException {
	return getDeviceManagement().createDeviceCommand(request);
    }

    /**
     * Get a device command by unique token.
     * 
     * @param token
     * @param commandToken
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{token}/commands/{commandToken}")
    public IDeviceCommand getDeviceCommandByToken(@PathVariable String token, @PathVariable String commandToken)
	    throws SiteWhereException {
	IDeviceType existing = assertDeviceTypeByToken(token);
	return getDeviceManagement().getDeviceCommandByToken(existing.getId(), commandToken);
    }

    /**
     * Update an existing device command.
     * 
     * @param token
     * @param commandToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PutMapping("/{token}/commands/{commandToken}")
    public IDeviceCommand updateDeviceCommand(@PathVariable String token, @PathVariable String commandToken,
	    @RequestBody DeviceCommandCreateRequest request) throws SiteWhereException {
	IDeviceType existing = assertDeviceTypeByToken(token);
	IDeviceCommand command = getDeviceManagement().getDeviceCommandByToken(existing.getId(), commandToken);
	return getDeviceManagement().updateDeviceCommand(command.getId(), request);
    }

    /**
     * Delete an existing device command.
     * 
     * @param token
     * @param commandToken
     * @return
     * @throws SiteWhereException
     */
    @DeleteMapping("/{token}/commands/{commandToken}")
    public IDeviceCommand deleteDeviceCommand(@PathVariable String token, @PathVariable String commandToken)
	    throws SiteWhereException {
	IDeviceType existing = assertDeviceTypeByToken(token);
	IDeviceCommand command = getDeviceManagement().getDeviceCommandByToken(existing.getId(), commandToken);
	return getDeviceManagement().deleteDeviceCommand(command.getId());
    }

    /**
     * Create a device status.
     * 
     * @param token
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PostMapping("/{token}/statuses")
    public IDeviceStatus createDeviceStatus(@PathVariable String token, @RequestBody DeviceStatusCreateRequest request)
	    throws SiteWhereException {
	return getDeviceManagement().createDeviceStatus(request);
    }

    /**
     * Get device status by unique token.
     * 
     * @param token
     * @param statusToken
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{token}/statuses/{statusToken}")
    public IDeviceStatus getDeviceStatusByToken(@PathVariable String token, @PathVariable String statusToken)
	    throws SiteWhereException {
	IDeviceType existing = assertDeviceTypeByToken(token);
	return getDeviceManagement().getDeviceStatusByToken(existing.getId(), statusToken);
    }

    /**
     * Update an existing device status.
     * 
     * @param token
     * @param statusToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PutMapping("/{token}/statuses/{statusToken}")
    public IDeviceStatus updateDeviceStatus(@PathVariable String token, @PathVariable String statusToken,
	    @RequestBody DeviceStatusCreateRequest request) throws SiteWhereException {
	IDeviceType existing = assertDeviceTypeByToken(token);
	IDeviceStatus status = getDeviceManagement().getDeviceStatusByToken(existing.getId(), statusToken);
	return getDeviceManagement().updateDeviceStatus(status.getId(), request);
    }

    /**
     * Delete an existing device command.
     * 
     * @param token
     * @param statusToken
     * @return
     * @throws SiteWhereException
     */
    @DeleteMapping("/{token}/statuses/{statusToken}")
    public IDeviceStatus deleteDeviceStatus(@PathVariable String token, @PathVariable String statusToken)
	    throws SiteWhereException {
	IDeviceType existing = assertDeviceTypeByToken(token);
	IDeviceStatus status = getDeviceManagement().getDeviceStatusByToken(existing.getId(), statusToken);
	return getDeviceManagement().deleteDeviceStatus(status.getId());
    }

    /**
     * Gets a device type by token and throws an exception if not found.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceType assertDeviceTypeByToken(String token) throws SiteWhereException {
	IDeviceType result = getDeviceManagement().getDeviceTypeByToken(token);
	if (result == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	}
	return result;
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