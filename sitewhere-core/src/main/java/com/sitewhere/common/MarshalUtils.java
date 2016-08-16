/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sitewhere.spi.SiteWhereException;

public class MarshalUtils {

	/** Static logger instance */
	@SuppressWarnings("unused")
	private static Logger LOGGER = LogManager.getLogger();

	/** Singleton object mapper for JSON marshaling */
	private static ObjectMapper MAPPER = new ObjectMapper();

	/** Singleton mapper with pretty print turned on */
	private static ObjectMapper PRETTY_MAPPER = new ObjectMapper();

	// Enable pretty printing on the mapper.
	static {
		PRETTY_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
	}

	/**
	 * Marshal an object to a byte array.
	 * 
	 * @param object
	 * @return
	 * @throws SiteWhereException
	 */
	public static byte[] marshalJson(Object object) throws SiteWhereException {
		try {
			return MAPPER.writeValueAsBytes(object);
		} catch (JsonProcessingException e) {
			throw new SiteWhereException("Could not marshal object as JSON: " + object.getClass().getName(), e);
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
		try {
			return MAPPER.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new SiteWhereException("Could not marshal object as JSON: " + object.getClass().getName(), e);
		}
	}

	/**
	 * Marshal an object to a formatted JSON string.
	 * 
	 * @param object
	 * @return
	 * @throws SiteWhereException
	 */
	public static String marshalJsonAsPrettyString(Object object) throws SiteWhereException {
		try {
			return PRETTY_MAPPER.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new SiteWhereException("Could not marshal object as JSON: " + object.getClass().getName(), e);
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
		try {
			return MAPPER.readValue(json, type);
		} catch (Throwable e) {
			throw new SiteWhereException("Unable to parse JSON.", e);
		}
	}
}
