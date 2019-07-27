/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.instance;

import java.util.List;

import com.sitewhere.grpc.client.ApiChannel;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.client.IInstanceManagementApiChannel;
import com.sitewhere.grpc.service.GGetDatasetTemplatesRequest;
import com.sitewhere.grpc.service.GGetDatasetTemplatesResponse;
import com.sitewhere.grpc.service.GGetTenantTemplatesRequest;
import com.sitewhere.grpc.service.GGetTenantTemplatesResponse;
import com.sitewhere.grpc.service.InstanceManagementGrpc;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.grpc.GrpcServiceIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcSettings;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.multitenant.IDatasetTemplate;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;

/**
 * Supports SiteWhere instance management APIs on top of an
 * {@link InstanceManagementGrpcChannel}.
 */
public class InstanceManagementApiChannel extends ApiChannel<InstanceManagementGrpcChannel>
	implements IInstanceManagementApiChannel<InstanceManagementGrpcChannel> {

    public InstanceManagementApiChannel(IInstanceSettings settings) {
	super(settings, MicroserviceIdentifier.InstanceManagement, GrpcServiceIdentifier.InstanceManagement,
		IGrpcSettings.DEFAULT_API_PORT);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.IApiChannel#createGrpcChannel(com.sitewhere.spi
     * .microservice.instance.IInstanceSettings,
     * com.sitewhere.spi.microservice.IFunctionIdentifier,
     * com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier, int)
     */
    @Override
    public InstanceManagementGrpcChannel createGrpcChannel(IInstanceSettings settings, IFunctionIdentifier identifier,
	    IGrpcServiceIdentifier grpcServiceIdentifier, int port) {
	return new InstanceManagementGrpcChannel(settings, identifier, grpcServiceIdentifier, port);
    }

    /*
     * @see com.sitewhere.spi.instance.IInstanceManagement#getTenantTemplates()
     */
    @Override
    public List<ITenantTemplate> getTenantTemplates() throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, InstanceManagementGrpc.getGetTenantTemplatesMethod());
	    GGetTenantTemplatesRequest.Builder grequest = GGetTenantTemplatesRequest.newBuilder();
	    GGetTenantTemplatesResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getTenantTemplates(grequest.build());
	    List<ITenantTemplate> response = InstanceModelConverter
		    .asApiTenantTemplateList(gresponse.getTemplateList());
	    GrpcUtils.logClientMethodResponse(InstanceManagementGrpc.getGetTenantTemplatesMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(InstanceManagementGrpc.getGetTenantTemplatesMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.instance.IInstanceManagement#getDatasetTemplates()
     */
    @Override
    public List<IDatasetTemplate> getDatasetTemplates() throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, InstanceManagementGrpc.getGetDatasetTemplatesMethod());
	    GGetDatasetTemplatesRequest.Builder grequest = GGetDatasetTemplatesRequest.newBuilder();
	    GGetDatasetTemplatesResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDatasetTemplates(grequest.build());
	    List<IDatasetTemplate> response = InstanceModelConverter
		    .asApiDatasetTemplateList(gresponse.getTemplateList());
	    GrpcUtils.logClientMethodResponse(InstanceManagementGrpc.getGetDatasetTemplatesMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(InstanceManagementGrpc.getGetDatasetTemplatesMethod(), t);
	}
    }
}
