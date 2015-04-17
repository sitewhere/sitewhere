/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication.protobuf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sitewhere.device.communication.protobuf.proto.Sitewhere.SiteWhere;
import com.sitewhere.device.communication.protobuf.proto.Sitewhere.SiteWhere.Acknowledge;
import com.sitewhere.device.communication.protobuf.proto.Sitewhere.SiteWhere.DeviceAlert;
import com.sitewhere.device.communication.protobuf.proto.Sitewhere.SiteWhere.DeviceLocation;
import com.sitewhere.device.communication.protobuf.proto.Sitewhere.SiteWhere.DeviceMeasurements;
import com.sitewhere.device.communication.protobuf.proto.Sitewhere.SiteWhere.DeviceStreamData;
import com.sitewhere.device.communication.protobuf.proto.Sitewhere.SiteWhere.Header;
import com.sitewhere.device.communication.protobuf.proto.Sitewhere.SiteWhere.Measurement;
import com.sitewhere.device.communication.protobuf.proto.Sitewhere.SiteWhere.Metadata;
import com.sitewhere.device.communication.protobuf.proto.Sitewhere.SiteWhere.RegisterDevice;
import com.sitewhere.rest.model.device.communication.DecodedDeviceEventRequest;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceCommandResponseCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceRegistrationRequest;
import com.sitewhere.rest.model.device.event.request.DeviceStreamDataCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IDecodedDeviceEventRequest;
import com.sitewhere.spi.device.communication.IDeviceEventDecoder;
import com.sitewhere.spi.device.event.AlertLevel;

/**
 * Decodes a message payload that was previously encoded using the Google Protocol Buffers
 * with the SiteWhere proto.
 * 
 * @author Derek
 */
