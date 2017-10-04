package com.sitewhere.device.grpc;

import com.sitewhere.grpc.model.DeviceModel.GDeviceSpecificationSearchResults;
import com.sitewhere.grpc.model.GrpcUtils;
import com.sitewhere.grpc.model.converter.CommonModelConverter;
import com.sitewhere.grpc.model.converter.DeviceModelConverter;
import com.sitewhere.grpc.service.*;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest;
import com.sitewhere.spi.search.ISearchResults;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for device management GRPC requests.
 * 
 * @author Derek
 */
public class DeviceManagementImpl extends DeviceManagementGrpc.DeviceManagementImplBase {

    /** Device management persistence */
    private IDeviceManagement deviceManagement;

    public DeviceManagementImpl(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
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
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_CREATE_DEVICE_SPECIFICATION);
	    IDeviceSpecificationCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceSpecificationCreateRequest(request.getRequest());
	    IDeviceSpecification apiResult = getDeviceManagement().createDeviceSpecification(apiRequest);
	    GCreateDeviceSpecificationResponse.Builder response = GCreateDeviceSpecificationResponse.newBuilder();
	    response.setSpecification(DeviceModelConverter.asGrpcDeviceSpecification(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_SPECIFICATION, e);
	    responseObserver.onError(e);
	}
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
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_SPECIFICATION_BY_TOKEN);
	    IDeviceSpecification apiResult = getDeviceManagement().getDeviceSpecificationByToken(request.getToken());
	    GGetDeviceSpecificationByTokenResponse.Builder response = GGetDeviceSpecificationByTokenResponse
		    .newBuilder();
	    if (apiResult != null) {
		response.setSpecification(DeviceModelConverter.asGrpcDeviceSpecification(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_SPECIFICATION_BY_TOKEN, e);
	    responseObserver.onError(e);
	}
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
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_SPECIFICATION);
	    IDeviceSpecificationCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceSpecificationCreateRequest(request.getRequest());
	    IDeviceSpecification apiResult = getDeviceManagement().updateDeviceSpecification(request.getToken(),
		    apiRequest);
	    GUpdateDeviceSpecificationResponse.Builder response = GUpdateDeviceSpecificationResponse.newBuilder();
	    response.setSpecification(DeviceModelConverter.asGrpcDeviceSpecification(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_SPECIFICATION, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listDeviceSpecifications(com.sitewhere.grpc.service.
     * GListDeviceSpecificationsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceSpecifications(GListDeviceSpecificationsRequest request,
	    StreamObserver<GListDeviceSpecificationsResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_LIST_DEVICE_SPECIFICATIONS);
	    boolean includeDeleted = request.getCriteria().hasIncludeDeleted()
		    ? request.getCriteria().getIncludeDeleted().getValue() : false;
	    ISearchResults<IDeviceSpecification> apiResult = getDeviceManagement().listDeviceSpecifications(
		    includeDeleted, CommonModelConverter.asApiSearchCriteria(request.getCriteria().getPaging()));
	    GListDeviceSpecificationsResponse.Builder response = GListDeviceSpecificationsResponse.newBuilder();
	    GDeviceSpecificationSearchResults.Builder results = GDeviceSpecificationSearchResults.newBuilder();
	    for (IDeviceSpecification apiSpecification : apiResult.getResults()) {
		results.addSpecifications(DeviceModelConverter.asGrpcDeviceSpecification(apiSpecification));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_SPECIFICATIONS, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteDeviceSpecification(com.sitewhere.grpc.service.
     * GDeleteDeviceSpecificationRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteDeviceSpecification(GDeleteDeviceSpecificationRequest request,
	    StreamObserver<GDeleteDeviceSpecificationResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_DELETE_DEVICE_SPECIFICATION);
	    IDeviceSpecification apiResult = getDeviceManagement().deleteDeviceSpecification(request.getToken(),
		    request.getForce());
	    GDeleteDeviceSpecificationResponse.Builder response = GDeleteDeviceSpecificationResponse.newBuilder();
	    response.setSpecification(DeviceModelConverter.asGrpcDeviceSpecification(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE_SPECIFICATION, e);
	    responseObserver.onError(e);
	}
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
    public void updateDeviceCommand(GGetDeviceCommandByTokenRequest request,
	    StreamObserver<GGetDeviceCommandByTokenResponse> responseObserver) {
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
    public void deleteDeviceCommand(GDeleteDeviceStatusRequest request,
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
    public void deleteDeviceStatus(GDeleteDeviceCommandRequest request,
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

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }
}