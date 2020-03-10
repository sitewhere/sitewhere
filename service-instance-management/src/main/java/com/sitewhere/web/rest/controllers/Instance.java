/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.configuration.model.instance.InstanceConfiguration;
import com.sitewhere.microservice.kubernetes.K8sModelConverter;
import com.sitewhere.microservice.scripting.ScriptCloneRequest;
import com.sitewhere.microservice.scripting.ScriptCreateRequest;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.rest.model.microservice.MicroserviceSummary;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.scripting.IScriptManagement;
import com.sitewhere.spi.microservice.tenant.ITenantManagement;
import com.sitewhere.web.rest.model.TenantEngineConfiguration;

import io.sitewhere.k8s.crd.exception.SiteWhereK8sException;
import io.sitewhere.k8s.crd.instance.SiteWhereInstance;
import io.sitewhere.k8s.crd.microservice.SiteWhereMicroservice;
import io.sitewhere.k8s.crd.microservice.SiteWhereMicroserviceList;
import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;
import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Controller for instance management.
 */
@Path("/api/instance")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "instance")
@Tag(name = "Instance", description = "Provides global, instance level information.")
@SecurityRequirements({ @SecurityRequirement(name = "jwtAuth", scopes = {}),
	@SecurityRequirement(name = "tenantIdHeader", scopes = {}),
	@SecurityRequirement(name = "tenantAuthHeader", scopes = {}) })
