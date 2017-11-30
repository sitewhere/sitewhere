/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.model.marshaling;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sitewhere.grpc.kafka.model.KafkaModel.GEnrichedEventPayload;
import com.sitewhere.grpc.kafka.model.KafkaModel.GInboundEventPayload;
import com.sitewhere.grpc.kafka.model.KafkaModel.GPersistedEventPayload;
import com.sitewhere.grpc.kafka.model.KafkaModel.GStateUpdate;
import com.sitewhere.grpc.kafka.model.KafkaModel.GTenantModelUpdate;
import com.sitewhere.grpc.kafka.model.KafkaModel.GTenantModelUpdateType;
import com.sitewhere.grpc.model.converter.KafkaModelConverter;
import com.sitewhere.grpc.model.converter.TenantModelConverter;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.kafka.payload.IInboundEventPayload;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Methods that support marshaling/unmarshaling Kafka payloads.
 * 
 * @author Derek
 */
public class KafkaModelMarshaler {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /**
     * Build message that reflects a tenant model update.
     * 
     * @param type
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    public static byte[] buildTenantModelUpdateMessage(GTenantModelUpdateType type, ITenant tenant)
	    throws SiteWhereException {
	GTenantModelUpdate.Builder update = GTenantModelUpdate.newBuilder();
	update.setType(type);
	update.setTenant(TenantModelConverter.asGrpcTenant(tenant));
	ByteArrayOutputStream output = new ByteArrayOutputStream();
	try {
	    update.build().writeTo(output);
	    return output.toByteArray();
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to build tenant update message.", e);
	} finally {
	    closeQuietly(output);
	}
    }

    /**
     * Parse message that reflects a tenant model update.
     * 
     * @param payload
     * @return
     * @throws SiteWhereException
     */
    public static GTenantModelUpdate parseTenantModelUpdateMessage(byte[] payload) throws SiteWhereException {
	try {
	    return GTenantModelUpdate.parseFrom(payload);
	} catch (InvalidProtocolBufferException e) {
	    throw new SiteWhereException("Unable to parse tenant update message.", e);
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
	GInboundEventPayload grpc = KafkaModelConverter.asGrpcInboundEventPayload(api);
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

    /**
     * Build binary message for GRPC state update.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static byte[] buildStateUpdatePayloadMessage(GStateUpdate grpc) throws SiteWhereException {
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

    protected static void closeQuietly(OutputStream output) {
	if (output != null) {
	    try {
		output.close();
	    } catch (IOException e) {
		LOGGER.error(e);
	    }
	}
    }
}