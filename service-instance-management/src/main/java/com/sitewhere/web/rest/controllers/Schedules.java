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
import com.sitewhere.microservice.api.schedule.IScheduleManagement;
import com.sitewhere.rest.model.scheduling.request.ScheduleCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Controller for schedule operations.
 */
@RestController
@RequestMapping("/api/schedules")
public class Schedules {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Schedules.class);

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a schedule.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PostMapping
    public ISchedule createSchedule(@RequestBody ScheduleCreateRequest request) throws SiteWhereException {
	return getScheduleManagement().createSchedule(request);
    }

    /**
     * Get a schedule by token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{token}")
    public ISchedule getScheduleByToken(@PathVariable String token) throws SiteWhereException {
	return getScheduleManagement().getScheduleByToken(token);
    }

    /**
     * Update an existing schedule.
     * 
     * @param request
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @PutMapping("/{token}")
    public ISchedule updateSchedule(@RequestBody ScheduleCreateRequest request, @PathVariable String token)
	    throws SiteWhereException {
	ISchedule schedule = getScheduleManagement().getScheduleByToken(token);
	if (schedule == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidScheduleToken, ErrorLevel.ERROR);
	}
	return getScheduleManagement().updateSchedule(schedule.getId(), request);
    }

    /**
     * List schedules that match criteria.
     * 
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GetMapping
    public ISearchResults<? extends ISchedule> listSchedules(
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	return getScheduleManagement().listSchedules(criteria);
    }

    /**
     * Delete a schedule.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @DeleteMapping("/{token}")
    public ISchedule deleteSchedule(@PathVariable String token) throws SiteWhereException {
	ISchedule schedule = getScheduleManagement().getScheduleByToken(token);
	if (schedule == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidScheduleToken, ErrorLevel.ERROR);
	}
	return getScheduleManagement().deleteSchedule(schedule.getId());
    }

    protected IScheduleManagement getScheduleManagement() {
	return getMicroservice().getScheduleManagementApiChannel();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}