/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.encoder;

import java.util.HashMap;
import java.util.Map;

/**
 * Resolves marshalers for various payload encodings.
 * 
 * @author Derek
 */
public class PayloadMarshalerResolver {

    /** Singleton instance */
    private static PayloadMarshalerResolver INSTANCE;

    /** Map of marshalers by encoding */
    private static Map<PayloadEncoding, IPayloadMarshaler> MARSHALERS = new HashMap<PayloadEncoding, IPayloadMarshaler>();

    static {
	MARSHALERS.put(PayloadEncoding.Json, new JsonPayloadMarshaler());
	MARSHALERS.put(PayloadEncoding.ProtocolBuffers, new ProtobufPayloadMarshaler());
    }

    /**
     * Get singleton instance.
     * 
     * @return
     */
    public static PayloadMarshalerResolver getInstance() {
	if (INSTANCE == null) {
	    INSTANCE = new PayloadMarshalerResolver();
	}
	return INSTANCE;
    }

    /**
     * Get marshaler for a given encoding.
     * 
     * @param encoding
     * @return
     */
    public IPayloadMarshaler getMarshaler(PayloadEncoding encoding) {
	IPayloadMarshaler marshaler = MARSHALERS.get(encoding);
	if (marshaler == null) {
	    throw new RuntimeException("No marshaler registered for type: " + encoding);
	}
	return marshaler;
    }

    /**
     * Get marshaler based on encoding indicator.
     * 
     * @param indicator
     * @return
     */
    public IPayloadMarshaler getMarshaler(byte[] indicator) {
	PayloadEncoding encoding = PayloadEncoding.getEncoding(indicator);
	if (encoding == null) {
	    throw new RuntimeException("Unknown encoding type: " + new String(indicator));
	}
	return getMarshaler(encoding);
    }
}