public class Instance {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Instance.class);

    @Inject
    private IInstanceManagementMicroservice<?> microservice;

    @GET
    @Path("/configuration")
    @Operation(summary = "Get instance global configuration", description = "Get global configuration used by all microservices")
    public Response getInstanceConfiguration() {
	InstanceConfiguration configuration = getMicroservice().getInstanceConfiguration();
	return Response.ok(configuration).build();
    }

    @PUT
    @Path("/{configuration}")
    @Operation(summary = "Update instance global configuration", description = "Update global configuration used by all microservices")
    public Response updateInstanceConfiguration(@RequestBody InstanceConfiguration request) throws SiteWhereException {
	try {
	    SiteWhereInstance instance = getMicroservice().getLastInstanceResource();
	    instance.getSpec().setConfiguration(MarshalUtils.marshalJsonNode(MarshalUtils.marshalJson(request)));
	    getMicroservice().updateInstanceResource(instance);
	    return Response.ok(request).build();
	} catch (IOException e) {
	    return Response.status(Status.BAD_REQUEST).build();
	}
    }

    @GET
    @Path("/microservices")
    @Operation(summary = "Get list of instance microservices", description = "Get detailed list of instance microservices")
    public Response getInstanceMicroservices() {
	String namespace = getMicroservice().getInstanceSettings().getKubernetesNamespace();
	SiteWhereMicroserviceList list = getMicroservice().getSiteWhereKubernetesClient()
		.getAllMicroservices(namespace);
	List<SiteWhereMicroservice> all = new ArrayList<>();
	all.addAll(list.getItems());
	Collections.sort(all, new Comparator<SiteWhereMicroservice>() {

	    @Override
	    public int compare(SiteWhereMicroservice arg0, SiteWhereMicroservice arg1) {
		return arg0.getMetadata().getName().compareTo(arg1.getMetadata().getName());
	    }
	});
	List<MicroserviceSummary> summaries = new ArrayList<>();
	for (SiteWhereMicroservice microservice : all) {
	    summaries.add(K8sModelConverter.convert(microservice));
	}
	return Response.ok(summaries).build();
    }

    /**
     * Get a tenant engine configuration based on functional area and tenant id.
     * 
     * @param identifier
     * @param token
     * @return
     */
    @GET
    @Path("/microservices/{identifier}/tenants/{token}/configuration")
    @Operation(summary = "Get tenant engine configuration", description = "Get tenant engine configuration based on functional area and tenant token")
    public Response getTenantEngineConfiguration(
	    @Parameter(description = "Service identifier", required = true) @PathParam("identifier") String identifier,
	    @Parameter(description = "Tenant token", required = true) @PathParam("token") String token) {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	try {
	    SiteWhereMicroservice microservice = getMicroserviceForIdentifier(msid);
	    SiteWhereTenant tenant = getTenantForToken(token);
	    SiteWhereTenantEngine engine = getMicroservice().getSiteWhereKubernetesClient()
		    .getTenantEngine(microservice, tenant);
	    if (engine == null) {
		return Response.status(Status.NOT_FOUND).build();
	    }
	    TenantEngineConfiguration response = new TenantEngineConfiguration();
	    response.setTenant(K8sModelConverter.convert(tenant));
	    response.setMicroservice(K8sModelConverter.convert(microservice));
	    response.setInstanceConfiguration(getMicroservice().getInstanceConfiguration());
	    response.setMicroserviceConfiguration(microservice.getSpec().getConfiguration());
	    response.setTenantConfiguration(engine.getSpec().getConfiguration());
	    return Response.ok(response).build();
	} catch (SiteWhereException e) {
	    return Response.status(Status.BAD_REQUEST).build();
	} catch (SiteWhereK8sException e) {
	    return Response.status(Status.CONFLICT).build();
	}
    }

    /**
     * Get list of script templates for a given microservice.
     * 
     * @param identifier
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/microservices/{identifier}/scripting/templates")
    @Operation(summary = "Get list of script templates for a given microservice", description = "Get list of script templates for a given microservice")
    public Response getScriptTemplates(
	    @Parameter(description = "Service identifier", required = true) @PathParam("identifier") String identifier)
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
    @Path("/microservices/{identifier}/scripting/templates/{templateId}")
    @ApiOperation(value = "Get list of script templates for a given microservice")
    @Operation(summary = "Get list of script templates for a given microservice", description = "Get list of script templates for a given microservice")
    public Response getScriptTemplateContent(
	    @Parameter(description = "Service identifier", required = true) @PathParam("identifier") String identifier,
	    @Parameter(description = "Template id", required = true) @PathParam("templateId") String templateId)
	    throws SiteWhereException {
	return null;
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
    @Path("/microservices/{identifier}/tenants/{tenantToken}/scripting/scripts")
    @Operation(summary = "Get list of script metadata for the given tenant", description = "Get list of script metadata for the given tenant")
    public Response listTenantScriptMetadata(
	    @Parameter(description = "Tenant token", required = true) @PathParam("tenantToken") String tenantToken,
	    @Parameter(description = "Function identifier", required = true) @PathParam("identifier") String identifier)
	    throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response.ok(getScriptManagement().getScriptMetadataList(msid, tenantToken)).build();
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
    @Path("/microservices/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}")
    @Operation(summary = "Get metadata for a tenant script based on unique script id", description = "Get metadata for a tenant script based on unique script id")
    public Response getTenantScriptMetadata(
	    @Parameter(description = "Tenant token", required = true) @PathParam("tenantToken") String tenantToken,
	    @Parameter(description = "Function identifier", required = true) @PathParam("identifier") String identifier,
	    @Parameter(description = "Script id", required = true) @PathParam("scriptId") String scriptId)
	    throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response.ok(getScriptManagement().getScriptMetadata(msid, tenantToken, scriptId)).build();
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
    @Path("/microservices/{identifier}/tenants/{tenantToken}/scripting/scripts")
    @Operation(summary = "Create a new tenant script", description = "Create a new tenant script")
    public Response createTenantScript(
	    @Parameter(description = "Tenant token", required = true) @PathParam("tenantToken") String tenantToken,
	    @Parameter(description = "Function identifier", required = true) @PathParam("identifier") String identifier,
	    @RequestBody ScriptCreateRequest request) throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response.ok(getScriptManagement().createScript(msid, tenantToken, request)).build();
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
    @Path("/microservices/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}/versions/{versionId}/content")
    @Operation(summary = "Get content for a tenant script", description = "Get content for a tenant script based on unique script id and version id")
    public Response getTenantScriptContent(
	    @Parameter(description = "Tenant token", required = true) @PathParam("tenantToken") String tenantToken,
	    @Parameter(description = "Function identifier", required = true) @PathParam("identifier") String identifier,
	    @Parameter(description = "Script id", required = true) @PathParam("scriptId") String scriptId,
	    @Parameter(description = "Version id", required = true) @PathParam("versionId") String versionId)
	    throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response.ok(new String(getScriptManagement().getScriptContent(msid, tenantToken, scriptId, versionId)))
		.build();
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
    @Path("/microservices/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}/versions/{versionId}")
    @Operation(summary = "Update an existing tenant script", description = "Update an existing tenant script")
    public Response updateTenantScript(
	    @Parameter(description = "Tenant token", required = true) @PathParam("tenantToken") String tenantToken,
	    @Parameter(description = "Function identifier", required = true) @PathParam("identifier") String identifier,
	    @Parameter(description = "Script id", required = true) @PathParam("scriptId") String scriptId,
	    @Parameter(description = "Version id", required = true) @PathParam("versionId") String versionId,
	    @RequestBody ScriptCreateRequest request) throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response.ok(getScriptManagement().updateScript(msid, tenantToken, scriptId, versionId, request)).build();
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
    @Path("/microservices/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}/versions/{versionId}/clone")
    @Operation(summary = "Clone tenant script", description = "Clone an existing tenant script version to create a new version")
    public Response cloneTenantScript(
	    @Parameter(description = "Tenant token", required = true) @PathParam("tenantToken") String tenantToken,
	    @Parameter(description = "Function identifier", required = true) @PathParam("identifier") String identifier,
	    @Parameter(description = "Script id", required = true) @PathParam("scriptId") String scriptId,
	    @Parameter(description = "Version id", required = true) @PathParam("versionId") String versionId,
	    @RequestBody ScriptCloneRequest request) throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response
		.ok(getScriptManagement().cloneScript(msid, tenantToken, scriptId, versionId, request.getComment()))
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
    @Path("/microservices/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}/versions/{versionId}/activate")
    @Operation(summary = "Activate a tenant script version", description = "Activate a tenant script version")
    public Response activateTenantScript(
	    @Parameter(description = "Tenant token", required = true) @PathParam("tenantToken") String tenantToken,
	    @Parameter(description = "Function identifier", required = true) @PathParam("identifier") String identifier,
	    @Parameter(description = "Script id", required = true) @PathParam("scriptId") String scriptId,
	    @Parameter(description = "Version id", required = true) @PathParam("versionId") String versionId)
	    throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response.ok(getScriptManagement().activateScript(msid, tenantToken, scriptId, versionId)).build();
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
    @Path("/microservices/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}")
    @Operation(summary = "Delete a tenant script and version history", description = "Delete a tenant script and version history")
    public Response deleteTenantScript(
	    @Parameter(description = "Tenant token", required = true) @PathParam("tenantToken") String tenantToken,
	    @Parameter(description = "Function identifier", required = true) @PathParam("identifier") String identifier,
	    @Parameter(description = "Script id", required = true) @PathParam("scriptId") String scriptId)
	    throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return Response.ok(getScriptManagement().deleteScript(msid, tenantToken, scriptId)).build();
    }

    /**
     * Attempt to locate microservice definition based on function identifier.
     * 
     * @param identifier
     * @return
     * @throws SiteWhereException
     */
    protected SiteWhereMicroservice getMicroserviceForIdentifier(IFunctionIdentifier identifier)
	    throws SiteWhereException {
	String namespace = getMicroservice().getInstanceSettings().getKubernetesNamespace();
	SiteWhereMicroservice match = getMicroservice().getSiteWhereKubernetesClient()
		.getMicroserviceForIdentifier(namespace, identifier.getPath());
	if (match != null) {
	    return match;
	}
	throw new SiteWhereSystemException(ErrorCode.InvalidMicroserviceIdentifier, ErrorLevel.ERROR);
    }

    /**
     * Attempt to locate tenant definition based on token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected SiteWhereTenant getTenantForToken(String token) throws SiteWhereException {
	String namespace = getMicroservice().getInstanceSettings().getKubernetesNamespace();
	SiteWhereTenant match = getMicroservice().getSiteWhereKubernetesClient().getTenantForToken(namespace, token);
	if (match != null) {
	    return match;
	}
	throw new SiteWhereSystemException(ErrorCode.InvalidTenantToken, ErrorLevel.ERROR);
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