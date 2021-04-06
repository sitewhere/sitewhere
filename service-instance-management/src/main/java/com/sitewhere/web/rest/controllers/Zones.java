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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.rest.model.area.request.ZoneCreateRequest;
import com.sitewhere.rest.model.search.device.ZoneSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.area.IZone;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Controller for site operations.
 */
@RestController
@RequestMapping("/api/zones")
public class Zones {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Zones.class);

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a new zone.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PostMapping
    public IZone createZone(@RequestBody ZoneCreateRequest request) throws SiteWhereException {
	return getDeviceManagement().createZone(request);
    }

    /**
     * Get zone based on unique token.
     * 
     * @param zoneToken
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{zoneToken}")
    public IZone getZone(@PathVariable String zoneToken) throws SiteWhereException {
	return assertZone(zoneToken);
    }

    /**
     * Update information for a zone.
     * 
     * @param zoneToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PutMapping("/{zoneToken}")
    public IZone updateZone(@PathVariable String zoneToken, @RequestBody ZoneCreateRequest request)
	    throws SiteWhereException {
	IZone existing = assertZone(zoneToken);
	return getDeviceManagement().updateZone(existing.getId(), request);
    }

    /**
     * List zones that match criteria.
     * 
     * @param areaToken
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GetMapping
    public ISearchResults<? extends IZone> listZones(@RequestParam String areaToken,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {
	ZoneSearchCriteria criteria = new ZoneSearchCriteria(page, pageSize);
	criteria.setAreaToken(areaToken);
	return getDeviceManagement().listZones(criteria);
    }

    /**
     * Delete an existing zone.
     * 
     * @param zoneToken
     * @return
     * @throws SiteWhereException
     */
    @DeleteMapping("/{zoneToken}")
    public IZone deleteZone(@PathVariable String zoneToken) throws SiteWhereException {
	IZone existing = assertZone(zoneToken);
	return getDeviceManagement().deleteZone(existing.getId());
    }

    /**
     * Get zone associated with token or throw an exception if invalid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected IZone assertZone(String token) throws SiteWhereException {
	IZone zone = getDeviceManagement().getZoneByToken(token);
	if (zone == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidZoneToken, ErrorLevel.ERROR);
	}
	return zone;
    }

    protected IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagement();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}