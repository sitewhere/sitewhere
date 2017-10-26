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

public class TracerLogUtils {

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
