/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.hbase;

import java.util.List;
import java.util.UUID;

import org.apache.hadoop.hbase.regionserver.BloomType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.device.persistence.DeviceManagementPersistence;
import com.sitewhere.hbase.DeviceIdManager;
import com.sitewhere.hbase.HBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.common.SiteWhereTables;
import com.sitewhere.hbase.encoder.IPayloadMarshaler;
import com.sitewhere.hbase.encoder.ProtobufPayloadMarshaler;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.common.IMetadataProvider;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceAssignmentState;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.IDeviceStatus;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.IZone;
import com.sitewhere.spi.device.batch.IBatchElement;
import com.sitewhere.spi.device.batch.IBatchOperation;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.IBatchCommandInvocationRequest;
import com.sitewhere.spi.device.request.IBatchElementUpdateRequest;
import com.sitewhere.spi.device.request.IBatchOperationCreateRequest;
import com.sitewhere.spi.device.request.IBatchOperationUpdateRequest;
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
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IAssignmentSearchCriteria;
import com.sitewhere.spi.search.device.IAssignmentsForAssetSearchCriteria;
import com.sitewhere.spi.search.device.IBatchElementSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * HBase implementation of SiteWhere device management.
 * 
 * @author Derek
 */
public class HBaseDeviceManagement extends TenantLifecycleComponent implements IDeviceManagement {

    /** Static logger instance */
    private static final Logger LOGGER = LogManager.getLogger();

    /** Used to communicate with HBase */
    private ISiteWhereHBaseClient client;

    /** Injected payload encoder */
    private IPayloadMarshaler payloadMarshaler = new ProtobufPayloadMarshaler();

    /** Supplies context to implementation methods */
    private HBaseContext context;

    /** Device id manager */
    private DeviceIdManager deviceIdManager;

