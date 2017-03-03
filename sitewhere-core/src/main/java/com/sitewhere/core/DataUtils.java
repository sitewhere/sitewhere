/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.core;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.google.bitcoin.core.Base58;

public class DataUtils {

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

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

    /**
     * Generates a short 22-character Base 64 encoded version of a UUID.
     * 
     * @return
     */
    public static String generateShortUUID() {
	UUID uuid = UUID.randomUUID();
	ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
	bb.putLong(uuid.getMostSignificantBits());
	bb.putLong(uuid.getLeastSignificantBits());
	return Base58.encode(bb.array());
    }
}