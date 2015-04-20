/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication.protobuf;

import java.io.ByteArrayOutputStream;
import java.util.Set;

import com.sitewhere.device.communication.protobuf.proto.Sitewhere.Model;
import com.sitewhere.device.communication.protobuf.proto.Sitewhere.SiteWhere;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IDecodedDeviceRequest;
import com.sitewhere.spi.device.communication.IDeviceEventEncoder;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;

/**
 * Implementation of {@link IDeviceEventEncoder} that encodes device events into binary
 * using the SiteWhere Google Protocol Buffers format.
 * 
 * @author Derek
 */
public class ProtobufDeviceEventEncoder implements IDeviceEventEncoder<byte[]> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IDeviceEventEncoder#encode(com.sitewhere
	 * .spi.device.communication.IDecodedDeviceRequest)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public byte[] encode(IDecodedDeviceRequest<?> event) throws SiteWhereException {
		if (event.getRequest() instanceof IDeviceMeasurementsCreateRequest) {
			return encodeDeviceMeasurements((IDecodedDeviceRequest<IDeviceMeasurementsCreateRequest>) event);
		}
		throw new SiteWhereException("Protobuf encoder encountered unknown event type: "
				+ event.getClass().getName());
	}

	/**
	 * Encode a {@link IDecodedDeviceRequest} in a protobuf message.
	 * 
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	protected byte[] encodeDeviceMeasurements(IDecodedDeviceRequest<IDeviceMeasurementsCreateRequest> event)
			throws SiteWhereException {
		try {
			IDeviceMeasurementsCreateRequest measurements =
					(IDeviceMeasurementsCreateRequest) event.getRequest();
			Model.DeviceMeasurements.Builder mb = Model.DeviceMeasurements.newBuilder();
			mb.setHardwareId(event.getHardwareId());
			Set<String> keys = measurements.getMeasurements().keySet();
			for (String key : keys) {
				mb.addMeasurement(Model.Measurement.newBuilder().setMeasurementId(key).setMeasurementValue(
						measurements.getMeasurement(key)).build());
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
}