/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.instance;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.grpc.model.InstanceModel.GDatasetTemplate;
import com.sitewhere.grpc.model.InstanceModel.GTenantTemplate;
import com.sitewhere.rest.model.tenant.DatasetTemplate;
import com.sitewhere.rest.model.tenant.TenantTemplate;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IDatasetTemplate;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;

/**
 * Convert instance entities between SiteWhere API model and GRPC model.
 */
public class InstanceModelConverter {

    /**
     * Convert tenant template from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static TenantTemplate asApiTenantTemplate(GTenantTemplate grpc) throws SiteWhereException {
	TenantTemplate api = new TenantTemplate();
	api.setId(grpc.getId());
	api.setName(grpc.getName());
	return api;
    }

    /**
     * Convert tenant templates list from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static List<ITenantTemplate> asApiTenantTemplateList(List<GTenantTemplate> grpc) throws SiteWhereException {
	List<ITenantTemplate> api = new ArrayList<>();
	for (GTenantTemplate gtemplate : grpc) {
	    api.add(InstanceModelConverter.asApiTenantTemplate(gtemplate));
	}
	return api;
    }

    /**
     * Convert tenant template from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GTenantTemplate asGrpcTenantTemplate(ITenantTemplate api) throws SiteWhereException {
	GTenantTemplate.Builder grpc = GTenantTemplate.newBuilder();
	grpc.setId(api.getId());
	grpc.setName(api.getName());
	return grpc.build();
    }

    /**
     * Convert tenant templates list from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static List<GTenantTemplate> asGrpcTenantTemplateList(List<ITenantTemplate> api) throws SiteWhereException {
	List<GTenantTemplate> grpc = new ArrayList<GTenantTemplate>();
	for (ITenantTemplate atemplate : api) {
	    grpc.add(InstanceModelConverter.asGrpcTenantTemplate(atemplate));
	}
	return grpc;
    }

    /**
     * Convert dataset template from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DatasetTemplate asApiDatasetTemplate(GDatasetTemplate grpc) throws SiteWhereException {
	DatasetTemplate api = new DatasetTemplate();
	api.setId(grpc.getId());
	api.setName(grpc.getName());
	return api;
    }

    /**
     * Convert dataset templates list from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static List<IDatasetTemplate> asApiDatasetTemplateList(List<GDatasetTemplate> grpc)
	    throws SiteWhereException {
	List<IDatasetTemplate> api = new ArrayList<>();
	for (GDatasetTemplate gtemplate : grpc) {
	    api.add(InstanceModelConverter.asApiDatasetTemplate(gtemplate));
	}
	return api;
    }

    /**
     * Convert dataset template from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDatasetTemplate asGrpcDatasetTemplate(IDatasetTemplate api) throws SiteWhereException {
	GDatasetTemplate.Builder grpc = GDatasetTemplate.newBuilder();
	grpc.setId(api.getId());
	grpc.setName(api.getName());
	return grpc.build();
    }

    /**
     * Convert dataset templates list from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static List<GDatasetTemplate> asGrpcDatasetTemplateList(List<IDatasetTemplate> api)
	    throws SiteWhereException {
	List<GDatasetTemplate> grpc = new ArrayList<GDatasetTemplate>();
	for (IDatasetTemplate atemplate : api) {
	    grpc.add(InstanceModelConverter.asGrpcDatasetTemplate(atemplate));
	}
	return grpc;
    }
}
