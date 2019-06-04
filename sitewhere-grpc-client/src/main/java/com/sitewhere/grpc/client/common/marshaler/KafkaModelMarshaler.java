/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.common.marshaler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sitewhere.grpc.kafka.model.KafkaModel.GMicroserviceLogMessage;
import com.sitewhere.grpc.kafka.model.KafkaModel.GStateUpdate;
import com.sitewhere.spi.SiteWhereException;

/**
 * Methods that support marshaling/unmarshaling Kafka payloads.
 * 
 * @author Derek
 */
public class KafkaModelMarshaler {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(KafkaModelMarshaler.class);

    /**
     * Build binary message for GRPC state update.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static byte[] buildStateUpdateMessage(GStateUpdate grpc) throws SiteWhereException {
	ByteArrayOutputStream output = new ByteArrayOutputStream();
	try {
	    grpc.writeTo(output);
	    return output.toByteArray();
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to build state update message.", e);
	} finally {
	    closeQuietly(output);
	}
    }

    /**
     * Parse message that reflects a microservice state update.
     * 
     * @param payload
     * @return
     * @throws SiteWhereException
     */
    public static GStateUpdate parseStateUpdateMessage(byte[] payload) throws SiteWhereException {
	try {
	    return GStateUpdate.parseFrom(payload);
	} catch (InvalidProtocolBufferException e) {
	    throw new SiteWhereException("Unable to parse state update message.", e);
	}
    }

    /**
     * Build binary message for GRPC microservice log message.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static byte[] buildMicroserviceLogMessage(GMicroserviceLogMessage grpc) throws SiteWhereException {
	ByteArrayOutputStream output = new ByteArrayOutputStream();
	try {
	    grpc.writeTo(output);
	    return output.toByteArray();
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to build microservice log message.", e);
	} finally {
	    closeQuietly(output);
	}
    }

    /**
     * Parse binary content for microservice log message.
     * 
     * @param payload
     * @return
     * @throws SiteWhereException
     */
    public static GMicroserviceLogMessage parseMicroserviceLogMessage(byte[] payload) throws SiteWhereException {
	try {
	    return GMicroserviceLogMessage.parseFrom(payload);
	} catch (InvalidProtocolBufferException e) {
	    throw new SiteWhereException("Unable to parse microservice log message.", e);
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