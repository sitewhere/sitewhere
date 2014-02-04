/*
 * ProtobufDeviceEventDecoder.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning.protobuf;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.sitewhere.device.provisioning.protobuf.proto.Sitewhere.SiteWhere;
import com.sitewhere.device.provisioning.protobuf.proto.Sitewhere.SiteWhere.Acknowledge;
import com.sitewhere.device.provisioning.protobuf.proto.Sitewhere.SiteWhere.Header;
import com.sitewhere.device.provisioning.protobuf.proto.Sitewhere.SiteWhere.RegisterDevice;
import com.sitewhere.rest.model.device.event.request.DeviceRegistrationRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.device.provisioning.IDeviceEventDecoder;

/**
 * Decodes a message payload that was previously encoded using the Google Protocol Buffers
 * with the SiteWhere proto.
 * 
 * @author Derek
 */
public class ProtobufDeviceEventDecoder implements IDeviceEventDecoder {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(ProtobufDeviceEventDecoder.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.provisioning.IDeviceEventDecoder#decode(byte[])
	 */
	@Override
	public IDeviceEventCreateRequest decode(byte[] payload) throws SiteWhereException {
		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(payload);
			Header sw = SiteWhere.Header.parseDelimitedFrom(stream);
			switch (sw.getCommand()) {
			case REGISTER: {
				RegisterDevice register = RegisterDevice.parseDelimitedFrom(stream);
				DeviceRegistrationRequest request = new DeviceRegistrationRequest();
				request.setHardwareId(register.getHardwareId());
				request.setSpecificationToken(register.getSpecificationToken());
				request.setReplyTo(null);
				return request;
			}
			case ACKNOWLEDGE: {
				Acknowledge ack = Acknowledge.parseDelimitedFrom(stream);
				LOGGER.info("Got ack for: " + ack.getHardwareId());
				return null;
			}
			default: {
				throw new SiteWhereException("Unable to decode message. Type not supported: "
						+ sw.getCommand().name());
			}
			}
		} catch (IOException e) {
			throw new SiteWhereException("Unable to decode protobuf message.", e);
		}
	}
}