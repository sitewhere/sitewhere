/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc;

import java.util.UUID;

import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.service.GCheckMultitenantServicesAvailableRequest;
import com.sitewhere.grpc.service.GCheckMultitenantServicesAvailableResponse;
import com.sitewhere.grpc.service.MultitenantManagementGrpc;
import com.sitewhere.security.UserContextManager;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;
import com.sitewhere.spi.tenant.ITenant;

import io.grpc.stub.StreamObserver;

/**
 *
 * @author Jorge Villaverde
 */
public class MultitenantManagementImpl extends MultitenantManagementGrpc.MultitenantManagementImplBase {

	/** Multitenant microservice */
	private IMultitenantMicroservice<?, ?> microservice;

	public MultitenantManagementImpl(IMultitenantMicroservice<?, ?> microservice) {
		super();
		this.microservice = microservice;
	}

	@Override
	public void checkMultitenantServicesAvailable(GCheckMultitenantServicesAvailableRequest request,
			StreamObserver<GCheckMultitenantServicesAvailableResponse> responseObserver) {
		boolean responseValue = false;
		try {
			String tenantId = TenantTokenServerInterceptor.TENANT_ID_KEY.get();
			if (tenantId == null) {
				throw new RuntimeException("Tenant id not found in device management request.");
			}
			UUID tenanUUID = UUID.fromString(tenantId);

			getMicroservice().assureTenantEngineAvailable(tenanUUID);
			ITenant tenant = getMicroservice().getTenantEngineByTenantId(tenanUUID).getTenant();
			UserContextManager.setCurrentTenant(tenant);
			responseValue = true;
		} catch (Throwable e) {
			GrpcUtils.handleServerMethodException(MultitenantManagementGrpc.METHOD_CHECK_MULTITENANT_SERVICES_AVAILABLE,
					e, responseObserver);
		}
		GCheckMultitenantServicesAvailableResponse response = GCheckMultitenantServicesAvailableResponse.newBuilder()
				.setAvailable(responseValue).build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	public IMultitenantMicroservice<?, ?> getMicroservice() {
		return microservice;
	}

	public void setMicroservice(IMultitenantMicroservice<IFunctionIdentifier, IMicroserviceTenantEngine> microservice) {
		this.microservice = microservice;
	}

}
