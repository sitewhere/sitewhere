/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.tenant;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.grpc.client.common.converter.CommonModelConverter;
import com.sitewhere.grpc.model.CommonModel.GOptionalString;
import com.sitewhere.grpc.model.CommonModel.GPaging;
import com.sitewhere.grpc.model.CommonModel.GSearchText;
import com.sitewhere.grpc.model.CommonModel.GUserReference;
import com.sitewhere.grpc.model.TenantModel;
import com.sitewhere.grpc.model.TenantModel.GTenant;
import com.sitewhere.grpc.model.TenantModel.GTenantCreateRequest;
import com.sitewhere.grpc.model.TenantModel.GTenantSearchCriteria;
import com.sitewhere.grpc.model.TenantModel.GTenantSearchResults;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.tenant.TenantSearchCriteria;
import com.sitewhere.rest.model.tenant.Tenant;
import com.sitewhere.rest.model.tenant.request.TenantCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.tenant.ITenantSearchCriteria;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;

/**
 * Convert tenant entities between SiteWhere API model and GRPC model.
 */
public class TenantModelConverter {

    /**
     * Convert a {@link GTenantCreateRequest} to an {@link ITenantCreateRequest}.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static ITenantCreateRequest asApiTenantCreateRequest(GTenantCreateRequest grpc) throws SiteWhereException {
	TenantCreateRequest api = new TenantCreateRequest();
	api.setToken(grpc.hasToken() ? grpc.getToken().getValue() : null);
	api.setName(grpc.hasName() ? grpc.getName().getValue() : null);
	api.setAuthenticationToken(grpc.hasAuthenticationToken() ? grpc.getAuthenticationToken().getValue() : null);
	api.setAuthorizedUserIds(grpc.getAuthorizedUserIdsList());
	api.setTenantTemplateId(grpc.hasTenantTemplateId() ? grpc.getTenantTemplateId().getValue() : null);
	api.setDatasetTemplateId(grpc.hasDatasetTemplateId() ? grpc.getDatasetTemplateId().getValue() : null);
	api.setMetadata(grpc.getMetadataMap());
	CommonModelConverter.setBrandingInformation(api, grpc.getBranding());
	return api;
    }

    /**
     * Convert an {@link ITenantCreateRequest} to a {@link GTenantCreateRequest}.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GTenantCreateRequest asGrpcTenantCreateRequest(ITenantCreateRequest api) throws SiteWhereException {
	GTenantCreateRequest.Builder grpc = GTenantCreateRequest.newBuilder();
	if (api.getToken() != null) {
	    grpc.setToken(GOptionalString.newBuilder().setValue(api.getToken()));
	}
	if (api.getName() != null) {
	    grpc.setName(GOptionalString.newBuilder().setValue(api.getName()));
	}
	if (api.getAuthenticationToken() != null) {
	    grpc.setAuthenticationToken(GOptionalString.newBuilder().setValue(api.getAuthenticationToken()));
	}
	grpc.addAllAuthorizedUserIds(api.getAuthorizedUserIds());
	if (api.getTenantTemplateId() != null) {
	    grpc.setTenantTemplateId(GOptionalString.newBuilder().setValue(api.getTenantTemplateId()));
	}
	if (api.getDatasetTemplateId() != null) {
	    grpc.setDatasetTemplateId(GOptionalString.newBuilder().setValue(api.getDatasetTemplateId()));
	}
	grpc.putAllMetadata(api.getMetadata());
	grpc.setBranding(CommonModelConverter.asGrpcBrandingInformation(api));
	return grpc.build();
    }

    /**
     * Convert a {@link GTenant} to an {@link ITenant}.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static ITenant asApiTenant(GTenant grpc) throws SiteWhereException {
	Tenant api = new Tenant();
	api.setName(grpc.getName());
	api.setAuthenticationToken(grpc.getAuthenticationToken());
	api.setAuthorizedUserIds(grpc.getAuthorizedUserIdsList());
	api.setTenantTemplateId(grpc.getTenantTemplateId());
	api.setDatasetTemplateId(grpc.getDatasetTemplateId());
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	CommonModelConverter.setBrandingInformation(api, grpc.getBranding());
	return api;
    }

    /**
     * Convert a list of tenants from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<ITenant> asApiTenants(List<GTenant> grpcs) throws SiteWhereException {
	List<ITenant> api = new ArrayList<ITenant>();
	for (GTenant gtenant : grpcs) {
	    api.add(TenantModelConverter.asApiTenant(gtenant));
	}
	return api;
    }

    /**
     * Convert a list of tenants from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GTenant> asGrpcTenants(List<ITenant> apis) throws SiteWhereException {
	List<GTenant> grpcs = new ArrayList<GTenant>();
	for (ITenant apiTenant : apis) {
	    grpcs.add(TenantModelConverter.asGrpcTenant(apiTenant));
	}
	return grpcs;
    }

    /**
     * Convert an {@link ITenant} to a {@link GTenant}.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GTenant asGrpcTenant(ITenant api) throws SiteWhereException {
	GTenant.Builder builder = TenantModel.GTenant.newBuilder();
	builder.setName(api.getName());
	builder.setAuthenticationToken(api.getAuthenticationToken());
	builder.addAllAuthorizedUserIds(api.getAuthorizedUserIds());
	builder.setTenantTemplateId(api.getTenantTemplateId());
	builder.setDatasetTemplateId(api.getDatasetTemplateId());
	builder.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	builder.setBranding(CommonModelConverter.asGrpcBrandingInformation(api));
	return builder.build();
    }

    /**
     * Converts a {@link GTenantSearchCriteria} to an {@link ITenantSearchCriteria}.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static ITenantSearchCriteria asApiTenantSearchCriteria(GTenantSearchCriteria grpc)
	    throws SiteWhereException {
	TenantSearchCriteria api = new TenantSearchCriteria(grpc.getPaging().getPageNumber(),
		grpc.getPaging().getPageSize());
	if (grpc.hasSearchText()) {
	    api.setTextSearch(grpc.getSearchText().getSearch());
	}
	if (grpc.hasAuthorizedUser()) {
	    api.setUserId(grpc.getAuthorizedUser().getUsername());
	}
	if (grpc.hasPaging()) {
	    api.setPageNumber(grpc.getPaging().getPageNumber());
	    api.setPageSize(grpc.getPaging().getPageSize());
	}
	return api;
    }

    /**
     * Convert an {@link ITenantSearchCriteria} to a {@link GTenantSearchCriteria}.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GTenantSearchCriteria asGrpcTenantSearchCriteria(ITenantSearchCriteria api)
	    throws SiteWhereException {
	GTenantSearchCriteria.Builder gcriteria = GTenantSearchCriteria.newBuilder();
	if (api.getTextSearch() != null) {
	    GSearchText.Builder search = GSearchText.newBuilder();
	    search.setSearch(api.getTextSearch());
	    gcriteria.setSearchText(search.build());
	}
	if (api.getUserId() != null) {
	    GUserReference.Builder user = GUserReference.newBuilder();
	    user.setUsername(api.getUserId());
	    gcriteria.setAuthorizedUser(user.build());
	}
	GPaging.Builder paging = GPaging.newBuilder();
	paging.setPageNumber(api.getPageNumber());
	paging.setPageSize(api.getPageSize());
	gcriteria.setPaging(paging.build());
	return gcriteria.build();
    }

    /**
     * Convert a {@link GTenantSearchResults} to its API equivalent.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<ITenant> asApiTenantSearchResults(GTenantSearchResults grpc)
	    throws SiteWhereException {
	List<ITenant> tenants = TenantModelConverter.asApiTenants(grpc.getTenantsList());
	return new SearchResults<ITenant>(tenants, grpc.getCount());
    }

    /**
     * Convert API tenant search results to {@link GTenantSearchResults}.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GTenantSearchResults asGrpcTenantSearchResults(ISearchResults<ITenant> api)
	    throws SiteWhereException {
	GTenantSearchResults.Builder grpc = GTenantSearchResults.newBuilder();
	grpc.setCount(api.getNumResults());
	grpc.addAllTenants(TenantModelConverter.asGrpcTenants(api.getResults()));
	return grpc.build();
    }
}