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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;

import com.sitewhere.configuration.ConfigurationContentParser;
import com.sitewhere.configuration.content.ElementContent;
import com.sitewhere.microservice.scripting.ScriptCloneRequest;
import com.sitewhere.microservice.scripting.ScriptCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroserviceManagement;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.microservice.management.IMicroserviceManagementCoordinator;
import com.sitewhere.spi.microservice.scripting.IMicroserviceScriptingManager;
import com.sitewhere.spi.microservice.scripting.IScriptMetadata;
import com.sitewhere.spi.microservice.scripting.IScriptVersion;
import com.sitewhere.spi.microservice.state.IInstanceTopologySnapshot;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.spi.microservice.state.ITopologyStateAggregator;
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
	IInstanceTopologySnapshot snapshot = getTopologyStateAggregator().getInstanceTopologySnapshot();
	return TopologySummaryBuilder.build(snapshot);
    }

    /**
     * Get most recent instance topology (includes only global microservices).
     * 
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/topology/global", method = RequestMethod.GET)
    @ApiOperation(value = "Get global microservices in current instance topology")
    @Secured({ SiteWhereRoles.REST })
    public List<InstanceTopologySummary> getGlobalInstanceTopology() throws SiteWhereException {
	IInstanceTopologySnapshot snapshot = getTopologyStateAggregator().getInstanceTopologySnapshot();
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
    @ApiOperation(value = "Get tenant microservices in current instance topology")
    @Secured({ SiteWhereRoles.REST })
    public List<InstanceTopologySummary> getTenantInstanceTopology() throws SiteWhereException {
	IInstanceTopologySnapshot snapshot = getTopologyStateAggregator().getInstanceTopologySnapshot();
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
     * For a given microservice identifier, find the state of all tenant engines
     * (across all microservice instances) for a given tenant id.
     * 
     * @param identifier
     * @param tenantId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/tenants/{tenantId}/state", method = RequestMethod.GET)
    @ApiOperation(value = "Get state information for specific tenant engine across all microservice instances")
    @Secured({ SiteWhereRoles.REST })
    public List<ITenantEngineState> getMicroserviceTenantRuntimeState(
	    @ApiParam(value = "Service identifier", required = true) @PathVariable String identifier,
	    @ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId) throws SiteWhereException {
	return getTopologyStateAggregator().getTenantEngineState(identifier, tenantId);
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
     * Get global configuration for microservice based on service identifier.
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
	return ConfigurationContentParser.parse(management.getGlobalConfiguration(),
		management.getConfigurationModel());
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

    /**
     * Update global configuration for microservice based on service identifier.
     * 
     * @param identifier
     * @param content
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/configuration", method = RequestMethod.POST)
    @ApiOperation(value = "Update global configuration based on service identifier.")
    @Secured({ SiteWhereRoles.REST })
    public void updateMicroserviceGlobalConfiguration(
	    @ApiParam(value = "Service identifier", required = true) @PathVariable String identifier,
	    @RequestBody ElementContent content) throws SiteWhereException {
	IMicroserviceManagement management = getMicroserviceManagementCoordinator()
		.getMicroserviceManagement(identifier);
	Document xml = ConfigurationContentParser.buildXml(content, management.getConfigurationModel());
	String config = ConfigurationContentParser.format(xml);
	management.updateGlobalConfiguration(config.getBytes());
    }

    /**
     * Update tenant configuration for microservice based on service identifier.
     * 
     * @param identifier
     * @param content
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/configuration/{tenantId}", method = RequestMethod.POST)
    @ApiOperation(value = "Update global configuration based on service identifier.")
    @Secured({ SiteWhereRoles.REST })
    public void updateMicroserviceTenantConfiguration(
	    @ApiParam(value = "Service identifier", required = true) @PathVariable String identifier,
	    @ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId,
	    @RequestBody ElementContent content) throws SiteWhereException {
	IMicroserviceManagement management = getMicroserviceManagementCoordinator()
		.getMicroserviceManagement(identifier);
	Document xml = ConfigurationContentParser.buildXml(content, management.getConfigurationModel());
	String config = ConfigurationContentParser.format(xml);
	management.updateTenantConfiguration(tenantId, config.getBytes());
    }

    /**
     * Get a list of script metadata for the given tenant.
     * 
     * @param tenantId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/scripting/tenants/{tenantId}/scripts", method = RequestMethod.GET)
    @ApiOperation(value = "Get list of script metadata for the given tenant")
    @Secured({ SiteWhereRoles.REST })
    public List<IScriptMetadata> listTenantScriptMetadata(
	    @ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId) throws SiteWhereException {
	return getScriptingManager().getScriptMetadataList(tenantId);
    }

    /**
     * Get metadata for a tenant script based on unique script id.
     * 
     * @param tenantId
     * @param scriptId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/scripting/tenants/{tenantId}/scripts/{scriptId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get metadata for a tenant script based on unique script id")
    @Secured({ SiteWhereRoles.REST })
    public IScriptMetadata getTenantScriptMetadata(
	    @ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId,
	    @ApiParam(value = "Script id", required = true) @PathVariable String scriptId) throws SiteWhereException {
	return getScriptingManager().getScriptMetadata(tenantId, scriptId);
    }

    /**
     * Create a new tenant script.
     * 
     * @param tenantId
     * @param request
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/scripting/tenants/{tenantId}/scripts", method = RequestMethod.POST)
    @ApiOperation(value = "Create a new tenant script")
    @Secured({ SiteWhereRoles.REST })
    public IScriptMetadata createTenantScript(
	    @ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId,
	    @RequestBody ScriptCreateRequest request) throws SiteWhereException {
	return getScriptingManager().createScript(tenantId, request);
    }

    /**
     * Get tenant script content based on unique script id and version identifier.
     * 
     * @param tenantId
     * @param scriptId
     * @param versionId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/scripting/tenants/{tenantId}/scripts/{scriptId}/versions/{versionId}/content", method = RequestMethod.GET)
    @ApiOperation(value = "Get content for a tenant script based on unique script id and version id")
    @Secured({ SiteWhereRoles.REST })
    public String getTenantScriptContent(@ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId,
	    @ApiParam(value = "Script id", required = true) @PathVariable String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathVariable String versionId) throws SiteWhereException {
	return new String(getScriptingManager().getScriptContent(tenantId, scriptId, versionId));
    }

    /**
     * Update an existing script.
     * 
     * @param tenantId
     * @param request
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/scripting/tenants/{tenantId}/scripts/{scriptId}/versions/{versionId}", method = RequestMethod.POST)
    @ApiOperation(value = "Create a new tenant script")
    @Secured({ SiteWhereRoles.REST })
    public IScriptMetadata updateTenantScript(
	    @ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId,
	    @ApiParam(value = "Script id", required = true) @PathVariable String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathVariable String versionId,
	    @RequestBody ScriptCreateRequest request) throws SiteWhereException {
	return getScriptingManager().updateScript(tenantId, scriptId, versionId, request);
    }

    /**
     * Clone an existing tenant script version to create a new version.
     * 
     * @param tenantId
     * @param scriptId
     * @param versionId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/scripting/tenants/{tenantId}/scripts/{scriptId}/versions/{versionId}/clone", method = RequestMethod.POST)
    @ApiOperation(value = "Clone an existing tenant script version to create a new version")
    @Secured({ SiteWhereRoles.REST })
    public IScriptVersion cloneTenantScript(
	    @ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId,
	    @ApiParam(value = "Script id", required = true) @PathVariable String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathVariable String versionId,
	    @RequestBody ScriptCloneRequest request) throws SiteWhereException {
	return getScriptingManager().cloneScript(tenantId, scriptId, versionId, request.getComment());
    }

    /**
     * Activate a tenant script. This action causes the given version to become the
     * active script and pushes the content out to all listening microservices.
     * 
     * @param tenantId
     * @param scriptId
     * @param versionId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/scripting/tenants/{tenantId}/scripts/{scriptId}/versions/{versionId}/activate", method = RequestMethod.POST)
    @ApiOperation(value = "Activate a tenant script version")
    @Secured({ SiteWhereRoles.REST })
    public IScriptMetadata activateTenantScript(
	    @ApiParam(value = "Tenant id", required = true) @PathVariable String tenantId,
	    @ApiParam(value = "Script id", required = true) @PathVariable String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathVariable String versionId) throws SiteWhereException {
	return getScriptingManager().activateScript(tenantId, scriptId, versionId);
    }

    public IMicroserviceManagementCoordinator getMicroserviceManagementCoordinator() {
	return getMicroservice().getMicroserviceManagementCoordinator();
    }

    public ITopologyStateAggregator getTopologyStateAggregator() {
	return getMicroservice().getTopologyStateAggregator();
    }

    public IMicroserviceScriptingManager getScriptingManager() {
	return getMicroservice().getScriptingManager();
    }
}