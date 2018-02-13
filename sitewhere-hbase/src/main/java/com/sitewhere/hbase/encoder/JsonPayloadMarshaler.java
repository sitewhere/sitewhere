/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.encoder;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.rest.model.area.Area;
import com.sitewhere.rest.model.area.Zone;
import com.sitewhere.rest.model.asset.AssetCategory;
import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.asset.LocationAsset;
import com.sitewhere.rest.model.asset.PersonAsset;
import com.sitewhere.rest.model.batch.BatchElement;
import com.sitewhere.rest.model.batch.BatchOperation;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceAssignmentState;
import com.sitewhere.rest.model.device.DeviceType;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.rest.model.device.event.DeviceStateChange;
import com.sitewhere.rest.model.device.event.DeviceStreamData;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.rest.model.device.group.DeviceGroupElement;
import com.sitewhere.rest.model.device.streaming.DeviceStream;
import com.sitewhere.rest.model.tenant.Tenant;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.area.IZone;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.IHardwareAsset;
import com.sitewhere.spi.asset.ILocationAsset;
import com.sitewhere.spi.asset.IPersonAsset;
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceAssignmentState;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.IDeviceStreamData;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;

/**
 * Implementation of {@link IPayloadMarshaler} that marshals objects to JSON.
 * 
 * @author Derek
 */
