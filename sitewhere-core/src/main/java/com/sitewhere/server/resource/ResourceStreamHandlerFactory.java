/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.resource;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * Factory that allows the custom SiteWhere resource protocol to be plugged in
 * for URL resolution.
 * 
 * @author Derek
 */
public class ResourceStreamHandlerFactory implements URLStreamHandlerFactory {

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.net.URLStreamHandlerFactory#createURLStreamHandler(java.lang.String)
     */
    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
	if (ResourceUrlConnection.PROTO_SITEWHERE.equals(protocol)) {
	    return new ResourceStreamHandler();
	}
	return null;
    }
}