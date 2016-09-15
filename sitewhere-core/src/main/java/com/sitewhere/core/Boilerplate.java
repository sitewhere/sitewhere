/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.core;

import java.util.List;

/**
 * Helper class for wrapping messages in a border.
 * 
 * @author Derek
 */
public class Boilerplate {

    /**
     * Wrap a list of messages in boilerplate.
     * 
     * @param messages
     * @param border
     * @param size
     * @return
     */
    public static String boilerplate(List<String> messages, char border, int size) {
	String line = "";
	String internal = "";
	for (int i = 0; i < size; i++) {
	    line += border;
	    if ((i == 0) || (i == (size - 1))) {
		internal += border;
	    } else {
		internal += " ";
	    }
	}
	String result = "\n" + line + "\n";
	for (String message : messages) {
	    result += (boilerplate(message, internal) + "\n");
	}
	result += (line + "\n");
	return result;
    }

    /**
     * Wrap a single message in boilerplate, wrapping if necessary.
     * 
     * @param message
     * @param blank
     * @return
     */
    protected static String boilerplate(String message, String blank) {
	if (message.length() == 0) {
	    return blank;
	}
	if (message.length() <= (blank.length() - 4)) {
	    return blank.substring(0, 2) + message + blank.substring(2 + message.length());
	} else {
	    int noBorderLength = blank.length() - 4;
	    String[] chunks = message.split(" ");
	    String output = "";
	    String current = "";
	    for (String chunk : chunks) {
		if (chunk.length() > noBorderLength) {
		    chunk = chunk.substring(0, noBorderLength - 1);
		}
		if ((current.length() + chunk.length()) > noBorderLength) {
		    output += blank.substring(0, 2) + current + blank.substring(2 + current.length()) + "\n";
		    current = "  " + chunk + " ";
		} else {
		    current += chunk + " ";
		}
	    }
	    if (current.length() > 0) {
		output += blank.substring(0, 2) + current + blank.substring(2 + current.length());
	    }
	    return output;
	}
    }
}