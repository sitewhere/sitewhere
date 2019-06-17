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
import com.sitewhere.grpc.model.DeviceEventModel.GDecodedEventPayload;
import com.sitewhere.grpc.model.DeviceEventModel.GEnrichedEventPayload;
import com.sitewhere.grpc.model.DeviceEventModel.GPreprocessedEventPayload;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.kafka.IDecodedEventPayload;
import com.sitewhere.spi.device.event.kafka.IEnrichedEventPayload;
import com.sitewhere.spi.device.event.kafka.IPreprocessedEventPayload;

/**
 * Methods that support marshaling/unmarshaling event model payloads.
 * 
 * @author Derek
 */
public class EventModelMarshaler {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(EventModelMarshaler.class);

    /**
     * Build binary message for API decoded event payload.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static byte[] buildDecodedEventPayloadMessage(IDecodedEventPayload api) throws SiteWhereException {
	GDecodedEventPayload grpc = EventModelConverter.asGrpcDecodedEventPayload(api);
	return buildDecodedEventPayloadMessage(grpc);
    }

    /**
     * Build binary message for GRPC decoded event payload.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static byte[] buildDecodedEventPayloadMessage(GDecodedEventPayload grpc) throws SiteWhereException {
	ByteArrayOutputStream output = new ByteArrayOutputStream();
	try {
	    grpc.writeTo(output);
	    return output.toByteArray();
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to build decoded event payload message.", e);
	} finally {
	    closeQuietly(output);
	}
    }

    /**
     * Parse message that contains a decoded event payload.
     * 
     * @param payload
     * @return
     * @throws SiteWhereException
     */
    public static GDecodedEventPayload parseDecodedEventPayloadMessage(byte[] payload) throws SiteWhereException {
	try {
	    return GDecodedEventPayload.parseFrom(payload);
	} catch (InvalidProtocolBufferException e) {
	    throw new SiteWhereException("Unable to parse decoded event payload message.", e);
	}
    }

    /**
     * Build binary message for API preprocessed event payload.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static byte[] buildPreprocessedEventPayloadMessage(IPreprocessedEventPayload api) throws SiteWhereException {
	GPreprocessedEventPayload grpc = EventModelConverter.asGrpcPreprocessedEventPayload(api);
	return buildPreprocessedEventPayloadMessage(grpc);
    }

    /**
     * Build binary message for GRPC preprocessed event payload.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static byte[] buildPreprocessedEventPayloadMessage(GPreprocessedEventPayload grpc)
	    throws SiteWhereException {
	ByteArrayOutputStream output = new ByteArrayOutputStream();
	try {
	    grpc.writeTo(output);
	    return output.toByteArray();
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to build preprocessed event payload message.", e);
	} finally {
	    closeQuietly(output);
	}
    }

    /**
     * Parse message that contains a preprocessed event payload.
     * 
     * @param payload
     * @return
     * @throws SiteWhereException
     */
    public static GPreprocessedEventPayload parsePreprocessedEventPayloadMessage(byte[] payload)
	    throws SiteWhereException {
	try {
	    return GPreprocessedEventPayload.parseFrom(payload);
	} catch (InvalidProtocolBufferException e) {
	    throw new SiteWhereException("Unable to parse preprocessed event payload message.", e);
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