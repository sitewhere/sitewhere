package com.sitewhere.grpc.model.client;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.model.GrpcUtils;
import com.sitewhere.grpc.model.converter.DeviceModelConverter;
import com.sitewhere.grpc.model.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.grpc.service.DeviceManagementGrpc;
import com.sitewhere.grpc.service.GCreateDeviceCommandRequest;
import com.sitewhere.grpc.service.GCreateDeviceCommandResponse;
import com.sitewhere.grpc.service.GCreateDeviceSpecificationRequest;
import com.sitewhere.grpc.service.GCreateDeviceSpecificationResponse;
import com.sitewhere.grpc.service.GDeleteDeviceCommandRequest;
import com.sitewhere.grpc.service.GDeleteDeviceCommandResponse;
import com.sitewhere.grpc.service.GDeleteDeviceSpecificationRequest;
import com.sitewhere.grpc.service.GDeleteDeviceSpecificationResponse;
import com.sitewhere.grpc.service.GGetDeviceCommandByTokenRequest;
import com.sitewhere.grpc.service.GGetDeviceCommandByTokenResponse;
import com.sitewhere.grpc.service.GGetDeviceSpecificationByTokenRequest;
import com.sitewhere.grpc.service.GGetDeviceSpecificationByTokenResponse;
import com.sitewhere.grpc.service.GListDeviceCommandsRequest;
import com.sitewhere.grpc.service.GListDeviceCommandsResponse;
import com.sitewhere.grpc.service.GListDeviceSpecificationsRequest;
import com.sitewhere.grpc.service.GListDeviceSpecificationsResponse;
import com.sitewhere.grpc.service.GUpdateDeviceCommandRequest;
import com.sitewhere.grpc.service.GUpdateDeviceCommandResponse;
import com.sitewhere.grpc.service.GUpdateDeviceSpecificationRequest;
import com.sitewhere.grpc.service.GUpdateDeviceSpecificationResponse;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.IDeviceStatus;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.IZone;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCommandCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;
import com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest;
import com.sitewhere.spi.device.request.IDeviceStatusCreateRequest;
import com.sitewhere.spi.device.request.ISiteCreateRequest;
import com.sitewhere.spi.device.request.IZoneCreateRequest;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IAssignmentSearchCriteria;
import com.sitewhere.spi.search.device.IAssignmentsForAssetSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;

/**
 * Supports SiteWhere device management APIs on top of a
 * {@link DeviceManagementGrpcChannel}.
 * 
 * @author Derek
 */
