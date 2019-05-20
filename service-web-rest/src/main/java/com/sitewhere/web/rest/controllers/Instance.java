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
import com.sitewhere.grpc.client.microservice.MicroserviceManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IMicroserviceManagementApiChannel;
import com.sitewhere.microservice.scripting.ScriptCloneRequest;
import com.sitewhere.microservice.scripting.ScriptCreateRequest;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.microservice.scripting.IScriptManagement;
import com.sitewhere.spi.microservice.scripting.IScriptMetadata;
import com.sitewhere.spi.microservice.scripting.IScriptTemplate;
import com.sitewhere.spi.microservice.scripting.IScriptVersion;
import com.sitewhere.spi.microservice.state.IInstanceTopologySnapshot;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
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
	IInstanceTopologySnapshot snapshot = getMicroservice().getTopologyStateAggregator()
		.getInstanceTopologySnapshot();
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
	IInstanceTopologySnapshot snapshot = getMicroservice().getTopologyStateAggregator()
		.getInstanceTopologySnapshot();
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
	IInstanceTopologySnapshot snapshot = getMicroservice().getTopologyStateAggregator()
		.getInstanceTopologySnapshot();
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
	return getMicroservice().getTopologyStateAggregator().getTenantEngineState(msid.getPath(), tenant.getId());
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
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	IMicroserviceManagementApiChannel<?> management = getManagementChannel(msid);
	try {
	    return management.getConfigurationModel();
	} finally {
	    releaseChannel(management);
	}
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
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	IMicroserviceManagementApiChannel<?> management = getManagementChannel(msid);
	try {
	    return ConfigurationContentParser.parse(management.getGlobalConfiguration(),
		    management.getConfigurationModel());
	} finally {
	    releaseChannel(management);
	}
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
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	IMicroserviceManagementApiChannel<?> management = getManagementChannel(msid);
	Document xml = ConfigurationContentParser.buildXml(content, management.getConfigurationModel());
	String config = ConfigurationContentParser.format(xml);
	try {
	    management.updateGlobalConfiguration(config.getBytes());
	} finally {
	    releaseChannel(management);
	}
    }

    /**
     * Get tenant configuration for microservice based on service identifier.
     * 
     * @param identifier
     * @param tenantToken
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/tenants/{tenantToken}/configuration", method = RequestMethod.GET)
    @ApiOperation(value = "Get tenant configuration based on service identifier")
    @Secured({ SiteWhereRoles.REST })
    public ElementContent getMicroserviceTenantConfiguration(
	    @ApiParam(value = "Service identifier", required = true) @PathVariable String identifier,
	    @ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken)
	    throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	IMicroserviceManagementApiChannel<?> management = getManagementChannel(msid);
	ITenant tenant = assureTenant(tenantToken);
	try {
	    return ConfigurationContentParser.parse(management.getTenantConfiguration(tenant.getId()),
		    management.getConfigurationModel());
	} finally {
	    releaseChannel(management);
	}
    }

    /**
     * Update tenant configuration for microservice based on service identifier.
     * 
     * @param identifier
     * @param tenantToken
     * @param content
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/tenants/{tenantToken}/configuration", method = RequestMethod.POST)
    @ApiOperation(value = "Update global configuration based on service identifier.")
    @Secured({ SiteWhereRoles.REST })
    public void updateMicroserviceTenantConfiguration(
	    @ApiParam(value = "Service identifier", required = true) @PathVariable String identifier,
	    @ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken,
	    @RequestBody ElementContent content) throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	IMicroserviceManagementApiChannel<?> management = getManagementChannel(msid);
	Document xml = ConfigurationContentParser.buildXml(content, management.getConfigurationModel());
	String config = ConfigurationContentParser.format(xml);
	ITenant tenant = assureTenant(tenantToken);
	try {
	    management.updateTenantConfiguration(tenant.getId(), config.getBytes());
	} finally {
	    releaseChannel(management);
	}
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
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	IMicroserviceManagementApiChannel<?> management = getManagementChannel(msid);
	try {
	    return management.getScriptTemplates();
	} finally {
	    releaseChannel(management);
	}
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
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	IMicroserviceManagementApiChannel<?> management = getManagementChannel(msid);
	byte[] content = management.getScriptTemplateContent(templateId);
	final HttpHeaders headers = new HttpHeaders();
	headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	try {
	    return new ResponseEntity<byte[]>(content, headers, HttpStatus.OK);
	} finally {
	    releaseChannel(management);
	}
    }

    /**
     * Get a list of global script metadata.
     * 
     * @param functionIdentifier
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/scripting/scripts", method = RequestMethod.GET)
    @ApiOperation(value = "Get list of global script metadata")
    @Secured({ SiteWhereRoles.REST })
    public List<IScriptMetadata> listGlobalScriptMetadata(
	    @ApiParam(value = "Function identifier", required = true) @PathVariable String identifier)
	    throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return getScriptManagement().getScriptMetadataList(msid, null);
    }

    /**
     * Get metadata for a global script based on unique script id.
     * 
     * @param identifier
     * @param scriptId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/scripting/scripts/{scriptId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get metadata for a tenant script based on unique script id")
    @Secured({ SiteWhereRoles.REST })
    public IScriptMetadata getGlobalScriptMetadata(
	    @ApiParam(value = "Function identifier", required = true) @PathVariable String identifier,
	    @ApiParam(value = "Script id", required = true) @PathVariable String scriptId) throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return getScriptManagement().getScriptMetadata(msid, null, scriptId);
    }

    /**
     * Create a global script.
     * 
     * @param identifier
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/scripting/scripts", method = RequestMethod.POST)
    @ApiOperation(value = "Create a new global script")
    @Secured({ SiteWhereRoles.REST })
    public IScriptMetadata createGlobalScript(
	    @ApiParam(value = "Function identifier", required = true) @PathVariable String identifier,
	    @RequestBody ScriptCreateRequest request) throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return getScriptManagement().createScript(msid, null, request);
    }

    /**
     * Get global script content based on unique script id and version identifier.
     * 
     * @param identifier
     * @param scriptId
     * @param versionId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/scripting/scripts/{scriptId}/versions/{versionId}/content", method = RequestMethod.GET)
    @ApiOperation(value = "Get content for a global script based on unique script id and version id")
    @Secured({ SiteWhereRoles.REST })
    public String getGlobalScriptContent(
	    @ApiParam(value = "Function identifier", required = true) @PathVariable String identifier,
	    @ApiParam(value = "Script id", required = true) @PathVariable String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathVariable String versionId) throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return new String(getScriptManagement().getScriptContent(msid, null, scriptId, versionId));
    }

    /**
     * Update an existing global script.
     * 
     * @param identifier
     * @param scriptId
     * @param versionId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/scripting/scripts/{scriptId}/versions/{versionId}", method = RequestMethod.POST)
    @ApiOperation(value = "Update an existing global script")
    @Secured({ SiteWhereRoles.REST })
    public IScriptMetadata updateGlobalScript(
	    @ApiParam(value = "Function identifier", required = true) @PathVariable String identifier,
	    @ApiParam(value = "Script id", required = true) @PathVariable String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathVariable String versionId,
	    @RequestBody ScriptCreateRequest request) throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return getScriptManagement().updateScript(msid, null, scriptId, versionId, request);
    }

    /**
     * Clone an existing global script version to create a new version.
     * 
     * @param identifier
     * @param scriptId
     * @param versionId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/scripting/scripts/{scriptId}/versions/{versionId}/clone", method = RequestMethod.POST)
    @ApiOperation(value = "Clone an existing global script version to create a new version")
    @Secured({ SiteWhereRoles.REST })
    public IScriptVersion cloneGlobalScript(
	    @ApiParam(value = "Function identifier", required = true) @PathVariable String identifier,
	    @ApiParam(value = "Script id", required = true) @PathVariable String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathVariable String versionId,
	    @RequestBody ScriptCloneRequest request) throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return getScriptManagement().cloneScript(msid, null, scriptId, versionId, request.getComment());
    }

    /**
     * Activate a global script. This action causes the given version to become the
     * active script and pushes the content out to all listening microservices.
     * 
     * @param identifier
     * @param scriptId
     * @param versionId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/scripting/scripts/{scriptId}/versions/{versionId}/activate", method = RequestMethod.POST)
    @ApiOperation(value = "Activate a global script version")
    @Secured({ SiteWhereRoles.REST })
    public IScriptMetadata activateGlobalScript(
	    @ApiParam(value = "Function identifier", required = true) @PathVariable String identifier,
	    @ApiParam(value = "Script id", required = true) @PathVariable String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathVariable String versionId) throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return getScriptManagement().activateScript(msid, null, scriptId, versionId);
    }

    /**
     * Delete a global script. This action causes the script metadata, content, and
     * all version information to be deleted.
     * 
     * @param identifier
     * @param scriptId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/scripting/scripts/{scriptId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete a global script and version history")
    @Secured({ SiteWhereRoles.REST })
    public IScriptMetadata deleteGlobalScript(
	    @ApiParam(value = "Function identifier", required = true) @PathVariable String identifier,
	    @ApiParam(value = "Script id", required = true) @PathVariable String scriptId) throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return getScriptManagement().deleteScript(msid, null, scriptId);
    }

    /**
     * Get a list of script metadata for the given tenant.
     * 
     * @param tenantToken
     * @param identifier
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/tenants/{tenantToken}/scripting/scripts", method = RequestMethod.GET)
    @ApiOperation(value = "Get list of script metadata for the given tenant")
    @Secured({ SiteWhereRoles.REST })
    public List<IScriptMetadata> listTenantScriptMetadata(
	    @ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken,
	    @ApiParam(value = "Function identifier", required = true) @PathVariable String identifier)
	    throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return getScriptManagement().getScriptMetadataList(msid, tenant.getId());
    }

    /**
     * Get metadata for a tenant script based on unique script id.
     * 
     * @param tenantToken
     * @param identifier
     * @param scriptId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get metadata for a tenant script based on unique script id")
    @Secured({ SiteWhereRoles.REST })
    public IScriptMetadata getTenantScriptMetadata(
	    @ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken,
	    @ApiParam(value = "Function identifier", required = true) @PathVariable String identifier,
	    @ApiParam(value = "Script id", required = true) @PathVariable String scriptId) throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return getScriptManagement().getScriptMetadata(msid, tenant.getId(), scriptId);
    }

    /**
     * Create tenant script.
     * 
     * @param tenantToken
     * @param identifier
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/tenants/{tenantToken}/scripting/scripts", method = RequestMethod.POST)
    @ApiOperation(value = "Create a new tenant script")
    @Secured({ SiteWhereRoles.REST })
    public IScriptMetadata createTenantScript(
	    @ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken,
	    @ApiParam(value = "Function identifier", required = true) @PathVariable String identifier,
	    @RequestBody ScriptCreateRequest request) throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return getScriptManagement().createScript(msid, tenant.getId(), request);
    }

    /**
     * Get tenant script content based on unique script id and version identifier.
     * 
     * @param tenantToken
     * @param identifier
     * @param scriptId
     * @param versionId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}/versions/{versionId}/content", method = RequestMethod.GET)
    @ApiOperation(value = "Get content for a tenant script based on unique script id and version id")
    @Secured({ SiteWhereRoles.REST })
    public String getTenantScriptContent(
	    @ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken,
	    @ApiParam(value = "Function identifier", required = true) @PathVariable String identifier,
	    @ApiParam(value = "Script id", required = true) @PathVariable String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathVariable String versionId) throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return new String(getScriptManagement().getScriptContent(msid, tenant.getId(), scriptId, versionId));
    }

    /**
     * Update an existing tenant script.
     * 
     * @param tenantToken
     * @param identifier
     * @param scriptId
     * @param versionId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}/versions/{versionId}", method = RequestMethod.POST)
    @ApiOperation(value = "Update an existing tenant script")
    @Secured({ SiteWhereRoles.REST })
    public IScriptMetadata updateTenantScript(
	    @ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken,
	    @ApiParam(value = "Function identifier", required = true) @PathVariable String identifier,
	    @ApiParam(value = "Script id", required = true) @PathVariable String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathVariable String versionId,
	    @RequestBody ScriptCreateRequest request) throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return getScriptManagement().updateScript(msid, tenant.getId(), scriptId, versionId, request);
    }

    /**
     * Clone an existing tenant script version to create a new version.
     * 
     * @param tenantToken
     * @param identifier
     * @param scriptId
     * @param versionId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}/versions/{versionId}/clone", method = RequestMethod.POST)
    @ApiOperation(value = "Clone an existing tenant script version to create a new version")
    @Secured({ SiteWhereRoles.REST })
    public IScriptVersion cloneTenantScript(
	    @ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken,
	    @ApiParam(value = "Function identifier", required = true) @PathVariable String identifier,
	    @ApiParam(value = "Script id", required = true) @PathVariable String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathVariable String versionId,
	    @RequestBody ScriptCloneRequest request) throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return getScriptManagement().cloneScript(msid, tenant.getId(), scriptId, versionId, request.getComment());
    }

    /**
     * Activate a tenant script. This action causes the given version to become the
     * active script and pushes the content out to all listening microservices.
     * 
     * @param tenantToken
     * @param identifier
     * @param scriptId
     * @param versionId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}/versions/{versionId}/activate", method = RequestMethod.POST)
    @ApiOperation(value = "Activate a tenant script version")
    @Secured({ SiteWhereRoles.REST })
    public IScriptMetadata activateTenantScript(
	    @ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken,
	    @ApiParam(value = "Function identifier", required = true) @PathVariable String identifier,
	    @ApiParam(value = "Script id", required = true) @PathVariable String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathVariable String versionId) throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return getScriptManagement().activateScript(msid, tenant.getId(), scriptId, versionId);
    }

    /**
     * Delete a tenant script. This action causes the script metadata, content, and
     * all version information to be deleted.
     * 
     * @param tenantToken
     * @param identifier
     * @param scriptId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/microservice/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete a tenant script and version history")
    @Secured({ SiteWhereRoles.REST })
    public IScriptMetadata deleteTenantScript(
	    @ApiParam(value = "Tenant token", required = true) @PathVariable String tenantToken,
	    @ApiParam(value = "Function identifier", required = true) @PathVariable String identifier,
	    @ApiParam(value = "Script id", required = true) @PathVariable String scriptId) throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return getScriptManagement().deleteScript(msid, tenant.getId(), scriptId);
    }

    /**
     * Find management channel for service that corresponds to identifier.
     * 
     * @param target
     * @return
     * @throws SiteWhereException
     */
    protected IMicroserviceManagementApiChannel<?> getManagementChannel(IFunctionIdentifier target)
	    throws SiteWhereException {
	LifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
		new LifecycleProgressContext(1, "Start management interface."), getMicroservice());
	MicroserviceManagementApiChannel channel = new MicroserviceManagementApiChannel(target.getPath(),
		getMicroservice().getInstanceSettings().getManagementGrpcPort());
	channel.setMicroservice(getMicroservice());
	channel.initialize(monitor);
	channel.start(monitor);
	channel.waitForChannelAvailable();
	return channel;
    }

    /**
     * Release an initialized management channel.
     * 
     * @param channel
     */
    protected void releaseChannel(IMicroserviceManagementApiChannel<?> channel) {
	try {
	    LifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
		    new LifecycleProgressContext(1, "Stop management interface."), getMicroservice());
	    channel.stop(monitor);
	    channel.terminate(monitor);
	} catch (Throwable t) {
	    getLogger().error("Unable to shut down management channel.", t);
	}
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

    public ITenantManagement getTenantManagement() {
	return getMicroservice().getTenantManagementApiChannel();
    }

    public IScriptManagement getScriptManagement() {
	return getMicroservice().getScriptManagement();
    }
}