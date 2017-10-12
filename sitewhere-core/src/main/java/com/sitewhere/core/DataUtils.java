/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;

import com.sitewhere.spi.SiteWhereException;

public class DataUtils {

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /** Singleton document builder factory instance */
    private static DocumentBuilderFactory DOCUMENTBUILDER_FACTORY;

    private static final Object LOCK = new Object();

    /** Supports many potential date formats */
    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(
	    "[yyyyMMdd][yyyy-MM-dd][yyyy-DDD]['T'[HHmmss][HHmm][HH:mm:ss][HH:mm][.SSSSSSSSS][.SSSSSS][.SSS][.SS][.S]][OOOO][O][z][XXXXX][XXXX]['['VV']']",
	    Locale.ENGLISH);

    /**
     * Get singleton instance of document builder factory. This prevents the
     * expense of creating it on-the-fly.
     * 
     * @return
     * @throws SiteWhereException
     */
    public static DocumentBuilderFactory getDocumentBuilderFactory() throws SiteWhereException {
	DocumentBuilderFactory factory = DOCUMENTBUILDER_FACTORY;
	if (factory == null) {
	    synchronized (LOCK) {
		if (factory == null) {
		    try {
			factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DOCUMENTBUILDER_FACTORY = factory;
		    } catch (FactoryConfigurationError e) {
			throw new SiteWhereException("Unable to create document builder factory.", e);
		    }
		}
	    }
	}
	return factory;
    }

    /**
     * Parses a date string using many potential formats. Copied from
     * http://stackoverflow.com/questions/34637626/java-datetimeformatter-for-time-zone-with-an-optional-colon-separator
     * 
     * @param date
     * @return
     * @throws DateTimeParseException
     */
    public static ZonedDateTime parseDateInMutipleFormats(String date) throws DateTimeParseException {
	TemporalAccessor temporalAccessor = FORMATTER.parseBest(date, ZonedDateTime::from, LocalDateTime::from,
		LocalDate::from);
	if (temporalAccessor instanceof ZonedDateTime) {
	    return ((ZonedDateTime) temporalAccessor);
	}
	if (temporalAccessor instanceof LocalDateTime) {
	    return ((LocalDateTime) temporalAccessor).atZone(ZoneId.systemDefault());
	}
	return ((LocalDate) temporalAccessor).atStartOfDay(ZoneId.systemDefault());
    }

    /**
     * Convert a byte array to a hex string.
     * 
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
	char[] hexChars = new char[bytes.length * 2];
	int v;
	for (int j = 0; j < bytes.length; j++) {
	    v = bytes[j] & 0xFF;
	    hexChars[j * 2] = hexArray[v >>> 4];
	    hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	}
	return new String(hexChars);
    }

    /**
     * Returns the regex version of a byte.
     * 
     * @param value
     * @return
     */
    public static String regexHex(byte value) {
	int v = value & 0xFF;
	char[] chars = new char[4];
	chars[0] = '\\';
	chars[1] = 'x';
	chars[2] = hexArray[v >>> 4];
	chars[3] = hexArray[v & 0x0F];
	return new String(chars);
    }
}