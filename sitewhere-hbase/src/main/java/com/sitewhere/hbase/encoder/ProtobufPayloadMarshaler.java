/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.encoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceAssignmentState;
import com.sitewhere.rest.model.device.DeviceElementMapping;
import com.sitewhere.rest.model.device.DeviceSpecification;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.SiteMapData;
import com.sitewhere.rest.model.device.command.CommandParameter;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.element.DeviceElementSchema;
import com.sitewhere.rest.model.device.element.DeviceSlot;
import com.sitewhere.rest.model.device.element.DeviceUnit;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceEvent;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.common.IMetadataProviderEntity;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.DeviceContainerPolicy;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceAssignmentState;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.ParameterType;
import com.sitewhere.spi.device.element.IDeviceElementSchema;
import com.sitewhere.spi.device.element.IDeviceSlot;
import com.sitewhere.spi.device.element.IDeviceUnit;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.AlertSource;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceMeasurements;

/**
 * Implementation of {@link IPayloadMarshaler} that uses Google Protocol Buffers to store
 * data in a binary format.
 * 
 * @author Derek
 */
public class ProtobufPayloadMarshaler extends JsonPayloadMarshaler implements IPayloadMarshaler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.hbase.encoder.IPayloadMarshaler#getEncoding()
	 */
	@Override
	public PayloadEncoding getEncoding() throws SiteWhereException {
		return PayloadEncoding.ProtocolBuffers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.hbase.encoder.JsonPayloadMarshaler#encode(java.lang.Object)
	 */
	@Override
	public byte[] encode(Object obj) throws SiteWhereException {
		if (obj instanceof ISite) {
			return encodeSite((ISite) obj);
		} else if (obj instanceof IDeviceSpecification) {
			return encodeDeviceSpecification((IDeviceSpecification) obj);
		} else if (obj instanceof IDeviceCommand) {
			return encodeDeviceCommand((IDeviceCommand) obj);
		} else if (obj instanceof IDevice) {
			return encodeDevice((IDevice) obj);
		} else if (obj instanceof IDeviceAssignment) {
			return encodeDeviceAssignment((IDeviceAssignment) obj);
		} else if (obj instanceof IDeviceAssignmentState) {
			return encodeDeviceAssignmentState((IDeviceAssignmentState) obj);
		} else if (obj instanceof IDeviceLocation) {
			return encodeDeviceLocation((IDeviceLocation) obj);
		} else if (obj instanceof IDeviceMeasurements) {
			return encodeDeviceMeasurements((IDeviceMeasurements) obj);
		} else if (obj instanceof IDeviceAlert) {
			return encodeDeviceAlert((IDeviceAlert) obj);
		}
		return super.encode(obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.hbase.encoder.JsonPayloadMarshaler#decode(byte[],
	 * java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T decode(byte[] payload, Class<T> type) throws SiteWhereException {
		if (ISite.class.isAssignableFrom(type)) {
			return (T) decodeSite(payload);
		}
		if (IDeviceSpecification.class.isAssignableFrom(type)) {
			return (T) decodeDeviceSpecification(payload);
		}
		if (IDeviceCommand.class.isAssignableFrom(type)) {
			return (T) decodeDeviceCommand(payload);
		}
		if (IDevice.class.isAssignableFrom(type)) {
			return (T) decodeDevice(payload);
		}
		if (IDeviceAssignment.class.isAssignableFrom(type)) {
			return (T) decodeDeviceAssignment(payload);
		}
		if (IDeviceAssignmentState.class.isAssignableFrom(type)) {
			return (T) decodeDeviceAssignmentState(payload);
		}
		if (IDeviceLocation.class.isAssignableFrom(type)) {
			return (T) decodeDeviceLocation(payload);
		}
		if (IDeviceMeasurements.class.isAssignableFrom(type)) {
			return (T) decodeDeviceMeasurements(payload);
		}
		if (IDeviceAlert.class.isAssignableFrom(type)) {
			return (T) decodeDeviceAlert(payload);
		}
		return super.decode(payload, type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.hbase.encoder.JsonPayloadMarshaler#encodeSite(com.sitewhere.spi.device
	 * .ISite)
	 */
	@Override
	public byte[] encodeSite(ISite site) throws SiteWhereException {
		ProtobufMarshaler.Site.Builder builder = ProtobufMarshaler.Site.newBuilder();
		builder.setToken(site.getToken());
		builder.setName(site.getName());
		builder.setDescription(site.getDescription());
		builder.setImageUrl(site.getImageUrl());
		builder.setMapType(site.getMap().getType());
		List<ProtobufMarshaler.MetadataEntry> mapEntries = asMetadataEntries(site.getMap().getMetadata());
		builder.addAllMapMetadata(mapEntries);
		builder.setEntityData(createEntityData(site));
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			builder.build().writeTo(out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new SiteWhereException("Unable to marshal site.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.hbase.encoder.JsonPayloadMarshaler#decodeSite(byte[])
	 */
	@Override
	public Site decodeSite(byte[] payload) throws SiteWhereException {
		ByteArrayInputStream stream = new ByteArrayInputStream(payload);
		try {
			ProtobufMarshaler.Site pb = ProtobufMarshaler.Site.parseFrom(stream);
			Site site = new Site();
			site.setToken(pb.getToken());
			site.setName(pb.getName());
			site.setDescription(pb.getDescription());
			site.setImageUrl(pb.getImageUrl());
			SiteMapData map = new SiteMapData();
			map.setType(pb.getMapType());
			for (ProtobufMarshaler.MetadataEntry entry : pb.getMapMetadataList()) {
				map.addOrReplaceMetadata(entry.getName(), entry.getValue());
			}
			site.setMap(map);
			loadEntityData(pb.getEntityData(), site);
			return site;
		} catch (IOException e) {
			throw new SiteWhereException("Unable to unmarshal site.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.hbase.encoder.JsonPayloadMarshaler#encodeDeviceSpecification(com.
	 * sitewhere.spi.device.IDeviceSpecification)
	 */
	@Override
	public byte[] encodeDeviceSpecification(IDeviceSpecification spec) throws SiteWhereException {
		ProtobufMarshaler.DeviceSpecification.Builder builder =
				ProtobufMarshaler.DeviceSpecification.newBuilder();
		builder.setToken(spec.getToken());
		builder.setName(spec.getName());
		builder.setAssetModuleId(spec.getAssetModuleId());
		builder.setAssetId(spec.getAssetId());
		builder.setContainerPolicy(spec.getContainerPolicy().name());
		if (spec.getDeviceElementSchema() != null) {
			builder.setDeviceElementSchema(encodeDeviceElementSchema(spec.getDeviceElementSchema()));
		}
		builder.setEntityData(createEntityData(spec));
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			builder.build().writeTo(out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new SiteWhereException("Unable to marshal device specification.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.hbase.encoder.JsonPayloadMarshaler#decodeDeviceSpecification(byte[])
	 */
	@Override
	public DeviceSpecification decodeDeviceSpecification(byte[] payload) throws SiteWhereException {
		ByteArrayInputStream stream = new ByteArrayInputStream(payload);
		try {
			ProtobufMarshaler.DeviceSpecification pb =
					ProtobufMarshaler.DeviceSpecification.parseFrom(stream);
			DeviceSpecification spec = new DeviceSpecification();
			spec.setToken(pb.getToken());
			spec.setName(pb.getName());
			spec.setAssetModuleId(pb.getAssetModuleId());
			spec.setAssetId(pb.getAssetId());
			spec.setContainerPolicy(DeviceContainerPolicy.valueOf(pb.getContainerPolicy()));
			if (pb.hasDeviceElementSchema()) {
				spec.setDeviceElementSchema(decodeDeviceElementSchema(pb.getDeviceElementSchema()));
			}
			loadEntityData(pb.getEntityData(), spec);
			return spec;
		} catch (IOException e) {
			throw new SiteWhereException("Unable to unmarshal device specification.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.hbase.encoder.JsonPayloadMarshaler#encodeDeviceCommand(com.sitewhere
	 * .spi.device.command.IDeviceCommand)
	 */
	@Override
	public byte[] encodeDeviceCommand(IDeviceCommand command) throws SiteWhereException {
		ProtobufMarshaler.DeviceCommand.Builder builder = ProtobufMarshaler.DeviceCommand.newBuilder();
		builder.setToken(command.getToken());
		builder.setSpecificationToken(command.getSpecificationToken());
		builder.setName(command.getName());
		builder.setNamespace(command.getNamespace());
		builder.setDescription(command.getDescription());
		for (ICommandParameter parameter : command.getParameters()) {
			ProtobufMarshaler.CommandParameter.Builder cbuilder =
					ProtobufMarshaler.CommandParameter.newBuilder();
			cbuilder.setName(parameter.getName());
			cbuilder.setType(parameter.getType().name());
			cbuilder.setRequired(parameter.isRequired());
			builder.addParameters(cbuilder.build());
		}
		builder.setEntityData(createEntityData(command));
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			builder.build().writeTo(out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new SiteWhereException("Unable to marshal device command.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.hbase.encoder.JsonPayloadMarshaler#decodeDeviceCommand(byte[])
	 */
	@Override
	public DeviceCommand decodeDeviceCommand(byte[] payload) throws SiteWhereException {
		ByteArrayInputStream stream = new ByteArrayInputStream(payload);
		try {
			ProtobufMarshaler.DeviceCommand pb = ProtobufMarshaler.DeviceCommand.parseFrom(stream);
			DeviceCommand command = new DeviceCommand();
			command.setToken(pb.getToken());
			command.setSpecificationToken(pb.getSpecificationToken());
			command.setName(pb.getName());
			command.setNamespace(pb.getNamespace());
			command.setDescription(pb.getDescription());
			for (ProtobufMarshaler.CommandParameter pbparam : pb.getParametersList()) {
				CommandParameter param = new CommandParameter();
				param.setName(pbparam.getName());
				param.setType(ParameterType.valueOf(pbparam.getType()));
				param.setRequired(pbparam.getRequired());
				command.getParameters().add(param);
			}
			loadEntityData(pb.getEntityData(), command);
			return command;
		} catch (IOException e) {
			throw new SiteWhereException("Unable to unmarshal device command.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.hbase.encoder.JsonPayloadMarshaler#encodeDevice(com.sitewhere.spi
	 * .device.IDevice)
	 */
	@Override
	public byte[] encodeDevice(IDevice device) throws SiteWhereException {
		ProtobufMarshaler.Device.Builder builder = ProtobufMarshaler.Device.newBuilder();
		builder.setHardwareId(device.getHardwareId());
		builder.setSiteToken(device.getSiteToken());
		builder.setSpecificationToken(device.getSpecificationToken());
		if (device.getParentHardwareId() != null) {
			builder.setParentHardwareId(device.getParentHardwareId());
		}
		for (IDeviceElementMapping mapping : device.getDeviceElementMappings()) {
			ProtobufMarshaler.DeviceElementMapping.Builder ebuilder =
					ProtobufMarshaler.DeviceElementMapping.newBuilder();
			ebuilder.setDeviceElementSchemaPath(mapping.getDeviceElementSchemaPath());
			ebuilder.setHardwareId(mapping.getHardwareId());
			builder.addDeviceElementMappings(ebuilder.build());
		}
		if (device.getAssignmentToken() != null) {
			builder.setAssignmentToken(device.getAssignmentToken());
		}
		if (device.getComments() != null) {
			builder.setComments(device.getComments());
		}
		builder.setEntityData(createEntityData(device));
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			builder.build().writeTo(out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new SiteWhereException("Unable to marshal device.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.hbase.encoder.JsonPayloadMarshaler#decodeDevice(byte[])
	 */
	@Override
	public Device decodeDevice(byte[] payload) throws SiteWhereException {
		ByteArrayInputStream stream = new ByteArrayInputStream(payload);
		try {
			ProtobufMarshaler.Device pb = ProtobufMarshaler.Device.parseFrom(stream);
			Device device = new Device();
			device.setHardwareId(pb.getHardwareId());
			device.setSiteToken(pb.getSiteToken());
			device.setSpecificationToken(pb.getSpecificationToken());
			if (pb.hasParentHardwareId()) {
				device.setParentHardwareId(pb.getParentHardwareId());
			}
			for (ProtobufMarshaler.DeviceElementMapping pbmapping : pb.getDeviceElementMappingsList()) {
				DeviceElementMapping mapping = new DeviceElementMapping();
				mapping.setDeviceElementSchemaPath(pbmapping.getDeviceElementSchemaPath());
				mapping.setHardwareId(pbmapping.getHardwareId());
				device.getDeviceElementMappings().add(mapping);
			}
			if (pb.hasAssignmentToken()) {
				device.setAssignmentToken(pb.getAssignmentToken());
			}
			if (pb.hasComments()) {
				device.setComments(pb.getComments());
			}
			loadEntityData(pb.getEntityData(), device);
			return device;
		} catch (IOException e) {
			throw new SiteWhereException("Unable to unmarshal device command.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.hbase.encoder.JsonPayloadMarshaler#encodeDeviceLocation(com.sitewhere
	 * .spi.device.event.IDeviceLocation)
	 */
	@Override
	public byte[] encodeDeviceLocation(IDeviceLocation location) throws SiteWhereException {
		try {
			ProtobufMarshaler.DeviceLocation pbloc = marshalDeviceLocation(location);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			pbloc.writeTo(out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new SiteWhereException("Unable to marshal device location.", e);
		}
	}

	/**
	 * Marshal an {@link IDeviceLocation}.
	 * 
	 * @param location
	 * @return
	 * @throws SiteWhereException
	 */
	protected ProtobufMarshaler.DeviceLocation marshalDeviceLocation(IDeviceLocation location)
			throws SiteWhereException {
		ProtobufMarshaler.DeviceLocation.Builder builder = ProtobufMarshaler.DeviceLocation.newBuilder();
		builder.setLatitude(location.getLatitude());
		builder.setLongitude(location.getLongitude());
		builder.setElevation(location.getElevation());
		builder.setEventData(createDeviceEventData(location));
		return builder.build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.hbase.encoder.JsonPayloadMarshaler#decodeDeviceLocation(byte[])
	 */
	@Override
	public DeviceLocation decodeDeviceLocation(byte[] payload) throws SiteWhereException {
		ByteArrayInputStream stream = new ByteArrayInputStream(payload);
		try {
			ProtobufMarshaler.DeviceLocation pb = ProtobufMarshaler.DeviceLocation.parseFrom(stream);
			return unmarshalDeviceLocation(pb);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to unmarshal device location.", e);
		}
	}

	/**
	 * Unmarshal a {@link DeviceLocation}.
	 * 
	 * @param pb
	 * @return
	 * @throws SiteWhereException
	 */
	protected DeviceLocation unmarshalDeviceLocation(ProtobufMarshaler.DeviceLocation pb)
			throws SiteWhereException {
		DeviceLocation location = new DeviceLocation();
		location.setLatitude(pb.getLatitude());
		location.setLongitude(pb.getLongitude());
		location.setElevation(pb.getElevation());
		loadDeviceEventData(location, pb.getEventData());
		return location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.hbase.encoder.JsonPayloadMarshaler#encodeDeviceMeasurements(com.sitewhere
	 * .spi.device.event.IDeviceMeasurements)
	 */
	@Override
	public byte[] encodeDeviceMeasurements(IDeviceMeasurements measurements) throws SiteWhereException {
		ProtobufMarshaler.DeviceMeasurements.Builder builder =
				ProtobufMarshaler.DeviceMeasurements.newBuilder();
		for (String key : measurements.getMeasurements().keySet()) {
			ProtobufMarshaler.DeviceMeasurement.Builder mbuilder =
					ProtobufMarshaler.DeviceMeasurement.newBuilder();
			mbuilder.setName(key);
			mbuilder.setValue(measurements.getMeasurement(key));
			builder.addMeasurements(mbuilder.build());
		}
		builder.setEventData(createDeviceEventData(measurements));
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			builder.build().writeTo(out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new SiteWhereException("Unable to marshal device measurements.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.hbase.encoder.JsonPayloadMarshaler#decodeDeviceMeasurements(byte[])
	 */
	@Override
	public DeviceMeasurements decodeDeviceMeasurements(byte[] payload) throws SiteWhereException {
		ByteArrayInputStream stream = new ByteArrayInputStream(payload);
		try {
			ProtobufMarshaler.DeviceMeasurements pb = ProtobufMarshaler.DeviceMeasurements.parseFrom(stream);
			DeviceMeasurements measurements = new DeviceMeasurements();
			for (ProtobufMarshaler.DeviceMeasurement pbmx : pb.getMeasurementsList()) {
				measurements.addOrReplaceMeasurement(pbmx.getName(), pbmx.getValue());
			}
			loadDeviceEventData(measurements, pb.getEventData());
			return measurements;
		} catch (IOException e) {
			throw new SiteWhereException("Unable to unmarshal device measurements.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.hbase.encoder.JsonPayloadMarshaler#encodeDeviceAlert(com.sitewhere
	 * .spi.device.event.IDeviceAlert)
	 */
	@Override
	public byte[] encodeDeviceAlert(IDeviceAlert alert) throws SiteWhereException {
		try {
			ProtobufMarshaler.DeviceAlert pbalert = marshalDeviceAlert(alert);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			pbalert.writeTo(out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new SiteWhereException("Unable to marshal device alert.", e);
		}
	}

	/**
	 * Marshal an {@link IDeviceAlert}.
	 * 
	 * @param alert
	 * @return
	 * @throws SiteWhereException
	 */
	protected ProtobufMarshaler.DeviceAlert marshalDeviceAlert(IDeviceAlert alert) throws SiteWhereException {
		ProtobufMarshaler.DeviceAlert.Builder builder = ProtobufMarshaler.DeviceAlert.newBuilder();
		builder.setSource(alert.getSource().name());
		builder.setLevel(alert.getLevel().name());
		builder.setType(alert.getType());
		builder.setMessage(alert.getMessage());
		builder.setEventData(createDeviceEventData(alert));
		return builder.build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.hbase.encoder.JsonPayloadMarshaler#decodeDeviceAlert(byte[])
	 */
	@Override
	public DeviceAlert decodeDeviceAlert(byte[] payload) throws SiteWhereException {
		ByteArrayInputStream stream = new ByteArrayInputStream(payload);
		try {
			ProtobufMarshaler.DeviceAlert pb = ProtobufMarshaler.DeviceAlert.parseFrom(stream);
			return unmarshalDeviceAlert(pb);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to unmarshal device alert.", e);
		}
	}

	/**
	 * Unmarshal a {@link DeviceAlert}.
	 * 
	 * @param pb
	 * @return
	 * @throws SiteWhereException
	 */
	protected DeviceAlert unmarshalDeviceAlert(ProtobufMarshaler.DeviceAlert pb) throws SiteWhereException {
		DeviceAlert alert = new DeviceAlert();
		alert.setSource(AlertSource.valueOf(pb.getSource()));
		alert.setLevel(AlertLevel.valueOf(pb.getLevel()));
		alert.setType(pb.getType());
		alert.setMessage(pb.getMessage());
		loadDeviceEventData(alert, pb.getEventData());
		return alert;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.hbase.encoder.JsonPayloadMarshaler#encodeDeviceAssignment(com.sitewhere
	 * .spi.device.IDeviceAssignment)
	 */
	@Override
	public byte[] encodeDeviceAssignment(IDeviceAssignment assignment) throws SiteWhereException {
		ProtobufMarshaler.DeviceAssignment.Builder builder = ProtobufMarshaler.DeviceAssignment.newBuilder();
		builder.setToken(assignment.getToken());
		builder.setDeviceHardwareId(assignment.getDeviceHardwareId());
		builder.setSiteToken(assignment.getSiteToken());
		builder.setAssignmentType(assignment.getAssignmentType().name());
		builder.setAssetModuleId(assignment.getAssetModuleId());
		builder.setAssetId(assignment.getAssetId());
		builder.setStatus(assignment.getStatus().name());
		if (assignment.getActiveDate() != null) {
			builder.setActiveDate(assignment.getActiveDate().getTime());
		}
		if (assignment.getReleasedDate() != null) {
			builder.setReleasedDate(assignment.getReleasedDate().getTime());
		}
		builder.setEntityData(createEntityData(assignment));
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			builder.build().writeTo(out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new SiteWhereException("Unable to marshal device assignment.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.hbase.encoder.JsonPayloadMarshaler#decodeDeviceAssignment(byte[])
	 */
	@Override
	public DeviceAssignment decodeDeviceAssignment(byte[] payload) throws SiteWhereException {
		ByteArrayInputStream stream = new ByteArrayInputStream(payload);
		try {
			ProtobufMarshaler.DeviceAssignment pb = ProtobufMarshaler.DeviceAssignment.parseFrom(stream);
			DeviceAssignment assignment = new DeviceAssignment();
			assignment.setToken(pb.getToken());
			assignment.setDeviceHardwareId(pb.getDeviceHardwareId());
			assignment.setSiteToken(pb.getSiteToken());
			assignment.setAssignmentType(DeviceAssignmentType.valueOf(pb.getAssignmentType()));
			assignment.setAssetModuleId(pb.getAssetModuleId());
			assignment.setAssetId(pb.getAssetId());
			assignment.setStatus(DeviceAssignmentStatus.valueOf(pb.getStatus()));
			if (pb.hasActiveDate()) {
				assignment.setActiveDate(new Date(pb.getActiveDate()));
			}
			if (pb.hasReleasedDate()) {
				assignment.setReleasedDate(new Date(pb.getReleasedDate()));
			}
			loadEntityData(pb.getEntityData(), assignment);
			return assignment;
		} catch (IOException e) {
			throw new SiteWhereException("Unable to unmarshal device assignment.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.hbase.encoder.JsonPayloadMarshaler#encodeDeviceAssignmentState(com
	 * .sitewhere.spi.device.IDeviceAssignmentState)
	 */
	@Override
	public byte[] encodeDeviceAssignmentState(IDeviceAssignmentState state) throws SiteWhereException {
		ProtobufMarshaler.DeviceAssignmentState.Builder builder =
				ProtobufMarshaler.DeviceAssignmentState.newBuilder();
		if (state.getLastInteractionDate() != null) {
			builder.setLastInteractionDate(state.getLastInteractionDate().getTime());
		}
		if (state.getLastLocation() != null) {
			builder.setLastLocation(marshalDeviceLocation(state.getLastLocation()));
		}
		if (state.getLatestMeasurements() != null) {
			for (IDeviceMeasurement mx : state.getLatestMeasurements()) {
				ProtobufMarshaler.DeviceMeasurement.Builder mbuilder =
						ProtobufMarshaler.DeviceMeasurement.newBuilder();
				mbuilder.setName(mx.getName());
				mbuilder.setValue(mx.getValue());
				mbuilder.setEventData(createDeviceEventData(mx));
				builder.addLatestMeasurements(mbuilder.build());
			}
		}
		if (state.getLatestAlerts() != null) {
			for (IDeviceAlert alert : state.getLatestAlerts()) {
				builder.addLatestAlerts(marshalDeviceAlert(alert));
			}
		}
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			builder.build().writeTo(out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new SiteWhereException("Unable to marshal device assignment state.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.hbase.encoder.JsonPayloadMarshaler#decodeDeviceAssignmentState(byte
	 * [])
	 */
	@Override
	public DeviceAssignmentState decodeDeviceAssignmentState(byte[] payload) throws SiteWhereException {
		ByteArrayInputStream stream = new ByteArrayInputStream(payload);
		try {
			ProtobufMarshaler.DeviceAssignmentState pb =
					ProtobufMarshaler.DeviceAssignmentState.parseFrom(stream);
			DeviceAssignmentState state = new DeviceAssignmentState();
			if (pb.hasLastInteractionDate()) {
				state.setLastInteractionDate(new Date(pb.getLastInteractionDate()));
			}
			if (pb.hasLastLocation()) {
				state.setLastLocation(unmarshalDeviceLocation(pb.getLastLocation()));
			}
			for (ProtobufMarshaler.DeviceAlert alert : pb.getLatestAlertsList()) {
				state.getLatestAlerts().add(unmarshalDeviceAlert(alert));
			}
			for (ProtobufMarshaler.DeviceMeasurement pbmx : pb.getLatestMeasurementsList()) {
				DeviceMeasurement mx = new DeviceMeasurement();
				mx.setName(pbmx.getName());
				mx.setValue(pbmx.getValue());
				loadDeviceEventData(mx, pbmx.getEventData());
			}
			return state;
		} catch (IOException e) {
			throw new SiteWhereException("Unable to unmarshal device assignment state.", e);
		}
	}

	/**
	 * Recursively encode an {@link IDeviceElementSchema}.
	 * 
	 * @param schema
	 * @return
	 */
	protected ProtobufMarshaler.DeviceElementSchema encodeDeviceElementSchema(IDeviceElementSchema schema) {
		ProtobufMarshaler.DeviceElementSchema.Builder builder =
				ProtobufMarshaler.DeviceElementSchema.newBuilder();
		for (IDeviceUnit subunit : schema.getDeviceUnits()) {
			ProtobufMarshaler.DeviceUnit encoded = encodeDeviceUnit(subunit);
			builder.addUnits(encoded);
		}
		for (IDeviceSlot slot : schema.getDeviceSlots()) {
			ProtobufMarshaler.DeviceSlot encoded = encodeDeviceSlot(slot);
			builder.addSlots(encoded);
		}
		return builder.build();
	}

	/**
	 * Recursively encode an {@link IDeviceUnit}.
	 * 
	 * @param unit
	 * @return
	 */
	protected ProtobufMarshaler.DeviceUnit encodeDeviceUnit(IDeviceUnit unit) {
		ProtobufMarshaler.DeviceUnit.Builder builder = ProtobufMarshaler.DeviceUnit.newBuilder();
		builder.setName(unit.getName());
		builder.setPath(unit.getPath());
		for (IDeviceUnit subunit : unit.getDeviceUnits()) {
			ProtobufMarshaler.DeviceUnit encoded = encodeDeviceUnit(subunit);
			builder.addUnits(encoded);
		}
		for (IDeviceSlot slot : unit.getDeviceSlots()) {
			ProtobufMarshaler.DeviceSlot encoded = encodeDeviceSlot(slot);
			builder.addSlots(encoded);
		}
		return builder.build();
	}

	/**
	 * Encode an {@link IDeviceSlot}.
	 * 
	 * @param slot
	 * @return
	 */
	protected ProtobufMarshaler.DeviceSlot encodeDeviceSlot(IDeviceSlot slot) {
		ProtobufMarshaler.DeviceSlot.Builder builder = ProtobufMarshaler.DeviceSlot.newBuilder();
		builder.setName(slot.getName());
		builder.setPath(slot.getPath());
		return builder.build();
	}

	protected DeviceElementSchema decodeDeviceElementSchema(ProtobufMarshaler.DeviceElementSchema pb) {
		DeviceElementSchema schema = new DeviceElementSchema();
		for (ProtobufMarshaler.DeviceUnit subunit : pb.getUnitsList()) {
			schema.getDeviceUnits().add(decodeDeviceUnit(subunit));
		}
		for (ProtobufMarshaler.DeviceSlot slot : pb.getSlotsList()) {
			schema.getDeviceSlots().add(decodeDeviceSlot(slot));
		}
		return schema;
	}

	protected DeviceUnit decodeDeviceUnit(ProtobufMarshaler.DeviceUnit pb) {
		DeviceUnit unit = new DeviceUnit();
		unit.setName(pb.getName());
		unit.setPath(pb.getPath());
		for (ProtobufMarshaler.DeviceUnit subunit : pb.getUnitsList()) {
			unit.getDeviceUnits().add(decodeDeviceUnit(subunit));
		}
		for (ProtobufMarshaler.DeviceSlot slot : pb.getSlotsList()) {
			unit.getDeviceSlots().add(decodeDeviceSlot(slot));
		}
		return unit;
	}

	protected DeviceSlot decodeDeviceSlot(ProtobufMarshaler.DeviceSlot pb) {
		DeviceSlot slot = new DeviceSlot();
		slot.setName(pb.getName());
		slot.setPath(pb.getPath());
		return slot;
	}

	/**
	 * Create object that stores common device event data.
	 * 
	 * @param event
	 * @return
	 */
	protected ProtobufMarshaler.DeviceEventData createDeviceEventData(IDeviceEvent event) {
		ProtobufMarshaler.DeviceEventData.Builder builder = ProtobufMarshaler.DeviceEventData.newBuilder();
		builder.setId(event.getId());
		builder.setEventType(event.getEventType().name());
		builder.setSiteToken(event.getSiteToken());
		builder.setDeviceAssignmentToken(event.getDeviceAssignmentToken());
		builder.setAssignmentType(event.getAssignmentType().name());
		builder.setAssetModuleId(event.getAssetModuleId());
		builder.setAssetId(event.getAssetId());
		builder.setEventDate(event.getEventDate().getTime());
		builder.setReceivedDate(event.getReceivedDate().getTime());
		builder.addAllMetadata(asMetadataEntries(event.getMetadata()));
		return builder.build();
	}

	/**
	 * Load event data from common storage object.
	 * 
	 * @param event
	 * @param pb
	 * @throws SiteWhereException
	 */
	protected void loadDeviceEventData(DeviceEvent event, ProtobufMarshaler.DeviceEventData pb)
			throws SiteWhereException {
		event.setId(pb.getId());
		event.setEventType(DeviceEventType.valueOf(pb.getEventType()));
		event.setSiteToken(pb.getSiteToken());
		event.setDeviceAssignmentToken(pb.getDeviceAssignmentToken());
		event.setAssignmentType(DeviceAssignmentType.valueOf(pb.getAssignmentType()));
		event.setAssetModuleId(pb.getAssetModuleId());
		event.setAssetId(pb.getAssetId());
		event.setEventDate(new Date(pb.getEventDate()));
		event.setReceivedDate(new Date(pb.getReceivedDate()));
		for (ProtobufMarshaler.MetadataEntry entry : pb.getMetadataList()) {
			event.addOrReplaceMetadata(entry.getName(), entry.getValue());
		}
	}

	/**
	 * Converts a metadata map into individial entries.
	 * 
	 * @param metadata
	 * @return
	 */
	protected List<ProtobufMarshaler.MetadataEntry> asMetadataEntries(Map<String, String> metadata) {
		List<ProtobufMarshaler.MetadataEntry> entries = new ArrayList<ProtobufMarshaler.MetadataEntry>();
		for (String key : metadata.keySet()) {
			ProtobufMarshaler.MetadataEntry entry =
					ProtobufMarshaler.MetadataEntry.newBuilder().setName(key).setValue(metadata.get(key)).build();
			entries.add(entry);
		}
		return entries;
	}

	/**
	 * Builds common entity data object.
	 * 
	 * @param entity
	 * @return
	 */
	protected ProtobufMarshaler.EntityData createEntityData(IMetadataProviderEntity entity) {
		ProtobufMarshaler.EntityData.Builder builder = ProtobufMarshaler.EntityData.newBuilder();
		builder.setCreatedDate(entity.getCreatedDate().getTime());
		builder.setCreatedBy(entity.getCreatedBy());
		if (entity.getUpdatedDate() != null) {
			builder.setUpdatedDate(entity.getUpdatedDate().getTime());
		}
		if (entity.getUpdatedBy() != null) {
			builder.setUpdatedBy(entity.getUpdatedBy());
		}
		builder.setDeleted(entity.isDeleted());
		return builder.build();
	}

	/**
	 * Load common entity data.
	 * 
	 * @param pb
	 * @param entity
	 */
	protected void loadEntityData(ProtobufMarshaler.EntityData pb, MetadataProviderEntity entity)
			throws SiteWhereException {
		entity.setCreatedDate(new Date(pb.getCreatedDate()));
		entity.setCreatedBy(pb.getCreatedBy());
		if (pb.hasUpdatedDate()) {
			entity.setUpdatedDate(new Date(pb.getUpdatedDate()));
		}
		if (pb.hasUpdatedBy()) {
			entity.setUpdatedBy(pb.getUpdatedBy());
		}
		if (pb.hasDeleted()) {
			entity.setDeleted(pb.getDeleted());
		}
		for (ProtobufMarshaler.MetadataEntry entry : pb.getMetadataList()) {
			entity.addOrReplaceMetadata(entry.getName(), entry.getValue());
		}
	}
}