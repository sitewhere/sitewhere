/*
 * MarshalUtils.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.spi.SiteWhereException;

public class MarshalUtils {

	/**
	 * Marshal an object to a JSON string.
	 * 
	 * @param object
	 * @return
	 * @throws SiteWhereException
	 */
	public static byte[] marshalJson(Object object) throws SiteWhereException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsBytes(object);
		} catch (JsonProcessingException e) {
			throw new SiteWhereException("Could not marshal device as JSON.", e);
		}
	}

	/**
	 * Unmarshal a JSON string to an object.
	 * 
	 * @param json
	 * @param type
	 * @return
	 * @throws SiteWhereException
	 */
	public static <T> T unmarshalJson(byte[] json, Class<T> type) throws SiteWhereException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(json, type);
		} catch (Throwable e) {
			throw new SiteWhereException("Unable to parse JSON.", e);
		}
	}
}
