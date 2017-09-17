package com.sitewhere.grpc.model.converter;

import com.sitewhere.grpc.model.TenantModel;
import com.sitewhere.grpc.model.TenantModel.GTenant;
import com.sitewhere.grpc.model.TenantModel.GTenantCreateRequest;
import com.sitewhere.rest.model.tenant.request.TenantCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;

/**
 * Convert between SiteWhere API model and GRPC model.
 * 
 * @author Derek
 */
public class TenantModelConverter {

    /**
     * Convert a {@link GTenantCreateRequest} to an
     * {@link ITenantCreateRequest}.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static ITenantCreateRequest asApiTenantCreateRequest(GTenantCreateRequest grpc) throws SiteWhereException {
	TenantCreateRequest api = new TenantCreateRequest();
	api.setId(grpc.getId());
	api.setName(grpc.getName());
	api.setAuthenticationToken(grpc.getAuthenticationToken());
	api.setAuthorizedUserIds(grpc.getAuthorizedUserIdsList());
	api.setLogoUrl(grpc.getLogoUrl());
	api.setTenantTemplateId(grpc.getTenantTemplateId());
	api.setMetadata(grpc.getMetadataMap());
	return api;
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
	builder.setId(api.getId());
	builder.setName(api.getName());
	builder.setAuthenticationToken(api.getAuthenticationToken());
	builder.addAllAuthorizedUserIds(api.getAuthorizedUserIds());
	builder.setLogoUrl(api.getLogoUrl());
	builder.setTenantTemplateId(api.getTenantTemplateId());
	builder.getMetadataMap().putAll(api.getMetadata());
	return builder.build();
    }
}