    public HBaseDeviceManagement() {
	super(LifecycleComponentType.DataStore);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create context from configured options.
	this.context = new HBaseContext();
	context.setTenant(getTenant());
	context.setClient(getClient());
	context.setPayloadMarshaler(getPayloadMarshaler());

	ensureTablesExist();

	// Create device id manager instance.
	deviceIdManager = new DeviceIdManager();
	deviceIdManager.load(context);
	context.setDeviceIdManager(deviceIdManager);
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

    /**
     * Make sure that all SiteWhere tables exist, creating them if necessary.
     * 
     * @throws SiteWhereException
     */
    protected void ensureTablesExist() throws SiteWhereException {
	SiteWhereTables.assureTenantTable(context, ISiteWhereHBase.UID_TABLE_NAME, BloomType.ROW);
	SiteWhereTables.assureTenantTable(context, ISiteWhereHBase.SITES_TABLE_NAME, BloomType.ROW);
	SiteWhereTables.assureTenantTable(context, ISiteWhereHBase.DEVICES_TABLE_NAME, BloomType.ROW);
	SiteWhereTables.assureTenantTable(context, ISiteWhereHBase.STREAMS_TABLE_NAME, BloomType.ROW);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceSpecification(com.
     * sitewhere .spi.device.request.IDeviceSpecificationCreateRequest)
     */
    @Override
    public IDeviceSpecification createDeviceSpecification(IDeviceSpecificationCreateRequest request)
	    throws SiteWhereException {
	return HBaseDeviceSpecification.createDeviceSpecification(context, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceSpecificationByToken(
     * java.lang .String)
     */
    @Override
    public IDeviceSpecification getDeviceSpecificationByToken(String token) throws SiteWhereException {
	return HBaseDeviceSpecification.getDeviceSpecificationByToken(context, token);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceSpecification(java
     * .lang. String,
     * com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest)
     */
    @Override
    public IDeviceSpecification updateDeviceSpecification(String token, IDeviceSpecificationCreateRequest request)
	    throws SiteWhereException {
	return HBaseDeviceSpecification.updateDeviceSpecification(context, token, request);
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
	return HBaseDeviceSpecification.listDeviceSpecifications(context, includeDeleted, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceSpecification(java
     * .lang. String, boolean)
     */
    @Override
    public IDeviceSpecification deleteDeviceSpecification(String token, boolean force) throws SiteWhereException {
	return HBaseDeviceSpecification.deleteDeviceSpecification(context, token, force);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#createDeviceCommand(com.
     * sitewhere.spi .device.IDeviceSpecification,
     * com.sitewhere.spi.device.request.IDeviceCommandCreateRequest)
     */
    @Override
    public IDeviceCommand createDeviceCommand(IDeviceSpecification spec, IDeviceCommandCreateRequest request)
	    throws SiteWhereException {
	return HBaseDeviceCommand.createDeviceCommand(context, spec, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceCommandByToken(java.
     * lang.String )
     */
    @Override
    public IDeviceCommand getDeviceCommandByToken(String token) throws SiteWhereException {
	return HBaseDeviceCommand.getDeviceCommandByToken(context, token);
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
	return HBaseDeviceCommand.updateDeviceCommand(context, token, request);
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
	return HBaseDeviceCommand.listDeviceCommands(context, specToken, includeDeleted);
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
	return HBaseDeviceCommand.deleteDeviceCommand(context, token, force);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceStatus(java.lang.
     * String, com.sitewhere.spi.device.request.IDeviceStatusCreateRequest)
     */
    @Override
    public IDeviceStatus createDeviceStatus(String specToken, IDeviceStatusCreateRequest request)
	    throws SiteWhereException {
	throw new SiteWhereException("Not implemented yet for HBase device managment.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceStatusByCode(java.
     * lang.String)
     */
    @Override
    public IDeviceStatus getDeviceStatusByCode(String specToken, String code) throws SiteWhereException {
	throw new SiteWhereException("Not implemented yet for HBase device managment.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceStatus(java.lang.
     * String, com.sitewhere.spi.device.request.IDeviceStatusCreateRequest)
     */
    @Override
    public IDeviceStatus updateDeviceStatus(String specToken, String code, IDeviceStatusCreateRequest request)
	    throws SiteWhereException {
	throw new SiteWhereException("Not implemented yet for HBase device managment.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceStatuses(java.lang.
     * String)
     */
    @Override
    public List<IDeviceStatus> listDeviceStatuses(String specToken) throws SiteWhereException {
	throw new SiteWhereException("Not implemented yet for HBase device managment.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceStatus(java.lang.
     * String, java.lang.String)
     */
    @Override
    public IDeviceStatus deleteDeviceStatus(String specToken, String code) throws SiteWhereException {
	throw new SiteWhereException("Not implemented yet for HBase device managment.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDevice(com.sitewhere.spi
     * .device .request.IDeviceCreateRequest)
     */
    public IDevice createDevice(IDeviceCreateRequest device) throws SiteWhereException {
	return HBaseDevice.createDevice(context, device);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceByHardwareId(java.
     * lang.String)
     */
    public IDevice getDeviceByHardwareId(String hardwareId) throws SiteWhereException {
	return HBaseDevice.getDeviceByHardwareId(context, hardwareId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDevice(java.lang.String,
     * com.sitewhere.spi.device.request.IDeviceCreateRequest)
     */
    public IDevice updateDevice(String hardwareId, IDeviceCreateRequest request) throws SiteWhereException {
	return HBaseDevice.updateDevice(context, hardwareId, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getCurrentDeviceAssignment(com
     * .sitewhere .spi.device.IDevice)
     */
    public IDeviceAssignment getCurrentDeviceAssignment(IDevice device) throws SiteWhereException {
	String token = HBaseDevice.getCurrentAssignmentId(context, device.getHardwareId());
	if (token == null) {
	    return null;
	}
	return HBaseDeviceAssignment.getDeviceAssignment(context, token);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#listDevices(boolean,
     * com.sitewhere.spi.search.device.IDeviceSearchCriteria)
     */
    public SearchResults<IDevice> listDevices(boolean includeDeleted, IDeviceSearchCriteria criteria)
	    throws SiteWhereException {
	return HBaseDevice.listDevices(context, includeDeleted, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceElementMapping(
     * java.lang .String, com.sitewhere.spi.device.IDeviceElementMapping)
     */
    @Override
    public IDevice createDeviceElementMapping(String hardwareId, IDeviceElementMapping mapping)
	    throws SiteWhereException {
	return DeviceManagementPersistence.deviceElementMappingCreateLogic(this, hardwareId, mapping);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceElementMapping(
     * java.lang .String, java.lang.String)
     */
    @Override
    public IDevice deleteDeviceElementMapping(String hardwareId, String path) throws SiteWhereException {
	return DeviceManagementPersistence.deviceElementMappingDeleteLogic(this, hardwareId, path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDevice(java.lang.String,
     * boolean)
     */
    public IDevice deleteDevice(String hardwareId, boolean force) throws SiteWhereException {
	return HBaseDevice.deleteDevice(context, hardwareId, force);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceAssignment(com.
     * sitewhere .spi.device.request.IDeviceAssignmentCreateRequest)
     */
    public IDeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request) throws SiteWhereException {
	return HBaseDeviceAssignment.createDeviceAssignment(context, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentByToken(
     * java.lang .String)
     */
    public IDeviceAssignment getDeviceAssignmentByToken(String token) throws SiteWhereException {
	return HBaseDeviceAssignment.getDeviceAssignment(context, token);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceAssignment(java.
     * lang.String, boolean)
     */
    public IDeviceAssignment deleteDeviceAssignment(String token, boolean force) throws SiteWhereException {
	return HBaseDeviceAssignment.deleteDeviceAssignment(context, token, force);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceForAssignment(com.
     * sitewhere .spi.device.IDeviceAssignment)
     */
    public IDevice getDeviceForAssignment(IDeviceAssignment assignment) throws SiteWhereException {
	return HBaseDevice.getDeviceByHardwareId(context, assignment.getDeviceHardwareId());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#getSiteForAssignment(com.
     * sitewhere.spi .device.IDeviceAssignment)
     */
    public ISite getSiteForAssignment(IDeviceAssignment assignment) throws SiteWhereException {
	return HBaseSite.getSiteByToken(context, assignment.getSiteToken());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignmentMetadata
     * (java. lang.String, com.sitewhere.spi.common.IMetadataProvider)
     */
    public IDeviceAssignment updateDeviceAssignmentMetadata(String token, IMetadataProvider metadata)
	    throws SiteWhereException {
	return HBaseDeviceAssignment.updateDeviceAssignmentMetadata(context, token, metadata);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignmentStatus(
     * java.lang .String, com.sitewhere.spi.device.DeviceAssignmentStatus)
     */
    public IDeviceAssignment updateDeviceAssignmentStatus(String token, DeviceAssignmentStatus status)
	    throws SiteWhereException {
	return HBaseDeviceAssignment.updateDeviceAssignmentStatus(context, token, status);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignmentState(
     * java.lang .String, com.sitewhere.spi.device.IDeviceAssignmentState)
     */
    public IDeviceAssignment updateDeviceAssignmentState(String token, IDeviceAssignmentState state)
	    throws SiteWhereException {
	return HBaseDeviceAssignment.updateDeviceAssignmentState(context, token, state);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#endDeviceAssignment(java.lang.
     * String)
     */
    public IDeviceAssignment endDeviceAssignment(String token) throws SiteWhereException {
	return HBaseDeviceAssignment.endDeviceAssignment(context, token);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentHistory(
     * java.lang .String, com.sitewhere.spi.common.ISearchCriteria)
     */
    public SearchResults<IDeviceAssignment> getDeviceAssignmentHistory(String hardwareId, ISearchCriteria criteria)
	    throws SiteWhereException {
	return HBaseDevice.getDeviceAssignmentHistory(context, hardwareId, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentsForSite(
     * java.lang.String,
     * com.sitewhere.spi.search.device.IAssignmentSearchCriteria)
     */
    public SearchResults<IDeviceAssignment> getDeviceAssignmentsForSite(String siteToken,
	    IAssignmentSearchCriteria criteria) throws SiteWhereException {
	return HBaseSite.listDeviceAssignmentsForSite(context, siteToken, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentsForAsset(
     * java.lang.String, java.lang.String,
     * com.sitewhere.spi.search.device.IAssignmentsForAssetSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForAsset(String assetModuleId, String assetId,
	    IAssignmentsForAssetSearchCriteria criteria) throws SiteWhereException {
	return HBaseSite.listDeviceAssignmentsForAsset(context, assetModuleId, assetId, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceStream(java.lang.
     * String,
     * com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest)
     */
    @Override
    public IDeviceStream createDeviceStream(String assignmentToken, IDeviceStreamCreateRequest request)
	    throws SiteWhereException {
	return HBaseDeviceStream.createDeviceStream(context, assignmentToken, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceStream(java.lang.
     * String, java.lang.String)
     */
    @Override
    public IDeviceStream getDeviceStream(String assignmentToken, String streamId) throws SiteWhereException {
	return HBaseDeviceStream.getDeviceStream(context, assignmentToken, streamId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceStreams(java.lang.
     * String, com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStream> listDeviceStreams(String assignmentToken, ISearchCriteria criteria)
	    throws SiteWhereException {
	return HBaseDeviceStream.listDeviceStreams(context, assignmentToken, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createSite(com.sitewhere.spi.
     * device. request.ISiteCreateRequest)
     */
    @Override
    public ISite createSite(ISiteCreateRequest request) throws SiteWhereException {
	return HBaseSite.createSite(context, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteSite(java.lang.String,
     * boolean)
     */
    @Override
    public ISite deleteSite(String siteToken, boolean force) throws SiteWhereException {
	return HBaseSite.deleteSite(context, siteToken, force);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateSite(java.lang.String,
     * com.sitewhere.spi.device.request.ISiteCreateRequest)
     */
    @Override
    public ISite updateSite(String siteToken, ISiteCreateRequest request) throws SiteWhereException {
	return HBaseSite.updateSite(context, siteToken, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#getSiteByToken(java.lang.
     * String)
     */
    @Override
    public ISite getSiteByToken(String token) throws SiteWhereException {
	return HBaseSite.getSiteByToken(context, token);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listSites(com.sitewhere.spi.
     * common. ISearchCriteria)
     */
    @Override
    public SearchResults<ISite> listSites(ISearchCriteria criteria) throws SiteWhereException {
	return HBaseSite.listSites(context, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createZone(com.sitewhere.spi.
     * device. ISite, com.sitewhere.spi.device.request.IZoneCreateRequest)
     */
    @Override
    public IZone createZone(ISite site, IZoneCreateRequest request) throws SiteWhereException {
	return HBaseZone.createZone(context, site, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateZone(java.lang.String,
     * com.sitewhere.spi.device.request.IZoneCreateRequest)
     */
    @Override
    public IZone updateZone(String token, IZoneCreateRequest request) throws SiteWhereException {
	return HBaseZone.updateZone(context, token, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#getZone(java.lang.String)
     */
    @Override
    public IZone getZone(String zoneToken) throws SiteWhereException {
	return HBaseZone.getZone(context, zoneToken);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listZones(java.lang.String,
     * com.sitewhere.spi.common.ISearchCriteria)
     */
    @Override
    public SearchResults<IZone> listZones(String siteToken, ISearchCriteria criteria) throws SiteWhereException {
	return HBaseSite.listZonesForSite(context, siteToken, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteZone(java.lang.String,
     * boolean)
     */
    @Override
    public IZone deleteZone(String zoneToken, boolean force) throws SiteWhereException {
	return HBaseZone.deleteZone(context, zoneToken, force);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#createDeviceGroup(com.
     * sitewhere.spi. device.request.IDeviceGroupCreateRequest)
     */
    @Override
    public IDeviceGroup createDeviceGroup(IDeviceGroupCreateRequest request) throws SiteWhereException {
	return HBaseDeviceGroup.createDeviceGroup(context, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceGroup(java.lang.
     * String, com.sitewhere.spi.device.request.IDeviceGroupCreateRequest)
     */
    @Override
    public IDeviceGroup updateDeviceGroup(String token, IDeviceGroupCreateRequest request) throws SiteWhereException {
	return HBaseDeviceGroup.updateDeviceGroup(context, token, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceGroup(java.lang.
     * String)
     */
    @Override
    public IDeviceGroup getDeviceGroup(String token) throws SiteWhereException {
	return HBaseDeviceGroup.getDeviceGroupByToken(context, token);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceGroups(boolean,
     * com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceGroup> listDeviceGroups(boolean includeDeleted, ISearchCriteria criteria)
	    throws SiteWhereException {
	return HBaseDeviceGroup.listDeviceGroups(context, includeDeleted, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceGroupsWithRole(java.
     * lang. String , boolean, com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceGroup> listDeviceGroupsWithRole(String role, boolean includeDeleted,
	    ISearchCriteria criteria) throws SiteWhereException {
	return HBaseDeviceGroup.listDeviceGroupsWithRole(context, role, includeDeleted, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceGroup(java.lang.
     * String, boolean)
     */
    @Override
    public IDeviceGroup deleteDeviceGroup(String token, boolean force) throws SiteWhereException {
	return HBaseDeviceGroup.deleteDeviceGroup(context, token, force);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#addDeviceGroupElements(java.
     * lang.String, java.util.List, boolean)
     */
    @Override
    public List<IDeviceGroupElement> addDeviceGroupElements(String networkToken,
	    List<IDeviceGroupElementCreateRequest> elements, boolean ignoreDuplicates) throws SiteWhereException {
	return HBaseDeviceGroupElement.createDeviceGroupElements(context, networkToken, elements, ignoreDuplicates);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#removeDeviceGroupElements(java
     * .lang. String, java.util.List)
     */
    @Override
    public List<IDeviceGroupElement> removeDeviceGroupElements(String networkToken,
	    List<IDeviceGroupElementCreateRequest> elements) throws SiteWhereException {
	return HBaseDeviceGroupElement.removeDeviceGroupElements(context, networkToken, elements);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceGroupElements(java.
     * lang.String , com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public SearchResults<IDeviceGroupElement> listDeviceGroupElements(String networkToken, ISearchCriteria criteria)
	    throws SiteWhereException {
	return HBaseDeviceGroupElement.listDeviceGroupElements(context, networkToken, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#createBatchOperation(com.
     * sitewhere.spi .device.request.IBatchOperationCreateRequest)
     */
    @Override
    public IBatchOperation createBatchOperation(IBatchOperationCreateRequest request) throws SiteWhereException {
	return HBaseBatchOperation.createBatchOperation(context, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateBatchOperation(java.lang
     * .String, com.sitewhere.spi.device.request.IBatchOperationUpdateRequest)
     */
    @Override
    public IBatchOperation updateBatchOperation(String token, IBatchOperationUpdateRequest request)
	    throws SiteWhereException {
	return HBaseBatchOperation.updateBatchOperation(context, token, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getBatchOperation(java.lang.
     * String)
     */
    @Override
    public IBatchOperation getBatchOperation(String token) throws SiteWhereException {
	return HBaseBatchOperation.getBatchOperationByToken(context, token);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listBatchOperations(boolean,
     * com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IBatchOperation> listBatchOperations(boolean includeDeleted, ISearchCriteria criteria)
	    throws SiteWhereException {
	return HBaseBatchOperation.listBatchOperations(context, includeDeleted, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteBatchOperation(java.lang
     * .String, boolean)
     */
    @Override
    public IBatchOperation deleteBatchOperation(String token, boolean force) throws SiteWhereException {
	return HBaseBatchOperation.deleteBatchOperation(context, token, force);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listBatchElements(java.lang.
     * String, com.sitewhere.spi.search.device.IBatchElementSearchCriteria)
     */
    @Override
    public SearchResults<IBatchElement> listBatchElements(String batchToken, IBatchElementSearchCriteria criteria)
	    throws SiteWhereException {
	return HBaseBatchElement.listBatchElements(context, batchToken, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateBatchElement(java.lang.
     * String, long,
     * com.sitewhere.spi.device.request.IBatchElementUpdateRequest)
     */
    @Override
    public IBatchElement updateBatchElement(String operationToken, long index, IBatchElementUpdateRequest request)
	    throws SiteWhereException {
	return HBaseBatchElement.updateBatchElement(context, operationToken, index, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createBatchCommandInvocation(
     * com. sitewhere .spi.device.request.IBatchCommandInvocationRequest)
     */
    @Override
    public IBatchOperation createBatchCommandInvocation(IBatchCommandInvocationRequest request)
	    throws SiteWhereException {
	String uuid = ((request.getToken() != null) ? request.getToken() : UUID.randomUUID().toString());
	IBatchOperationCreateRequest generic = DeviceManagementPersistence.batchCommandInvocationCreateLogic(request,
		uuid);
	return createBatchOperation(generic);
    }

    /**
     * Verify that the given assignment exists.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceAssignment assertDeviceAssignment(String token) throws SiteWhereException {
	IDeviceAssignment result = HBaseDeviceAssignment.getDeviceAssignment(context, token);
	if (result == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
	}
	return result;
    }

    public ISiteWhereHBaseClient getClient() {
	return client;
    }

    public void setClient(ISiteWhereHBaseClient client) {
	this.client = client;
    }

    public IPayloadMarshaler getPayloadMarshaler() {
	return payloadMarshaler;
    }

    public void setPayloadMarshaler(IPayloadMarshaler payloadMarshaler) {
	this.payloadMarshaler = payloadMarshaler;
    }
}