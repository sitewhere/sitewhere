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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.system.IVersion;

/**
 * Controller for system operations.
 */
@RestController
@RequestMapping("/api/system")
public class System {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(System.class);

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /**
     * Get version information about the server.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/version")
    public IVersion getVersion() throws SiteWhereException {
	return getMicroservice().getVersion();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}