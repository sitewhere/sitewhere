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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.instance.spi.microservice.IInstanceManagementTenantEngine;
import com.sitewhere.microservice.configuration.model.instance.InstanceConfiguration;
import com.sitewhere.microservice.kubernetes.K8sModelConverter;
import com.sitewhere.microservice.scripting.ScriptActivationRequest;
import com.sitewhere.microservice.scripting.ScriptCloneRequest;
import com.sitewhere.microservice.scripting.ScriptCreateRequest;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.rest.model.microservice.MicroserviceSummary;
import com.sitewhere.rest.model.search.Pager;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.instance.IEventPipelineLog;
import com.sitewhere.spi.microservice.scripting.IScriptManagement;
import com.sitewhere.spi.microservice.scripting.IScriptMetadata;
import com.sitewhere.spi.microservice.scripting.IScriptVersion;
import com.sitewhere.spi.microservice.tenant.ITenantManagement;
import com.sitewhere.web.rest.marshaling.MarshaledScriptCategory;
import com.sitewhere.web.rest.marshaling.MarshaledScriptTemplate;
import com.sitewhere.web.rest.model.TenantEngineConfiguration;

import io.sitewhere.k8s.crd.ResourceLabels;
import io.sitewhere.k8s.crd.exception.SiteWhereK8sException;
import io.sitewhere.k8s.crd.instance.SiteWhereInstance;
import io.sitewhere.k8s.crd.microservice.SiteWhereMicroservice;
import io.sitewhere.k8s.crd.microservice.SiteWhereMicroserviceList;
import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;
import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;
import io.sitewhere.k8s.crd.tenant.scripting.category.SiteWhereScriptCategory;
import io.sitewhere.k8s.crd.tenant.scripting.category.SiteWhereScriptCategoryList;
import io.sitewhere.k8s.crd.tenant.scripting.template.SiteWhereScriptTemplate;
import io.sitewhere.k8s.crd.tenant.scripting.template.SiteWhereScriptTemplateList;

/**
 * Controller for instance management.
 */
