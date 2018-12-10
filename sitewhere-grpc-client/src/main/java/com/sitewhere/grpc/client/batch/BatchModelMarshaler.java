package com.sitewhere.grpc.client.batch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sitewhere.grpc.model.BatchModel.GBatchOperation;
import com.sitewhere.spi.SiteWhereException;

public class BatchModelMarshaler {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(BatchModelMarshaler.class);

    /**
     * Build binary message for batch operation payload.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static byte[] buildBatchOperationPayloadMessage(GBatchOperation grpc) throws SiteWhereException {
	ByteArrayOutputStream output = new ByteArrayOutputStream();
	try {
	    grpc.writeTo(output);
	    return output.toByteArray();
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to build batch operation payload message.", e);
	} finally {
	    closeQuietly(output);
	}
    }

    /**
     * Parse message that contains a batch operation payload.
     * 
     * @param payload
     * @return
     * @throws SiteWhereException
     */
    public static GBatchOperation parseBatchOperationPayloadMessage(byte[] payload) throws SiteWhereException {
	try {
	    return GBatchOperation.parseFrom(payload);
	} catch (InvalidProtocolBufferException e) {
	    throw new SiteWhereException("Unable to parse batch operation payload message.", e);
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
