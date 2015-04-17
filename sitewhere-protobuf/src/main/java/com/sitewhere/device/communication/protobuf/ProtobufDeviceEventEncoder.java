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

import com.sitewhere.device.communication.protobuf.proto.Sitewhere.SiteWhere;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IDecodedDeviceEventRequest;
import com.sitewhere.spi.device.communication.IDeviceEventEncoder;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
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
	 * .spi.device.communication.IDecodedDeviceEventRequest)
	 */
	@Override
	public byte[] encode(IDecodedDeviceEventRequest event) throws SiteWhereException {
		IDeviceEventCreateRequest request = event.getRequest();
		if (request instanceof IDeviceMeasurementsCreateRequest) {
			return encodeDeviceMeasurements(event);
		}
		throw new SiteWhereException("Protobuf encoder encountered unknown event type: "
				+ event.getClass().getName());
	}

	/**
	 * Encode a {@link IDecodedDeviceEventRequest} in a protobuf message.
	 * 
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	protected byte[] encodeDeviceMeasurements(IDecodedDeviceEventRequest event) throws SiteWhereException {
		try {
			IDeviceMeasurementsCreateRequest measurements =
					(IDeviceMeasurementsCreateRequest) event.getRequest();
			SiteWhere.DeviceMeasurements.Builder mb = SiteWhere.DeviceMeasurements.newBuilder();
			mb.setHardwareId(event.getHardwareId());
			Set<String> keys = measurements.getMeasurements().keySet();
			for (String key : keys) {
				mb.addMeasurement(SiteWhere.Measurement.newBuilder().setMeasurementId(key).setMeasurementValue(
						measurements.getMeasurement(key)).build());
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			SiteWhere.Header.Builder builder = SiteWhere.Header.newBuilder();
			builder.setCommand(SiteWhere.Command.DEVICE_MEASUREMENTS);
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