@RestController
@RequestMapping("/api/instance")
public class Instance {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Instance.class);

    @Autowired
    private IInstanceManagementMicroservice microservice;

    @GetMapping("/configuration")
    public InstanceConfiguration getInstanceConfiguration() {
	return getMicroservice().getInstanceConfiguration();
    }

    @PutMapping("/{configuration}")
    public ResponseEntity<?> updateInstanceConfiguration(@RequestBody InstanceConfiguration request)
	    throws SiteWhereException {
	try {
	    SiteWhereInstance instance = getMicroservice().getLastInstanceResource();
	    instance.getSpec().setConfiguration(MarshalUtils.marshalJsonNode(MarshalUtils.marshalJson(request)));
	    getMicroservice().updateInstanceResource(instance);
	    return ResponseEntity.ok(request);
	} catch (IOException e) {
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}
    }

    @GetMapping("/microservices")
    public List<MicroserviceSummary> getInstanceMicroservices() {
	String namespace = getMicroservice().getInstanceSettings().getK8s().getNamespace();
	SiteWhereMicroserviceList list = getMicroservice().getSiteWhereKubernetesClient()
		.getAllMicroservices(namespace);
	List<SiteWhereMicroservice> multitenant = new ArrayList<>();
	for (SiteWhereMicroservice microservice : list.getItems()) {
	    if (microservice.getSpec().isMultitenant()) {
		multitenant.add(microservice);
	    }
	}
	Collections.sort(multitenant, new Comparator<SiteWhereMicroservice>() {

	    @Override
	    public int compare(SiteWhereMicroservice arg0, SiteWhereMicroservice arg1) {
		return arg0.getMetadata().getName().compareTo(arg1.getMetadata().getName());
	    }
	});
	List<MicroserviceSummary> summaries = new ArrayList<>();
	for (SiteWhereMicroservice microservice : multitenant) {
	    summaries.add(K8sModelConverter.convert(microservice));
	}
	return summaries;
    }

    /**
     * Get a tenant engine configuration based on functional area and tenant id.
     * 
     * @param identifier
     * @param token
     * @return
     */
    @GetMapping("/microservices/{identifier}/tenants/{token}/configuration")
    public ResponseEntity<?> getTenantEngineConfiguration(@PathVariable String identifier, @PathVariable String token) {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	try {
	    SiteWhereMicroservice microservice = getMicroserviceForIdentifier(msid);
	    SiteWhereTenant tenant = getTenantForToken(token);
	    SiteWhereTenantEngine engine = getMicroservice().getSiteWhereKubernetesClient()
		    .getTenantEngine(microservice, tenant);
	    if (engine == null) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	    }
	    TenantEngineConfiguration response = new TenantEngineConfiguration();
	    response.setTenant(K8sModelConverter.convert(tenant));
	    response.setMicroservice(K8sModelConverter.convert(microservice));
	    response.setInstanceConfiguration(getMicroservice().getInstanceConfiguration());
	    response.setMicroserviceConfiguration(microservice.getSpec().getConfiguration());
	    response.setTenantConfiguration(engine.getSpec().getConfiguration());
	    return ResponseEntity.ok(response);
	} catch (SiteWhereException e) {
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	} catch (SiteWhereK8sException e) {
	    return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}
    }

    /**
     * Get a tenant engine configuration based on functional area and tenant id.
     * 
     * @param identifier
     * @param token
     * @param configuration
     * @return
     */
    @PostMapping("/microservices/{identifier}/tenants/{token}/configuration")
    public ResponseEntity<?> updateTenantEngineConfiguration(@PathVariable String identifier,
	    @PathVariable String token, @RequestBody JsonNode configuration) {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	try {
	    SiteWhereMicroservice microservice = getMicroserviceForIdentifier(msid);
	    SiteWhereTenant tenant = getTenantForToken(token);
	    SiteWhereTenantEngine engine = getMicroservice().getSiteWhereKubernetesClient()
		    .getTenantEngine(microservice, tenant);
	    if (engine == null) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	    }

	    // Save configuration updates.
	    engine.getSpec().setConfiguration(configuration);
	    getMicroservice().getSiteWhereKubernetesClient().getTenantEngines()
		    .inNamespace(engine.getMetadata().getNamespace()).createOrReplace(engine);

	    TenantEngineConfiguration response = new TenantEngineConfiguration();
	    response.setTenant(K8sModelConverter.convert(tenant));
	    response.setMicroservice(K8sModelConverter.convert(microservice));
	    response.setInstanceConfiguration(getMicroservice().getInstanceConfiguration());
	    response.setMicroserviceConfiguration(microservice.getSpec().getConfiguration());
	    response.setTenantConfiguration(engine.getSpec().getConfiguration());
	    return ResponseEntity.ok(response);
	} catch (SiteWhereException e) {
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	} catch (SiteWhereK8sException e) {
	    return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}
    }

    /**
     * Get script category list based on microservice identifier.
     * 
     * @param identifier
     * @return
     */
    protected List<MarshaledScriptCategory> getScriptCategoryList(String identifier) {
	SiteWhereScriptCategoryList list = getMicroservice().getSiteWhereKubernetesClient().getScriptCategories()
		.withLabel(ResourceLabels.LABEL_SITEWHERE_FUNCTIONAL_AREA, identifier).list();
	List<MarshaledScriptCategory> result = new ArrayList<>();
	for (SiteWhereScriptCategory category : list.getItems()) {
	    MarshaledScriptCategory converted = new MarshaledScriptCategory();
	    converted.setId(category.getMetadata().getName());
	    converted.setName(category.getSpec().getName());
	    converted.setDescription(category.getSpec().getDescription());
	    result.add(converted);
	}
	Collections.sort(result, new Comparator<MarshaledScriptCategory>() {

	    @Override
	    public int compare(MarshaledScriptCategory c1, MarshaledScriptCategory c2) {
		return c1.getName().compareTo(c2.getName());
	    }
	});
	return result;
    }

    /**
     * Get list of script categories for a given functional area.
     * 
     * @param identifier
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/microservices/{identifier}/scripting/categories")
    public ResponseEntity<?> getScriptCategories(@PathVariable String identifier) throws SiteWhereException {
	List<MarshaledScriptCategory> result = getScriptCategoryList(identifier);
	return ResponseEntity.ok(result);
    }

    /**
     * Get list of script templates for a given functional area and category.
     * 
     * @param identifier
     * @param category
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/microservices/{identifier}/scripting/categories/{category}/templates")
    public ResponseEntity<?> getScriptTemplates(@PathVariable String identifier, @PathVariable String category)
	    throws SiteWhereException {
	Map<String, String> labels = new HashMap<>();
	labels.put(ResourceLabels.LABEL_SITEWHERE_FUNCTIONAL_AREA, identifier);
	labels.put(ResourceLabels.LABEL_SCRIPTING_SCRIPT_CATEGORY, category);
	SiteWhereScriptTemplateList list = getMicroservice().getSiteWhereKubernetesClient().getScriptTemplates()
		.withLabels(labels).list();

	List<MarshaledScriptTemplate> result = new ArrayList<>();
	for (SiteWhereScriptTemplate template : list.getItems()) {
	    MarshaledScriptTemplate converted = new MarshaledScriptTemplate();
	    converted.setId(template.getMetadata().getName());
	    converted.setName(template.getSpec().getName());
	    converted.setDescription(template.getSpec().getDescription());
	    converted.setInterpreterType(template.getSpec().getInterpreterType());
	    converted.setScript(template.getSpec().getScript());
	    result.add(converted);
	}
	Collections.sort(result, new Comparator<MarshaledScriptTemplate>() {

	    @Override
	    public int compare(MarshaledScriptTemplate c1, MarshaledScriptTemplate c2) {
		return c1.getName().compareTo(c2.getName());
	    }
	});

	return ResponseEntity.ok(result);
    }

    /**
     * Get content for a script template for a given microservice.
     * 
     * @param identifier
     * @param templateId
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/microservices/{identifier}/scripting/templates/{templateId}")
    public ResponseEntity<?> getScriptTemplateContent(@PathVariable String identifier, @PathVariable String templateId)
	    throws SiteWhereException {
	return null;
    }

    /**
     * List tenant scripts for the given functional area.
     * 
     * @param tenantToken
     * @param identifier
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/microservices/{identifier}/tenants/{tenantToken}/scripting/scripts")
    public List<IScriptMetadata> listTenantScripts(@PathVariable String tenantToken, @PathVariable String identifier)
	    throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return getScriptManagement().getScriptMetadataList(msid, tenantToken);
    }

    /**
     * List tenant scripts for the given functional area organized by category
     * 
     * @param tenantToken
     * @param identifier
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/microservices/{identifier}/tenants/{tenantToken}/scripting/categories")
    public List<MarshaledScriptCategory> listTenantScriptsByCategory(@PathVariable String tenantToken,
	    @PathVariable String identifier) throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	List<MarshaledScriptCategory> categories = getScriptCategoryList(identifier);
	List<IScriptMetadata> scripts = getScriptManagement().getScriptMetadataList(msid, tenantToken);

	Map<String, List<IScriptMetadata>> scriptsByCategory = new HashMap<>();
	for (IScriptMetadata script : scripts) {
	    List<IScriptMetadata> existing = scriptsByCategory.get(script.getCategory());
	    if (existing == null) {
		existing = new ArrayList<IScriptMetadata>();
		scriptsByCategory.put(script.getCategory(), existing);
	    }
	    existing.add(script);
	}

	for (MarshaledScriptCategory category : categories) {
	    category.setScripts(scriptsByCategory.get(category.getId()));
	}

	return categories;
    }

    /**
     * List tenant scripts for the given functional area and belonging to the given
     * category.
     * 
     * @param tenantToken
     * @param identifier
     * @param category
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/microservices/{identifier}/tenants/{tenantToken}/scripting/categories/{category}")
    public List<IScriptMetadata> listTenantScriptsForCategory(@PathVariable String tenantToken,
	    @PathVariable String identifier, @PathVariable String category) throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return getScriptManagement().getScriptMetadataListForCategory(msid, tenantToken, category);
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
    @GetMapping("/microservices/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}")
    public IScriptMetadata getTenantScript(@PathVariable String tenantToken, @PathVariable String identifier,
	    @PathVariable String scriptId) throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return getScriptManagement().getScriptMetadata(msid, tenantToken, scriptId);
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
    @PostMapping("/microservices/{identifier}/tenants/{tenantToken}/scripting/scripts")
    public IScriptMetadata createTenantScript(@PathVariable String tenantToken, @PathVariable String identifier,
	    @RequestBody ScriptCreateRequest request) throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return getScriptManagement().createScript(msid, tenantToken, request);
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
    @GetMapping("/microservices/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}/versions/{versionId}/content")
    public String getTenantScriptContent(@PathVariable String tenantToken, @PathVariable String identifier,
	    @PathVariable String scriptId, @PathVariable String versionId) throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return new String(getScriptManagement().getScriptContent(msid, tenantToken, scriptId, versionId));
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
    @PostMapping("/microservices/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}/versions/{versionId}")
    public IScriptMetadata updateTenantScript(@PathVariable String tenantToken, @PathVariable String identifier,
	    @PathVariable String scriptId, @PathVariable String versionId, @RequestBody ScriptCreateRequest request)
	    throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return getScriptManagement().updateScript(msid, tenantToken, scriptId, versionId, request);
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
    @PostMapping("/microservices/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}/versions/{versionId}/clone")
    public IScriptVersion cloneTenantScript(@PathVariable String tenantToken, @PathVariable String identifier,
	    @PathVariable String scriptId, @PathVariable String versionId, @RequestBody ScriptCloneRequest request)
	    throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return getScriptManagement().cloneScript(msid, tenantToken, scriptId, versionId, request.getComment());
    }

    /**
     * Activate a tenant script. This action causes the given version to become the
     * active script and pushes the content out to all listening microservices.
     * 
     * @param tenantToken
     * @param identifier
     * @param scriptId
     * @param versionId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PostMapping("/microservices/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}/versions/{versionId}/activate")
    public IScriptMetadata activateTenantScript(@PathVariable String tenantToken, @PathVariable String identifier,
	    @PathVariable String scriptId, @PathVariable String versionId, @RequestBody ScriptActivationRequest request)
	    throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return getScriptManagement().activateScript(msid, tenantToken, scriptId, versionId);
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
    @DeleteMapping("/microservices/{identifier}/tenants/{tenantToken}/scripting/scripts/{scriptId}")
    public IScriptMetadata deleteTenantScript(@PathVariable String tenantToken, @PathVariable String identifier,
	    @PathVariable String scriptId) throws SiteWhereException {
	MicroserviceIdentifier msid = MicroserviceIdentifier.getByPath(identifier);
	return getScriptManagement().deleteScript(msid, tenantToken, scriptId);
    }

    /**
     * Get instance pipeline log entries.
     * 
     * @param tenantToken
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/eventpipeline/tenants/{tenantToken}/recent")
    public SearchResults<IEventPipelineLog> getInstancePipelineLogEntries(@PathVariable String tenantToken,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	Pager<IEventPipelineLog> pager = new Pager<>(criteria);
	IInstanceManagementTenantEngine engine = getMicroservice().getTenantEngineManager()
		.getTenantEngineByToken(tenantToken);
	if (engine == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidTenantToken, ErrorLevel.ERROR);
	}
	engine.getPipelineLogMonitor().getPipelineLogQueue().forEach(log -> pager.process(log));
	return new SearchResults<>(pager.getResults(), pager.getTotal());
    }

    @DeleteMapping("/eventpipeline/tenants/{tenantToken}/recent")
    public ResponseEntity<?> deleteInstancePipelineLogEntries(@PathVariable String tenantToken)
	    throws SiteWhereException {
	IInstanceManagementTenantEngine engine = getMicroservice().getTenantEngineManager()
		.getTenantEngineByToken(tenantToken);
	if (engine == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidTenantToken, ErrorLevel.ERROR);
	}
	engine.getPipelineLogMonitor().clearPipelineLog();
	return ResponseEntity.ok().build();
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
	String namespace = getMicroservice().getInstanceSettings().getK8s().getNamespace();
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
	String namespace = getMicroservice().getInstanceSettings().getK8s().getNamespace();
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

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}