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

import com.google.protobuf.InvalidProtocolBufferException;
import com.sitewhere.device.provisioning.protobuf.proto.Sitewhere.sitewhere;
import com.sitewhere.device.provisioning.protobuf.proto.Sitewhere.sitewhere._type_registerDevice;
import com.sitewhere.rest.model.device.event.request.DeviceRegistrationCreateRequest;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.provisioning.IDeviceEventDecoder#decode(byte[])
	 */
	@Override
	public IDeviceEventCreateRequest decode(byte[] payload) throws SiteWhereException {
		try {
			sitewhere sw = sitewhere.parseFrom(payload);
			switch (sw.getCommand()) {
			case REGISTER: {
				_type_registerDevice register = sw.getRegisterDevice();
				DeviceRegistrationCreateRequest request = new DeviceRegistrationCreateRequest();
				request.setHardwareId(register.getHardwareId());
				request.setSpecificationToken(register.getSpecificationToken());
				request.setReplyTo(register.getReplyTo());
				return request;
			}
			default: {
				throw new SiteWhereException("Unable to decode message. Type not supported: "
						+ sw.getCommand().name());
			}
			}
		} catch (InvalidProtocolBufferException e) {
			throw new SiteWhereException("Unable to decode because message is not of expected type.", e);
		}
	}
}