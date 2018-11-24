/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.decoder.protobuf;

import java.io.ByteArrayOutputStream;

import com.sitewhere.communication.protobuf.proto.SiteWhere;
import com.sitewhere.communication.protobuf.proto.SiteWhere.DeviceEvent;
import com.sitewhere.communication.protobuf.proto.SiteWhere.DeviceEvent.Command;
import com.sitewhere.communication.protobuf.proto.SiteWhere.DeviceEvent.Header;
import com.sitewhere.communication.protobuf.proto.SiteWhere.GOptionalDouble;
import com.sitewhere.communication.protobuf.proto.SiteWhere.GOptionalFixed64;
import com.sitewhere.communication.protobuf.proto.SiteWhere.GOptionalString;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventEncoder;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
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
	if (event.getRequest() instanceof IDeviceMeasurementCreateRequest) {
	    return encodeDeviceMeasurements((IDecodedDeviceRequest<IDeviceMeasurementCreateRequest>) event);
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
    protected byte[] encodeDeviceMeasurements(IDecodedDeviceRequest<IDeviceMeasurementCreateRequest> event)
	    throws SiteWhereException {
	try {
	    IDeviceMeasurementCreateRequest measurements = (IDeviceMeasurementCreateRequest) event.getRequest();
	    // Header
	    DeviceEvent.Header.Builder headerBuilder = builHeader(event, Command.SendMeasurement);
	    // Payload
	    DeviceEvent.DeviceMeasurements.Builder payloadBuilder = DeviceEvent.DeviceMeasurements.newBuilder();
	    payloadBuilder.setEventDate(GOptionalFixed64.newBuilder().setValue(measurements.getEventDate().getTime()));
	    if (measurements.getMetadata() != null) {
		payloadBuilder.putAllMetadata(measurements.getMetadata());
	    }
	    payloadBuilder.addMeasurement(DeviceEvent.Measurement.newBuilder()
		    .setMeasurementId(GOptionalString.newBuilder().setValue(measurements.getName()))
		    .setMeasurementValue(GOptionalDouble.newBuilder().setValue(measurements.getValue())).build());
	    // Write to byte-stream
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    headerBuilder.build().writeDelimitedTo(out);
	    payloadBuilder.build().writeDelimitedTo(out);
	    out.close();
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
	    // Header
	    DeviceEvent.Header.Builder headerBuilder = builHeader(event, Command.SendAlert);
	    // Payload
	    DeviceEvent.DeviceAlert.Builder payloadBuilder = DeviceEvent.DeviceAlert.newBuilder();

	    payloadBuilder.setEventDate(GOptionalFixed64.newBuilder().setValue(alert.getEventDate().getTime()));
	    payloadBuilder.setAlertType(GOptionalString.newBuilder().setValue(alert.getType()));
	    payloadBuilder.setAlertMessage(GOptionalString.newBuilder().setValue(alert.getMessage()));
	    payloadBuilder.setLevel(fromModel(alert.getLevel()));
	    if (alert.getMetadata() != null) {
		payloadBuilder.putAllMetadata(alert.getMetadata());
	    }
	    // Write to byte-stream
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    headerBuilder.build().writeDelimitedTo(out);
	    payloadBuilder.build().writeDelimitedTo(out);
	    out.close();
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
	    // Header
	    DeviceEvent.Header.Builder headerBuilder = builHeader(event, Command.SendLocation);
	    // Payload
	    DeviceEvent.DeviceLocation.Builder payloadBuilder = DeviceEvent.DeviceLocation.newBuilder();
	    payloadBuilder.setEventDate(GOptionalFixed64.newBuilder().setValue(location.getEventDate().getTime()));
	    payloadBuilder.setLatitude(GOptionalDouble.newBuilder().setValue(location.getLatitude()));
	    payloadBuilder.setLongitude(GOptionalDouble.newBuilder().setValue(location.getLongitude()));
	    payloadBuilder.setElevation(GOptionalDouble.newBuilder().setValue(location.getElevation()));
	    if (location.getMetadata() != null) {
		payloadBuilder.putAllMetadata(location.getMetadata());
	    }
	    // Write to byte-stream
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    headerBuilder.build().writeDelimitedTo(out);
	    payloadBuilder.build().writeDelimitedTo(out);
	    out.close();
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
	    // Header
	    DeviceEvent.Header.Builder headerBuilder = builHeader(decoded, Command.SendRegistration);
	    // Payload
	    DeviceEvent.DeviceRegistrationRequest.Builder payloadBuilder = DeviceEvent.DeviceRegistrationRequest
		    .newBuilder();
	    payloadBuilder.setAreaToken(GOptionalString.newBuilder().setValue(request.getAreaToken()));
	    payloadBuilder.setDeviceTypeToken(GOptionalString.newBuilder().setValue(request.getDeviceTypeToken()));
	    payloadBuilder.setCustomerToken(GOptionalString.newBuilder().setValue(request.getCustomerToken()));
	    if (request.getMetadata() != null) {
		payloadBuilder.putAllMetadata(request.getMetadata());
	    }
	    // Write to byte-stream
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    headerBuilder.build().writeDelimitedTo(out);
	    payloadBuilder.build().writeDelimitedTo(out);
	    out.close();
	    return out.toByteArray();
	} catch (Exception e) {
	    throw new SiteWhereException(e);
	}
    }

    private static SiteWhere.DeviceEvent.AlertLevel fromModel(AlertLevel level) {
	if (level == null)
	    return SiteWhere.DeviceEvent.AlertLevel.Info;
	switch (level) {
	case Info:
	    return SiteWhere.DeviceEvent.AlertLevel.Info;
	case Warning:
	    return SiteWhere.DeviceEvent.AlertLevel.Warning;
	case Error:
	    return SiteWhere.DeviceEvent.AlertLevel.Error;
	case Critical:
	    return SiteWhere.DeviceEvent.AlertLevel.Critical;
	default:
	    return SiteWhere.DeviceEvent.AlertLevel.UNRECOGNIZED;
	}
    }

    private static Header.Builder builHeader(IDecodedDeviceRequest<?> event, DeviceEvent.Command command) {
	DeviceEvent.Header.Builder headerBuilder = DeviceEvent.Header.newBuilder();
	headerBuilder.setCommand(command);
	headerBuilder.setDeviceToken(GOptionalString.newBuilder().setValue(event.getDeviceToken()));
	if (event.getOriginator() != null) {
	    headerBuilder.setOriginator(GOptionalString.newBuilder().setValue(event.getOriginator()));
	}
	return headerBuilder;
    }
}