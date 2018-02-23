/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.decoder.protobuf;

import java.io.ByteArrayOutputStream;
import java.util.Set;

import com.sitewhere.communication.protobuf.proto.Sitewhere.Model;
import com.sitewhere.communication.protobuf.proto.Sitewhere.SiteWhere;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventEncoder;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;

/**
 * Implementation of {@link IDeviceEventEncoder} that encodes device events into
 * binary using the SiteWhere Google Protocol Buffers format.
 * 
 * @author Derek
 */
public class ProtobufDeviceEventEncoder implements IDeviceEventEncoder<byte[]> {

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceEventEncoder#encode(com.
     * sitewhere .spi.device.communication.IDecodedDeviceRequest)
     */
    @Override
    @SuppressWarnings("unchecked")
    public byte[] encode(IDecodedDeviceRequest<?> event) throws SiteWhereException {
	if (event.getRequest() instanceof IDeviceMeasurementsCreateRequest) {
	    return encodeDeviceMeasurements((IDecodedDeviceRequest<IDeviceMeasurementsCreateRequest>) event);
	} else if (event.getRequest() instanceof IDeviceAlertCreateRequest) {
	    return encodeDeviceAlert((IDecodedDeviceRequest<IDeviceAlertCreateRequest>) event);
	} else if (event.getRequest() instanceof IDeviceLocationCreateRequest) {
	    return encodeDeviceLocation((IDecodedDeviceRequest<IDeviceLocationCreateRequest>) event);
	} else if (event.getRequest() instanceof IDeviceRegistrationRequest) {
	    return encodeDeviceRegistration((IDecodedDeviceRequest<IDeviceRegistrationRequest>) event);
	}
	throw new SiteWhereException("Protobuf encoder encountered unknown event type: " + event.getClass().getName());
    }

    /**
     * Encode a {@link IDecodedDeviceRequest} containing measurements in a protobuf
     * message.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    protected byte[] encodeDeviceMeasurements(IDecodedDeviceRequest<IDeviceMeasurementsCreateRequest> event)
	    throws SiteWhereException {
	try {
	    IDeviceMeasurementsCreateRequest measurements = (IDeviceMeasurementsCreateRequest) event.getRequest();
	    Model.DeviceMeasurements.Builder mb = Model.DeviceMeasurements.newBuilder();
	    mb.setHardwareId(event.getDeviceToken());
	    mb.setEventDate(measurements.getEventDate().getTime());

	    if (measurements.getMetadata() != null) {
		Set<String> metaKeys = measurements.getMetadata().keySet();
		for (String key : metaKeys) {
		    mb.addMetadata(
			    Model.Metadata.newBuilder().setName(key).setValue(measurements.getMetadata().get(key)))
			    .build();
		}
	    }

	    Set<String> mxKeys = measurements.getMeasurements().keySet();
	    for (String key : mxKeys) {
		mb.addMeasurement(Model.Measurement.newBuilder().setMeasurementId(key)
			.setMeasurementValue(measurements.getMeasurement(key)).build());
	    }

	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    SiteWhere.Header.Builder builder = SiteWhere.Header.newBuilder();
	    builder.setCommand(SiteWhere.Command.SEND_DEVICE_MEASUREMENTS);
	    if (event.getOriginator() != null) {
		builder.setOriginator(event.getOriginator());
	    }

	    builder.build().writeDelimitedTo(out);
	    mb.build().writeDelimitedTo(out);
	    return out.toByteArray();
	} catch (Exception e) {
	    throw new SiteWhereException(e);
	}
    }

    /**
     * Encode a {@link IDecodedDeviceRequest} containing an alert in a protobuf
     * message.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    protected byte[] encodeDeviceAlert(IDecodedDeviceRequest<IDeviceAlertCreateRequest> event)
	    throws SiteWhereException {
	try {
	    IDeviceAlertCreateRequest alert = (IDeviceAlertCreateRequest) event.getRequest();
	    Model.DeviceAlert.Builder mb = Model.DeviceAlert.newBuilder();
	    mb.setHardwareId(event.getDeviceToken());
	    mb.setEventDate(alert.getEventDate().getTime());
	    mb.setAlertType(alert.getType());
	    mb.setAlertMessage(alert.getMessage());

	    if (alert.getMetadata() != null) {
		Set<String> metaKeys = alert.getMetadata().keySet();
		for (String key : metaKeys) {
		    mb.addMetadata(Model.Metadata.newBuilder().setName(key).setValue(alert.getMetadata().get(key)))
			    .build();
		}
	    }

	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    SiteWhere.Header.Builder builder = SiteWhere.Header.newBuilder();
	    builder.setCommand(SiteWhere.Command.SEND_DEVICE_ALERT);
	    if (event.getOriginator() != null) {
		builder.setOriginator(event.getOriginator());
	    }

	    builder.build().writeDelimitedTo(out);
	    mb.build().writeDelimitedTo(out);
	    return out.toByteArray();
	} catch (Exception e) {
	    throw new SiteWhereException(e);
	}
    }

    /**
     * Encode a {@link IDecodedDeviceRequest} containing a location in a protobuf
     * message.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    protected byte[] encodeDeviceLocation(IDecodedDeviceRequest<IDeviceLocationCreateRequest> event)
	    throws SiteWhereException {
	try {
	    IDeviceLocationCreateRequest location = (IDeviceLocationCreateRequest) event.getRequest();
	    Model.DeviceLocation.Builder mb = Model.DeviceLocation.newBuilder();
	    mb.setHardwareId(event.getDeviceToken());
	    mb.setEventDate(location.getEventDate().getTime());
	    mb.setLatitude(location.getLatitude());
	    mb.setLongitude(location.getLongitude());
	    mb.setElevation(location.getElevation());

	    if (location.getMetadata() != null) {
		Set<String> metaKeys = location.getMetadata().keySet();
		for (String key : metaKeys) {
		    mb.addMetadata(Model.Metadata.newBuilder().setName(key).setValue(location.getMetadata().get(key)))
			    .build();
		}
	    }

	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    SiteWhere.Header.Builder builder = SiteWhere.Header.newBuilder();
	    builder.setCommand(SiteWhere.Command.SEND_DEVICE_LOCATION);
	    if (event.getOriginator() != null) {
		builder.setOriginator(event.getOriginator());
	    }

	    builder.build().writeDelimitedTo(out);
	    mb.build().writeDelimitedTo(out);
	    return out.toByteArray();
	} catch (Exception e) {
	    throw new SiteWhereException(e);
	}
    }

    /**
     * Encode a {@link IDecodedDeviceRequest} containing a device registration in a
     * protobuf message.
     * 
     * @param decoded
     * @return
     * @throws SiteWhereException
     */
    protected byte[] encodeDeviceRegistration(IDecodedDeviceRequest<IDeviceRegistrationRequest> decoded)
	    throws SiteWhereException {
	try {
	    IDeviceRegistrationRequest request = (IDeviceRegistrationRequest) decoded.getRequest();
	    SiteWhere.RegisterDevice.Builder register = SiteWhere.RegisterDevice.newBuilder();
	    register.setHardwareId(request.getDeviceToken());
	    register.setAreaToken(request.getAreaToken());
	    register.setDeviceTypeToken(request.getDeviceTypeToken());

	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    SiteWhere.Header.Builder builder = SiteWhere.Header.newBuilder();
	    builder.setCommand(SiteWhere.Command.SEND_REGISTRATION);
	    if (decoded.getOriginator() != null) {
		builder.setOriginator(decoded.getOriginator());
	    }

	    builder.build().writeDelimitedTo(out);
	    register.build().writeDelimitedTo(out);
	    return out.toByteArray();
	} catch (Exception e) {
	    throw new SiteWhereException(e);
	}
    }
}