/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.MicroserviceUtils;
import com.sitewhere.microservice.scripting.ScriptCloneRequest;
import com.sitewhere.microservice.scripting.ScriptCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.scripting.IScriptManagement;
import com.sitewhere.spi.microservice.tenant.ITenantManagement;
import com.sitewhere.spi.tenant.ITenant;

import io.sitewhere.k8s.crd.microservice.SiteWhereMicroservice;
import io.sitewhere.k8s.crd.microservice.SiteWhereMicroserviceList;
import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;
import io.sitewhere.k8s.crd.tenant.SiteWhereTenantList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * Controller for instance management.
 */
@Path("/api/instance")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "instance")
public class Instance {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Instance.class);

    @Inject
    private IInstanceManagementMicroservice<?> microservice;

    /**
     * Get most recent instance topology (includes both global and tenant
     * microservices).
     * 
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/topology")
    @ApiOperation(value = "Get current instance topology")
    public Response getInstanceTopology() throws SiteWhereException {
	return Response.ok().build();
    }

    /**
     * Get most recent instance topology (includes only global microservices).
     * 
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/topology/global")
    @ApiOperation(value = "Get global microservices in current instance topology")
    public Response getGlobalInstanceTopology() throws SiteWhereException {
	return Response.ok().build();
    }

    /**
     * Get most recent instance topology (includes only tenant microservices).
     * 
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/topology/tenant")
    @ApiOperation(value = "Get tenant microservices in current instance topology")
    public Response getTenantInstanceTopology() throws SiteWhereException {
	return Response.ok().build();
    }

    /**
     * For a given microservice identifier, find the state of all tenant engines
     * (across all microservice instances) for a given tenant id.
     * 
     * @param identifier
     * @param tenantToken
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/microservice/{identifier}/tenants/{tenantToken}/state")
    @ApiOperation(value = "Get state information for specific tenant engine across all microservice instances")
    public Response getMicroserviceTenantRuntimeState(
	    @ApiParam(value = "Service identifier", required = true) @PathParam("identifier") String identifier,
	    @ApiParam(value = "Tenant token", required = true) @PathParam("tenantToken") String tenantToken)
	    throws SiteWhereException {
	return Response.ok().build();
    }

    /**
     * Get configuration model for microservice based on service identifier.
     * 
     * @param identifier
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/microservice/{identifier}/configuration/model")
    @ApiOperation(value = "Get configuration model based on service identifier")
    public Response getMicroserviceConfigurationModel(
	    @ApiParam(value = "Service identifier", required = true) @PathParam("identifier") String identifier)
	    throws SiteWhereException {
	return null;
    }

    /**
     * Get global configuration for microservice based on service identifier.
     * 
     * @param identifier
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/microservice/{identifier}/configuration")
    @ApiOperation(value = "Get global configuration based on service identifier")
    public Response getInstanceConfiguration(
	    @ApiParam(value = "Service identifier", required = true) @PathParam("identifier") String identifier)
	    throws SiteWhereException {
	return null;
    }

    /**
     * Update global configuration for microservice based on service identifier.
     * 
     * @param identifier
     * @param content
     * @throws SiteWhereException
     */
    @POST
    @Path("/microservice/{identifier}/configuration")
    @ApiOperation(value = "Update global configuration based on service identifier.")
    public void updateInstanceConfiguration(
	    @ApiParam(value = "Service identifier", required = true) @PathParam("identifier") String identifier)
	    throws SiteWhereException {
    }

    /**
     * Get tenant configuration for microservice based on service identifier.
     * 
     * @param identifier
     * @param tenantToken
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/microservice/{identifier}/tenants/{tenantToken}/configuration")
    @ApiOperation(value = "Get tenant configuration based on service identifier")
    public Response getTenantEngineConfiguration(
	    @ApiParam(value = "Service identifier", required = true) @PathParam("identifier") String identifier,
	    @ApiParam(value = "Tenant token", required = true) @PathParam("tenantToken") String tenantToken)
	    throws SiteWhereException {
	return null;
    }

    /**
     * Update tenant configuration for microservice based on service identifier.
     * 
     * @param identifier
     * @param tenantToken
     * @param content
     * @throws SiteWhereException
     */
    @POST
    @Path("/microservice/{identifier}/tenants/{tenantToken}/configuration")
    @ApiOperation(value = "Update global configuration based on service identifier.")
    public void updateTenantEngineConfiguration(
	    @ApiParam(value = "Service identifier", required = true) @PathParam("identifier") String identifier,
	    @ApiParam(value = "Tenant token", required = true) @PathParam("tenantToken") String tenantToken)
	    throws SiteWhereException {
    }

    /**
     * Get list of script templates for a given microservice.
     * 
     * @param identifier
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/microservice/{identifier}/scripting/templates")
    @ApiOperation(value = "Get list of script templates for a given microservice")
    public Response getScriptTemplates(
	    @ApiParam(value = "Service identifier", required = true) @PathParam("identifier") String identifier)
	    throws SiteWhereException {
	return Response.ok().build();
    }

    /**
     * Get content for a script template for a given microservice.
     * 
     * @param identifier
     * @param templateId
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/microservice/{identifier}/scripting/templates/{templateId}")
    @ApiOperation(value = "Get list of script templates for a given microservice")
    public Response getScriptTemplateContent(
	    @ApiParam(value = "Service identifier", required = true) @PathParam("identifier") String identifier,
	    @ApiParam(value = "Template id", required = true) @PathParam("templateId") String templateId)
	    throws SiteWhereException {
	return null;
    }

    /**
     * Get a list of global script metadata.
     * 
     * @param identifier
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/microservice/{identifier}/scripting/scripts")
    @ApiOperation(value = "Get list of global script metadata")
    public Response listGlobalScriptMetadata(
	    @ApiParam(value = "Function identifier", required = true) @PathParam("identifier") String identifier)
	    throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response.ok(getScriptManagement().getScriptMetadataList(msid, null)).build();
    }

    /**
     * Get metadata for a global script based on unique script id.
     * 
     * @param identifier
     * @param scriptId
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/microservice/{identifier}/scripting/scripts/{scriptId}")
    @ApiOperation(value = "Get metadata for a tenant script based on unique script id")
    public Response getGlobalScriptMetadata(
	    @ApiParam(value = "Function identifier", required = true) @PathParam("identifier") String identifier,
	    @ApiParam(value = "Script id", required = true) @PathParam("scriptId") String scriptId)
	    throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response.ok(getScriptManagement().getScriptMetadata(msid, null, scriptId)).build();
    }

    /**
     * Create a global script.
     * 
     * @param identifier
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/microservice/{identifier}/scripting/scripts")
    @ApiOperation(value = "Create a new global script")
    public Response createGlobalScript(
	    @ApiParam(value = "Function identifier", required = true) @PathParam("identifier") String identifier,
	    @RequestBody ScriptCreateRequest request) throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response.ok(getScriptManagement().createScript(msid, null, request)).build();
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
    @GET
    @Path("/microservice/{identifier}/scripting/scripts/{scriptId}/versions/{versionId}/content")
    @ApiOperation(value = "Get content for a global script based on unique script id and version id")
    public Response getGlobalScriptContent(
	    @ApiParam(value = "Function identifier", required = true) @PathParam("identifier") String identifier,
	    @ApiParam(value = "Script id", required = true) @PathParam("scriptId") String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathParam("versionId") String versionId)
	    throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response.ok(new String(getScriptManagement().getScriptContent(msid, null, scriptId, versionId))).build();
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
    @POST
    @Path("/microservice/{identifier}/scripting/scripts/{scriptId}/versions/{versionId}")
    @ApiOperation(value = "Update an existing global script")
    public Response updateGlobalScript(
	    @ApiParam(value = "Function identifier", required = true) @PathParam("identifier") String identifier,
	    @ApiParam(value = "Script id", required = true) @PathParam("scriptId") String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathParam("versionId") String versionId,
	    @RequestBody ScriptCreateRequest request) throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response.ok(getScriptManagement().updateScript(msid, null, scriptId, versionId, request)).build();
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
    @POST
    @Path("/microservice/{identifier}/scripting/scripts/{scriptId}/versions/{versionId}/clone")
    @ApiOperation(value = "Clone an existing global script version to create a new version")
    public Response cloneGlobalScript(
	    @ApiParam(value = "Function identifier", required = true) @PathParam("identifier") String identifier,
	    @ApiParam(value = "Script id", required = true) @PathParam("scriptId") String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathParam("versionId") String versionId,
	    @RequestBody ScriptCloneRequest request) throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response.ok(getScriptManagement().cloneScript(msid, null, scriptId, versionId, request.getComment()))
		.build();
    }

    /**
     * Activate a global script. This action causes the given version to become the
     * active script and pushes the content out to all listening microservices.
     * 
     * @param identifier
     * @param scriptId
     * @param versionId
     * @return
     */
    @POST
    @Path("/microservice/{identifier}/scripting/scripts/{scriptId}/versions/{versionId}/activate")
    @ApiOperation(value = "Activate a global script version")
    public Response activateGlobalScript(
	    @ApiParam(value = "Function identifier", required = true) @PathParam("identifier") String identifier,
	    @ApiParam(value = "Script id", required = true) @PathParam("scriptId") String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathParam("versionId") String versionId)
	    throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response.ok(getScriptManagement().activateScript(msid, null, scriptId, versionId)).build();
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
    @DELETE
    @Path("/microservice/{identifier}/scripting/scripts/{scriptId}")
    @ApiOperation(value = "Delete a global script and version history")
    public Response deleteGlobalScript(
	    @ApiParam(value = "Function identifier", required = true) @PathParam("identifier") String identifier,
	    @ApiParam(value = "Script id", required = true) @PathParam("scriptId") String scriptId)
	    throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response.ok(getScriptManagement().deleteScript(msid, null, scriptId)).build();
    }

    /**
     * Get a list of script metadata for the given tenant.
     * 
     * @param tenantToken
     * @param identifier
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/microservice/{identifier}/tenants/{tenantToken}/scripting/scripts")
    @SuppressWarnings("unused")
    @ApiOperation(value = "Get list of script metadata for the given tenant")
    public Response listTenantScriptMetadata(
	    @ApiParam(value = "Tenant token", required = true) @PathParam("tenantToken") String tenantToken,
	    @ApiParam(value = "Function identifier", required = true) @PathParam("identifier") String identifier)
	    throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response.ok(getScriptManagement().getScriptMetadataList(msid, null)).build();
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
    @GET
    @Path("/microservice/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}")
    @ApiOperation(value = "Get metadata for a tenant script based on unique script id")
    public Response getTenantScriptMetadata(
	    @ApiParam(value = "Tenant token", required = true) @PathParam("tenantToken") String tenantToken,
	    @ApiParam(value = "Function identifier", required = true) @PathParam("identifier") String identifier,
	    @ApiParam(value = "Script id", required = true) @PathParam("scriptId") String scriptId)
	    throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response.ok(getScriptManagement().getScriptMetadata(msid, null, scriptId)).build();
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
    @POST
    @Path("/microservice/{identifier}/tenants/{tenantToken}/scripting/scripts")
    @ApiOperation(value = "Create a new tenant script")
    public Response createTenantScript(
	    @ApiParam(value = "Tenant token", required = true) @PathParam("tenantToken") String tenantToken,
	    @ApiParam(value = "Function identifier", required = true) @PathParam("identifier") String identifier,
	    @RequestBody ScriptCreateRequest request) throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response.ok(getScriptManagement().createScript(msid, null, request)).build();
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
    @GET
    @Path("/microservice/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}/versions/{versionId}/content")
    @ApiOperation(value = "Get content for a tenant script based on unique script id and version id")
    public Response getTenantScriptContent(
	    @ApiParam(value = "Tenant token", required = true) @PathParam("tenantToken") String tenantToken,
	    @ApiParam(value = "Function identifier", required = true) @PathParam("identifier") String identifier,
	    @ApiParam(value = "Script id", required = true) @PathParam("scriptId") String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathParam("versionId") String versionId)
	    throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response.ok(new String(getScriptManagement().getScriptContent(msid, null, scriptId, versionId))).build();
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
    @POST
    @Path("/microservice/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}/versions/{versionId}")
    @ApiOperation(value = "Update an existing tenant script")
    public Response updateTenantScript(
	    @ApiParam(value = "Tenant token", required = true) @PathParam("tenantToken") String tenantToken,
	    @ApiParam(value = "Function identifier", required = true) @PathParam("identifier") String identifier,
	    @ApiParam(value = "Script id", required = true) @PathParam("scriptId") String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathParam("versionId") String versionId,
	    @RequestBody ScriptCreateRequest request) throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response.ok(getScriptManagement().updateScript(msid, null, scriptId, versionId, request)).build();
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
    @POST
    @Path("/microservice/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}/versions/{versionId}/clone")
    @ApiOperation(value = "Clone an existing tenant script version to create a new version")
    public Response cloneTenantScript(
	    @ApiParam(value = "Tenant token", required = true) @PathParam("tenantToken") String tenantToken,
	    @ApiParam(value = "Function identifier", required = true) @PathParam("identifier") String identifier,
	    @ApiParam(value = "Script id", required = true) @PathParam("scriptId") String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathParam("versionId") String versionId,
	    @RequestBody ScriptCloneRequest request) throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response.ok(getScriptManagement().cloneScript(msid, null, scriptId, versionId, request.getComment()))
		.build();
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
    @POST
    @Path("/microservice/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}/versions/{versionId}/activate")
    @ApiOperation(value = "Activate a tenant script version")
    public Response activateTenantScript(
	    @ApiParam(value = "Tenant token", required = true) @PathParam("tenantToken") String tenantToken,
	    @ApiParam(value = "Function identifier", required = true) @PathParam("identifier") String identifier,
	    @ApiParam(value = "Script id", required = true) @PathParam("scriptId") String scriptId,
	    @ApiParam(value = "Version id", required = true) @PathParam("versionId") String versionId)
	    throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response.ok(getScriptManagement().activateScript(msid, null, scriptId, versionId)).build();
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
    @DELETE
    @Path("/microservice/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}")
    @ApiOperation(value = "Delete a tenant script and version history")
    public Response deleteTenantScript(
	    @ApiParam(value = "Tenant token", required = true) @PathParam("tenantToken") String tenantToken,
	    @ApiParam(value = "Function identifier", required = true) @PathParam("identifier") String identifier,
	    @ApiParam(value = "Script id", required = true) @PathParam("scriptId") String scriptId)
	    throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response.ok(getScriptManagement().deleteScript(msid, null, scriptId)).build();
    }

    /**
     * Attempt to look up microservice based on instance id and function identifier.
     * 
     * @param identifier
     * @return
     * @throws SiteWhereException
     */
    protected SiteWhereMicroservice getMicroserviceForIdentifier(IFunctionIdentifier identifier)
	    throws SiteWhereException {
	String instanceId = getMicroservice().getInstanceSettings().getInstanceId();
	SiteWhereMicroserviceList list = getMicroservice().getSiteWhereKubernetesClient().getMicroservices().list();
	for (SiteWhereMicroservice microservice : list.getItems()) {
	    if (MicroserviceUtils.getInstanceName(microservice).equals(instanceId)
		    && MicroserviceUtils.getFunctionalArea(microservice).equals(identifier.getPath())) {
		return microservice;
	    }
	}
	throw new SiteWhereSystemException(ErrorCode.InvalidMicroserviceIdentifier, ErrorLevel.ERROR);
    }

    /**
     * Get tenant associated with token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected SiteWhereTenant getTenantForToken(String token) throws SiteWhereException {
	SiteWhereTenantList list = getMicroservice().getSiteWhereKubernetesClient().getTenants().list();
	for (SiteWhereTenant tenant : list.getItems()) {
	    if (tenant.getMetadata().getName().equals(token)) {
		return tenant;
	    }
	}
	throw new SiteWhereSystemException(ErrorCode.InvalidTenantToken, ErrorLevel.ERROR);
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

    protected ITenantManagement getTenantManagement() {
	return getMicroservice().getTenantManagement();
    }

    protected IScriptManagement getScriptManagement() {
	return getMicroservice().getScriptManagement();
    }

    protected IInstanceManagementMicroservice<?> getMicroservice() {
	return microservice;
    }
}