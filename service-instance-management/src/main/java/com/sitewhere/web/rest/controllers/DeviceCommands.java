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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.rest.model.device.command.DeviceCommandNamespace;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.device.DeviceCommandSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.IDeviceCommandNamespace;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Controller for device command operations.
 */
@RestController
@RequestMapping("/api/commands")
public class DeviceCommands {

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /**
     * List commands that match the given criteria.
     * 
     * @param deviceTypeToken
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GetMapping
    public ISearchResults<? extends IDeviceCommand> listDeviceCommands(
	    @RequestParam(required = false) String deviceTypeToken,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {
	DeviceCommandSearchCriteria criteria = new DeviceCommandSearchCriteria(page, pageSize);
	criteria.setDeviceTypeToken(deviceTypeToken);

	return getDeviceManagement().listDeviceCommands(criteria);
    }

    /**
     * List commands grouped by namespace.
     * 
     * @param deviceTypeToken
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/namespaces")
    public SearchResults<IDeviceCommandNamespace> listAllDeviceCommandsByNamespace(
	    @RequestParam(required = false) String deviceTypeToken) throws SiteWhereException {
	DeviceCommandSearchCriteria criteria = new DeviceCommandSearchCriteria(1, 0);
	criteria.setDeviceTypeToken(deviceTypeToken);

	List<? extends IDeviceCommand> results = getDeviceManagement().listDeviceCommands(criteria).getResults();
	Collections.sort(results, new Comparator<IDeviceCommand>() {
	    public int compare(IDeviceCommand o1, IDeviceCommand o2) {
		if ((o1.getNamespace() == null) && (o2.getNamespace() != null)) {
		    return -1;
		}
		if ((o1.getNamespace() != null) && (o2.getNamespace() == null)) {
		    return 1;
		}
		if ((o1.getNamespace() == null) && (o2.getNamespace() == null)) {
		    return o1.getName().compareTo(o2.getName());
		}
		if (!o1.getNamespace().equals(o2.getNamespace())) {
		    return o1.getNamespace().compareTo(o2.getNamespace());
		}
		return o1.getName().compareTo(o2.getName());
	    }
	});
	List<IDeviceCommandNamespace> namespaces = new ArrayList<IDeviceCommandNamespace>();
	DeviceCommandNamespace current = null;
	for (IDeviceCommand command : results) {
	    if ((current == null) || ((current.getValue() == null) && (command.getNamespace() != null))
		    || ((current.getValue() != null) && (!current.getValue().equals(command.getNamespace())))) {
		current = new DeviceCommandNamespace();
		current.setValue(command.getNamespace());
		namespaces.add(current);
	    }
	    current.getCommands().add(command);
	}
	return new SearchResults<IDeviceCommandNamespace>(namespaces);
    }

    protected IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagement();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}