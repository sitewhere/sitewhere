package com.sitewhere.asset.grpc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.asset.spi.microservice.IAssetManagementMicroservice;
import com.sitewhere.asset.spi.microservice.IAssetManagementTenantEngine;
import com.sitewhere.grpc.service.AssetManagementGrpc;
import com.sitewhere.grpc.service.GCreateAssetCategoryRequest;
import com.sitewhere.grpc.service.GCreateAssetCategoryResponse;
import com.sitewhere.grpc.service.GCreateHardwareAssetRequest;
import com.sitewhere.grpc.service.GCreateHardwareAssetResponse;
import com.sitewhere.grpc.service.GCreateLocationAssetRequest;
import com.sitewhere.grpc.service.GCreateLocationAssetResponse;
import com.sitewhere.grpc.service.GCreatePersonAssetRequest;
import com.sitewhere.grpc.service.GCreatePersonAssetResponse;
import com.sitewhere.grpc.service.GDeleteAssetCategoryRequest;
import com.sitewhere.grpc.service.GDeleteAssetCategoryResponse;
import com.sitewhere.grpc.service.GDeleteAssetRequest;
import com.sitewhere.grpc.service.GDeleteAssetResponse;
import com.sitewhere.grpc.service.GGetAssetByIdRequest;
import com.sitewhere.grpc.service.GGetAssetByIdResponse;
import com.sitewhere.grpc.service.GGetAssetCategoryByIdRequest;
import com.sitewhere.grpc.service.GGetAssetCategoryByIdResponse;
import com.sitewhere.grpc.service.GListAssetCategoriesRequest;
import com.sitewhere.grpc.service.GListAssetCategoriesResponse;
import com.sitewhere.grpc.service.GListAssetsRequest;
import com.sitewhere.grpc.service.GListAssetsResponse;
import com.sitewhere.grpc.service.GUpdateAssetCategoryRequest;
import com.sitewhere.grpc.service.GUpdateAssetCategoryResponse;
import com.sitewhere.grpc.service.GUpdateHardwareAssetRequest;
import com.sitewhere.grpc.service.GUpdateHardwareAssetResponse;
import com.sitewhere.grpc.service.GUpdateLocationAssetRequest;
import com.sitewhere.grpc.service.GUpdateLocationAssetResponse;
import com.sitewhere.grpc.service.GUpdatePersonAssetRequest;
import com.sitewhere.grpc.service.GUpdatePersonAssetResponse;
import com.sitewhere.microservice.grpc.TenantTokenServerInterceptor;
import com.sitewhere.spi.SiteWhereException;

import io.grpc.stub.StreamObserver;

/**
 * Routes GRPC calls to service implementations in tenants.
 * 
 * @author Derek
 */
