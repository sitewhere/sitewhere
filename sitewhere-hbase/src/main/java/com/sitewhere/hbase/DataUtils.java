/*
 * DataUtils.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase;

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
}