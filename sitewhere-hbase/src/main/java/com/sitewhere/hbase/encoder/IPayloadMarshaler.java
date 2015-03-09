/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.encoder;

import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceSpecification;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.rest.model.device.batch.BatchElement;
import com.sitewhere.rest.model.device.batch.BatchOperation;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.rest.model.device.event.DeviceStateChange;
import com.sitewhere.rest.model.device.group.DeviceGroupElement;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceAssignmentState;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.IZone;
import com.sitewhere.spi.device.batch.IBatchElement;
import com.sitewhere.spi.device.batch.IBatchOperation;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;

/**
 * Interface for classes that can encode SiteWhere objects into byte arrays.
 * 
 * @author Derek
 */
public interface IPayloadMarshaler {

	/**
	 * Gets encoding type for the encoder.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public PayloadEncoding getEncoding() throws SiteWhereException;

	/**
	 * Encode an object.
	 * 
	 * @param obj
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] encode(Object obj) throws SiteWhereException;

	/**
	 * Decode a payload into an object.
	 * 
	 * @param payload
	 * @param type
	 * @return
	 * @throws SiteWhereException
	 */
	public <T> T decode(byte[] payload, Class<T> type) throws SiteWhereException;

	/**
	 * Encode an {@link ISite}.
	 * 
	 * @param site
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] encodeSite(ISite site) throws SiteWhereException;

	/**
	 * Decode a {@link Site} from the binary payload.
	 * 
	 * @param payload
	 * @return
	 * @throws SiteWhereException
	 */
	public Site decodeSite(byte[] payload) throws SiteWhereException;

	/**
	 * Encode an {@link IZone}.
	 * 
	 * @param zone
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] encodeZone(IZone zone) throws SiteWhereException;

	/**
	 * Decode a {@link Zone} from the binary payload.
	 * 
	 * @param payload
	 * @return
	 * @throws SiteWhereException
	 */
	public Zone decodeZone(byte[] payload) throws SiteWhereException;

	/**
	 * Encode an {@link IDeviceSpecification}.
	 * 
	 * @param specification
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] encodeDeviceSpecification(IDeviceSpecification specification) throws SiteWhereException;

	/**
	 * Decode a {@link DeviceSpecification} from the binary payload.
	 * 
	 * @param payload
	 * @return
	 * @throws SiteWhereException
	 */
	public DeviceSpecification decodeDeviceSpecification(byte[] payload) throws SiteWhereException;

	/**
	 * Encode an {@link IDevice}.
	 * 
	 * @param device
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] encodeDevice(IDevice device) throws SiteWhereException;

	/**
	 * Decodea {@link Device} from the binary payload.
	 * 
	 * @param payload
	 * @return
	 * @throws SiteWhereException
	 */
	public Device decodeDevice(byte[] payload) throws SiteWhereException;

	/**
	 * Encode an {@link IDeviceAssignment}.
	 * 
	 * @param assignment
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] encodeDeviceAssignment(IDeviceAssignment assignment) throws SiteWhereException;

	/**
	 * Decode a {@link DeviceAssignment} from the binary payload.
	 * 
	 * @param payload
	 * @return
	 * @throws SiteWhereException
	 */
	public DeviceAssignment decodeDeviceAssignment(byte[] payload) throws SiteWhereException;

	/**
	 * Encode an {@link IDeviceAssignmentState}.
	 * 
	 * @param state
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] encodeDeviceAssignmentState(IDeviceAssignmentState state) throws SiteWhereException;

	/**
	 * Encode an {@link IDeviceMeasurements}.
	 * 
	 * @param measurements
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] encodeDeviceMeasurements(IDeviceMeasurements measurements) throws SiteWhereException;

	/**
	 * Decode a {@link DeviceMeasurements} from the binary payload.
	 * 
	 * @param payload
	 * @return
	 * @throws SiteWhereException
	 */
	public DeviceMeasurements decodeDeviceMeasurements(byte[] payload) throws SiteWhereException;

	/**
	 * Encode an {@link IDeviceLocation}.
	 * 
	 * @param location
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] encodeDeviceLocation(IDeviceLocation location) throws SiteWhereException;

	/**
	 * Decode a {@link DeviceLocation} from the binary payload.
	 * 
	 * @param payload
	 * @return
	 * @throws SiteWhereException
	 */
	public DeviceLocation decodeDeviceLocation(byte[] payload) throws SiteWhereException;

