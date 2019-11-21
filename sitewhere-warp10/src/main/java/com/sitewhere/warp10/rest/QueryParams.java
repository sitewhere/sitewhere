/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.warp10.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryParams {

    private Map<String, String> parameters = new HashMap<String, String>();

    public static QueryParams builder() {
        QueryParams queryParams = new QueryParams();
        return queryParams;
    }

    public void addParameter(String key, String value) {
        this.parameters.put(key, value);
    }

    @Override
    public String toString() {
        return "{" + parameters.entrySet().stream().map(entry -> entry.getKey() + '=' + entry.getValue()).collect(Collectors.joining(",")) + "}&format=json&showattr=true&dedup=true";
    }
}
