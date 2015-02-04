/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.common;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.Tracer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.debug.TracerCategory;

public class MarshalUtils {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(MarshalUtils.class);

	/** Singleton object mapper for JSON marshaling */
	private static ObjectMapper MAPPER = new ObjectMapper();

	/**
	 * Marshal an object to a byte array.
	 * 
	 * @param object
	 * @return
	 * @throws SiteWhereException
	 */
	public static byte[] marshalJson(Object object) throws SiteWhereException {
		Tracer.push(TracerCategory.DataStore, "marshalJson", LOGGER);
		try {
			return MAPPER.writeValueAsBytes(object);
		} catch (JsonProcessingException e) {
			throw new SiteWhereException("Could not marshal device as JSON.", e);
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * Marshal an object to a JSON string.
	 * 
	 * @param object
	 * @return
	 * @throws SiteWhereException
	 */
	public static String marshalJsonAsString(Object object) throws SiteWhereException {
		Tracer.push(TracerCategory.DataStore, "marshalJson", LOGGER);
		try {
			return MAPPER.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new SiteWhereException("Could not marshal device as JSON.", e);
		} finally {
			Tracer.pop(LOGGER);
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
		Tracer.push(TracerCategory.DataStore, "unmarshalJson", LOGGER);
		try {
			return MAPPER.readValue(json, type);
		} catch (Throwable e) {
			throw new SiteWhereException("Unable to parse JSON.", e);
		} finally {
			Tracer.pop(LOGGER);
		}
	}
}