public class DeviceManagementApiChannel extends ApiChannel<DeviceManagementGrpcChannel>
	implements IDeviceManagementApiChannel {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Tenant management GRPC channel */
    private DeviceManagementGrpcChannel grpcChannel;

    public DeviceManagementApiChannel(DeviceManagementGrpcChannel grpcChannel) {
	this.grpcChannel = grpcChannel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceSpecification(com.
     * sitewhere.spi.device.request.IDeviceSpecificationCreateRequest)
     */
    @Override
    public IDeviceSpecification createDeviceSpecification(IDeviceSpecificationCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_CREATE_DEVICE_SPECIFICATION);
	    GCreateDeviceSpecificationRequest.Builder grequest = GCreateDeviceSpecificationRequest.newBuilder();
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceSpecificationCreateRequest(request));
	    GCreateDeviceSpecificationResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createDeviceSpecification(grequest.build());
	    IDeviceSpecification response = (gresponse.hasSpecification())
		    ? DeviceModelConverter.asApiDeviceSpecification(gresponse.getSpecification()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_CREATE_DEVICE_SPECIFICATION, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_SPECIFICATION, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceSpecificationByToken(
     * java.lang.String)
     */
    @Override
    public IDeviceSpecification getDeviceSpecificationByToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_SPECIFICATION_BY_TOKEN);
	    GGetDeviceSpecificationByTokenRequest.Builder grequest = GGetDeviceSpecificationByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetDeviceSpecificationByTokenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceSpecificationByToken(grequest.build());
	    IDeviceSpecification response = (gresponse.hasSpecification())
		    ? DeviceModelConverter.asApiDeviceSpecification(gresponse.getSpecification()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_DEVICE_SPECIFICATION_BY_TOKEN, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_SPECIFICATION_BY_TOKEN,
		    t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceSpecification(java
     * .lang.String,
     * com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest)
     */
    @Override
    public IDeviceSpecification updateDeviceSpecification(String token, IDeviceSpecificationCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_SPECIFICATION);
	    GUpdateDeviceSpecificationRequest.Builder grequest = GUpdateDeviceSpecificationRequest.newBuilder();
	    grequest.setToken(token);
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceSpecificationCreateRequest(request));
	    GUpdateDeviceSpecificationResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateDeviceSpecification(grequest.build());
	    IDeviceSpecification response = (gresponse.hasSpecification())
		    ? DeviceModelConverter.asApiDeviceSpecification(gresponse.getSpecification()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_SPECIFICATION, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_SPECIFICATION, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceSpecifications(
     * boolean, com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceSpecification> listDeviceSpecifications(boolean includeDeleted,
	    ISearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_LIST_DEVICE_SPECIFICATIONS);
	    GListDeviceSpecificationsRequest.Builder grequest = GListDeviceSpecificationsRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asApiDeviceSpecificationSearchCriteria(includeDeleted, criteria));
	    GListDeviceSpecificationsResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listDeviceSpecifications(grequest.build());
	    ISearchResults<IDeviceSpecification> results = DeviceModelConverter
		    .asApiDeviceSpecificationSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_LIST_DEVICE_SPECIFICATIONS, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_SPECIFICATIONS, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceSpecification(java
     * .lang.String, boolean)
     */
    @Override
    public IDeviceSpecification deleteDeviceSpecification(String token, boolean force) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_DELETE_DEVICE_SPECIFICATION);
	    GDeleteDeviceSpecificationRequest.Builder grequest = GDeleteDeviceSpecificationRequest.newBuilder();
	    grequest.setToken(token);
	    grequest.setForce(force);
	    GDeleteDeviceSpecificationResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteDeviceSpecification(grequest.build());
	    IDeviceSpecification response = (gresponse.hasSpecification())
		    ? DeviceModelConverter.asApiDeviceSpecification(gresponse.getSpecification()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_DELETE_DEVICE_SPECIFICATION, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE_SPECIFICATION, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceCommand(java.lang.
     * String, com.sitewhere.spi.device.request.IDeviceCommandCreateRequest)
     */
    @Override
    public IDeviceCommand createDeviceCommand(String specificationToken, IDeviceCommandCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_CREATE_DEVICE_COMMAND);
	    GCreateDeviceCommandRequest.Builder grequest = GCreateDeviceCommandRequest.newBuilder();
	    grequest.setSpecificationToken(specificationToken);
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceCommandCreateRequest(request));
	    GCreateDeviceCommandResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createDeviceCommand(grequest.build());
	    IDeviceCommand response = (gresponse.hasCommand())
		    ? DeviceModelConverter.asApiDeviceCommand(gresponse.getCommand()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_CREATE_DEVICE_COMMAND, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_COMMAND, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceCommandByToken(java.
     * lang.String)
     */
    @Override
    public IDeviceCommand getDeviceCommandByToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_COMMAND_BY_TOKEN);
	    GGetDeviceCommandByTokenRequest.Builder grequest = GGetDeviceCommandByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetDeviceCommandByTokenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceCommandByToken(grequest.build());
	    IDeviceCommand response = (gresponse.hasCommand())
		    ? DeviceModelConverter.asApiDeviceCommand(gresponse.getCommand()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_DEVICE_COMMAND_BY_TOKEN, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_COMMAND_BY_TOKEN, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceCommand(java.lang.
     * String, com.sitewhere.spi.device.request.IDeviceCommandCreateRequest)
     */
    @Override
    public IDeviceCommand updateDeviceCommand(String token, IDeviceCommandCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_COMMAND);
	    GUpdateDeviceCommandRequest.Builder grequest = GUpdateDeviceCommandRequest.newBuilder();
	    grequest.setToken(token);
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceCommandCreateRequest(request));
	    GUpdateDeviceCommandResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateDeviceCommand(grequest.build());
	    IDeviceCommand response = (gresponse.hasCommand())
		    ? DeviceModelConverter.asApiDeviceCommand(gresponse.getCommand()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_COMMAND, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_COMMAND, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceCommands(java.lang.
     * String, boolean)
     */
    @Override
    public List<IDeviceCommand> listDeviceCommands(String specToken, boolean includeDeleted) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_LIST_DEVICE_COMMANDS);
	    GListDeviceCommandsRequest.Builder grequest = GListDeviceCommandsRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asApiDeviceCommandSearchCriteria(includeDeleted, specToken));
	    GListDeviceCommandsResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listDeviceCommands(grequest.build());
	    List<IDeviceCommand> results = DeviceModelConverter
		    .asApiDeviceCommandSearchResults(gresponse.getCommandsList());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_LIST_DEVICE_COMMANDS, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_COMMANDS, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceCommand(java.lang.
     * String, boolean)
     */
    @Override
    public IDeviceCommand deleteDeviceCommand(String token, boolean force) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_DELETE_DEVICE_COMMAND);
	    GDeleteDeviceCommandRequest.Builder grequest = GDeleteDeviceCommandRequest.newBuilder();
	    grequest.setToken(token);
	    grequest.setForce(force);
	    GDeleteDeviceCommandResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteDeviceCommand(grequest.build());
	    IDeviceCommand response = (gresponse.hasCommand())
		    ? DeviceModelConverter.asApiDeviceCommand(gresponse.getCommand()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_DELETE_DEVICE_COMMAND, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE_COMMAND, t);
	}
    }

    @Override
    public IDeviceStatus createDeviceStatus(String specToken, IDeviceStatusCreateRequest request)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceStatus getDeviceStatusByCode(String specToken, String code) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceStatus updateDeviceStatus(String specToken, String code, IDeviceStatusCreateRequest request)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<IDeviceStatus> listDeviceStatuses(String specToken) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceStatus deleteDeviceStatus(String specToken, String code) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDevice createDevice(IDeviceCreateRequest device) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDevice getDeviceByHardwareId(String hardwareId) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDevice updateDevice(String hardwareId, IDeviceCreateRequest request) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISearchResults<IDevice> listDevices(boolean includeDeleted, IDeviceSearchCriteria criteria)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDevice createDeviceElementMapping(String hardwareId, IDeviceElementMapping mapping)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDevice deleteDeviceElementMapping(String hardwareId, String path) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDevice deleteDevice(String hardwareId, boolean force) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceAssignment getDeviceAssignmentByToken(String token) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceAssignment getCurrentDeviceAssignment(String hardwareId) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceAssignment deleteDeviceAssignment(String token, boolean force) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceAssignment updateDeviceAssignmentMetadata(String token, Map<String, String> metadata)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceAssignment updateDeviceAssignmentStatus(String token, DeviceAssignmentStatus status)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceAssignment endDeviceAssignment(String token) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentHistory(String hardwareId, ISearchCriteria criteria)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForSite(String siteToken,
	    IAssignmentSearchCriteria criteria) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForAsset(String assetModuleId, String assetId,
	    IAssignmentsForAssetSearchCriteria criteria) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceStream createDeviceStream(String assignmentToken, IDeviceStreamCreateRequest request)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceStream getDeviceStream(String assignmentToken, String streamId) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISearchResults<IDeviceStream> listDeviceStreams(String assignmentToken, ISearchCriteria criteria)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISite createSite(ISiteCreateRequest request) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISite getSiteByToken(String token) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISite updateSite(String siteToken, ISiteCreateRequest request) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISearchResults<ISite> listSites(ISearchCriteria criteria) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISite deleteSite(String siteToken, boolean force) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IZone createZone(String siteToken, IZoneCreateRequest request) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IZone getZone(String zoneToken) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IZone updateZone(String token, IZoneCreateRequest request) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISearchResults<IZone> listZones(String siteToken, ISearchCriteria criteria) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IZone deleteZone(String zoneToken, boolean force) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceGroup createDeviceGroup(IDeviceGroupCreateRequest request) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceGroup getDeviceGroup(String token) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceGroup updateDeviceGroup(String token, IDeviceGroupCreateRequest request) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISearchResults<IDeviceGroup> listDeviceGroups(boolean includeDeleted, ISearchCriteria criteria)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISearchResults<IDeviceGroup> listDeviceGroupsWithRole(String role, boolean includeDeleted,
	    ISearchCriteria criteria) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceGroup deleteDeviceGroup(String token, boolean force) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<IDeviceGroupElement> addDeviceGroupElements(String groupToken,
	    List<IDeviceGroupElementCreateRequest> elements, boolean ignoreDuplicates) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<IDeviceGroupElement> removeDeviceGroupElements(String groupToken,
	    List<IDeviceGroupElementCreateRequest> elements) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISearchResults<IDeviceGroupElement> listDeviceGroupElements(String groupToken, ISearchCriteria criteria)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.ApiChannel#getGrpcChannel()
     */
    @Override
    public DeviceManagementGrpcChannel getGrpcChannel() {
	return grpcChannel;
    }

    public void setGrpcChannel(DeviceManagementGrpcChannel grpcChannel) {
	this.grpcChannel = grpcChannel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}