public class ProtobufDeviceEventDecoder implements IDeviceEventDecoder<byte[]> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(ProtobufDeviceEventDecoder.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IDeviceEventDecoder#decode(java.lang.Object)
	 */
	@Override
	public List<IDecodedDeviceEventRequest> decode(byte[] payload) throws SiteWhereException {
		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(payload);
			Header header = SiteWhere.Header.parseDelimitedFrom(stream);
			List<IDecodedDeviceEventRequest> results = new ArrayList<IDecodedDeviceEventRequest>();
			DecodedDeviceEventRequest decoded = new DecodedDeviceEventRequest();
			if (header.hasOriginator()) {
				decoded.setOriginator(header.getOriginator());
			}
			results.add(decoded);
			switch (header.getCommand()) {
			case REGISTER: {
				RegisterDevice register = RegisterDevice.parseDelimitedFrom(stream);
				LOGGER.debug("Decoded registration for: " + register.getHardwareId());
				DeviceRegistrationRequest request = new DeviceRegistrationRequest();
				request.setHardwareId(register.getHardwareId());
				request.setSpecificationToken(register.getSpecificationToken());
				if (register.hasSiteToken()) {
					request.setSiteToken(register.getSiteToken());
				}

				List<Metadata> pbmeta = register.getMetadataList();
				Map<String, String> metadata = new HashMap<String, String>();
				for (Metadata meta : pbmeta) {
					metadata.put(meta.getName(), meta.getValue());
				}
				request.setMetadata(metadata);

				decoded.setHardwareId(register.getHardwareId());
				decoded.setRequest(request);
				return results;
			}
			case ACKNOWLEDGE: {
				Acknowledge ack = Acknowledge.parseDelimitedFrom(stream);
				LOGGER.debug("Decoded acknowledge for: " + ack.getHardwareId());
				DeviceCommandResponseCreateRequest request = new DeviceCommandResponseCreateRequest();
				request.setOriginatingEventId(header.getOriginator());
				request.setResponse(ack.getMessage());
				decoded.setHardwareId(ack.getHardwareId());
				decoded.setRequest(request);
				return results;
			}
			case DEVICE_MEASUREMENTS: {
				DeviceMeasurements dm = DeviceMeasurements.parseDelimitedFrom(stream);
				LOGGER.debug("Decoded measurement for: " + dm.getHardwareId());
				DeviceMeasurementsCreateRequest request = new DeviceMeasurementsCreateRequest();
				List<Measurement> measurements = dm.getMeasurementList();
				for (Measurement current : measurements) {
					request.addOrReplaceMeasurement(current.getMeasurementId(), current.getMeasurementValue());
				}

				List<Metadata> pbmeta = dm.getMetadataList();
				Map<String, String> metadata = new HashMap<String, String>();
				for (Metadata meta : pbmeta) {
					metadata.put(meta.getName(), meta.getValue());
				}
				request.setMetadata(metadata);

				if (dm.hasEventDate()) {
					request.setEventDate(new Date(dm.getEventDate()));
				} else {
					request.setEventDate(new Date());
				}
				decoded.setHardwareId(dm.getHardwareId());
				decoded.setRequest(request);
				return results;
			}
			case DEVICE_LOCATION: {
				DeviceLocation location = DeviceLocation.parseDelimitedFrom(stream);
				LOGGER.debug("Decoded location for: " + location.getHardwareId());
				DeviceLocationCreateRequest request = new DeviceLocationCreateRequest();
				request.setLatitude(location.getLatitude());
				request.setLongitude(location.getLongitude());
				request.setElevation(location.getElevation());

				List<Metadata> pbmeta = location.getMetadataList();
				Map<String, String> metadata = new HashMap<String, String>();
				for (Metadata meta : pbmeta) {
					metadata.put(meta.getName(), meta.getValue());
				}
				request.setMetadata(metadata);

				if (location.hasEventDate()) {
					request.setEventDate(new Date(location.getEventDate()));
				} else {
					request.setEventDate(new Date());
				}
				decoded.setHardwareId(location.getHardwareId());
				decoded.setRequest(request);
				return results;
			}
			case DEVICE_ALERT: {
				DeviceAlert alert = DeviceAlert.parseDelimitedFrom(stream);
				LOGGER.debug("Decoded alert for: " + alert.getHardwareId());
				DeviceAlertCreateRequest request = new DeviceAlertCreateRequest();
				request.setType(alert.getAlertType());
				request.setMessage(alert.getAlertMessage());
				request.setLevel(AlertLevel.Info);

				List<Metadata> pbmeta = alert.getMetadataList();
				Map<String, String> metadata = new HashMap<String, String>();
				for (Metadata meta : pbmeta) {
					metadata.put(meta.getName(), meta.getValue());
				}
				request.setMetadata(metadata);

				if (alert.hasEventDate()) {
					request.setEventDate(new Date(alert.getEventDate()));
				} else {
					request.setEventDate(new Date());
				}
				decoded.setHardwareId(alert.getHardwareId());
				decoded.setRequest(request);
				return results;
			}
			case DEVICE_STREAM_DATA: {
				DeviceStreamData streamData = DeviceStreamData.parseDelimitedFrom(stream);
				LOGGER.debug("Decoded stream data for: " + streamData.getHardwareId());
				DeviceStreamDataCreateRequest request = new DeviceStreamDataCreateRequest();
				request.setStreamId(streamData.getStreamId());
				request.setSequenceNumber(streamData.getSequenceNumber());
				request.setData(streamData.getData().toByteArray());

				List<Metadata> pbmeta = streamData.getMetadataList();
				Map<String, String> metadata = new HashMap<String, String>();
				for (Metadata meta : pbmeta) {
					metadata.put(meta.getName(), meta.getValue());
				}
				request.setMetadata(metadata);

				if (streamData.hasEventDate()) {
					request.setEventDate(new Date(streamData.getEventDate()));
				} else {
					request.setEventDate(new Date());
				}
				decoded.setHardwareId(streamData.getHardwareId());
				decoded.setRequest(request);
				return results;
			}
			default: {
				throw new SiteWhereException("Unable to decode message. Type not supported: "
						+ header.getCommand().name());
			}
			}
		} catch (IOException e) {
			throw new SiteWhereException("Unable to decode protobuf message.", e);
		}
	}
}