public class AssetManagementRouter extends AssetManagementGrpc.AssetManagementImplBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    /** Parent microservice */
    private IAssetManagementMicroservice microservice;

    public AssetManagementRouter(IAssetManagementMicroservice microservice) {
	this.microservice = microservice;
    }

    /**
     * Based on token passed via GRPC header, look up service implementation
     * running in tenant engine.
     * 
     * @return
     */
    protected AssetManagementGrpc.AssetManagementImplBase getTenantImplementation() {
	String tenantToken = TenantTokenServerInterceptor.TENANT_TOKEN_KEY.get();
	if (tenantToken == null) {
	    throw new RuntimeException("Tenant token not found in asset management request.");
	}
	try {
	    IAssetManagementTenantEngine engine = getMicroservice().getTenantEngineByTenantId(tenantToken);
	    if (engine != null) {
		return engine.getAssetManagementImpl();
	    }
	    throw new RuntimeException("Tenant engine not found.");
	} catch (SiteWhereException e) {
	    throw new RuntimeException("Error locating tenant engine.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * createAssetCategory(com.sitewhere.grpc.service.
     * GCreateAssetCategoryRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void createAssetCategory(GCreateAssetCategoryRequest request,
	    StreamObserver<GCreateAssetCategoryResponse> responseObserver) {
	getTenantImplementation().createAssetCategory(request, responseObserver);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * getAssetCategoryById(com.sitewhere.grpc.service.
     * GGetAssetCategoryByIdRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetCategoryById(GGetAssetCategoryByIdRequest request,
	    StreamObserver<GGetAssetCategoryByIdResponse> responseObserver) {
	getTenantImplementation().getAssetCategoryById(request, responseObserver);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * updateAssetCategory(com.sitewhere.grpc.service.
     * GUpdateAssetCategoryRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateAssetCategory(GUpdateAssetCategoryRequest request,
	    StreamObserver<GUpdateAssetCategoryResponse> responseObserver) {
	getTenantImplementation().updateAssetCategory(request, responseObserver);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * listAssetCategories(com.sitewhere.grpc.service.
     * GListAssetCategoriesRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listAssetCategories(GListAssetCategoriesRequest request,
	    StreamObserver<GListAssetCategoriesResponse> responseObserver) {
	getTenantImplementation().listAssetCategories(request, responseObserver);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * deleteAssetCategory(com.sitewhere.grpc.service.
     * GDeleteAssetCategoryRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteAssetCategory(GDeleteAssetCategoryRequest request,
	    StreamObserver<GDeleteAssetCategoryResponse> responseObserver) {
	getTenantImplementation().deleteAssetCategory(request, responseObserver);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * createHardwareAsset(com.sitewhere.grpc.service.
     * GCreateHardwareAssetRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void createHardwareAsset(GCreateHardwareAssetRequest request,
	    StreamObserver<GCreateHardwareAssetResponse> responseObserver) {
	getTenantImplementation().createHardwareAsset(request, responseObserver);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * updateHardwareAsset(com.sitewhere.grpc.service.
     * GUpdateHardwareAssetRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateHardwareAsset(GUpdateHardwareAssetRequest request,
	    StreamObserver<GUpdateHardwareAssetResponse> responseObserver) {
	getTenantImplementation().updateHardwareAsset(request, responseObserver);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * createPersonAsset(com.sitewhere.grpc.service.GCreatePersonAssetRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createPersonAsset(GCreatePersonAssetRequest request,
	    StreamObserver<GCreatePersonAssetResponse> responseObserver) {
	getTenantImplementation().createPersonAsset(request, responseObserver);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * updatePersonAsset(com.sitewhere.grpc.service.GUpdatePersonAssetRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updatePersonAsset(GUpdatePersonAssetRequest request,
	    StreamObserver<GUpdatePersonAssetResponse> responseObserver) {
	getTenantImplementation().updatePersonAsset(request, responseObserver);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * createLocationAsset(com.sitewhere.grpc.service.
     * GCreateLocationAssetRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void createLocationAsset(GCreateLocationAssetRequest request,
	    StreamObserver<GCreateLocationAssetResponse> responseObserver) {
	getTenantImplementation().createLocationAsset(request, responseObserver);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * updateLocationAsset(com.sitewhere.grpc.service.
     * GUpdateLocationAssetRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateLocationAsset(GUpdateLocationAssetRequest request,
	    StreamObserver<GUpdateLocationAssetResponse> responseObserver) {
	getTenantImplementation().updateLocationAsset(request, responseObserver);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * getAssetById(com.sitewhere.grpc.service.GGetAssetByIdRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetById(GGetAssetByIdRequest request, StreamObserver<GGetAssetByIdResponse> responseObserver) {
	getTenantImplementation().getAssetById(request, responseObserver);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * deleteAsset(com.sitewhere.grpc.service.GDeleteAssetRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteAsset(GDeleteAssetRequest request, StreamObserver<GDeleteAssetResponse> responseObserver) {
	getTenantImplementation().deleteAsset(request, responseObserver);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * listAssets(com.sitewhere.grpc.service.GListAssetsRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listAssets(GListAssetsRequest request, StreamObserver<GListAssetsResponse> responseObserver) {
	getTenantImplementation().listAssets(request, responseObserver);
    }

    public IAssetManagementMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IAssetManagementMicroservice microservice) {
	this.microservice = microservice;
    }
}