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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.configuration.ConfigurationContentParser;
import com.sitewhere.configuration.content.ElementContent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroserviceManagement;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.microservice.management.IMicroserviceManagementCoordinator;
import com.sitewhere.spi.microservice.state.IInstanceTopologySnapshot;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.rest.RestControllerBase;
import com.sitewhere.web.rest.model.InstanceTopologySummary;
import com.sitewhere.web.rest.model.TopologySummaryBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for instance management.
 * 
 * @author Derek Adams
 */
@RestController
@CrossOrigin(exposedHeaders = { "X-SiteWhere-Error", "X-SiteWhere-Error-Code" })
@RequestMapping(value = "/instance")
@Api(value = "instance")
public class Instance extends RestControllerBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    /**
     * Get most recent instance topology (includes both global and tenant
     * microservices).
     * 
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/topology", method = RequestMethod.GET)
    @ApiOperation(value = "Get current instance topology")
    @Secured({ SiteWhereRoles.REST })
    public List<InstanceTopologySummary> getInstanceTopology() throws SiteWhereException {
	IInstanceTopologySnapshot snapshot = getMicroserviceManagementCoordinator().getInstanceTopologySnapshot();
	return TopologySummaryBuilder.build(snapshot);
    }

    /**
     * Get most recent instance topology (includes only global microservices).
     * 
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/topology/global", method = RequestMethod.GET)
    @ApiOperation(value = "Get current instance topology")
    @Secured({ SiteWhereRoles.REST })
    public List<InstanceTopologySummary> getGlobalInstanceTopology() throws SiteWhereException {
	IInstanceTopologySnapshot snapshot = getMicroserviceManagementCoordinator().getInstanceTopologySnapshot();
	List<InstanceTopologySummary> summary = TopologySummaryBuilder.build(snapshot);
	List<InstanceTopologySummary> filtered = new ArrayList<>();
	for (InstanceTopologySummary current : summary) {
	    if (current.isGlobal()) {
		filtered.add(current);
	    }
	}
	return filtered;
    }

    /**
     * Get most recent instance topology (includes only tenant microservices).
     * 
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/topology/tenant", method = RequestMethod.GET)
    @ApiOperation(value = "Get current instance topology")
    @Secured({ SiteWhereRoles.REST })
    public List<InstanceTopologySummary> getTenantInstanceTopology() throws SiteWhereException {
	IInstanceTopologySnapshot snapshot = getMicroserviceManagementCoordinator().getInstanceTopologySnapshot();
	List<InstanceTopologySummary> summary = TopologySummaryBuilder.build(snapshot);
	List<InstanceTopologySummary> filtered = new ArrayList<>();
	for (InstanceTopologySummary current : summary) {
	    if (!current.isGlobal()) {
		filtered.add(current);
	    }
	}
	return filtered;
    }

    /**
     * Get configuration model for microservice based on service identifier.
     * 
     * @param identifier
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/configuration/model", method = RequestMethod.GET)
    @ApiOperation(value = "Get configuration model based on service identifier")
    @Secured({ SiteWhereRoles.REST })
    public IConfigurationModel getMicroserviceConfigurationModel(
	    @ApiParam(value = "Service identifier", required = true) @PathVariable String identifier)
	    throws SiteWhereException {
	return getMicroserviceManagementCoordinator().getMicroserviceManagement(identifier).getConfigurationModel();
    }

    /**
     * Get configuration for microservice based on service identifier.
     * 
     * @param identifier
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/configuration", method = RequestMethod.GET)
    @ApiOperation(value = "Get global configuration based on service identifier")
    @Secured({ SiteWhereRoles.REST })
    public ElementContent getMicroserviceGlobalConfiguration(
	    @ApiParam(value = "Service identifier", required = true) @PathVariable String identifier)
	    throws SiteWhereException {
	IMicroserviceManagement management = getMicroserviceManagementCoordinator()
		.getMicroserviceManagement(identifier);
	return ConfigurationContentParser.parse(management.getConfiguration(), management.getConfigurationModel());
    }

    /**
     * Get tenant configuration for microservice based on service identifier.
     * 
     * @param identifier
     * @param tenantId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/configuration/{tenantId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get tenant configuration based on service identifier")
    @Secured({ SiteWhereRoles.REST })
    public ElementContent getMicroserviceTenantConfiguration(
	    @ApiParam(value = "Service identifier", required = true) @PathVariable String identifier,
	    @ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId) throws SiteWhereException {
	IMicroserviceManagement management = getMicroserviceManagementCoordinator()
		.getMicroserviceManagement(identifier);
	return ConfigurationContentParser.parse(management.getTenantConfiguration(tenantId),
		management.getConfigurationModel());
    }

    public IMicroserviceManagementCoordinator getMicroserviceManagementCoordinator() {
	return getMicroservice().getMicroserviceManagementCoordinator();
    }
}