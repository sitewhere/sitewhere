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
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.schedule.IScheduleManagement;
import com.sitewhere.rest.model.scheduling.request.ScheduledJobCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.web.rest.marshaling.ScheduledJobMarshalHelper;

/**
 * Controller for scheduled jobs.
 */
@RestController
@RequestMapping("/api/jobs")
public class ScheduledJobs {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(ScheduledJobs.class);

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a new scheduled job.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PostMapping
    public IScheduledJob createScheduledJob(@RequestBody ScheduledJobCreateRequest request) throws SiteWhereException {
	return getScheduleManagement().createScheduledJob(request);
    }

    /**
     * Get scheduled job by token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{token}")
    public IScheduledJob getScheduledJobByToken(@PathVariable String token) throws SiteWhereException {
	return getScheduleManagement().getScheduledJobByToken(token);
    }

    /**
     * Update an existing scheduled job.
     * 
     * @param request
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @PutMapping("/{token}")
    public IScheduledJob updateScheduledJob(@RequestBody ScheduledJobCreateRequest request, @PathVariable String token)
	    throws SiteWhereException {
	IScheduledJob job = getScheduleManagement().getScheduledJobByToken(token);
	if (job == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidScheduledJobToken, ErrorLevel.ERROR);
	}
	return getScheduleManagement().updateScheduledJob(job.getId(), request);
    }

    /**
     * List scheduled jobs that match the criteria.
     * 
     * @param includeContext
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GetMapping
    public ResponseEntity<?> listScheduledJobs(
	    @RequestParam(defaultValue = "false", required = false) boolean includeContext,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	ISearchResults<? extends IScheduledJob> results = getScheduleManagement().listScheduledJobs(criteria);
	if (!includeContext) {
	    return ResponseEntity.ok(results);
	} else {
	    List<IScheduledJob> converted = new ArrayList<IScheduledJob>();
	    ScheduledJobMarshalHelper helper = new ScheduledJobMarshalHelper(getScheduleManagement(),
		    getDeviceManagement(), getAssetManagement(), true);
	    for (IScheduledJob job : results.getResults()) {
		converted.add(helper.convert(job));
	    }
	    return ResponseEntity.ok(new SearchResults<IScheduledJob>(converted, results.getNumResults()));
	}
    }

    /**
     * Delete an existing scheduled job.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @DeleteMapping("/{token}")
    public IScheduledJob deleteScheduledJob(@PathVariable String token) throws SiteWhereException {
	IScheduledJob job = getScheduleManagement().getScheduledJobByToken(token);
	if (job == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidScheduledJobToken, ErrorLevel.ERROR);
	}
	return getScheduleManagement().deleteScheduledJob(job.getId());
    }

    protected IScheduleManagement getScheduleManagement() {
	return getMicroservice().getScheduleManagementApiChannel();
    }

    protected IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagement();
    }

    protected IAssetManagement getAssetManagement() {
	return getMicroservice().getAssetManagement();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}