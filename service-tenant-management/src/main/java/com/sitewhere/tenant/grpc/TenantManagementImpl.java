package com.sitewhere.tenant.grpc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.model.TenantManagementGrpc;
import com.sitewhere.grpc.model.TenantModel.GTenant;
import com.sitewhere.grpc.model.TenantModel.GTenantCreateRequest;
import com.sitewhere.grpc.model.converter.TenantModelConverter;
import com.sitewhere.spi.SiteWhereException;
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
     * com.sitewhere.grpc.model.TenantManagementGrpc.TenantManagementImplBase#
     * createTenant(com.sitewhere.grpc.model.TenantModel.GTenantCreateRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createTenant(GTenantCreateRequest request, StreamObserver<GTenant> responseObserver) {
	try {
	    ITenantCreateRequest apiRequest = TenantModelConverter.asApiTenantCreateRequest(request);
	    ITenant apiResult = getTenantMangagement().createTenant(apiRequest);
	    GTenant response = TenantModelConverter.asGrpcTenant(apiResult);
	    responseObserver.onNext(response);
	    responseObserver.onCompleted();
	} catch (SiteWhereException e) {
	    LOGGER.error("Unable to create tenant.", e);
	}
    }

    public ITenantManagement getTenantMangagement() {
	return tenantMangagement;
    }

    public void setTenantMangagement(ITenantManagement tenantMangagement) {
	this.tenantMangagement = tenantMangagement;
    }
}