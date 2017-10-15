/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.grpc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.device.spi.microservice.IDeviceManagementMicroservice;
import com.sitewhere.device.spi.microservice.IDeviceManagementTenantEngine;
import com.sitewhere.grpc.service.*;
import com.sitewhere.microservice.grpc.TenantTokenServerInterceptor;
import com.sitewhere.spi.SiteWhereException;

import io.grpc.stub.StreamObserver;

/**
 * Routes GRPC calls to service implementations in tenants.
 * 
 * @author Derek
 */
public class DeviceManagementRouter extends DeviceManagementGrpc.DeviceManagementImplBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    /** Parent microservice */
    private IDeviceManagementMicroservice microservice;

    public DeviceManagementRouter(IDeviceManagementMicroservice microservice) {
	this.microservice = microservice;
    }

    /**
     * Based on token passed via GRPC header, look up service implementation
     * running in tenant engine.
     * 
     * @return
     */
    protected DeviceManagementGrpc.DeviceManagementImplBase getTenantImplementation() {
	String tenantToken = TenantTokenServerInterceptor.TENANT_TOKEN_KEY.get();
	if (tenantToken == null) {
	    throw new RuntimeException("Tenant token not found in device management request.");
	}
	try {
	    IDeviceManagementTenantEngine engine = getMicroservice().getTenantEngineByTenantId(tenantToken);
	    if (engine != null) {
		return engine.getDeviceManagementImpl();
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
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createDeviceSpecification(com.sitewhere.grpc.service.
     * GCreateDeviceSpecificationRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void createDeviceSpecification(GCreateDeviceSpecificationRequest request,
	    StreamObserver<GCreateDeviceSpecificationResponse> responseObserver) {
	getTenantImplementation().createDeviceSpecification(request, responseObserver);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceSpecificationByToken(com.sitewhere.grpc.service.
     * GGetDeviceSpecificationByTokenRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceSpecificationByToken(GGetDeviceSpecificationByTokenRequest request,
	    StreamObserver<GGetDeviceSpecificationByTokenResponse> responseObserver) {
	getTenantImplementation().getDeviceSpecificationByToken(request, responseObserver);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateDeviceSpecification(com.sitewhere.grpc.service.
     * GUpdateDeviceSpecificationRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateDeviceSpecification(GUpdateDeviceSpecificationRequest request,
	    StreamObserver<GUpdateDeviceSpecificationResponse> responseObserver) {
	getTenantImplementation().updateDeviceSpecification(request, responseObserver);
    }

    @Override
    public void listDeviceSpecifications(GListDeviceSpecificationsRequest request,
	    StreamObserver<GListDeviceSpecificationsResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listDeviceSpecifications(request, responseObserver);
    }

    @Override
    public void deleteDeviceSpecification(GDeleteDeviceSpecificationRequest request,
	    StreamObserver<GDeleteDeviceSpecificationResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.deleteDeviceSpecification(request, responseObserver);
    }

    @Override
    public void createDeviceCommand(GCreateDeviceCommandRequest request,
	    StreamObserver<GCreateDeviceCommandResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.createDeviceCommand(request, responseObserver);
    }

    @Override
    public void getDeviceCommandByToken(GGetDeviceCommandByTokenRequest request,
	    StreamObserver<GGetDeviceCommandByTokenResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.getDeviceCommandByToken(request, responseObserver);
    }

    @Override
    public void updateDeviceCommand(GUpdateDeviceCommandRequest request,
	    StreamObserver<GUpdateDeviceCommandResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.updateDeviceCommand(request, responseObserver);
    }

    @Override
    public void listDeviceCommands(GListDeviceCommandsRequest request,
	    StreamObserver<GListDeviceCommandsResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listDeviceCommands(request, responseObserver);
    }

    @Override
    public void deleteDeviceCommand(GDeleteDeviceCommandRequest request,
	    StreamObserver<GDeleteDeviceCommandResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.deleteDeviceCommand(request, responseObserver);
    }

    @Override
    public void createDeviceStatus(GCreateDeviceStatusRequest request,
	    StreamObserver<GCreateDeviceStatusResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.createDeviceStatus(request, responseObserver);
    }

    @Override
    public void getDeviceStatusByCode(GGetDeviceStatusByCodeRequest request,
	    StreamObserver<GGetDeviceStatusByCodeResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.getDeviceStatusByCode(request, responseObserver);
    }

    @Override
    public void updateDeviceStatus(GUpdateDeviceStatusRequest request,
	    StreamObserver<GUpdateDeviceStatusResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.updateDeviceStatus(request, responseObserver);
    }

    @Override
    public void listDeviceStatuses(GListDeviceStatusesRequest request,
	    StreamObserver<GListDeviceStatusesResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listDeviceStatuses(request, responseObserver);
    }

    @Override
    public void deleteDeviceStatus(GDeleteDeviceStatusRequest request,
	    StreamObserver<GDeleteDeviceStatusResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.deleteDeviceStatus(request, responseObserver);
    }

    @Override
    public void createDevice(GCreateDeviceRequest request, StreamObserver<GCreateDeviceResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.createDevice(request, responseObserver);
    }

    @Override
    public void getDeviceByHardwareId(GGetDeviceByaHardwareIdRequest request,
	    StreamObserver<GGetDeviceByaHardwareIdResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.getDeviceByHardwareId(request, responseObserver);
    }

    @Override
    public void updateDevice(GUpdateDeviceRequest request, StreamObserver<GUpdateDeviceResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.updateDevice(request, responseObserver);
    }

    @Override
    public void listDevices(GListDevicesRequest request, StreamObserver<GListDevicesResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listDevices(request, responseObserver);
    }

    @Override
    public void createDeviceElementMapping(GCreateDeviceElementMappingRequest request,
	    StreamObserver<GCreateDeviceElementMappingResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.createDeviceElementMapping(request, responseObserver);
    }

    @Override
    public void deleteDeviceElementMapping(GDeleteDeviceElementMappingRequest request,
	    StreamObserver<GDeleteDeviceElementMappingResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.deleteDeviceElementMapping(request, responseObserver);
    }

    @Override
    public void deleteDevice(GDeleteDeviceRequest request, StreamObserver<GDeleteDeviceResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.deleteDevice(request, responseObserver);
    }

    @Override
    public void createDeviceGroup(GCreateDeviceGroupRequest request,
	    StreamObserver<GCreateDeviceGroupResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.createDeviceGroup(request, responseObserver);
    }

    @Override
    public void getDeviceGroupByToken(GGetDeviceGroupByTokenRequest request,
	    StreamObserver<GGetDeviceGroupByTokenResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.getDeviceGroupByToken(request, responseObserver);
    }

    @Override
    public void updateDeviceGroup(GUpdateDeviceGroupRequest request,
	    StreamObserver<GUpdateDeviceGroupResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.updateDeviceGroup(request, responseObserver);
    }

    @Override
    public void listDeviceGroups(GListDeviceGroupsRequest request,
	    StreamObserver<GListDeviceGroupsResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listDeviceGroups(request, responseObserver);
    }

    @Override
    public void listDeviceGroupsWithRole(GListDeviceGroupsWithRoleRequest request,
	    StreamObserver<GListDeviceGroupsWithRoleResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listDeviceGroupsWithRole(request, responseObserver);
    }

    @Override
    public void deleteDeviceGroup(GDeleteDeviceGroupRequest request,
	    StreamObserver<GDeleteDeviceGroupResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.deleteDeviceGroup(request, responseObserver);
    }

    @Override
    public void addDeviceGroupElements(GAddDeviceGroupElementsRequest request,
	    StreamObserver<GAddDeviceGroupElementsResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.addDeviceGroupElements(request, responseObserver);
    }

    @Override
    public void removeDeviceGroupElements(GRemoveDeviceGroupElementsRequest request,
	    StreamObserver<GRemoveDeviceGroupElementsResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.removeDeviceGroupElements(request, responseObserver);
    }

    @Override
    public void listDeviceGroupElements(GListDeviceGroupElementsRequest request,
	    StreamObserver<GListDeviceGroupElementsResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listDeviceGroupElements(request, responseObserver);
    }

    @Override
    public void createDeviceAssignment(GCreateDeviceAssignmentRequest request,
	    StreamObserver<GCreateDeviceAssignmentResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.createDeviceAssignment(request, responseObserver);
    }

    @Override
    public void getDeviceAssignmentByToken(GGetDeviceAssignmentByTokenRequest request,
	    StreamObserver<GGetDeviceAssignmentByTokenResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.getDeviceAssignmentByToken(request, responseObserver);
    }

    @Override
    public void getCurrentAssignmentForDevice(GGetCurrentAssignmentForDeviceRequest request,
	    StreamObserver<GGetCurrentAssignmentForDeviceResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.getCurrentAssignmentForDevice(request, responseObserver);
    }

    @Override
    public void deleteDeviceAssignment(GDeleteDeviceAssignmentRequest request,
	    StreamObserver<GDeleteDeviceAssignmentResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.deleteDeviceAssignment(request, responseObserver);
    }

    @Override
    public void updateDeviceAssignmentMetadata(GUpdateDeviceAssignmentMetadataRequest request,
	    StreamObserver<GUpdateDeviceAssignmentMetadataResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.updateDeviceAssignmentMetadata(request, responseObserver);
    }

    @Override
    public void updateDeviceAssignmentStatus(GUpdateDeviceAssignmentStatusRequest request,
	    StreamObserver<GUpdateDeviceAssignmentStatusResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.updateDeviceAssignmentStatus(request, responseObserver);
    }

    @Override
    public void endDeviceAssignment(GEndDeviceAssignmentRequest request,
	    StreamObserver<GEndDeviceAssignmentResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.endDeviceAssignment(request, responseObserver);
    }

    @Override
    public void getDeviceAssignmentHistory(GGetDeviceAssignmentHistoryRequest request,
	    StreamObserver<GGetDeviceAssignmentHistoryResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.getDeviceAssignmentHistory(request, responseObserver);
    }

    @Override
    public void getDeviceAssignmentsForSite(GGetDeviceAssignmentsForSiteRequest request,
	    StreamObserver<GGetDeviceAssignmentsForSiteResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.getDeviceAssignmentsForSite(request, responseObserver);
    }

    @Override
    public void getDeviceAssignmentsForAsset(GGetDeviceAssignmentsForAssetRequest request,
	    StreamObserver<GGetDeviceAssignmentsForAssetResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.getDeviceAssignmentsForAsset(request, responseObserver);
    }

    @Override
    public void createDeviceStream(GCreateDeviceStreamRequest request,
	    StreamObserver<GCreateDeviceStreamResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.createDeviceStream(request, responseObserver);
    }

    @Override
    public void getDeviceStreamByStreamId(GGetDeviceStreamByStreamIdRequest request,
	    StreamObserver<GGetDeviceStreamByStreamIdResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.getDeviceStreamByStreamId(request, responseObserver);
    }

    @Override
    public void listDeviceStreams(GListDeviceStreamsRequest request,
	    StreamObserver<GListDeviceStreamsResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listDeviceStreams(request, responseObserver);
    }

    @Override
    public void createSite(GCreateSiteRequest request, StreamObserver<GCreateSiteResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.createSite(request, responseObserver);
    }

    @Override
    public void getSiteByToken(GGetSiteByTokenRequest request,
	    StreamObserver<GGetSiteByTokenResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.getSiteByToken(request, responseObserver);
    }

    @Override
    public void updateSite(GUpdateSiteRequest request, StreamObserver<GUpdateSiteResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.updateSite(request, responseObserver);
    }

    @Override
    public void listSites(GListSitesRequest request, StreamObserver<GListSitesResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listSites(request, responseObserver);
    }

    @Override
    public void deleteSite(GDeleteSiteRequest request, StreamObserver<GDeleteSiteResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.deleteSite(request, responseObserver);
    }

    @Override
    public void createZone(GCreateZoneRequest request, StreamObserver<GCreateZoneResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.createZone(request, responseObserver);
    }

    @Override
    public void getZoneByToken(GGetZoneByTokenRequest request,
	    StreamObserver<GGetZoneByTokenResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.getZoneByToken(request, responseObserver);
    }

    @Override
    public void updateZone(GUpdateZoneRequest request, StreamObserver<GUpdateZoneResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.updateZone(request, responseObserver);
    }

    @Override
    public void listZones(GListZonesRequest request, StreamObserver<GListZonesResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listZones(request, responseObserver);
    }

    @Override
    public void deleteZone(GDeleteZoneRequest request, StreamObserver<GDeleteZoneResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.deleteZone(request, responseObserver);
    }

    public IDeviceManagementMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IDeviceManagementMicroservice microservice) {
	this.microservice = microservice;
    }
}