	/**
	 * Encode an {@link IDeviceAlert}.
	 * 
	 * @param alert
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] encodeDeviceAlert(IDeviceAlert alert) throws SiteWhereException;

	/**
	 * Decode a {@link DeviceAlert} from the binary payload.
	 * 
	 * @param payload
	 * @return
	 * @throws SiteWhereException
	 */
	public DeviceAlert decodeDeviceAlert(byte[] payload) throws SiteWhereException;

	/**
	 * Encode an {@link IDeviceCommandInvocation}.
	 * 
	 * @param invocation
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] encodeDeviceCommandInvocation(IDeviceCommandInvocation invocation)
			throws SiteWhereException;

	/**
	 * Decode a {@link DeviceCommandInvocation} from the binary payload.
	 * 
	 * @param payload
	 * @return
	 * @throws SiteWhereException
	 */
	public DeviceCommandInvocation decodeDeviceCommandInvocation(byte[] payload) throws SiteWhereException;

	/**
	 * Encode an {@link IDeviceStateChange}.
	 * 
	 * @param change
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] encodeDeviceStateChange(IDeviceStateChange change) throws SiteWhereException;

	/**
	 * Decode a {@link DeviceStateChange} from the binary payload.
	 * 
	 * @param payload
	 * @return
	 * @throws SiteWhereException
	 */
	public DeviceStateChange decodeDeviceStateChange(byte[] payload) throws SiteWhereException;

	/**
	 * Encode an {@link IDeviceCommandResponse}.
	 * 
	 * @param response
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] encodeDeviceCommandResponse(IDeviceCommandResponse response) throws SiteWhereException;

	/**
	 * Decode a {@link DeviceCommandResponse} from the binary payload.
	 * 
	 * @param payload
	 * @return
	 * @throws SiteWhereException
	 */
	public DeviceCommandResponse decodeDeviceCommandResponse(byte[] payload) throws SiteWhereException;

	/**
	 * Encode an {@link IBatchOperation}.
	 * 
	 * @param operation
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] encodeBatchOperation(IBatchOperation operation) throws SiteWhereException;

	/**
	 * Decode a {@link BatchOperation} from the binary payload.
	 * 
	 * @param payload
	 * @return
	 * @throws SiteWhereException
	 */
	public BatchOperation decodeBatchOperation(byte[] payload) throws SiteWhereException;

	/**
	 * Encode an {@link IBatchElement}.
	 * 
	 * @param element
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] encodeBatchElement(IBatchElement element) throws SiteWhereException;

	/**
	 * Decode a {@link BatchElement} from the binary payload.
	 * 
	 * @param payload
	 * @return
	 * @throws SiteWhereException
	 */
	public BatchElement decodeBatchElement(byte[] payload) throws SiteWhereException;

	/**
	 * Encode an {@link IDeviceGroupElement}.
	 * 
	 * @param element
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] encodeDeviceGroupElement(IDeviceGroupElement element) throws SiteWhereException;

	/**
	 * Decode a {@link DeviceGroupElement} from the binary payload.
	 * 
	 * @param payload
	 * @return
	 * @throws SiteWhereException
	 */
	public DeviceGroupElement decodeDeviceGroupElement(byte[] payload) throws SiteWhereException;

	/**
	 * Encode an {@link IDeviceCommand}.
	 * 
	 * @param command
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] encodeDeviceCommand(IDeviceCommand command) throws SiteWhereException;

	/**
	 * Decode a {@link DeviceCommand} from the binary payload.
	 * 
	 * @param payload
	 * @return
	 * @throws SiteWhereException
	 */
	public DeviceCommand decodeDeviceCommand(byte[] payload) throws SiteWhereException;

	/**
	 * Encode an {@link IUser}.
	 * 
	 * @param user
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] encodeUser(IUser user) throws SiteWhereException;

	/**
	 * Decode a {@link UserError} from the binary payload.
	 * 
	 * @param payload
	 * @return
	 * @throws SiteWhereException
	 */
	public User decodeUser(byte[] payload) throws SiteWhereException;

	/**
	 * Encode an {@link IGrantedAuthority}.
	 * 
	 * @param auth
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] encodeGrantedAuthority(IGrantedAuthority auth) throws SiteWhereException;

	/**
	 * Encode a {@link GrantedAuthority} from the binary payload.
	 * 
	 * @param payload
	 * @return
	 * @throws SiteWhereException
	 */
	public GrantedAuthority decodeGrantedAuthority(byte[] payload) throws SiteWhereException;
}