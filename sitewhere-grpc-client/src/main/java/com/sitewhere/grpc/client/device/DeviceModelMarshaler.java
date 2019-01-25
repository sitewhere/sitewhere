/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.device;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sitewhere.grpc.model.DeviceModel.GDeviceRegistationPayload;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.kafka.IDeviceRegistrationPayload;

/**
 * Methods that support marshaling/unmarshaling device model payloads.
 * 
 * @author Derek
 */
public class DeviceModelMarshaler {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(DeviceModelMarshaler.class);

    /**
     * Build binary message for API device registration payload.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static byte[] buildDeviceRegistrationPayloadMessage(IDeviceRegistrationPayload api)
	    throws SiteWhereException {
	GDeviceRegistationPayload grpc = DeviceModelConverter.asGrpcDeviceRegistrationPayload(api);
	return buildDeviceRegistrationPayloadMessage(grpc);
    }

    /**
     * Build binary message for GRPC device registration payload.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static byte[] buildDeviceRegistrationPayloadMessage(GDeviceRegistationPayload grpc)
	    throws SiteWhereException {
	ByteArrayOutputStream output = new ByteArrayOutputStream();
	try {
	    grpc.writeTo(output);
	    return output.toByteArray();
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to build device registration payload message.", e);
	} finally {
	    closeQuietly(output);
	}
    }

    /**
     * Parse message that contains an device registration payload.
     * 
     * @param payload
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceRegistationPayload parseDeviceRegistrationPayloadMessage(byte[] payload)
	    throws SiteWhereException {
	try {
	    return GDeviceRegistationPayload.parseFrom(payload);
	} catch (InvalidProtocolBufferException e) {
	    throw new SiteWhereException("Unable to parse device registration payload message.", e);
	}
    }

    protected static void closeQuietly(OutputStream output) {
	if (output != null) {
	    try {
		output.close();
	    } catch (IOException e) {
		LOGGER.error("Unable to close output stream.", e);
	    }
	}
    }
}
