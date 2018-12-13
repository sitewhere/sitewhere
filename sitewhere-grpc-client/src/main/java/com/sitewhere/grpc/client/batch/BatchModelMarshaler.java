package com.sitewhere.grpc.client.batch;

/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sitewhere.grpc.model.BatchModel.GUnprocessedBatchElement;
import com.sitewhere.grpc.model.BatchModel.GUnprocessedBatchOperation;
import com.sitewhere.spi.SiteWhereException;

public class BatchModelMarshaler {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(BatchModelMarshaler.class);

    /**
     * Build binary message for unprocessed batch operation payload.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static byte[] buildUnprocessedBatchOperationPayloadMessage(GUnprocessedBatchOperation grpc)
	    throws SiteWhereException {
	ByteArrayOutputStream output = new ByteArrayOutputStream();
	try {
	    grpc.writeTo(output);
	    return output.toByteArray();
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to build unprocessed batch operation payload message.", e);
	} finally {
	    closeQuietly(output);
	}
    }

    /**
     * Parse message that contains a unprocessed batch operation payload.
     * 
     * @param payload
     * @return
     * @throws SiteWhereException
     */
    public static GUnprocessedBatchOperation parseUnprocessedBatchOperationPayloadMessage(byte[] payload)
	    throws SiteWhereException {
	try {
	    return GUnprocessedBatchOperation.parseFrom(payload);
	} catch (InvalidProtocolBufferException e) {
	    throw new SiteWhereException("Unable to parse unprocessed batch operation payload message.", e);
	}
    }

    /**
     * Build binary message for unprocessed batch element payload.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static byte[] buildUnprocessedBatchElementPayloadMessage(GUnprocessedBatchElement grpc)
	    throws SiteWhereException {
	ByteArrayOutputStream output = new ByteArrayOutputStream();
	try {
	    grpc.writeTo(output);
	    return output.toByteArray();
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to build unprocessed batch element payload message.", e);
	} finally {
	    closeQuietly(output);
	}
    }

    /**
     * Parse message that contains a unprocessed batch element payload.
     * 
     * @param payload
     * @return
     * @throws SiteWhereException
     */
    public static GUnprocessedBatchElement parseUnprocessedBatchElementPayloadMessage(byte[] payload)
	    throws SiteWhereException {
	try {
	    return GUnprocessedBatchElement.parseFrom(payload);
	} catch (InvalidProtocolBufferException e) {
	    throw new SiteWhereException("Unable to parse unprocessed batch element payload message.", e);
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
