/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

/**
 * Helper class for wrapping messages in a border.
 */
public class Boilerplate {

    /** Default message width for banners */
    public static final int DEFAULT_MESSAGE_WIDTH = 80;

    public static String boilerplate(String message) {
	return boilerplate(message, "*", DEFAULT_MESSAGE_WIDTH);
    }

    public static String boilerplate(String message, String character, int maxlength) {
	return boilerplate(new ArrayList<String>(Arrays.asList(new String[] { message })), character, maxlength);
    }

    public static String boilerplate(List<String> messages, String c) {
	return boilerplate(messages, c, DEFAULT_MESSAGE_WIDTH);
    }

    @SuppressWarnings("deprecation")
    public static String boilerplate(List<String> messages, String c, int maxlength) {
	int size;
	StringBuffer buf = new StringBuffer(messages.size() * maxlength);
	boolean charIsSpace = " ".equals(c);
	int trimLength = maxlength - (charIsSpace ? 2 : 4);

	for (int i = 0; i < messages.size(); i++) {
	    size = messages.get(i).toString().length();
	    if (size > trimLength) {
		String temp = messages.get(i).toString();
		int k = i;
		int x;
		int len;
		messages.remove(i);
		while (temp.length() > 0) {
		    len = (trimLength <= temp.length() ? trimLength : temp.length());
		    String msg = temp.substring(0, len);
		    x = msg.indexOf(SystemUtils.LINE_SEPARATOR);

		    if (x > -1) {
			msg = msg.substring(0, x);
			len = x + 1;
		    } else {
			x = msg.lastIndexOf(' ');
			if (x > -1 && len == trimLength) {
			    msg = msg.substring(0, x);
			    len = x + 1;
			}
		    }
		    if (msg.startsWith(" ")) {
			msg = msg.substring(1);
		    }

		    temp = temp.substring(len);
		    messages.add(k, msg);
		    k++;
		}
	    }
	}

	buf.append(SystemUtils.LINE_SEPARATOR);
	if (!charIsSpace) {
	    buf.append(StringUtils.repeat(c, maxlength));
	}

	for (int i = 0; i < messages.size(); i++) {
	    buf.append(SystemUtils.LINE_SEPARATOR);
	    if (!charIsSpace) {
		buf.append(c);
	    }
	    buf.append(" ");
	    buf.append(messages.get(i));

	    int padding;
	    padding = trimLength - messages.get(i).toString().getBytes().length;
	    if (padding > 0) {
		buf.append(StringUtils.repeat(" ", padding));
	    }
	    buf.append(' ');
	    if (!charIsSpace) {
		buf.append(c);
	    }
	}
	buf.append(SystemUtils.LINE_SEPARATOR);
	if (!charIsSpace) {
	    buf.append(StringUtils.repeat(c, maxlength));
	}
	return buf.toString();
    }
}