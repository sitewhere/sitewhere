/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.event;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceRegistationPayload;
import com.sitewhere.grpc.model.DeviceEventModel.GEnrichedEventPayload;
import com.sitewhere.grpc.model.DeviceEventModel.GInboundEventPayload;
import com.sitewhere.grpc.model.DeviceEventModel.GPersistedEventPayload;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.kafka.IDeviceRegistrationPayload;
import com.sitewhere.spi.device.event.kafka.IEnrichedEventPayload;
import com.sitewhere.spi.device.event.kafka.IInboundEventPayload;

/**
 * Methods that support marshaling/unmarshaling event model payloads.
 * 
 * @author Derek
 */
public class EventModelMarshaler {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(EventModelMarshaler.class);

    /**
     * Build binary message for API device registration payload.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static byte[] buildDeviceRegistrationPayloadMessage(IDeviceRegistrationPayload api)
	    throws SiteWhereException {
	GDeviceRegistationPayload grpc = EventModelConverter.asGrpcDeviceRegistrationPayload(api);
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

    /**
     * Build binary message for API inbound event payload.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static byte[] buildInboundEventPayloadMessage(IInboundEventPayload api) throws SiteWhereException {
	GInboundEventPayload grpc = EventModelConverter.asGrpcInboundEventPayload(api);
	return buildInboundEventPayloadMessage(grpc);
    }

    /**
     * Build binary message for GRPC inbound event payload.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static byte[] buildInboundEventPayloadMessage(GInboundEventPayload grpc) throws SiteWhereException {
	ByteArrayOutputStream output = new ByteArrayOutputStream();
	try {
	    grpc.writeTo(output);
	    return output.toByteArray();
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to build inbound event payload message.", e);
	} finally {
	    closeQuietly(output);
	}
    }

    /**
     * Parse message that contains an inbound event payload.
     * 
     * @param payload
     * @return
     * @throws SiteWhereException
     */
    public static GInboundEventPayload parseInboundEventPayloadMessage(byte[] payload) throws SiteWhereException {
	try {
	    return GInboundEventPayload.parseFrom(payload);
	} catch (InvalidProtocolBufferException e) {
	    throw new SiteWhereException("Unable to parse inbound event payload message.", e);
	}
    }

    /**
     * Build binary message for GRPC persisted event payload.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static byte[] buildPersistedEventPayloadMessage(GPersistedEventPayload grpc) throws SiteWhereException {
	ByteArrayOutputStream output = new ByteArrayOutputStream();
	try {
	    grpc.writeTo(output);
	    return output.toByteArray();
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to build persisted event payload message.", e);
	} finally {
	    closeQuietly(output);
	}
    }

    /**
     * Parse message that contains a persisted event payload.
     * 
     * @param payload
     * @return
     * @throws SiteWhereException
     */
    public static GPersistedEventPayload parsePersistedEventPayloadMessage(byte[] payload) throws SiteWhereException {
	try {
	    return GPersistedEventPayload.parseFrom(payload);
	} catch (InvalidProtocolBufferException e) {
	    throw new SiteWhereException("Unable to parse inbound event payload message.", e);
	}
    }

    /**
     * Build binary message for GRPC enriched event payload.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static byte[] buildEnrichedEventPayloadMessage(GEnrichedEventPayload grpc) throws SiteWhereException {
	ByteArrayOutputStream output = new ByteArrayOutputStream();
	try {
	    grpc.writeTo(output);
	    return output.toByteArray();
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to build enriched event payload message.", e);
	} finally {
	    closeQuietly(output);
	}
    }

    /**
     * Build binary message for API enriched event payload.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static byte[] buildEnrichedEventPayloadMessage(IEnrichedEventPayload api) throws SiteWhereException {
	GEnrichedEventPayload grpc = EventModelConverter.asGrpcEnrichedEventPayload(api);
	return buildEnrichedEventPayloadMessage(grpc);
    }

    /**
     * Parse message that contains an enriched event payload.
     * 
     * @param payload
     * @return
     * @throws SiteWhereException
     */
    public static GEnrichedEventPayload parseEnrichedEventPayloadMessage(byte[] payload) throws SiteWhereException {
	try {
	    return GEnrichedEventPayload.parseFrom(payload);
	} catch (InvalidProtocolBufferException e) {
	    throw new SiteWhereException("Unable to parse enriched event payload message.", e);
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