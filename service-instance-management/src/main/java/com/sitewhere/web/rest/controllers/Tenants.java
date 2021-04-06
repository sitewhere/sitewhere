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
import com.sitewhere.microservice.security.SiteWhereAuthentication;
import com.sitewhere.microservice.security.UserContext;
import com.sitewhere.microservice.tenant.MarshaledTenantConfigurationTemplate;
import com.sitewhere.microservice.tenant.MarshaledTenantDatasetTemplate;
import com.sitewhere.rest.model.search.Pager;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.tenant.TenantSearchCriteria;
import com.sitewhere.rest.model.tenant.request.TenantCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.tenant.ITenantManagement;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.tenant.ITenant;

import io.sitewhere.k8s.crd.tenant.configuration.TenantConfigurationTemplate;
import io.sitewhere.k8s.crd.tenant.configuration.TenantConfigurationTemplateList;
import io.sitewhere.k8s.crd.tenant.dataset.TenantDatasetTemplate;
import io.sitewhere.k8s.crd.tenant.dataset.TenantDatasetTemplateList;

/**
 * Controller for tenant operations.
 */
@RestController
@RequestMapping("/api/tenants")
public class Tenants {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Tenants.class);

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a new tenant.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PostMapping
    public ITenant createTenant(@RequestBody TenantCreateRequest request) throws SiteWhereException {
	return getTenantManagement().createTenant(request);
    }

    /**
     * Update an existing tenant.
     * 
     * @param tenantToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PutMapping("/{tenantToken}")
    public ITenant updateTenant(@PathVariable String tenantToken, @RequestBody TenantCreateRequest request)
	    throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	SiteWhereAuthentication user = UserContext.getCurrentUser();
	if (!tenant.getAuthorizedUserIds().contains(user.getUsername())) {
	    throw new SiteWhereSystemException(ErrorCode.NotAuthorizedForTenant, ErrorLevel.ERROR);
	}
	return getTenantManagement().updateTenant(tenantToken, request);
    }

    /**
     * Get a tenant by unique id.
     * 
     * @param tenantToken
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{tenantToken}")
    public ITenant getTenantByToken(@PathVariable String tenantToken) throws SiteWhereException {
	ITenant tenant = assureTenant(tenantToken);
	SiteWhereAuthentication user = UserContext.getCurrentUser();
	if (!tenant.getAuthorizedUserIds().contains(user.getUsername())) {
	    throw new SiteWhereSystemException(ErrorCode.NotAuthorizedForTenant, ErrorLevel.ERROR);
	}
	return tenant;
    }

    /**
     * List tenants that match the given criteria.
     * 
     * @param textSearch
     * @param authUserId
     * @param includeRuntimeInfo
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GetMapping
    public SearchResults<ITenant> listTenants(@RequestParam(required = false) String textSearch,
	    @RequestParam(required = false) String authUserId,
	    @RequestParam(defaultValue = "true", required = false) boolean includeRuntimeInfo,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {

	TenantSearchCriteria criteria = new TenantSearchCriteria(page, pageSize);
	criteria.setTextSearch(textSearch);
	criteria.setUserId(authUserId);
	criteria.setIncludeRuntimeInfo(includeRuntimeInfo);
	ISearchResults<ITenant> tenants = getTenantManagement().listTenants(new TenantSearchCriteria(1, 0));

	SiteWhereAuthentication user = UserContext.getCurrentUser();
	Pager<ITenant> authorized = new Pager<ITenant>(criteria);
	for (ITenant tenant : tenants.getResults()) {
	    if (tenant.getAuthorizedUserIds().contains(user.getUsername())) {
		authorized.process(tenant);
	    }
	}
	return new SearchResults<ITenant>(authorized.getResults(), authorized.getTotal());
    }

    /**
     * Delete tenant by token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @DeleteMapping("/{token}")
    public ITenant deleteTenant(@PathVariable String token) throws SiteWhereException {
	ITenant tenant = assureTenant(token);
	SiteWhereAuthentication user = UserContext.getCurrentUser();
	if (!tenant.getAuthorizedUserIds().contains(user.getUsername())) {
	    throw new SiteWhereSystemException(ErrorCode.NotAuthorizedForTenant, ErrorLevel.ERROR);
	}
	return getTenantManagement().deleteTenant(token);
    }

    /**
     * Lists all available tenant templates.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/templates/configuration")
    public List<MarshaledTenantConfigurationTemplate> listTenantConfigurationTemplates() throws SiteWhereException {
	TenantConfigurationTemplateList list = getMicroservice().getSiteWhereKubernetesClient()
		.getTenantConfigurationTemplates().list();
	List<MarshaledTenantConfigurationTemplate> templates = new ArrayList<>();
	for (TenantConfigurationTemplate template : list.getItems()) {
	    MarshaledTenantConfigurationTemplate marshaled = new MarshaledTenantConfigurationTemplate();
	    marshaled.setId(template.getMetadata().getName());
	    marshaled.setName(template.getSpec().getName());
	    marshaled.setDescription(template.getSpec().getDescription());
	    templates.add(marshaled);
	}

	return templates;
    }

    /**
     * Lists all available dataset templates.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/templates/dataset")
    public List<MarshaledTenantDatasetTemplate> listTenantDatasetTemplates() throws SiteWhereException {
	TenantDatasetTemplateList list = getMicroservice().getSiteWhereKubernetesClient().getTenantDatasetTemplates()
		.list();
	List<MarshaledTenantDatasetTemplate> templates = new ArrayList<>();
	for (TenantDatasetTemplate template : list.getItems()) {
	    MarshaledTenantDatasetTemplate marshaled = new MarshaledTenantDatasetTemplate();
	    marshaled.setId(template.getMetadata().getName());
	    marshaled.setName(template.getSpec().getName());
	    marshaled.setDescription(template.getSpec().getDescription());
	    templates.add(marshaled);
	}

	return templates;
    }

    /**
     * Assure that a tenant exists for the given token.
     * 
     * @param tenantToken
     * @return
     * @throws SiteWhereException
     */
    protected ITenant assureTenant(String tenantToken) throws SiteWhereException {
	ITenant tenant = getTenantManagement().getTenant(tenantToken);
	if (tenant == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidTenantToken, ErrorLevel.ERROR);
	}
	return tenant;
    }

    protected ITenantManagement getTenantManagement() {
	return getMicroservice().getTenantManagement();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}