public class JsonPayloadMarshaler implements IPayloadMarshaler {

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#getEncoding()
     */
    @Override
    public PayloadEncoding getEncoding() throws SiteWhereException {
	return PayloadEncoding.Json;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#encode(java.lang.Object)
     */
    @Override
    public byte[] encode(Object obj) throws SiteWhereException {
	return MarshalUtils.marshalJson(obj);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#decode(byte[],
     * java.lang.Class)
     */
    @Override
    public <T> T decode(byte[] payload, Class<T> type) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, type);
    }

    /*
     * @see
     * com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeArea(com.sitewhere.spi.
     * area.IArea)
     */
    @Override
    public byte[] encodeArea(IArea area) throws SiteWhereException {
	return MarshalUtils.marshalJson(area);
    }

    /*
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeArea(byte[])
     */
    @Override
    public Area decodeArea(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, Area.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeZone(com.sitewhere.
     * spi.device .IZone)
     */
    @Override
    public byte[] encodeZone(IZone zone) throws SiteWhereException {
	return MarshalUtils.marshalJson(zone);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeZone(byte[])
     */
    @Override
    public Zone decodeZone(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, Zone.class);
    }

    /*
     * @see
     * com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeDeviceType(com.sitewhere.
     * spi.device.IDeviceType)
     */
    @Override
    public byte[] encodeDeviceType(IDeviceType deviceType) throws SiteWhereException {
	return MarshalUtils.marshalJson(deviceType);
    }

    /*
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeDeviceType(byte[])
     */
    @Override
    public DeviceType decodeDeviceType(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, DeviceType.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeDevice(com.sitewhere.
     * spi.device .IDevice)
     */
    @Override
    public byte[] encodeDevice(IDevice device) throws SiteWhereException {
	return MarshalUtils.marshalJson(device);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeDevice(byte[])
     */
    @Override
    public Device decodeDevice(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, Device.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeDeviceAssignment(com.
     * sitewhere .spi.device.IDeviceAssignment)
     */
    @Override
    public byte[] encodeDeviceAssignment(IDeviceAssignment assignment) throws SiteWhereException {
	return MarshalUtils.marshalJson(assignment);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeDeviceAssignment(byte [])
     */
    @Override
    public DeviceAssignment decodeDeviceAssignment(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, DeviceAssignment.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeDeviceStream(com.
     * sitewhere. spi.device.streaming.IDeviceStream)
     */
    @Override
    public byte[] encodeDeviceStream(IDeviceStream stream) throws SiteWhereException {
	return MarshalUtils.marshalJson(stream);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeDeviceStream(byte[])
     */
    @Override
    public DeviceStream decodeDeviceStream(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, DeviceStream.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeDeviceAssignmentState
     * (com.sitewhere .spi.device.IDeviceAssignmentState)
     */
    @Override
    public byte[] encodeDeviceAssignmentState(IDeviceAssignmentState state) throws SiteWhereException {
	return MarshalUtils.marshalJson(state);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeDeviceAssignmentState
     * (byte[])
     */
    @Override
    public DeviceAssignmentState decodeDeviceAssignmentState(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, DeviceAssignmentState.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeDeviceMeasurements(
     * com.sitewhere .spi.device.event.IDeviceMeasurements)
     */
    @Override
    public byte[] encodeDeviceMeasurements(IDeviceMeasurements measurements) throws SiteWhereException {
	return MarshalUtils.marshalJson(measurements);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeDeviceMeasurements(
     * byte[])
     */
    @Override
    public DeviceMeasurements decodeDeviceMeasurements(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, DeviceMeasurements.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeDeviceLocation(com.
     * sitewhere .spi.device.event.IDeviceLocation)
     */
    @Override
    public byte[] encodeDeviceLocation(IDeviceLocation location) throws SiteWhereException {
	return MarshalUtils.marshalJson(location);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeDeviceLocation(byte[] )
     */
    @Override
    public DeviceLocation decodeDeviceLocation(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, DeviceLocation.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeDeviceAlert(com.
     * sitewhere.spi .device.event.IDeviceAlert)
     */
    @Override
    public byte[] encodeDeviceAlert(IDeviceAlert alert) throws SiteWhereException {
	return MarshalUtils.marshalJson(alert);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeDeviceAlert(byte[])
     */
    @Override
    public DeviceAlert decodeDeviceAlert(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, DeviceAlert.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeDeviceStreamData(com.
     * sitewhere .spi.device.event.IDeviceStreamData)
     */
    @Override
    public byte[] encodeDeviceStreamData(IDeviceStreamData streamData) throws SiteWhereException {
	return MarshalUtils.marshalJson(streamData);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeDeviceStreamData(byte [])
     */
    @Override
    public DeviceStreamData decodeDeviceStreamData(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, DeviceStreamData.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#
     * encodeDeviceCommandInvocation(com
     * .sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public byte[] encodeDeviceCommandInvocation(IDeviceCommandInvocation invocation) throws SiteWhereException {
	return MarshalUtils.marshalJson(invocation);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#
     * decodeDeviceCommandInvocation(byte[])
     */
    @Override
    public DeviceCommandInvocation decodeDeviceCommandInvocation(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, DeviceCommandInvocation.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeDeviceStateChange(com
     * .sitewhere .spi.device.event.IDeviceStateChange)
     */
    @Override
    public byte[] encodeDeviceStateChange(IDeviceStateChange change) throws SiteWhereException {
	return MarshalUtils.marshalJson(change);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeDeviceStateChange(
     * byte[])
     */
    @Override
    public DeviceStateChange decodeDeviceStateChange(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, DeviceStateChange.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeDeviceCommandResponse
     * (com.sitewhere .spi.device.event.IDeviceCommandResponse)
     */
    @Override
    public byte[] encodeDeviceCommandResponse(IDeviceCommandResponse response) throws SiteWhereException {
	return MarshalUtils.marshalJson(response);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeDeviceCommandResponse
     * (byte[])
     */
    @Override
    public DeviceCommandResponse decodeDeviceCommandResponse(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, DeviceCommandResponse.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeBatchOperation(com.
     * sitewhere .spi.device.batch.IBatchOperation)
     */
    @Override
    public byte[] encodeBatchOperation(IBatchOperation operation) throws SiteWhereException {
	return MarshalUtils.marshalJson(operation);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeBatchOperation(byte[] )
     */
    @Override
    public BatchOperation decodeBatchOperation(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, BatchOperation.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeBatchElement(com.
     * sitewhere. spi.device.batch.IBatchElement)
     */
    @Override
    public byte[] encodeBatchElement(IBatchElement element) throws SiteWhereException {
	return MarshalUtils.marshalJson(element);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeBatchElement(byte[])
     */
    @Override
    public BatchElement decodeBatchElement(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, BatchElement.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeDeviceGroup(com.
     * sitewhere.spi .device.group.IDeviceGroup)
     */
    @Override
    public byte[] encodeDeviceGroup(IDeviceGroup group) throws SiteWhereException {
	return MarshalUtils.marshalJson(group);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeDeviceGroup(byte[])
     */
    @Override
    public DeviceGroup decodeDeviceGroup(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, DeviceGroup.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeDeviceGroupElement(
     * com.sitewhere .spi.device.group.IDeviceGroupElement)
     */
    @Override
    public byte[] encodeDeviceGroupElement(IDeviceGroupElement element) throws SiteWhereException {
	return MarshalUtils.marshalJson(element);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeDeviceGroupElement(
     * byte[])
     */
    @Override
    public DeviceGroupElement decodeDeviceGroupElement(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, DeviceGroupElement.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeDeviceCommand(com.
     * sitewhere .spi.device.command.IDeviceCommand)
     */
    @Override
    public byte[] encodeDeviceCommand(IDeviceCommand command) throws SiteWhereException {
	return MarshalUtils.marshalJson(command);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeDeviceCommand(byte[])
     */
    @Override
    public DeviceCommand decodeDeviceCommand(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, DeviceCommand.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeUser(com.sitewhere.
     * spi.user .IUser)
     */
    @Override
    public byte[] encodeUser(IUser user) throws SiteWhereException {
	return MarshalUtils.marshalJson(user);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeUser(byte[])
     */
    @Override
    public User decodeUser(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, User.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeGrantedAuthority(com.
     * sitewhere .spi.user.IGrantedAuthority)
     */
    @Override
    public byte[] encodeGrantedAuthority(IGrantedAuthority auth) throws SiteWhereException {
	return MarshalUtils.marshalJson(auth);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeGrantedAuthority(byte [])
     */
    @Override
    public GrantedAuthority decodeGrantedAuthority(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, GrantedAuthority.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeAssetCategory(com.
     * sitewhere .spi.asset.IAssetCategory)
     */
    @Override
    public byte[] encodeAssetCategory(IAssetCategory category) throws SiteWhereException {
	return MarshalUtils.marshalJson(category);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeAssetCategory(byte[])
     */
    @Override
    public AssetCategory decodeAssetCategory(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, AssetCategory.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#encodePersonAsset(com.
     * sitewhere.spi .asset.IPersonAsset)
     */
    @Override
    public byte[] encodePersonAsset(IPersonAsset asset) throws SiteWhereException {
	return MarshalUtils.marshalJson(asset);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#decodePersonAsset(byte[])
     */
    @Override
    public PersonAsset decodePersonAsset(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, PersonAsset.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeHardwareAsset(com.
     * sitewhere .spi.asset.IHardwareAsset)
     */
    @Override
    public byte[] encodeHardwareAsset(IHardwareAsset asset) throws SiteWhereException {
	return MarshalUtils.marshalJson(asset);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeHardwareAsset(byte[])
     */
    @Override
    public HardwareAsset decodeHardwareAsset(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, HardwareAsset.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeLocationAsset(com.
     * sitewhere .spi.asset.ILocationAsset)
     */
    @Override
    public byte[] encodeLocationAsset(ILocationAsset asset) throws SiteWhereException {
	return MarshalUtils.marshalJson(asset);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeLocationAsset(byte[])
     */
    @Override
    public LocationAsset decodeLocationAsset(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, LocationAsset.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.hbase.encoder.IPayloadMarshaler#encodeTenant(com.sitewhere.
     * spi.user .ITenant)
     */
    @Override
    public byte[] encodeTenant(ITenant tenant) throws SiteWhereException {
	return MarshalUtils.marshalJson(tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#decodeTenant(byte[])
     */
    @Override
    public Tenant decodeTenant(byte[] payload) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(payload, Tenant.class);
    }
}