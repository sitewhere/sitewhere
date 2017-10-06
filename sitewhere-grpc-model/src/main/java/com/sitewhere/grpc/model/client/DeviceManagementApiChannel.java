package com.sitewhere.grpc.model.client;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.model.spi.client.IDeviceManagementApiChannel;
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

    @Override
    public IDeviceSpecification createDeviceSpecification(IDeviceSpecificationCreateRequest request)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceSpecification getDeviceSpecificationByToken(String token) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceSpecification updateDeviceSpecification(String token, IDeviceSpecificationCreateRequest request)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISearchResults<IDeviceSpecification> listDeviceSpecifications(boolean includeDeleted,
	    ISearchCriteria criteria) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceSpecification deleteDeviceSpecification(String token, boolean force) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceCommand createDeviceCommand(String specificationToken, IDeviceCommandCreateRequest request)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceCommand getDeviceCommandByToken(String token) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceCommand updateDeviceCommand(String token, IDeviceCommandCreateRequest request)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<IDeviceCommand> listDeviceCommands(String specToken, boolean includeDeleted) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceCommand deleteDeviceCommand(String token, boolean force) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
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