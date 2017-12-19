/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.lifecycle;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.opentracing.ActiveSpan;
import io.opentracing.tag.Tags;

public class TracerUtils {

    /**
     * Log message to active span.
     * 
     * @param span
     * @param message
     */
    public static void logToSpan(ActiveSpan span, String message) {
	if (span != null) {
	    span.log(message);
	}
    }

    /**
     * Add tags and logs for error in span.
     * 
     * @param span
     * @param t
     */
    public static void handleErrorInTracerSpan(ActiveSpan span, Throwable t) {
	if (span != null) {
	    span.setTag(Tags.ERROR.getKey(), true);
	    span.log(TracerUtils.mapOf("error.object", t));
	    span.log(TracerUtils.mapOf("message", t.getMessage()));
	}
    }

    /**
     * Finish a started tracer span.
     * 
     * @param span
     */
    public static void finishTracerSpan(ActiveSpan span) {
	if (span != null) {
	    span.deactivate();
	}
    }

    /**
     * Builds unmodifiable map of a single value.
     * 
     * @param name
     * @param value
     * @return
     */
    public static Map<String, Object> mapOf(String name, Object value) {
	Map<String, Object> map = new HashMap<String, Object>();
	map.put(name, value);
	return Collections.unmodifiableMap(map);
    }
}
