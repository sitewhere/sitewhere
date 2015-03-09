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
import com.sitewhere.rest.model.device.DeviceSpecification;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.SiteMapData;
import com.sitewhere.rest.model.device.element.DeviceElementSchema;
import com.sitewhere.rest.model.device.element.DeviceSlot;
import com.sitewhere.rest.model.device.element.DeviceUnit;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.common.IMetadataProviderEntity;
import com.sitewhere.spi.device.DeviceContainerPolicy;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.element.IDeviceElementSchema;
import com.sitewhere.spi.device.element.IDeviceSlot;
import com.sitewhere.spi.device.element.IDeviceUnit;

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