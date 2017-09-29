package com.sitewhere.tenant.grpc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.model.GrpcUtils;
import com.sitewhere.grpc.model.converter.TenantModelConverter;
import com.sitewhere.grpc.service.GCreateTenantRequest;
import com.sitewhere.grpc.service.GCreateTenantResponse;
import com.sitewhere.grpc.service.TenantManagementGrpc;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for tenant management GRPC requests.
 * 
 * @author Derek
 */
public class TenantManagementImpl extends TenantManagementGrpc.TenantManagementImplBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    /** Tenant management persistence */
    private ITenantManagement tenantMangagement;

    public TenantManagementImpl(ITenantManagement tenantManagement) {
	this.tenantMangagement = tenantManagement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.TenantManagementGrpc.TenantManagementImplBase#
     * createTenant(com.sitewhere.grpc.service.GCreateTenantRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createTenant(GCreateTenantRequest request, StreamObserver<GCreateTenantResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(TenantManagementGrpc.METHOD_CREATE_TENANT);
	    ITenantCreateRequest apiRequest = TenantModelConverter.asApiTenantCreateRequest(request.getRequest());
	    ITenant apiResult = getTenantMangagement().createTenant(apiRequest);
	    GCreateTenantResponse.Builder response = GCreateTenantResponse.newBuilder();
	    response.setTenant(TenantModelConverter.asGrpcTenant(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(TenantManagementGrpc.METHOD_CREATE_TENANT, e);
	    responseObserver.onError(e);
	}
    }

    public ITenantManagement getTenantMangagement() {
	return tenantMangagement;
    }

    public void setTenantMangagement(ITenantManagement tenantMangagement) {
	this.tenantMangagement = tenantMangagement;
    }
}