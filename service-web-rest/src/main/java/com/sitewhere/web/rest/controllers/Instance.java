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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.IMicroserviceManagement;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.microservice.management.IMicroserviceManagementCoordinator;
import com.sitewhere.spi.microservice.scripting.IScriptManagement;
import com.sitewhere.spi.microservice.scripting.IScriptMetadata;
import com.sitewhere.spi.microservice.scripting.IScriptTemplate;
import com.sitewhere.spi.microservice.scripting.IScriptVersion;
import com.sitewhere.spi.microservice.state.IInstanceTopologySnapshot;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.spi.microservice.state.ITopologyStateAggregator;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.annotation.SiteWhereCrossOrigin;
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
@SiteWhereCrossOrigin
@RequestMapping(value = "/instance")
@Api(value = "instance")
public class Instance extends RestControllerBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Instance.class);

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
    @RequestMapping(value = "/microservice/{identifier}/tenants/{tenantToken}/state", method = RequestMethod.GET)
    @ApiOperation(value = "Get state information for specific tenant engine across all microservice instances")
    @Secured({ SiteWhereRoles.REST })
    public List<ITenantEngineState> getMicroserviceTenantRuntimeState(
	    @ApiParam(value = "Service identifier", required = true) @PathVariable String identifier,
	    @ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken)
	    throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	if (msid == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidMicroserviceIdentifier, ErrorLevel.ERROR);
	}
	return getTopologyStateAggregator().getTenantEngineState(msid.getPath(), tenant.getId());
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
	IMicroserviceManagement management = getMicroserviceManagementCoordinator()
		.getMicroserviceManagement(identifier);
	return management.getConfigurationModel();
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
     * @param tenantToken
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/configuration/{tenantToken}", method = RequestMethod.GET)
    @ApiOperation(value = "Get tenant configuration based on service identifier")
    @Secured({ SiteWhereRoles.REST })
    public ElementContent getMicroserviceTenantConfiguration(
	    @ApiParam(value = "Service identifier", required = true) @PathVariable String identifier,
	    @ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken)
	    throws SiteWhereException {
	IMicroserviceManagement management = getMicroserviceManagementCoordinator()
		.getMicroserviceManagement(identifier);
	ITenant tenant = assureTenant(tenantToken);
	return ConfigurationContentParser.parse(management.getTenantConfiguration(tenant.getId()),
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
     * @param tenantToken
     * @param content
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/configuration/{tenantToken}", method = RequestMethod.POST)
    @ApiOperation(value = "Update global configuration based on service identifier.")
    @Secured({ SiteWhereRoles.REST })
    public void updateMicroserviceTenantConfiguration(
	    @ApiParam(value = "Service identifier", required = true) @PathVariable String identifier,
	    @ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken,
	    @RequestBody ElementContent content) throws SiteWhereException {
	IMicroserviceManagement management = getMicroserviceManagementCoordinator()
		.getMicroserviceManagement(identifier);
	Document xml = ConfigurationContentParser.buildXml(content, management.getConfigurationModel());
	String config = ConfigurationContentParser.format(xml);
	ITenant tenant = assureTenant(tenantToken);
	management.updateTenantConfiguration(tenant.getId(), config.getBytes());
    }

    /**
     * Get list of script templates for a given microservice.
     * 
     * @param identifier
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/scripting/templates", method = RequestMethod.GET)
    @ApiOperation(value = "Get list of script templates for a given microservice")
    @Secured({ SiteWhereRoles.REST })
    public List<IScriptTemplate> getMicroserviceScriptTemplates(
	    @ApiParam(value = "Service identifier", required = true) @PathVariable String identifier)
	    throws SiteWhereException {
	IMicroserviceManagement management = getMicroserviceManagementCoordinator()
		.getMicroserviceManagement(identifier);
	return management.getScriptTemplates();
    }

    /**
     * Get content for a script template for a given microservice.
     * 
     * @param identifier
     * @param templateId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/scripting/templates/{templateId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get list of script templates for a given microservice")
    @Secured({ SiteWhereRoles.REST })
    public ResponseEntity<byte[]> getMicroserviceScriptTemplate(
	    @ApiParam(value = "Service identifier", required = true) @PathVariable String identifier,
	    @ApiParam(value = "Template id", required = true) @PathVariable String templateId)
	    throws SiteWhereException {
	IMicroserviceManagement management = getMicroserviceManagementCoordinator()
		.getMicroserviceManagement(identifier);
	byte[] content = management.getScriptTemplateContent(templateId);
	final HttpHeaders headers = new HttpHeaders();
	headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	return new ResponseEntity<byte[]>(content, headers, HttpStatus.OK);
    }

    /**
     * Get a list of script metadata for the given tenant.
     * 
     * @param tenantToken
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/scripting/tenants/{tenantToken}/scripts", method = RequestMethod.GET)
    @ApiOperation(value = "Get list of script metadata for the given tenant")
    @Secured({ SiteWhereRoles.REST })
    public List<IScriptMetadata> listTenantScriptMetadata(
	    @ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken)
	    throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	return getScriptManagement().getScriptMetadataList(tenant.getId());
    }

    /**
     * Get metadata for a tenant script based on unique script id.
     * 
     * @param tenantToken
     * @param scriptId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/scripting/tenants/{tenantToken}/scripts/{scriptId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get metadata for a tenant script based on unique script id")
    @Secured({ SiteWhereRoles.REST })
    public IScriptMetadata getTenantScriptMetadata(
	    @ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken,
	    @ApiParam(value = "Script id", required = true) @PathVariable String scriptId) throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	return getScriptManagement().getScriptMetadata(tenant.getId(), scriptId);
    }

    /**
     * Create a new tenant script.
     * 
     * @param tenantToken
     * @param request
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/scripting/tenants/{tenantToken}/scripts", method = RequestMethod.POST)
    @ApiOperation(value = "Create a new tenant script")
    @Secured({ SiteWhereRoles.REST })
    public IScriptMetadata createTenantScript(
	    @ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken,
	    @RequestBody ScriptCreateRequest request) throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	return getScriptManagement().createScript(tenant.getId(), request);
    }

    /**
     * Get tenant script content based on unique script id and version identifier.
     * 
     * @param tenantToken
     * @param scriptId
     * @param versionId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/scripting/tenants/{tenantToken}/scripts/{scriptId}/versions/{versionId}/content", method = RequestMethod.GET)
    @ApiOperation(value = "Get content for a tenant script based on unique script id and version id")
    @Secured({ SiteWhereRoles.REST })
    public String getTenantScriptContent(
	    @ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken,
	    @ApiParam(value = "Script id", required = true) @PathVariable String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathVariable String versionId) throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	return new String(getScriptManagement().getScriptContent(tenant.getId(), scriptId, versionId));
    }

    /**
     * Update an existing script.
     * 
     * @param tenantToken
     * @param request
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/scripting/tenants/{tenantToken}/scripts/{scriptId}/versions/{versionId}", method = RequestMethod.POST)
    @ApiOperation(value = "Create a new tenant script")
    @Secured({ SiteWhereRoles.REST })
    public IScriptMetadata updateTenantScript(
	    @ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken,
	    @ApiParam(value = "Script id", required = true) @PathVariable String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathVariable String versionId,
	    @RequestBody ScriptCreateRequest request) throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	return getScriptManagement().updateScript(tenant.getId(), scriptId, versionId, request);
    }

    /**
     * Clone an existing tenant script version to create a new version.
     * 
     * @param tenantToken
     * @param scriptId
     * @param versionId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/scripting/tenants/{tenantToken}/scripts/{scriptId}/versions/{versionId}/clone", method = RequestMethod.POST)
    @ApiOperation(value = "Clone an existing tenant script version to create a new version")
    @Secured({ SiteWhereRoles.REST })
    public IScriptVersion cloneTenantScript(
	    @ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken,
	    @ApiParam(value = "Script id", required = true) @PathVariable String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathVariable String versionId,
	    @RequestBody ScriptCloneRequest request) throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	return getScriptManagement().cloneScript(tenant.getId(), scriptId, versionId, request.getComment());
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
    @RequestMapping(value = "/scripting/tenants/{tenantToken}/scripts/{scriptId}/versions/{versionId}/activate", method = RequestMethod.POST)
    @ApiOperation(value = "Activate a tenant script version")
    @Secured({ SiteWhereRoles.REST })
    public IScriptMetadata activateTenantScript(
	    @ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken,
	    @ApiParam(value = "Script id", required = true) @PathVariable String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathVariable String versionId) throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	return getScriptManagement().activateScript(tenant.getId(), scriptId, versionId);
    }

    /**
     * Delete a tenant script. This action causes the script metadata, content, and
     * all version information to be deleted.
     * 
     * @param tenantToken
     * @param scriptId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/scripting/tenants/{tenantToken}/scripts/{scriptId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete a tenant script and version history")
    @Secured({ SiteWhereRoles.REST })
    public IScriptMetadata deleteTenantScript(
	    @ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken,
	    @ApiParam(value = "Script id", required = true) @PathVariable String scriptId) throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	return getScriptManagement().deleteScript(tenant.getId(), scriptId);
    }

    /**
     * Verify that a tenant exists based on reference token.
     * 
     * @param tenantToken
     * @return
     * @throws SiteWhereException
     */
    protected ITenant assureTenant(String tenantToken) throws SiteWhereException {
	ITenant tenant = getTenantManagement().getTenantByToken(tenantToken);
	if (tenant == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidTenantToken, ErrorLevel.ERROR);
	}
	return tenant;
    }

    public IMicroserviceManagementCoordinator getMicroserviceManagementCoordinator() {
	return getMicroservice().getMicroserviceManagementCoordinator();
    }

    public ITopologyStateAggregator getTopologyStateAggregator() {
	return getMicroservice().getTopologyStateAggregator();
    }

    public ITenantManagement getTenantManagement() {
	return getMicroservice().getTenantManagementApiDemux().getApiChannel();
    }

    public IScriptManagement getScriptManagement() {
	return getMicroservice().getScriptManagement();
    }
}